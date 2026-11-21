package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

enum class PaymentMethod(val displayName: String, val tagline: String) {
    JAZZCASH("JazzCash", "Pakistan Mobile Wallet"),
    EASYPAISA("Easypaisa", "Telenor Microfinance Wallet")
}

enum class WithdrawalStatus(val label: String) {
    PENDING("PENDING"),
    UNDER_REVIEW("UNDER REVIEW"),
    APPROVED("APPROVED"),
    PAYMENT_SENT("PAYMENT SENT"),
    PAID("PAID"),
    REJECTED("REJECTED");

    companion object {
        fun fromRaw(raw: String?): WithdrawalStatus =
            entries.firstOrNull { it.name == raw } ?: PENDING
    }
}

enum class LedgerType(val label: String, val isCredit: Boolean) {
    AD_REWARD("Verified Ad Reward", true),
    DAILY_CHECKIN("Daily Check-In", true),
    TASK_REWARD("Approved Activity", true),
    REFERRAL_BONUS("Referral Bonus", true),
    WITHDRAW_RESERVE("Withdrawal Reserved", false),
    WITHDRAW_PAID("Withdrawal Confirmed Paid", false),
    WITHDRAW_RELEASE("Withdrawal Rejected Release", true);

    companion object {
        fun fromRaw(raw: String?): LedgerType =
            entries.firstOrNull { it.name == raw } ?: AD_REWARD
    }
}

enum class NotificationCategory(val label: String) {
    REWARD("Reward"),
    WITHDRAWAL("Withdrawal"),
    ACCOUNT("Account"),
    REFERRAL("Referral"),
    CAMPAIGN("Campaign");

    companion object {
        fun fromRaw(raw: String?): NotificationCategory =
            entries.firstOrNull { it.name == raw } ?: REWARD
    }
}

@Keep
@IgnoreExtraProperties
data class UserWalletProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Royal Guest",
    val email: String = "",
    val referralCode: String = "KINGDEMO",
    val referredBy: String = "",
    val availableDemoPoints: Long = 0L,
    val pendingDemoPoints: Long = 0L,
    val reservedDemoPoints: Long = 0L,
    val lifetimeDemoPoints: Long = 0L,
    val totalWithdrawnDemoPoints: Long = 0L,
    val completedAdsCount: Long = 0L,
    val dailyAdsWatchedToday: Long = 0L,
    val dailyAdsDate: String = "",
    val dailyStreak: Long = 0L,
    val lastDailyClaimDate: String = "",
    val jazzCashHolder: String = "",
    val jazzCashNumber: String = "",
    val easypaisaHolder: String = "",
    val easypaisaNumber: String = "",
    val accountVerified: Boolean = false,
    val fraudFlag: Boolean = false,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LedgerEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = LedgerType.AD_REWARD.name,
    val pointsDelta: Long = 0L,
    val balanceAfter: Long = 0L,
    val description: String = "",
    val referenceId: String = "",
    val demoMode: Boolean = true,
    val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AdRewardEvent(
    @DocumentId val id: String = "",
    val userId: String = "",
    val adUnitId: String = "",
    val adTitle: String = "",
    val category: String = "",
    val rewardPoints: Long = 2L,
    val rewardType: String = "DEMO_POINTS",
    val verifiedCallback: Boolean = true,
    val demoMode: Boolean = true,
    val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AppNotification(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val category: String = NotificationCategory.REWARD.name,
    val readStatus: Boolean = false,
    val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class WithdrawalRequest(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val method: String = PaymentMethod.JAZZCASH.name,
    val accountHolderName: String = "",
    val mobileNumber: String = "",
    val requestedPoints: Long = 100L,
    val feePoints: Long = 0L,
    val netPoints: Long = 100L,
    val status: String = WithdrawalStatus.PENDING.name,
    val externalTxReference: String = "",
    val rejectionReason: String = "",
    val reviewedByAdmin: String = "",
    val demoMode: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ReferralRecord(
    @DocumentId val id: String = "",
    val referrerId: String = "",
    val referredUserId: String = "",
    val referredName: String = "",
    val referralCode: String = "",
    val status: String = "REGISTERED",
    val rewardPoints: Long = 10L,
    val demoMode: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SupportTicket(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val subject: String = "",
    val message: String = "",
    val status: String = "OPEN",
    val adminReply: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class PlatformConfig(
    @DocumentId val id: String = "global",
    val pointsPerAd: Long = 2L,
    val minWithdrawalPoints: Long = 100L,
    val feePoints: Long = 0L,
    val dailyAdLimit: Long = 50L,
    val dailyCheckInPoints: Long = 5L,
    val referralBonusPoints: Long = 10L,
    val demoMode: Boolean = true,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AdminAuditLog(
    @DocumentId val id: String = "",
    val adminUid: String = "",
    val adminEmail: String = "",
    val action: String = "",
    val targetType: String = "",
    val targetId: String = "",
    val details: String = "",
    val createdAt: Timestamp? = null
)

data class AdPlacementOffer(
    val id: String,
    val title: String,
    val category: String,
    val advertiser: String,
    val durationLabel: String,
    val rewardPoints: Long,
    val adUnitId: String,
    val eligibilityNote: String
)

data class EarningActivityItem(
    val id: String,
    val section: String,
    val title: String,
    val description: String,
    val rewardPoints: Long,
    val requirement: String,
    val eligibility: String,
    val completed: Boolean
)
