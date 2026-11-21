package com.example.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobLoadState
import com.example.ads.RewardedAdManager
import com.example.data.DemoSessionStore
import com.example.data.DemoSnapshot
import com.example.data.KingsEarnsRepository
import com.example.model.AdPlacementOffer
import com.example.model.AdminAuditLog
import com.example.model.AppNotification
import com.example.model.EarningActivityItem
import com.example.model.LedgerEntry
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
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class KingsEarnsUiState(
    val isCloudSignedIn: Boolean = false,
    val isAdmin: Boolean = false,
    val firebaseUserEmail: String = "",
    val onboardingSeen: Boolean = false,
    val profile: UserWalletProfile = UserWalletProfile(),
    val ledger: List<LedgerEntry> = emptyList(),
    val withdrawals: List<WithdrawalRequest> = emptyList(),
    val notifications: List<AppNotification> = emptyList(),
    val referrals: List<ReferralRecord> = emptyList(),
    val supportTickets: List<SupportTicket> = emptyList(),
    val config: PlatformConfig = PlatformConfig(),
    val adOffers: List<AdPlacementOffer> = defaultAdOffers(),
    val activities: List<EarningActivityItem> = emptyList(),
    val adMobState: AdMobLoadState = AdMobLoadState.Idle,
    val adMobUnitStates: Map<String, AdMobLoadState> = emptyMap(),
    val useTestAdUnit: Boolean = false,
    val activeAdUnitId: String = RewardedAdManager.LIVE_REWARDED_AD_UNIT_ID,
    val adminWithdrawals: List<WithdrawalRequest> = emptyList(),
    val adminUsers: List<UserWalletProfile> = emptyList(),
    val adminTickets: List<SupportTicket> = emptyList(),
    val adminAuditLogs: List<AdminAuditLog> = emptyList(),
    val isBusy: Boolean = false,
    val statusBanner: String? = null,
    val errorMessage: String? = null
)

fun defaultAdOffers(
    isTestFallback: Boolean = false
): List<AdPlacementOffer> {
    val suffix = if (isTestFallback) " (Test Ad)" else ""
    val networkName = if (isTestFallback) "Google AdMob Test Network" else "Google AdMob Rewarded Network"
    val unitIds = if (isTestFallback) {
        List(4) { RewardedAdManager.TEST_REWARDED_AD_UNIT_ID }
    } else {
        RewardedAdManager.LIVE_REWARDED_AD_UNIT_IDS
    }
    return listOf(
        AdPlacementOffer(
            id = "admob_rewarded_video_1",
            title = "Featured Rewarded Video$suffix",
            category = "Video Ad",
            advertiser = networkName,
            durationLabel = "15–30s",
            rewardPoints = 2L,
            adUnitId = unitIds[0],
            eligibilityNote = "Watch until completion callback fires. Credited only via OnUserEarnedReward."
        ),
        AdPlacementOffer(
            id = "admob_rewarded_app_2",
            title = "App Discovery Showcase$suffix",
            category = "Interactive Ad",
            advertiser = networkName,
            durationLabel = "15–30s",
            rewardPoints = 2L,
            adUnitId = unitIds[1],
            eligibilityNote = "Verified completion awards +2 DEMO Points toward the 100-pt threshold."
        ),
        AdPlacementOffer(
            id = "admob_rewarded_brand_3",
            title = "Royal Sponsor Spotlight$suffix",
            category = "Brand Campaign",
            advertiser = networkName,
            durationLabel = "15–30s",
            rewardPoints = 2L,
            adUnitId = unitIds[2],
            eligibilityNote = "Subject to daily ad cap and standard AdMob policy compliance."
        ),
        AdPlacementOffer(
            id = "admob_rewarded_bonus_4",
            title = "VIP Bonus Campaign$suffix",
            category = "Promotional Ad",
            advertiser = networkName,
            durationLabel = "15–30s",
            rewardPoints = 2L,
            adUnitId = unitIds[3],
            eligibilityNote = "Verified completion awards +2 DEMO Points with auditable ledger ref."
        )
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class KingsEarnsViewModel(
    private val repository: KingsEarnsRepository,
    private val demoStore: DemoSessionStore,
    private val adManager: RewardedAdManager,
    private val currentUserId: String?
) : ViewModel() {

    private val _isAdmin = MutableStateFlow(false)
    private val _isBusy = MutableStateFlow(false)
    private val _statusBanner = MutableStateFlow<String?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _useTestAdUnit = MutableStateFlow(false)

    private val todayStr: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private data class CloudUserBundle(
        val profile: UserWalletProfile?,
        val ledger: List<LedgerEntry>,
        val withdrawals: List<WithdrawalRequest>,
        val notifications: List<AppNotification>,
        val referrals: List<ReferralRecord>,
        val tickets: List<SupportTicket>,
        val config: PlatformConfig
    )

    private data class AdminBundle(
        val withdrawals: List<WithdrawalRequest>,
        val users: List<UserWalletProfile>,
        val tickets: List<SupportTicket>,
        val auditLogs: List<AdminAuditLog>
    )

    init {
        adManager.preloadAll(RewardedAdManager.LIVE_REWARDED_AD_UNIT_IDS)
        if (!currentUserId.isNullOrBlank()) {
            viewModelScope.launch {
                val user: FirebaseUser? = Firebase.auth.currentUser
                if (user != null) {
                    repository.ensureUserProfile(user)
                    _isAdmin.value = repository.checkIsAdmin()
                }
            }
        }
    }

    private fun <T> Flow<T>.safeCatch(fallback: T, label: String): Flow<T> = this.catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val root = firestoreError ?: error
        _errorMessage.value = "$label: ${root.localizedMessage ?: "Connection interrupted"}"
        emit(fallback)
    }

    private val cloudFlow: Flow<CloudUserBundle?> =
        if (currentUserId.isNullOrBlank()) {
            flowOf(null)
        } else {
            val baseA = combine(
                repository.observeUserProfile(currentUserId).safeCatch(null, "Profile"),
                repository.observeUserLedger(currentUserId).safeCatch(emptyList(), "Ledger"),
                repository.observeUserWithdrawals(currentUserId).safeCatch(emptyList(), "Withdrawals")
            ) { profile, ledger, withdrawals -> Triple(profile, ledger, withdrawals) }

            val baseB = combine(
                repository.observeUserNotifications(currentUserId).safeCatch(emptyList(), "Notifications"),
                repository.observeUserReferrals(currentUserId).safeCatch(emptyList(), "Referrals"),
                repository.observeUserSupportTickets(currentUserId).safeCatch(emptyList(), "Tickets"),
                repository.observePlatformConfig().safeCatch(PlatformConfig(), "Config")
            ) { notifs, referrals, tickets, config ->
                listOf(notifs, referrals, tickets, config)
            }

            combine(baseA, baseB) { triple, list ->
                @Suppress("UNCHECKED_CAST")
                CloudUserBundle(
                    profile = triple.first,
                    ledger = triple.second,
                    withdrawals = triple.third,
                    notifications = list[0] as List<AppNotification>,
                    referrals = list[1] as List<ReferralRecord>,
                    tickets = list[2] as List<SupportTicket>,
                    config = list[3] as PlatformConfig
                )
            }
        }

    private val adminFlow: Flow<AdminBundle> = _isAdmin.flatMapLatest { admin ->
        if (!admin || currentUserId.isNullOrBlank()) {
            flowOf(AdminBundle(emptyList(), emptyList(), emptyList(), emptyList()))
        } else {
            combine(
                repository.observeAllWithdrawalsForAdmin().safeCatch(emptyList(), "Admin Withdrawals"),
                repository.observeAllUsersForAdmin().safeCatch(emptyList(), "Admin Users"),
                repository.observeAllTicketsForAdmin().safeCatch(emptyList(), "Admin Tickets"),
                repository.observeAuditLogsForAdmin().safeCatch(emptyList(), "Admin Audit Logs")
            ) { wd, users, tickets, logs ->
                AdminBundle(wd, users, tickets, logs)
            }
        }
    }

    private val metaCoreFlow = combine(
        _isAdmin,
        _isBusy,
        _statusBanner,
        _errorMessage,
        adManager.loadState
    ) { isAdmin, busy, banner, err, adState ->
        MetaHolder(isAdmin, busy, banner, err, adState, adUnitStates = emptyMap(), useTestAdUnit = false)
    }

    private val metaFlow = combine(metaCoreFlow, adManager.unitStates, _useTestAdUnit) { holder, unitStates, useTest ->
        holder.copy(adUnitStates = unitStates, useTestAdUnit = useTest)
    }

    private data class MetaHolder(
        val isAdmin: Boolean,
        val isBusy: Boolean,
        val banner: String?,
        val error: String?,
        val adState: AdMobLoadState,
        val adUnitStates: Map<String, AdMobLoadState>,
        val useTestAdUnit: Boolean
    )

    val uiState: StateFlow<KingsEarnsUiState> = combine(
        demoStore.demoSnapshotFlow,
        cloudFlow,
        adminFlow,
        metaFlow
    ) { demo: DemoSnapshot, cloud: CloudUserBundle?, adminBundle: AdminBundle, meta: MetaHolder ->
        val signedIn = !currentUserId.isNullOrBlank()
        val activeProfile = if (signedIn && cloud?.profile != null) cloud.profile else demo.profile
        val activeConfig = if (signedIn && cloud != null) cloud.config else demo.config
        val activeLedger = if (signedIn && cloud != null) cloud.ledger else demo.ledger
        val activeWithdrawals = if (signedIn && cloud != null) cloud.withdrawals else demo.withdrawals
        val activeNotifications = if (signedIn && cloud != null) cloud.notifications else demo.notifications
        val activeReferrals = if (signedIn && cloud != null) cloud.referrals else emptyList()
        val activeTickets = if (signedIn && cloud != null) cloud.tickets else emptyList()

        val activeUnitId = if (meta.useTestAdUnit) {
            RewardedAdManager.TEST_REWARDED_AD_UNIT_ID
        } else {
            RewardedAdManager.LIVE_REWARDED_AD_UNIT_ID
        }
        val adOffers = defaultAdOffers(
            isTestFallback = meta.useTestAdUnit
        ).map { it.copy(rewardPoints = activeConfig.pointsPerAd) }
        val activities = buildActivities(activeProfile, activeConfig, demo.completedTaskIds)

        KingsEarnsUiState(
            isCloudSignedIn = signedIn,
            isAdmin = meta.isAdmin,
            firebaseUserEmail = Firebase.auth.currentUser?.email ?: "",
            onboardingSeen = demo.onboardingSeen,
            profile = activeProfile,
            ledger = activeLedger,
            withdrawals = activeWithdrawals,
            notifications = activeNotifications,
            referrals = activeReferrals,
            supportTickets = activeTickets,
            config = activeConfig,
            adOffers = adOffers,
            activities = activities,
            adMobState = meta.adState,
            adMobUnitStates = meta.adUnitStates,
            useTestAdUnit = meta.useTestAdUnit,
            activeAdUnitId = activeUnitId,
            adminWithdrawals = adminBundle.withdrawals,
            adminUsers = adminBundle.users,
            adminTickets = adminBundle.tickets,
            adminAuditLogs = adminBundle.auditLogs,
            isBusy = meta.isBusy,
            statusBanner = meta.banner,
            errorMessage = meta.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = KingsEarnsUiState()
    )

    private fun buildActivities(
        profile: UserWalletProfile,
        config: PlatformConfig,
        completedIds: Set<String>
    ): List<EarningActivityItem> {
        val checkedInToday = profile.lastDailyClaimDate == todayStr
        val reached50 = profile.completedAdsCount >= 50L
        return listOf(
            EarningActivityItem(
                id = "act_daily_checkin",
                section = "B. Daily Rewards",
                title = "Daily Royal Check-In",
                description = "Check in once per calendar day to build your consecutive streak.",
                rewardPoints = config.dailyCheckInPoints,
                requirement = "1 claim per 24h calendar day",
                eligibility = if (checkedInToday) "Claimed today" else "Available now",
                completed = checkedInToday
            ),
            EarningActivityItem(
                id = "act_watch_ads_milestone",
                section = "A. Eligible Advertisements",
                title = "Complete 50 Verified Ads Milestone",
                description = "Watch 50 verified AdMob rewarded ads at ${config.pointsPerAd} DEMO pts each.",
                rewardPoints = 100L,
                requirement = "${profile.completedAdsCount.coerceAtMost(50)} / 50 verified ads",
                eligibility = "Progress tracked automatically on each AdMob reward callback",
                completed = reached50
            ),
            EarningActivityItem(
                id = "act_payout_setup",
                section = "C. Approved Promotional Activities",
                title = "Configure JazzCash or Easypaisa Account",
                description = "Save a verified Pakistani mobile wallet name and number for manual review readiness.",
                rewardPoints = 4L,
                requirement = "Save valid 03XX account details",
                eligibility = "One-time profile readiness check",
                completed = completedIds.contains("act_payout_setup") ||
                    profile.jazzCashNumber.isNotBlank() || profile.easypaisaNumber.isNotBlank()
            ),
            EarningActivityItem(
                id = "act_transparency_read",
                section = "E. Special Transparency Campaign",
                title = "Review Reward & Withdrawal Policy",
                description = "Acknowledge that DEMO points are test credits and payouts require manual admin verification.",
                rewardPoints = 2L,
                requirement = "Read & acknowledge policy disclosure",
                eligibility = "DEMO Mode onboarding activity",
                completed = completedIds.contains("act_transparency_read")
            )
        )
    }

    fun dismissBanner() {
        _statusBanner.value = null
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun markOnboardingSeen() {
        viewModelScope.launch {
            demoStore.markOnboardingSeen()
        }
    }

    fun reloadRewardedAd() {
        val unitIds = if (_useTestAdUnit.value) {
            listOf(RewardedAdManager.TEST_REWARDED_AD_UNIT_ID)
        } else {
            RewardedAdManager.LIVE_REWARDED_AD_UNIT_IDS
        }
        adManager.preloadAll(adUnitIds = unitIds, forceReload = true)
    }

    fun setUseTestAdUnit(enabled: Boolean) {
        _useTestAdUnit.value = enabled
        val unitIds = if (enabled) {
            listOf(RewardedAdManager.TEST_REWARDED_AD_UNIT_ID)
        } else {
            RewardedAdManager.LIVE_REWARDED_AD_UNIT_IDS
        }
        adManager.preloadAll(adUnitIds = unitIds, forceReload = true)
        _statusBanner.value = if (enabled) {
            "Switched all placements to Google Official Test Ad Unit for safe QA testing."
        } else {
            "Switched to KINGS EARNS Live Ad Units (${unitIds.size} configured)."
        }
    }

    fun watchRewardedAd(activity: Activity, offer: AdPlacementOffer) {
        val state = uiState.value
        if (state.profile.dailyAdsWatchedToday >= state.config.dailyAdLimit) {
            _errorMessage.value = "Daily ad limit (${state.config.dailyAdLimit}) reached. Please try again tomorrow."
            return
        }
        adManager.showRewardedAd(
            activity = activity,
            adUnitId = offer.adUnitId,
            onVerifiedRewardEarned = { rewardType, _ ->
                viewModelScope.launch {
                    _isBusy.value = true
                    val points = state.config.pointsPerAd
                    val result = if (state.isCloudSignedIn) {
                        repository.recordVerifiedAdReward(
                            adUnitId = offer.adUnitId,
                            adTitle = offer.title,
                            category = offer.category,
                            rewardPoints = points,
                            rewardType = rewardType,
                            dailyLimit = state.config.dailyAdLimit
                        )
                    } else {
                        demoStore.recordVerifiedAdReward(
                            adTitle = offer.title,
                            points = points,
                            dailyLimit = state.config.dailyAdLimit
                        )
                    }
                    _isBusy.value = false
                    result.onSuccess { newBalance ->
                        _statusBanner.value = "+$points DEMO Points verified! New balance: $newBalance DEMO pts."
                    }.onFailure { err ->
                        _errorMessage.value = err.localizedMessage ?: "Could not credit verified ad reward."
                    }
                }
            },
            onAdDismissedWithoutReward = {
                _errorMessage.value = "Ad closed before completion. No reward credited — full ad playback is required."
            },
            onAdShowError = { msg ->
                _errorMessage.value = "Ad display unavailable: $msg"
            }
        )
    }

    fun claimDailyCheckIn() {
        val state = uiState.value
        viewModelScope.launch {
            _isBusy.value = true
            val pts = state.config.dailyCheckInPoints
            val res = if (state.isCloudSignedIn) {
                repository.claimDailyCheckIn(pts)
            } else {
                demoStore.claimDailyCheckIn(pts)
            }
            _isBusy.value = false
            res.onSuccess { newBal ->
                _statusBanner.value = "+$pts DEMO Points daily check-in claimed! Balance: $newBal DEMO pts."
            }.onFailure { err ->
                _errorMessage.value = err.localizedMessage ?: "Daily check-in already claimed today."
            }
        }
    }

    fun completePromoActivity(item: EarningActivityItem) {
        if (item.completed) return
        viewModelScope.launch {
            _isBusy.value = true
            val res = demoStore.completeActivityTask(item.id, item.title, item.rewardPoints)
            _isBusy.value = false
            res.onSuccess { newBal ->
                _statusBanner.value = "+${item.rewardPoints} DEMO Points awarded for ${item.title}. Balance: $newBal pts."
            }.onFailure { err ->
                _errorMessage.value = err.localizedMessage ?: "Could not complete activity."
            }
        }
    }

    fun savePayoutDetails(method: PaymentMethod, holderName: String, mobileNumber: String) {
        if (holderName.trim().length < 2) {
            _errorMessage.value = "Enter the account holder's full name."
            return
        }
        val cleanPhone = mobileNumber.trim()
        if (!cleanPhone.matches(Regex("^03\\d{9}$")) && cleanPhone.length !in 10..15) {
            _errorMessage.value = "Enter a valid Pakistani mobile number (e.g. 03001234567)."
            return
        }
        viewModelScope.launch {
            _isBusy.value = true
            if (uiState.value.isCloudSignedIn) {
                repository.savePayoutDetails(method, holderName, cleanPhone)
                    .onSuccess { _statusBanner.value = "${method.displayName} payout details saved." }
                    .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to save details." }
            } else {
                demoStore.savePayoutDetails(method, holderName, cleanPhone)
                _statusBanner.value = "${method.displayName} payout details saved locally (DEMO)."
            }
            _isBusy.value = false
        }
    }

    fun applyReferralCode(code: String) {
        viewModelScope.launch {
            _isBusy.value = true
            demoStore.applyReferralCode(code)
                .onSuccess { clean -> _statusBanner.value = "Referral code $clean linked." }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Invalid referral code." }
            _isBusy.value = false
        }
    }

    fun submitWithdrawal(
        method: PaymentMethod?,
        holderName: String,
        mobileNumber: String,
        amountText: String,
        onSuccessSubmitted: (WithdrawalRequest) -> Unit
    ) {
        val state = uiState.value
        if (method == null) {
            _errorMessage.value = "Please select either JazzCash or Easypaisa before continuing."
            return
        }
        val cleanHolder = holderName.trim()
        val cleanMobile = mobileNumber.trim()
        val amount = amountText.trim().toLongOrNull()
        val minThreshold = state.config.minWithdrawalPoints
        val fee = state.config.feePoints

        when {
            cleanHolder.length < 2 -> {
                _errorMessage.value = "Please enter the account holder name."
                return
            }
            !cleanMobile.matches(Regex("^03\\d{9}$")) -> {
                _errorMessage.value = "Enter an 11-digit Pakistani mobile number starting with 03 (e.g. 03001234567)."
                return
            }
            amount == null || amount < minThreshold -> {
                _errorMessage.value = "Minimum DEMO withdrawal threshold is $minThreshold DEMO Points."
                return
            }
            amount > state.profile.availableDemoPoints -> {
                _errorMessage.value = "Insufficient available DEMO Points (${state.profile.availableDemoPoints} pts available)."
                return
            }
            amount <= fee -> {
                _errorMessage.value = "Requested points must exceed the platform fee ($fee pts)."
                return
            }
        }

        viewModelScope.launch {
            _isBusy.value = true
            val result = if (state.isCloudSignedIn) {
                repository.submitWithdrawalRequest(method, cleanHolder, cleanMobile, amount, fee)
            } else {
                demoStore.submitDemoWithdrawal(method, cleanHolder, cleanMobile, amount, fee)
            }
            _isBusy.value = false
            result.onSuccess { req ->
                _statusBanner.value = "Withdrawal ${req.id} submitted as PENDING."
                onSuccessSubmitted(req)
            }.onFailure { err ->
                _errorMessage.value = err.localizedMessage ?: "Could not submit withdrawal request."
            }
        }
    }

    fun submitSupportTicket(subject: String, message: String) {
        val state = uiState.value
        if (!state.isCloudSignedIn) {
            _statusBanner.value = "Support inquiry noted in DEMO mode. Sign in with Google to sync tickets to the administrator queue."
            return
        }
        viewModelScope.launch {
            _isBusy.value = true
            repository.submitSupportTicket(subject, message, state.profile.displayName)
                .onSuccess { _statusBanner.value = "Support request sent to KINGS EARNS administrators." }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to submit ticket." }
            _isBusy.value = false
        }
    }

    fun markNotificationRead(notificationId: String) {
        val state = uiState.value
        viewModelScope.launch {
            if (state.isCloudSignedIn && !currentUserId.isNullOrBlank()) {
                repository.markNotificationRead(currentUserId, notificationId)
            } else {
                demoStore.markNotificationRead(notificationId)
            }
        }
    }

    // --- ADMIN ACTIONS ---

    fun adminTransitionWithdrawal(
        withdrawal: WithdrawalRequest,
        nextStatus: WithdrawalStatus,
        externalTxRef: String = "",
        rejectionReason: String = ""
    ) {
        viewModelScope.launch {
            _isBusy.value = true
            repository.adminTransitionWithdrawal(withdrawal, nextStatus, externalTxRef, rejectionReason)
                .onSuccess { _statusBanner.value = "Withdrawal ${withdrawal.id} updated to ${nextStatus.label}." }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Admin action failed." }
            _isBusy.value = false
        }
    }

    fun adminToggleUserFlags(user: UserWalletProfile, verified: Boolean, fraudFlag: Boolean) {
        viewModelScope.launch {
            _isBusy.value = true
            repository.adminToggleUserVerification(user, verified, fraudFlag)
                .onSuccess { _statusBanner.value = "Updated account flags for ${user.displayName}." }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to update user." }
            _isBusy.value = false
        }
    }

    fun adminSaveConfig(config: PlatformConfig) {
        viewModelScope.launch {
            _isBusy.value = true
            repository.adminUpdatePlatformConfig(config)
                .onSuccess { _statusBanner.value = "Platform reward configuration saved." }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to save config." }
            _isBusy.value = false
        }
    }

    fun adminReplyTicket(ticket: SupportTicket, status: String, reply: String) {
        viewModelScope.launch {
            _isBusy.value = true
            repository.adminReplySupportTicket(ticket, status, reply)
                .onSuccess { _statusBanner.value = "Ticket updated ($status)." }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to update ticket." }
            _isBusy.value = false
        }
    }
}
