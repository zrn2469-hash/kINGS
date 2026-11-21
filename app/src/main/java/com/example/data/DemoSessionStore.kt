package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AppNotification
import com.example.model.LedgerEntry
import com.example.model.LedgerType
import com.example.model.NotificationCategory
import com.example.model.PaymentMethod
import com.example.model.PlatformConfig
import com.example.model.UserWalletProfile
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val Context.demoDataStore: DataStore<Preferences> by preferencesDataStore(name = "kings_earns_demo_store")

data class DemoSnapshot(
    val onboardingSeen: Boolean = false,
    val profile: UserWalletProfile = UserWalletProfile(),
    val ledger: List<LedgerEntry> = emptyList(),
    val withdrawals: List<WithdrawalRequest> = emptyList(),
    val notifications: List<AppNotification> = emptyList(),
    val completedTaskIds: Set<String> = emptySet(),
    val config: PlatformConfig = PlatformConfig()
)

class DemoSessionStore(private val context: Context) {

    private object Keys {
        val ONBOARDING_SEEN = booleanPreferencesKey("onboarding_seen")
        val AVAILABLE_POINTS = longPreferencesKey("available_demo_points")
        val RESERVED_POINTS = longPreferencesKey("reserved_demo_points")
        val LIFETIME_POINTS = longPreferencesKey("lifetime_demo_points")
        val WITHDRAWN_POINTS = longPreferencesKey("withdrawn_demo_points")
        val COMPLETED_ADS = longPreferencesKey("completed_ads_count")
        val DAILY_ADS_TODAY = longPreferencesKey("daily_ads_today")
        val DAILY_ADS_DATE = stringPreferencesKey("daily_ads_date")
        val DAILY_STREAK = longPreferencesKey("daily_streak")
        val LAST_DAILY_CLAIM = stringPreferencesKey("last_daily_claim")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val REFERRAL_CODE = stringPreferencesKey("referral_code")
        val REFERRED_BY = stringPreferencesKey("referred_by")
        val JAZZCASH_HOLDER = stringPreferencesKey("jazzcash_holder")
        val JAZZCASH_NUMBER = stringPreferencesKey("jazzcash_number")
        val EASYPAISA_HOLDER = stringPreferencesKey("easypaisa_holder")
        val EASYPAISA_NUMBER = stringPreferencesKey("easypaisa_number")
        val LEDGER_JSON = stringPreferencesKey("ledger_json")
        val WITHDRAWALS_JSON = stringPreferencesKey("withdrawals_json")
        val NOTIFICATIONS_JSON = stringPreferencesKey("notifications_json")
        val COMPLETED_TASKS_JSON = stringPreferencesKey("completed_tasks_json")
    }

    val demoSnapshotFlow: Flow<DemoSnapshot> = context.demoDataStore.data.map { prefs ->
        val today = todayString()
        val savedDate = prefs[Keys.DAILY_ADS_DATE] ?: today
        val adsToday = if (savedDate == today) (prefs[Keys.DAILY_ADS_TODAY] ?: 0L) else 0L
        val referralCode = prefs[Keys.REFERRAL_CODE] ?: "KINGDEMO"

        val profile = UserWalletProfile(
            id = "demo_guest",
            userId = "demo_guest",
            displayName = prefs[Keys.DISPLAY_NAME] ?: "Royal Demo Member",
            email = "",
            referralCode = referralCode,
            referredBy = prefs[Keys.REFERRED_BY] ?: "",
            availableDemoPoints = prefs[Keys.AVAILABLE_POINTS] ?: 0L,
            pendingDemoPoints = 0L,
            reservedDemoPoints = prefs[Keys.RESERVED_POINTS] ?: 0L,
            lifetimeDemoPoints = prefs[Keys.LIFETIME_POINTS] ?: 0L,
            totalWithdrawnDemoPoints = prefs[Keys.WITHDRAWN_POINTS] ?: 0L,
            completedAdsCount = prefs[Keys.COMPLETED_ADS] ?: 0L,
            dailyAdsWatchedToday = adsToday,
            dailyAdsDate = today,
            dailyStreak = prefs[Keys.DAILY_STREAK] ?: 0L,
            lastDailyClaimDate = prefs[Keys.LAST_DAILY_CLAIM] ?: "",
            jazzCashHolder = prefs[Keys.JAZZCASH_HOLDER] ?: "",
            jazzCashNumber = prefs[Keys.JAZZCASH_NUMBER] ?: "",
            easypaisaHolder = prefs[Keys.EASYPAISA_HOLDER] ?: "",
            easypaisaNumber = prefs[Keys.EASYPAISA_NUMBER] ?: "",
            accountVerified = false,
            fraudFlag = false
        )

        val notifications = parseNotifications(prefs[Keys.NOTIFICATIONS_JSON]).ifEmpty {
            listOf(
                AppNotification(
                    id = "welcome_demo",
                    userId = "demo_guest",
                    title = "Welcome to KINGS EARNS (DEMO)",
                    message = "You earn 2 DEMO Points per verified AdMob test rewarded ad. Reach 100 DEMO Points (50 ads) to submit a manual withdrawal review request.",
                    category = NotificationCategory.CAMPAIGN.name,
                    readStatus = false,
                    createdAt = Timestamp.now()
                )
            )
        }

        DemoSnapshot(
            onboardingSeen = prefs[Keys.ONBOARDING_SEEN] ?: false,
            profile = profile,
            ledger = parseLedger(prefs[Keys.LEDGER_JSON]),
            withdrawals = parseWithdrawals(prefs[Keys.WITHDRAWALS_JSON]),
            notifications = notifications,
            completedTaskIds = parseStringSet(prefs[Keys.COMPLETED_TASKS_JSON]),
            config = PlatformConfig()
        )
    }

    suspend fun markOnboardingSeen() {
        context.demoDataStore.edit { it[Keys.ONBOARDING_SEEN] = true }
    }

    suspend fun updateDisplayName(name: String) {
        context.demoDataStore.edit { it[Keys.DISPLAY_NAME] = name.trim().take(80) }
    }

    suspend fun savePayoutDetails(method: PaymentMethod, holder: String, number: String) {
        context.demoDataStore.edit { prefs ->
            when (method) {
                PaymentMethod.JAZZCASH -> {
                    prefs[Keys.JAZZCASH_HOLDER] = holder.trim()
                    prefs[Keys.JAZZCASH_NUMBER] = number.trim()
                }
                PaymentMethod.EASYPAISA -> {
                    prefs[Keys.EASYPAISA_HOLDER] = holder.trim()
                    prefs[Keys.EASYPAISA_NUMBER] = number.trim()
                }
            }
        }
    }

    suspend fun applyReferralCode(code: String): Result<String> {
        val clean = code.trim().uppercase(Locale.US)
        if (clean.length < 4) return Result.failure(IllegalArgumentException("Referral code must be at least 4 characters."))
        var failure: String? = null
        context.demoDataStore.edit { prefs ->
            val myCode = prefs[Keys.REFERRAL_CODE] ?: "KINGDEMO"
            val existing = prefs[Keys.REFERRED_BY] ?: ""
            when {
                clean == myCode -> failure = "Self-referral is not permitted."
                existing.isNotBlank() -> failure = "A referral code has already been linked to this account."
                else -> prefs[Keys.REFERRED_BY] = clean
            }
        }
        return if (failure != null) Result.failure(IllegalStateException(failure)) else Result.success(clean)
    }

    suspend fun recordVerifiedAdReward(adTitle: String, points: Long, dailyLimit: Long): Result<Long> {
        var result: Result<Long> = Result.success(0L)
        val today = todayString()
        context.demoDataStore.edit { prefs ->
            val savedDate = prefs[Keys.DAILY_ADS_DATE] ?: today
            val watchedToday = if (savedDate == today) (prefs[Keys.DAILY_ADS_TODAY] ?: 0L) else 0L
            if (watchedToday >= dailyLimit) {
                result = Result.failure(IllegalStateException("Daily limit of $dailyLimit verified ads reached. Come back tomorrow!"))
                return@edit
            }
            val currentAvail = prefs[Keys.AVAILABLE_POINTS] ?: 0L
            val currentLife = prefs[Keys.LIFETIME_POINTS] ?: 0L
            val currentAds = prefs[Keys.COMPLETED_ADS] ?: 0L
            val newBalance = currentAvail + points

            prefs[Keys.AVAILABLE_POINTS] = newBalance
            prefs[Keys.LIFETIME_POINTS] = currentLife + points
            prefs[Keys.COMPLETED_ADS] = currentAds + 1L
            prefs[Keys.DAILY_ADS_TODAY] = watchedToday + 1L
            prefs[Keys.DAILY_ADS_DATE] = today

            val refId = "DEMO_AD_${UUID.randomUUID().toString().take(8).uppercase(Locale.US)}"
            val ledgerList = parseLedger(prefs[Keys.LEDGER_JSON]).toMutableList()
            ledgerList.add(
                0,
                LedgerEntry(
                    id = UUID.randomUUID().toString(),
                    userId = "demo_guest",
                    type = LedgerType.AD_REWARD.name,
                    pointsDelta = points,
                    balanceAfter = newBalance,
                    description = "Verified Ad: $adTitle (DEMO)",
                    referenceId = refId,
                    demoMode = true,
                    createdAt = Timestamp.now()
                )
            )
            prefs[Keys.LEDGER_JSON] = serializeLedger(ledgerList.take(80))

            val notifList = parseNotifications(prefs[Keys.NOTIFICATIONS_JSON]).toMutableList()
            notifList.add(
                0,
                AppNotification(
                    id = UUID.randomUUID().toString(),
                    userId = "demo_guest",
                    title = "+$points DEMO Points Credited",
                    message = "Verified completion of $adTitle. Available DEMO balance: $newBalance pts.",
                    category = NotificationCategory.REWARD.name,
                    readStatus = false,
                    createdAt = Timestamp.now()
                )
            )
            prefs[Keys.NOTIFICATIONS_JSON] = serializeNotifications(notifList.take(40))
            result = Result.success(newBalance)
        }
        return result
    }

    suspend fun claimDailyCheckIn(points: Long): Result<Long> {
        var result: Result<Long> = Result.success(0L)
        val today = todayString()
        context.demoDataStore.edit { prefs ->
            val lastClaim = prefs[Keys.LAST_DAILY_CLAIM] ?: ""
            if (lastClaim == today) {
                result = Result.failure(IllegalStateException("Today's daily reward has already been claimed."))
                return@edit
            }
            val currentAvail = prefs[Keys.AVAILABLE_POINTS] ?: 0L
            val currentLife = prefs[Keys.LIFETIME_POINTS] ?: 0L
            val streak = (prefs[Keys.DAILY_STREAK] ?: 0L) + 1L
            val newBalance = currentAvail + points

            prefs[Keys.AVAILABLE_POINTS] = newBalance
            prefs[Keys.LIFETIME_POINTS] = currentLife + points
            prefs[Keys.DAILY_STREAK] = streak
            prefs[Keys.LAST_DAILY_CLAIM] = today

            val ledgerList = parseLedger(prefs[Keys.LEDGER_JSON]).toMutableList()
            ledgerList.add(
                0,
                LedgerEntry(
                    id = UUID.randomUUID().toString(),
                    userId = "demo_guest",
                    type = LedgerType.DAILY_CHECKIN.name,
                    pointsDelta = points,
                    balanceAfter = newBalance,
                    description = "Day $streak Daily Check-In (DEMO)",
                    referenceId = "DAILY_$today",
                    demoMode = true,
                    createdAt = Timestamp.now()
                )
            )
            prefs[Keys.LEDGER_JSON] = serializeLedger(ledgerList.take(80))
            result = Result.success(newBalance)
        }
        return result
    }

    suspend fun completeActivityTask(taskId: String, title: String, points: Long): Result<Long> {
        var result: Result<Long> = Result.success(0L)
        context.demoDataStore.edit { prefs ->
            val completed = parseStringSet(prefs[Keys.COMPLETED_TASKS_JSON]).toMutableSet()
            if (completed.contains(taskId)) {
                result = Result.failure(IllegalStateException("Activity already completed."))
                return@edit
            }
            completed.add(taskId)
            val currentAvail = prefs[Keys.AVAILABLE_POINTS] ?: 0L
            val currentLife = prefs[Keys.LIFETIME_POINTS] ?: 0L
            val newBalance = currentAvail + points

            prefs[Keys.AVAILABLE_POINTS] = newBalance
            prefs[Keys.LIFETIME_POINTS] = currentLife + points
            prefs[Keys.COMPLETED_TASKS_JSON] = JSONArray(completed.toList()).toString()

            val ledgerList = parseLedger(prefs[Keys.LEDGER_JSON]).toMutableList()
            ledgerList.add(
                0,
                LedgerEntry(
                    id = UUID.randomUUID().toString(),
                    userId = "demo_guest",
                    type = LedgerType.TASK_REWARD.name,
                    pointsDelta = points,
                    balanceAfter = newBalance,
                    description = "Activity: $title (DEMO)",
                    referenceId = taskId,
                    demoMode = true,
                    createdAt = Timestamp.now()
                )
            )
            prefs[Keys.LEDGER_JSON] = serializeLedger(ledgerList.take(80))
            result = Result.success(newBalance)
        }
        return result
    }

    suspend fun submitDemoWithdrawal(
        method: PaymentMethod,
        holderName: String,
        mobileNumber: String,
        requestedPoints: Long,
        feePoints: Long
    ): Result<WithdrawalRequest> {
        var result: Result<WithdrawalRequest> = Result.failure(IllegalStateException("Unknown error"))
        context.demoDataStore.edit { prefs ->
            val avail = prefs[Keys.AVAILABLE_POINTS] ?: 0L
            val reserved = prefs[Keys.RESERVED_POINTS] ?: 0L
            if (requestedPoints > avail) {
                result = Result.failure(IllegalStateException("Insufficient available DEMO points."))
                return@edit
            }
            val newAvail = avail - requestedPoints
            val newReserved = reserved + requestedPoints
            prefs[Keys.AVAILABLE_POINTS] = newAvail
            prefs[Keys.RESERVED_POINTS] = newReserved

            when (method) {
                PaymentMethod.JAZZCASH -> {
                    prefs[Keys.JAZZCASH_HOLDER] = holderName
                    prefs[Keys.JAZZCASH_NUMBER] = mobileNumber
                }
                PaymentMethod.EASYPAISA -> {
                    prefs[Keys.EASYPAISA_HOLDER] = holderName
                    prefs[Keys.EASYPAISA_NUMBER] = mobileNumber
                }
            }

            val reqId = "WD-DEMO-${UUID.randomUUID().toString().take(8).uppercase(Locale.US)}"
            val request = WithdrawalRequest(
                id = reqId,
                userId = "demo_guest",
                username = prefs[Keys.DISPLAY_NAME] ?: "Royal Demo Member",
                method = method.name,
                accountHolderName = holderName,
                mobileNumber = mobileNumber,
                requestedPoints = requestedPoints,
                feePoints = feePoints,
                netPoints = requestedPoints - feePoints,
                status = WithdrawalStatus.PENDING.name,
                demoMode = true,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            val wdList = parseWithdrawals(prefs[Keys.WITHDRAWALS_JSON]).toMutableList()
            wdList.add(0, request)
            prefs[Keys.WITHDRAWALS_JSON] = serializeWithdrawals(wdList.take(50))

            val ledgerList = parseLedger(prefs[Keys.LEDGER_JSON]).toMutableList()
            ledgerList.add(
                0,
                LedgerEntry(
                    id = UUID.randomUUID().toString(),
                    userId = "demo_guest",
                    type = LedgerType.WITHDRAW_RESERVE.name,
                    pointsDelta = -requestedPoints,
                    balanceAfter = newAvail,
                    description = "Reserved for ${method.displayName} manual review (DEMO)",
                    referenceId = reqId,
                    demoMode = true,
                    createdAt = Timestamp.now()
                )
            )
            prefs[Keys.LEDGER_JSON] = serializeLedger(ledgerList.take(80))

            val notifList = parseNotifications(prefs[Keys.NOTIFICATIONS_JSON]).toMutableList()
            notifList.add(
                0,
                AppNotification(
                    id = UUID.randomUUID().toString(),
                    userId = "demo_guest",
                    title = "Withdrawal Pending Review (DEMO)",
                    message = "Request $reqId for $requestedPoints DEMO pts via ${method.displayName} is PENDING manual administrator review.",
                    category = NotificationCategory.WITHDRAWAL.name,
                    readStatus = false,
                    createdAt = Timestamp.now()
                )
            )
            prefs[Keys.NOTIFICATIONS_JSON] = serializeNotifications(notifList.take(40))
            result = Result.success(request)
        }
        return result
    }

    suspend fun markNotificationRead(notificationId: String) {
        context.demoDataStore.edit { prefs ->
            val current = parseNotifications(prefs[Keys.NOTIFICATIONS_JSON]).map {
                if (it.id == notificationId) it.copy(readStatus = true) else it
            }
            prefs[Keys.NOTIFICATIONS_JSON] = serializeNotifications(current)
        }
    }

    private fun todayString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun parseStringSet(raw: String?): Set<String> {
        if (raw.isNullOrBlank()) return emptySet()
        return try {
            val arr = JSONArray(raw)
            buildSet {
                for (i in 0 until arr.length()) add(arr.optString(i))
            }
        } catch (_: Exception) {
            emptySet()
        }
    }

    private fun parseLedger(raw: String?): List<LedgerEntry> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        LedgerEntry(
                            id = o.optString("id"),
                            userId = "demo_guest",
                            type = o.optString("type"),
                            pointsDelta = o.optLong("pointsDelta"),
                            balanceAfter = o.optLong("balanceAfter"),
                            description = o.optString("description"),
                            referenceId = o.optString("referenceId"),
                            demoMode = true,
                            createdAt = Timestamp(Date(o.optLong("ts", System.currentTimeMillis())))
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun serializeLedger(items: List<LedgerEntry>): String {
        val arr = JSONArray()
        items.forEach { entry ->
            arr.put(JSONObject().apply {
                put("id", entry.id)
                put("type", entry.type)
                put("pointsDelta", entry.pointsDelta)
                put("balanceAfter", entry.balanceAfter)
                put("description", entry.description)
                put("referenceId", entry.referenceId)
                put("ts", (entry.createdAt ?: Timestamp.now()).toDate().time)
            })
        }
        return arr.toString()
    }

    private fun parseWithdrawals(raw: String?): List<WithdrawalRequest> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val ts = Timestamp(Date(o.optLong("ts", System.currentTimeMillis())))
                    add(
                        WithdrawalRequest(
                            id = o.optString("id"),
                            userId = "demo_guest",
                            username = o.optString("username"),
                            method = o.optString("method"),
                            accountHolderName = o.optString("holder"),
                            mobileNumber = o.optString("mobile"),
                            requestedPoints = o.optLong("requested"),
                            feePoints = o.optLong("fee"),
                            netPoints = o.optLong("net"),
                            status = o.optString("status"),
                            externalTxReference = o.optString("txRef"),
                            rejectionReason = o.optString("reason"),
                            demoMode = true,
                            createdAt = ts,
                            updatedAt = ts
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun serializeWithdrawals(items: List<WithdrawalRequest>): String {
        val arr = JSONArray()
        items.forEach { w ->
            arr.put(JSONObject().apply {
                put("id", w.id)
                put("username", w.username)
                put("method", w.method)
                put("holder", w.accountHolderName)
                put("mobile", w.mobileNumber)
                put("requested", w.requestedPoints)
                put("fee", w.feePoints)
                put("net", w.netPoints)
                put("status", w.status)
                put("txRef", w.externalTxReference)
                put("reason", w.rejectionReason)
                put("ts", (w.createdAt ?: Timestamp.now()).toDate().time)
            })
        }
        return arr.toString()
    }

    private fun parseNotifications(raw: String?): List<AppNotification> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        AppNotification(
                            id = o.optString("id"),
                            userId = "demo_guest",
                            title = o.optString("title"),
                            message = o.optString("message"),
                            category = o.optString("category"),
                            readStatus = o.optBoolean("read"),
                            createdAt = Timestamp(Date(o.optLong("ts", System.currentTimeMillis())))
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun serializeNotifications(items: List<AppNotification>): String {
        val arr = JSONArray()
        items.forEach { n ->
            arr.put(JSONObject().apply {
                put("id", n.id)
                put("title", n.title)
                put("message", n.message)
                put("category", n.category)
                put("read", n.readStatus)
                put("ts", (n.createdAt ?: Timestamp.now()).toDate().time)
            })
        }
        return arr.toString()
    }
}
