const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_001";
const BOB_UID = "bob_uid_002";
const ADMIN_UID = "admin_uid_999";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function pastDate(msOffset = 1000) {
  return new Date(Date.now() - msOffset);
}

function buildValidProfile(uid, overrides = {}) {
  const now = pastDate(5000);
  return {
    userId: uid,
    displayName: "Royal Earner",
    email: "earner@example.com",
    referralCode: "KING1234",
    availableDemoPoints: 0,
    pendingDemoPoints: 0,
    reservedDemoPoints: 0,
    lifetimeDemoPoints: 0,
    totalWithdrawnDemoPoints: 0,
    completedAdsCount: 0,
    dailyAdsWatchedToday: 0,
    dailyAdsDate: "2026-11-20",
    dailyStreak: 0,
    lastDailyClaimDate: "",
    accountVerified: false,
    fraudFlag: false,
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated requests are denied across all collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("withdrawals").where("userId", "==", ALICE_UID).get());
  await assertFails(unauthDb.collection("platformConfig").doc("global").get());
  await assertFails(unauthDb.collection("auditLogs").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(buildValidProfile(ALICE_UID))
  );
});

test("User profile create enforces zero starting points and blocks self-verification or ghost fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Valid initial profile succeeds
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(buildValidProfile(ALICE_UID))
  );

  // Non-zero initial points rejected
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set(
      buildValidProfile(BOB_UID, { availableDemoPoints: 500, lifetimeDemoPoints: 500 })
    )
  );

  // Self-assigned accountVerified=true rejected
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(BOB_UID).set(
      buildValidProfile(BOB_UID, { accountVerified: true })
    )
  );

  // Ghost field rejected
  await assertFails(
    bobDb.collection("users").doc(BOB_UID).set({
      ...buildValidProfile(BOB_UID),
      isAdmin: true,
    })
  );
});

test("User bounded ad reward update succeeds (+2 DEMO pts), unbounded tamper fails, and adEvents are immutable", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(buildValidProfile(ALICE_UID))
  );

  // Valid +2 DEMO points ad reward + adEvent + ledger entry
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      availableDemoPoints: 2,
      lifetimeDemoPoints: 2,
      completedAdsCount: 1,
      dailyAdsWatchedToday: 1,
      dailyAdsDate: "2026-11-20",
      updatedAt: pastDate(500),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("adEvents").doc("evt_001").set({
      userId: ALICE_UID,
      adUnitId: "ca-app-pub-3940256099942544/5224354917",
      adTitle: "Rewarded Video Test Ad",
      category: "AdMob Test",
      rewardPoints: 2,
      rewardType: "DEMO_POINTS",
      verifiedCallback: true,
      demoMode: true,
      createdAt: pastDate(500),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("ledger").doc("led_001").set({
      userId: ALICE_UID,
      type: "AD_REWARD",
      pointsDelta: 2,
      balanceAfter: 2,
      description: "Verified AdMob Rewarded Ad (DEMO)",
      referenceId: "evt_001",
      demoMode: true,
      createdAt: pastDate(500),
    })
  );

  // Unbounded reward tamper (+1000 points) must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      availableDemoPoints: 1002,
      lifetimeDemoPoints: 1002,
      updatedAt: pastDate(200),
    })
  );

  // Ledger & adEvent cannot be updated or deleted
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("ledger").doc("led_001").update({
      pointsDelta: 999,
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("adEvents").doc("evt_001").delete()
  );
});

test("Withdrawal creation enforces PENDING status, >=100 DEMO points, zero-sum reserve, and blocks self-approval", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(ALICE_UID).set(
      buildValidProfile(ALICE_UID, {
        availableDemoPoints: 120,
        lifetimeDemoPoints: 120,
        completedAdsCount: 60,
      })
    );
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Zero-sum balance reservation succeeds (120 -> 20 available, 100 reserved)
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      availableDemoPoints: 20,
      reservedDemoPoints: 100,
      updatedAt: pastDate(500),
    })
  );

  // Non-conserving reserve (creating money out of thin air) fails
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      availableDemoPoints: 20,
      reservedDemoPoints: 200,
      updatedAt: pastDate(400),
    })
  );

  // Valid PENDING JazzCash withdrawal succeeds
  const now = pastDate(500);
  await assertSucceeds(
    aliceDb.collection("withdrawals").doc("wd_001").set({
      userId: ALICE_UID,
      username: "Royal Earner",
      method: "JAZZCASH",
      accountHolderName: "Ali Khan",
      mobileNumber: "03001234567",
      requestedPoints: 100,
      feePoints: 0,
      netPoints: 100,
      status: "PENDING",
      demoMode: true,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Creating withdrawal directly as PAID fails
  await assertFails(
    aliceDb.collection("withdrawals").doc("wd_002").set({
      userId: ALICE_UID,
      username: "Royal Earner",
      method: "EASYPAISA",
      accountHolderName: "Ali Khan",
      mobileNumber: "03451234567",
      requestedPoints: 100,
      feePoints: 0,
      netPoints: 100,
      status: "PAID",
      demoMode: true,
      createdAt: now,
      updatedAt: now,
    })
  );

  // User cannot update own withdrawal to PAID
  await assertFails(
    aliceDb.collection("withdrawals").doc("wd_001").update({
      status: "PAID",
      updatedAt: pastDate(200),
    })
  );

  // Filtered withdrawal list query succeeds; unfiltered query fails for normal user
  await assertSucceeds(
    aliceDb.collection("withdrawals").where("userId", "==", ALICE_UID).get()
  );
  await assertFails(aliceDb.collection("withdrawals").get());
});

test("Cross-user isolation and self-referral prevention", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(BOB_UID).set(buildValidProfile(BOB_UID));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("ledger").get());

  // Self-referral fails
  const now = pastDate(500);
  await assertFails(
    aliceDb.collection("referrals").doc("ref_self").set({
      referrerId: ALICE_UID,
      referredUserId: ALICE_UID,
      referredName: "Alice",
      referralCode: "KING1234",
      status: "REGISTERED",
      rewardPoints: 10,
      demoMode: true,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("Spoofed admin email (email_verified=false) fails; verified admin completes manual payout & terminal lock holds", async () => {
  const now = pastDate(5000);
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("withdrawals").doc("wd_admin_1").set({
      userId: ALICE_UID,
      username: "Royal Earner",
      method: "EASYPAISA",
      accountHolderName: "Sara Ahmed",
      mobileNumber: "03459876543",
      requestedPoints: 100,
      feePoints: 0,
      netPoints: 100,
      status: "PENDING",
      demoMode: true,
      createdAt: now,
      updatedAt: now,
    });
  });

  // Spoof attack: email matches admin but email_verified is false
  const spoofAdminDb = testEnv.authenticatedContext("spoof_uid", {
    email: "zrn2469@gmail.com",
    email_verified: false,
  }).firestore();
  await assertFails(spoofAdminDb.collection("withdrawals").get());
  await assertFails(
    spoofAdminDb.collection("withdrawals").doc("wd_admin_1").update({
      status: "PAID",
      updatedAt: pastDate(1000),
    })
  );

  // Verified admin succeeds
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "zrn2469@gmail.com",
    email_verified: true,
  }).firestore();

  await assertSucceeds(adminDb.collection("withdrawals").get());
  await assertSucceeds(
    adminDb.collection("withdrawals").doc("wd_admin_1").update({
      status: "PAID",
      externalTxReference: "EP-TX-889911",
      reviewedByAdmin: "zrn2469@gmail.com",
      updatedAt: pastDate(500),
    })
  );

  await assertSucceeds(
    adminDb.collection("auditLogs").doc("log_001").set({
      adminUid: ADMIN_UID,
      adminEmail: "zrn2469@gmail.com",
      action: "MARK_WITHDRAWAL_PAID",
      targetType: "WITHDRAWAL",
      targetId: "wd_admin_1",
      details: "Confirmed manual Easypaisa payout EP-TX-889911",
      createdAt: pastDate(500),
    })
  );

  // Terminal state lock: once PAID, even admin cannot mutate withdrawal status
  await assertFails(
    adminDb.collection("withdrawals").doc("wd_admin_1").update({
      status: "REJECTED",
      updatedAt: pastDate(100),
    })
  );
});
