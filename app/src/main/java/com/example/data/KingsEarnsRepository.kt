package com.example.data

import android.content.Context
import com.example.R
import com.example.model.AdminAuditLog
import com.example.model.AppNotification
import com.example.model.LedgerEntry
import com.example.model.LedgerType
import com.example.model.NotificationCategory
import com.example.model.PaymentMethod
import com.example.model.PlatformConfig
import com.example.model.ReferralRecord
import com.example.model.SupportTicket
import com.example.model.UserWalletProfile
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class KingsEarnsRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing cloud data.")

    private fun todayString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    suspend fun ensureUserProfile(user: FirebaseUser): Result<UserWalletProfile> {
        val uid = user.uid
        val path = "users/$uid"
        return try {
            val docRef = db.collection("users").document(uid)
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val today = todayString()
                val code = "KING" + uid.take(6).uppercase(Locale.US)
                val displayName = (user.displayName?.takeIf { it.isNotBlank() } ?: "Royal Member").take(80)
                val email = (user.email ?: "").take(120)
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to displayName,
                    "email" to email,
                    "referralCode" to code,
                    "availableDemoPoints" to 0L,
                    "pendingDemoPoints" to 0L,
                    "reservedDemoPoints" to 0L,
                    "lifetimeDemoPoints" to 0L,
                    "totalWithdrawnDemoPoints" to 0L,
                    "completedAdsCount" to 0L,
                    "dailyAdsWatchedToday" to 0L,
                    "dailyAdsDate" to today,
                    "dailyStreak" to 0L,
                    "lastDailyClaimDate" to "",
                    "accountVerified" to false,
                    "fraudFlag" to false,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                val welcomeId = "welcome_${uid.take(8)}"
                db.collection("users").document(uid).collection("notifications").document(welcomeId)
                    .set(
                        mapOf(
                            "userId" to uid,
                            "title" to "Cloud Sync Active (DEMO)",
                            "message" to "Your KINGS EARNS DEMO ledger is now backed up to Firebase Firestore.",
                            "category" to NotificationCategory.ACCOUNT.name,
                            "readStatus" to false,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
            }
            val refreshed = docRef.get().await()
                .toObject(UserWalletProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?: UserWalletProfile(userId = uid)
            Result.success(refreshed)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeUserProfile(userId: String): Flow<UserWalletProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snap ->
                    if (snap.exists()) {
                        snap.toObject(UserWalletProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .catch { error ->
                    unwrapAndLog(error, OperationType.GET, path, userId)
                    throw error
                }
        )
    }

    fun observeUserLedger(userId: String): Flow<List<LedgerEntry>> = flow {
        val path = "users/$userId/ledger"
        emitAll(
            db.collection("users").document(userId).collection("ledger")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .snapshots()
                .map { snap -> snap.toObjects(LedgerEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observeUserNotifications(userId: String): Flow<List<AppNotification>> = flow {
        val path = "users/$userId/notifications"
        emitAll(
            db.collection("users").document(userId).collection("notifications")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(40)
                .snapshots()
                .map { snap -> snap.toObjects(AppNotification::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observeUserWithdrawals(userId: String): Flow<List<WithdrawalRequest>> = flow {
        val path = "withdrawals"
        emitAll(
            db.collection("withdrawals")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snap ->
                    snap.toObjects(WithdrawalRequest::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observeUserReferrals(userId: String): Flow<List<ReferralRecord>> = flow {
        val path = "referrals"
        emitAll(
            db.collection("referrals")
                .whereEqualTo("referrerId", userId)
                .snapshots()
                .map { snap ->
                    snap.toObjects(ReferralRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observeUserSupportTickets(userId: String): Flow<List<SupportTicket>> = flow {
        val path = "supportTickets"
        emitAll(
            db.collection("supportTickets")
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snap ->
                    snap.toObjects(SupportTicket::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, path, userId)
                    throw error
                }
        )
    }

    fun observePlatformConfig(): Flow<PlatformConfig> = flow {
        val path = "platformConfig/global"
        emitAll(
            db.collection("platformConfig").document("global")
                .snapshots()
                .map { snap ->
                    if (snap.exists()) {
                        snap.toObject(PlatformConfig::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                            ?: PlatformConfig()
                    } else PlatformConfig()
                }
                .catch { error ->
                    val uid = auth.currentUser?.uid
                    if (uid != null) unwrapAndLog(error, OperationType.GET, path, uid)
                    emit(PlatformConfig())
                }
        )
    }

    suspend fun recordVerifiedAdReward(
        adUnitId: String,
        adTitle: String,
        category: String,
        rewardPoints: Long,
        rewardType: String,
        dailyLimit: Long
    ): Result<Long> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            docPath = "users/$uid"
            val userRef = db.collection("users").document(uid)
            val eventId = "ad_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val ledgerId = "led_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val notifId = "not_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val today = todayString()

            val newAvail = db.runTransaction { tx ->
                val snap = tx.get(userRef)
                val curAvail = (snap.getLong("availableDemoPoints") ?: 0L)
                val curLife = (snap.getLong("lifetimeDemoPoints") ?: 0L)
                val curCompleted = (snap.getLong("completedAdsCount") ?: 0L)
                val savedDate = snap.getString("dailyAdsDate") ?: today
                val watchedToday = if (savedDate == today) (snap.getLong("dailyAdsWatchedToday") ?: 0L) else 0L
                if (watchedToday >= dailyLimit) {
                    throw IllegalStateException("Daily limit of $dailyLimit verified ads reached.")
                }
                val updatedAvail = curAvail + rewardPoints
                tx.update(
                    userRef,
                    mapOf(
                        "availableDemoPoints" to updatedAvail,
                        "lifetimeDemoPoints" to (curLife + rewardPoints),
                        "completedAdsCount" to (curCompleted + 1L),
                        "dailyAdsWatchedToday" to (watchedToday + 1L),
                        "dailyAdsDate" to today,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                tx.set(
                    userRef.collection("adEvents").document(eventId),
                    mapOf(
                        "userId" to uid,
                        "adUnitId" to adUnitId.take(128),
                        "adTitle" to adTitle.take(120),
                        "category" to category.take(60),
                        "rewardPoints" to rewardPoints.coerceIn(1L, 25L),
                        "rewardType" to rewardType.ifBlank { "DEMO_POINTS" }.take(60),
                        "verifiedCallback" to true,
                        "demoMode" to true,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                tx.set(
                    userRef.collection("ledger").document(ledgerId),
                    mapOf(
                        "userId" to uid,
                        "type" to LedgerType.AD_REWARD.name,
                        "pointsDelta" to rewardPoints,
                        "balanceAfter" to updatedAvail,
                        "description" to "Verified Ad: $adTitle (DEMO)".take(200),
                        "referenceId" to eventId,
                        "demoMode" to true,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                tx.set(
                    userRef.collection("notifications").document(notifId),
                    mapOf(
                        "userId" to uid,
                        "title" to "+$rewardPoints DEMO Points Verified",
                        "message" to "AdMob completion verified for $adTitle. Balance: $updatedAvail DEMO pts.",
                        "category" to NotificationCategory.REWARD.name,
                        "readStatus" to false,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                updatedAvail
            }.await()
            Result.success(newAvail)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun claimDailyCheckIn(points: Long): Result<Long> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            docPath = "users/$uid"
            val userRef = db.collection("users").document(uid)
            val ledgerId = "led_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val today = todayString()

            val newAvail = db.runTransaction { tx ->
                val snap = tx.get(userRef)
                val lastClaim = snap.getString("lastDailyClaimDate") ?: ""
                if (lastClaim == today) {
                    throw IllegalStateException("Today's daily reward has already been claimed.")
                }
                val curAvail = snap.getLong("availableDemoPoints") ?: 0L
                val curLife = snap.getLong("lifetimeDemoPoints") ?: 0L
                val streak = (snap.getLong("dailyStreak") ?: 0L) + 1L
                val updatedAvail = curAvail + points
                tx.update(
                    userRef,
                    mapOf(
                        "availableDemoPoints" to updatedAvail,
                        "lifetimeDemoPoints" to (curLife + points),
                        "dailyStreak" to streak,
                        "lastDailyClaimDate" to today,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                tx.set(
                    userRef.collection("ledger").document(ledgerId),
                    mapOf(
                        "userId" to uid,
                        "type" to LedgerType.DAILY_CHECKIN.name,
                        "pointsDelta" to points,
                        "balanceAfter" to updatedAvail,
                        "description" to "Day $streak Daily Check-In (DEMO)",
                        "referenceId" to "DAILY_$today",
                        "demoMode" to true,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                updatedAvail
            }.await()
            Result.success(newAvail)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun savePayoutDetails(method: PaymentMethod, holder: String, number: String): Result<Unit> {
        var docPath = "users"
        return try {
            val uid = requireUserId()
            docPath = "users/$uid"
            val updates = when (method) {
                PaymentMethod.JAZZCASH -> mapOf(
                    "jazzCashHolder" to holder.trim().take(80),
                    "jazzCashNumber" to number.trim().take(20),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                PaymentMethod.EASYPAISA -> mapOf(
                    "easypaisaHolder" to holder.trim().take(80),
                    "easypaisaNumber" to number.trim().take(20),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            }
            db.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitWithdrawalRequest(
        method: PaymentMethod,
        accountHolderName: String,
        mobileNumber: String,
        requestedPoints: Long,
        feePoints: Long
    ): Result<WithdrawalRequest> {
        var docPath = "withdrawals"
        return try {
            val uid = requireUserId()
            require(requestedPoints >= 100L) { "Minimum DEMO withdrawal is 100 points." }
            require(accountHolderName.trim().length >= 2) { "Enter a valid account holder name." }
            require(mobileNumber.trim().length in 10..15) { "Enter a valid mobile account number (e.g. 03001234567)." }

            val userRef = db.collection("users").document(uid)
            val wdId = "WD_${UUID.randomUUID().toString().replace("-", "").take(12).uppercase(Locale.US)}"
            docPath = "withdrawals/$wdId"
            val wdRef = db.collection("withdrawals").document(wdId)
            val ledgerId = "led_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val notifId = "not_${UUID.randomUUID().toString().replace("-", "").take(16)}"

            val created = db.runTransaction { tx ->
                val userSnap = tx.get(userRef)
                val avail = userSnap.getLong("availableDemoPoints") ?: 0L
                val reserved = userSnap.getLong("reservedDemoPoints") ?: 0L
                val username = userSnap.getString("displayName") ?: "Royal Member"
                if (avail < requestedPoints) {
                    throw IllegalStateException("Insufficient available DEMO points.")
                }
                val newAvail = avail - requestedPoints
                val newReserved = reserved + requestedPoints
                val netPoints = requestedPoints - feePoints

                tx.update(
                    userRef,
                    mapOf(
                        "availableDemoPoints" to newAvail,
                        "reservedDemoPoints" to newReserved,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                tx.set(
                    wdRef,
                    mapOf(
                        "userId" to uid,
                        "username" to username.take(80),
                        "method" to method.name,
                        "accountHolderName" to accountHolderName.trim().take(80),
                        "mobileNumber" to mobileNumber.trim().take(15),
                        "requestedPoints" to requestedPoints,
                        "feePoints" to feePoints,
                        "netPoints" to netPoints,
                        "status" to WithdrawalStatus.PENDING.name,
                        "demoMode" to true,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                tx.set(
                    userRef.collection("ledger").document(ledgerId),
                    mapOf(
                        "userId" to uid,
                        "type" to LedgerType.WITHDRAW_RESERVE.name,
                        "pointsDelta" to -requestedPoints,
                        "balanceAfter" to newAvail,
                        "description" to "Reserved for ${method.displayName} manual review (DEMO)",
                        "referenceId" to wdId,
                        "demoMode" to true,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                tx.set(
                    userRef.collection("notifications").document(notifId),
                    mapOf(
                        "userId" to uid,
                        "title" to "Withdrawal Request Pending",
                        "message" to "Request $wdId ($requestedPoints DEMO pts via ${method.displayName}) is PENDING manual administrator verification.",
                        "category" to NotificationCategory.WITHDRAWAL.name,
                        "readStatus" to false,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                WithdrawalRequest(
                    id = wdId,
                    userId = uid,
                    username = username,
                    method = method.name,
                    accountHolderName = accountHolderName.trim(),
                    mobileNumber = mobileNumber.trim(),
                    requestedPoints = requestedPoints,
                    feePoints = feePoints,
                    netPoints = netPoints,
                    status = WithdrawalStatus.PENDING.name,
                    demoMode = true
                )
            }.await()
            Result.success(created)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitSupportTicket(subject: String, message: String, username: String): Result<Unit> {
        var docPath = "supportTickets"
        return try {
            val uid = requireUserId()
            require(subject.trim().length >= 3) { "Subject must be at least 3 characters." }
            require(message.trim().length >= 5) { "Message must be at least 5 characters." }
            val ticketId = "tkt_${UUID.randomUUID().toString().replace("-", "").take(14)}"
            docPath = "supportTickets/$ticketId"
            db.collection("supportTickets").document(ticketId).set(
                mapOf(
                    "userId" to uid,
                    "username" to username.ifBlank { "Royal Member" }.take(80),
                    "subject" to subject.trim().take(120),
                    "message" to message.trim().take(1000),
                    "status" to "OPEN",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun markNotificationRead(userId: String, notificationId: String): Result<Unit> {
        val path = "users/$userId/notifications/$notificationId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Refusing cross-user write." }
            db.collection("users").document(uid).collection("notifications").document(notificationId)
                .update("readStatus", true).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // --- ADMINISTRATOR QUERIES & ACTIONS ---

    suspend fun checkIsAdmin(): Boolean {
        val user = auth.currentUser ?: return false
        if (user.email.equals("zrn2469@gmail.com", ignoreCase = true) && user.isEmailVerified) {
            return true
        }
        return try {
            db.collection("admins").document(user.uid).get().await().exists()
        } catch (_: Exception) {
            false
        }
    }

    fun observeAllWithdrawalsForAdmin(): Flow<List<WithdrawalRequest>> = flow {
        val uid = auth.currentUser?.uid ?: return@flow
        emitAll(
            db.collection("withdrawals")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100)
                .snapshots()
                .map { snap -> snap.toObjects(WithdrawalRequest::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, "withdrawals", uid)
                    throw error
                }
        )
    }

    fun observeAllUsersForAdmin(): Flow<List<UserWalletProfile>> = flow {
        val uid = auth.currentUser?.uid ?: return@flow
        emitAll(
            db.collection("users")
                .limit(100)
                .snapshots()
                .map { snap -> snap.toObjects(UserWalletProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, "users", uid)
                    throw error
                }
        )
    }

    fun observeAllTicketsForAdmin(): Flow<List<SupportTicket>> = flow {
        val uid = auth.currentUser?.uid ?: return@flow
        emitAll(
            db.collection("supportTickets")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .snapshots()
                .map { snap -> snap.toObjects(SupportTicket::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, "supportTickets", uid)
                    throw error
                }
        )
    }

    fun observeAuditLogsForAdmin(): Flow<List<AdminAuditLog>> = flow {
        val uid = auth.currentUser?.uid ?: return@flow
        emitAll(
            db.collection("auditLogs")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(80)
                .snapshots()
                .map { snap -> snap.toObjects(AdminAuditLog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
                .catch { error ->
                    unwrapAndLog(error, OperationType.LIST, "auditLogs", uid)
                    throw error
                }
        )
    }

    suspend fun adminTransitionWithdrawal(
        withdrawal: WithdrawalRequest,
        nextStatus: WithdrawalStatus,
        externalTxRef: String = "",
        rejectionReason: String = ""
    ): Result<Unit> {
        val path = "withdrawals/${withdrawal.id}"
        return try {
            val adminUser = auth.currentUser ?: error("Admin authentication required.")
            val adminUid = adminUser.uid
            val adminEmail = (adminUser.email ?: adminUid).take(120)

            if (nextStatus == WithdrawalStatus.PAYMENT_SENT || nextStatus == WithdrawalStatus.PAID) {
                require(externalTxRef.trim().length >= 3) { "Enter the genuine JazzCash/Easypaisa transaction reference first." }
            }
            if (nextStatus == WithdrawalStatus.REJECTED) {
                require(rejectionReason.trim().length >= 3) { "Enter a clear rejection reason." }
            }

            val wdRef = db.collection("withdrawals").document(withdrawal.id)
            val targetUserRef = db.collection("users").document(withdrawal.userId)
            val ledgerId = "led_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val notifId = "not_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val auditId = "aud_${UUID.randomUUID().toString().replace("-", "").take(16)}"

            db.runTransaction { tx ->
                val wdSnap = tx.get(wdRef)
                val currentStatus = WithdrawalStatus.fromRaw(wdSnap.getString("status"))
                if (currentStatus == WithdrawalStatus.PAID || currentStatus == WithdrawalStatus.REJECTED) {
                    throw IllegalStateException("Terminal withdrawal status ($currentStatus) cannot be modified.")
                }
                val userSnap = tx.get(targetUserRef)
                val curAvail = userSnap.getLong("availableDemoPoints") ?: 0L
                val curReserved = userSnap.getLong("reservedDemoPoints") ?: 0L
                val curWithdrawn = userSnap.getLong("totalWithdrawnDemoPoints") ?: 0L
                val amount = withdrawal.requestedPoints

                when (nextStatus) {
                    WithdrawalStatus.PAID -> {
                        val newReserved = (curReserved - amount).coerceAtLeast(0L)
                        tx.update(
                            targetUserRef,
                            mapOf(
                                "reservedDemoPoints" to newReserved,
                                "totalWithdrawnDemoPoints" to (curWithdrawn + amount),
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                        tx.set(
                            targetUserRef.collection("ledger").document(ledgerId),
                            mapOf(
                                "userId" to withdrawal.userId,
                                "type" to LedgerType.WITHDRAW_PAID.name,
                                "pointsDelta" to 0L,
                                "balanceAfter" to curAvail,
                                "description" to "Manual ${withdrawal.method} payout confirmed (Ref: ${externalTxRef.trim()})",
                                "referenceId" to withdrawal.id,
                                "demoMode" to withdrawal.demoMode,
                                "createdAt" to FieldValue.serverTimestamp()
                            )
                        )
                    }
                    WithdrawalStatus.REJECTED -> {
                        val newReserved = (curReserved - amount).coerceAtLeast(0L)
                        val restoredAvail = curAvail + amount
                        tx.update(
                            targetUserRef,
                            mapOf(
                                "availableDemoPoints" to restoredAvail,
                                "reservedDemoPoints" to newReserved,
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                        tx.set(
                            targetUserRef.collection("ledger").document(ledgerId),
                            mapOf(
                                "userId" to withdrawal.userId,
                                "type" to LedgerType.WITHDRAW_RELEASE.name,
                                "pointsDelta" to amount,
                                "balanceAfter" to restoredAvail,
                                "description" to "Rejected withdrawal released (${rejectionReason.trim()})",
                                "referenceId" to withdrawal.id,
                                "demoMode" to withdrawal.demoMode,
                                "createdAt" to FieldValue.serverTimestamp()
                            )
                        )
                    }
                    else -> Unit
                }

                val wdUpdates = mutableMapOf<String, Any>(
                    "status" to nextStatus.name,
                    "reviewedByAdmin" to adminEmail,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (externalTxRef.isNotBlank()) {
                    wdUpdates["externalTxReference"] = externalTxRef.trim().take(120)
                }
                if (rejectionReason.isNotBlank()) {
                    wdUpdates["rejectionReason"] = rejectionReason.trim().take(300)
                }
                tx.update(wdRef, wdUpdates)

                tx.set(
                    targetUserRef.collection("notifications").document(notifId),
                    mapOf(
                        "userId" to withdrawal.userId,
                        "title" to "Withdrawal ${nextStatus.label}",
                        "message" to when (nextStatus) {
                            WithdrawalStatus.REJECTED -> "Withdrawal ${withdrawal.id} was rejected: ${rejectionReason.trim()}. Reserved points returned."
                            WithdrawalStatus.PAID -> "Withdrawal ${withdrawal.id} confirmed PAID. Ref: ${externalTxRef.trim()}."
                            else -> "Withdrawal ${withdrawal.id} status updated to ${nextStatus.label}."
                        },
                        "category" to NotificationCategory.WITHDRAWAL.name,
                        "readStatus" to false,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )

                tx.set(
                    db.collection("auditLogs").document(auditId),
                    mapOf(
                        "adminUid" to adminUid,
                        "adminEmail" to adminEmail,
                        "action" to "WITHDRAWAL_${nextStatus.name}",
                        "targetType" to "WITHDRAWAL",
                        "targetId" to withdrawal.id,
                        "details" to "Set ${withdrawal.id} (${withdrawal.method}, ${withdrawal.requestedPoints} pts) -> ${nextStatus.name}".take(400),
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminToggleUserVerification(targetUser: UserWalletProfile, verified: Boolean, fraudFlag: Boolean): Result<Unit> {
        val path = "users/${targetUser.userId}"
        return try {
            val adminUser = auth.currentUser ?: error("Admin authentication required.")
            val auditId = "aud_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val batch = db.batch()
            batch.update(
                db.collection("users").document(targetUser.userId),
                mapOf(
                    "accountVerified" to verified,
                    "fraudFlag" to fraudFlag,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.set(
                db.collection("auditLogs").document(auditId),
                mapOf(
                    "adminUid" to adminUser.uid,
                    "adminEmail" to (adminUser.email ?: adminUser.uid).take(120),
                    "action" to "USER_FLAGS_UPDATED",
                    "targetType" to "USER",
                    "targetId" to targetUser.userId,
                    "details" to "Updated verified=$verified, fraudFlag=$fraudFlag for ${targetUser.displayName}".take(400),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun adminUpdatePlatformConfig(config: PlatformConfig): Result<Unit> {
        val path = "platformConfig/global"
        return try {
            val adminUser = auth.currentUser ?: error("Admin authentication required.")
            val auditId = "aud_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val batch = db.batch()
            batch.set(
                db.collection("platformConfig").document("global"),
                mapOf(
                    "pointsPerAd" to config.pointsPerAd.coerceIn(1L, 25L),
                    "minWithdrawalPoints" to config.minWithdrawalPoints.coerceIn(10L, 100000L),
                    "feePoints" to config.feePoints.coerceIn(0L, 1000L),
                    "dailyAdLimit" to config.dailyAdLimit.coerceIn(1L, 500L),
                    "dailyCheckInPoints" to config.dailyCheckInPoints.coerceIn(1L, 50L),
                    "referralBonusPoints" to config.referralBonusPoints.coerceIn(0L, 100L),
                    "demoMode" to config.demoMode,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.set(
                db.collection("auditLogs").document(auditId),
                mapOf(
                    "adminUid" to adminUser.uid,
                    "adminEmail" to (adminUser.email ?: adminUser.uid).take(120),
                    "action" to "UPDATE_PLATFORM_CONFIG",
                    "targetType" to "PLATFORM_CONFIG",
                    "targetId" to "global",
                    "details" to "pointsPerAd=${config.pointsPerAd}, minWd=${config.minWithdrawalPoints}, fee=${config.feePoints}".take(400),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun adminReplySupportTicket(ticket: SupportTicket, status: String, reply: String): Result<Unit> {
        val path = "supportTickets/${ticket.id}"
        return try {
            val adminUser = auth.currentUser ?: error("Admin authentication required.")
            val auditId = "aud_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            val batch = db.batch()
            batch.update(
                db.collection("supportTickets").document(ticket.id),
                mapOf(
                    "status" to status,
                    "adminReply" to reply.trim().take(1000),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.set(
                db.collection("auditLogs").document(auditId),
                mapOf(
                    "adminUid" to adminUser.uid,
                    "adminEmail" to (adminUser.email ?: adminUser.uid).take(120),
                    "action" to "REPLY_SUPPORT_TICKET",
                    "targetType" to "SUPPORT_TICKET",
                    "targetId" to ticket.id,
                    "details" to "Status=$status".take(400),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    private fun unwrapAndLog(error: Throwable, op: OperationType, path: String, expectedUid: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(exceptionToLog, op, path)
        }
    }
}
