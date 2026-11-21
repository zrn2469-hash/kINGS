package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ads.AdMobLoadState
import com.example.model.AdPlacementOffer
import com.example.model.LedgerType
import com.example.ui.KingsEarnsUiState
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ResponsiveContentColumn
import com.example.ui.components.RoyalSectionCard
import com.example.ui.components.StatMiniChip
import com.example.ui.components.WalletSummaryHeroCard
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.CoolSilver
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DeepRoyalPurple
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SoftViolet
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun HomeDashboardScreen(
    state: KingsEarnsUiState,
    onNavigateWatchAds: () -> Unit,
    onNavigateActivities: () -> Unit,
    onNavigateReferral: () -> Unit,
    onNavigateWithdraw: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateProfile: () -> Unit,
    onNavigateWebPortal: () -> Unit = {},
    onWatchOffer: (Activity, AdPlacementOffer) -> Unit
) {
    val activity = LocalActivity.current
    val unreadCount = state.notifications.count { !it.readStatus }

    ResponsiveContentColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Brand Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.kings_earns_icon_1795180677249),
                    contentDescription = "KINGS EARNS Crown Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "KINGS EARNS",
                        style = MaterialTheme.typography.headlineMedium,
                        color = RoyalGold
                    )
                    Text(
                        text = "Welcome, ${state.profile.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CoolSilver,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    onClick = onNavigateProfile,
                    color = ElevatedNavy,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        if (state.isCloudSignedIn) SuccessGreen.copy(alpha = 0.6f) else BorderSlate
                    ),
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (state.isCloudSignedIn) Icons.Filled.CloudDone else Icons.Filled.CloudOff,
                            contentDescription = "Sync status",
                            tint = if (state.isCloudSignedIn) SuccessGreen else RoyalGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (state.isCloudSignedIn) "CLOUD" else "DEMO",
                            style = MaterialTheme.typography.labelSmall,
                            color = PureWhite
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onNavigateWebPortal,
                    modifier = Modifier.testTag("home_web_portal_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = "Open Official Website",
                        tint = RoyalGold
                    )
                }
                IconButton(
                    onClick = onNavigateNotifications,
                    modifier = Modifier.testTag("home_notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(containerColor = RoyalPurple, contentColor = PureWhite) {
                                    Text(unreadCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = RoyalGold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Gold/Purple Wallet Card
        WalletSummaryHeroCard(
            availablePoints = state.profile.availableDemoPoints,
            reservedPoints = state.profile.reservedDemoPoints,
            lifetimePoints = state.profile.lifetimeDemoPoints,
            completedAds = state.profile.completedAdsCount,
            minThreshold = state.config.minWithdrawalPoints,
            onWithdrawClick = onNavigateWithdraw
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Stats Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMiniChip(
                label = "Today's Ads",
                value = "${state.profile.dailyAdsWatchedToday}/${state.config.dailyAdLimit}",
                accent = RoyalGold,
                modifier = Modifier.weight(1f)
            )
            StatMiniChip(
                label = "Daily Streak",
                value = "${state.profile.dailyStreak} Days",
                accent = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            StatMiniChip(
                label = "Per Ad",
                value = "+${state.config.pointsPerAd} DEMO",
                accent = SoftViolet,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2x2 Quick Action Grid
        Text(
            text = "Earning Hub",
            style = MaterialTheme.typography.titleLarge,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionTile(
                icon = Icons.Filled.PlayArrow,
                title = "Watch Ads",
                subtitle = "+${state.config.pointsPerAd} DEMO pts",
                accent = RoyalPurple,
                onClick = onNavigateWatchAds,
                testTag = "quick_watch_ads",
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Filled.TaskAlt,
                title = "Complete Tasks",
                subtitle = "Approved items",
                accent = SuccessGreen,
                onClick = onNavigateActivities,
                testTag = "quick_complete_tasks",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionTile(
                icon = Icons.Filled.CardGiftcard,
                title = "Daily Rewards",
                subtitle = "+${state.config.dailyCheckInPoints} pts/day",
                accent = RoyalGold,
                onClick = onNavigateActivities,
                testTag = "quick_daily_rewards",
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Filled.GroupAdd,
                title = "Refer & Earn",
                subtitle = "Share invite",
                accent = Color(0xFF38BDF8),
                onClick = onNavigateReferral,
                testTag = "quick_refer_earn",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Invite Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        listOf(DeepRoyalPurple, Color(0xFF3A1C12), DarkNavy)
                    )
                )
                .clickable(onClick = onNavigateReferral)
                .minimumInteractiveComponentSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoyalGold.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "Referral gift",
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Invite Friends · Code ${state.profile.referralCode}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Text(
                            text = "Earn ${state.config.referralBonusPoints} DEMO pts for verified eligible referrals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoyalGold
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open referrals",
                    tint = RoyalGold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Available Rewarded Ads Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Available Rewarded Ads",
                style = MaterialTheme.typography.titleLarge,
                color = PureWhite
            )
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelLarge,
                color = RoyalGold,
                modifier = Modifier
                    .clickable(onClick = onNavigateWatchAds)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        state.adOffers.take(2).forEach { offer ->
            RoyalSectionCard(
                modifier = Modifier.padding(bottom = 10.dp),
                borderColor = BorderSlate
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RoyalPurple.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = offer.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${offer.category} · ${offer.durationLabel} · +${offer.rewardPoints} DEMO pts",
                                style = MaterialTheme.typography.bodySmall,
                                color = RoyalGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val unitReady = (state.adMobUnitStates[offer.adUnitId] ?: state.adMobState) is AdMobLoadState.Ready
                    Button(
                        onClick = {
                            if (activity != null && unitReady) {
                                onWatchOffer(activity, offer)
                            } else {
                                onNavigateWatchAds()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalGold,
                            contentColor = MidnightBlack
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_watch_${offer.id}")
                    ) {
                        Text(
                            text = if (unitReady) "Watch" else "Open",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Recent Transactions
        Text(
            text = "Recent Auditable Ledger",
            style = MaterialTheme.typography.titleLarge,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (state.ledger.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Filled.CheckCircle,
                title = "No Verified Transactions Yet",
                subtitle = "Watch a test rewarded ad or claim your daily check-in to record your first DEMO ledger entry."
            )
        } else {
            state.ledger.take(4).forEach { entry ->
                val type = LedgerType.fromRaw(entry.type)
                RoyalSectionCard(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.description,
                                style = MaterialTheme.typography.titleSmall,
                                color = PureWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${type.label} · DEMO",
                                style = MaterialTheme.typography.bodySmall,
                                color = CoolSilver
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (entry.pointsDelta >= 0) "+${entry.pointsDelta} pts" else "${entry.pointsDelta} pts",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (entry.pointsDelta >= 0) SuccessGreen else WarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .testTag(testTag),
        color = DarkNavy,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = PureWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolSilver,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
