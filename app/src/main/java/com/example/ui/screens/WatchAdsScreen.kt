package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ads.AdMobLoadState
import com.example.model.AdPlacementOffer
import com.example.model.LedgerType
import com.example.ui.KingsEarnsUiState
import com.example.ui.components.DemoModeBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.ResponsiveContentColumn
import com.example.ui.components.RoyalSectionCard
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.CoolSilver
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SoftViolet
import com.example.ui.theme.SuccessGreen

@Composable
fun WatchAdsScreen(
    state: KingsEarnsUiState,
    onWatchAd: (Activity, AdPlacementOffer) -> Unit,
    onReloadAd: () -> Unit,
    onToggleTestAdUnit: (Boolean) -> Unit = {}
) {
    val activity = LocalActivity.current
    val dailyProgress = (state.profile.dailyAdsWatchedToday.toFloat() /
        state.config.dailyAdLimit.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
    val adHistory = state.ledger.filter { it.type == LedgerType.AD_REWARD.name }

    ResponsiveContentColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Watch Advertisements",
                    style = MaterialTheme.typography.headlineMedium,
                    color = RoyalGold
                )
                Text(
                    text = if (state.useTestAdUnit) {
                        "Google AdMob Rewarded (Test Unit)"
                    } else {
                        "Google AdMob Rewarded Ads"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolSilver
                )
            }
            DemoModeBadge(if (state.useTestAdUnit) "TEST AD UNIT" else "DEMO POINTS")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AdMob Verification & Transparency Banner
        RoyalSectionCard(borderColor = RoyalPurple.copy(alpha = 0.6f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = "Verified callback",
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verified Completion Only · +${state.config.pointsPerAd} DEMO Points",
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rewards are credited strictly when Google AdMob fires the OnUserEarnedReward completion callback. Closing an ad early or timer simulation never awards points. 50 verified ads = 100 DEMO Points.",
                style = MaterialTheme.typography.bodySmall,
                color = CoolSilver
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = ElevatedNavy,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderSlate)
            ) {
                Text(
                    text = if (state.useTestAdUnit) {
                        "Active Unit: ${state.activeAdUnitId} (Test Mode)"
                    } else {
                        "Active Placements: ${state.adOffers.size} Live Rewarded Units Configured"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = RoyalGold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Policy-Safe Test Ad Fallback",
                        style = MaterialTheme.typography.labelLarge,
                        color = PureWhite
                    )
                    Text(
                        text = "Enable official Google test ads during development to avoid invalid clicks or no-fill before AdMob app approval.",
                        style = MaterialTheme.typography.labelSmall,
                        color = CoolSilver
                    )
                }
                Switch(
                    checked = state.useTestAdUnit,
                    onCheckedChange = onToggleTestAdUnit,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MidnightBlack,
                        checkedTrackColor = RoyalGold,
                        uncheckedThumbColor = CoolSilver,
                        uncheckedTrackColor = ElevatedNavy
                    ),
                    modifier = Modifier.testTag("test_ad_unit_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Daily Limit Progress & AdMob SDK Status
        RoyalSectionCard(borderColor = BorderSlate) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Verified Ads Cap",
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
                Text(
                    text = "${state.profile.dailyAdsWatchedToday} / ${state.config.dailyAdLimit}",
                    style = MaterialTheme.typography.labelLarge,
                    color = RoyalGold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { dailyProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = RoyalGold,
                trackColor = MidnightBlack
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    when (val adState = state.adMobState) {
                        AdMobLoadState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = RoyalGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (state.useTestAdUnit) {
                                    "Preloading AdMob Test Rewarded Ad..."
                                } else {
                                    "Preloading AdMob Rewarded Ad..."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = CoolSilver
                            )
                        }
                        AdMobLoadState.Ready -> {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Ad Ready",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (state.useTestAdUnit) "Test Rewarded Ad Ready" else "Rewarded Ad Ready",
                                style = MaterialTheme.typography.bodySmall,
                                color = SuccessGreen
                            )
                        }
                        AdMobLoadState.Showing -> {
                            Text(
                                text = "Ad in progress...",
                                style = MaterialTheme.typography.bodySmall,
                                color = RoyalGold
                            )
                        }
                        is AdMobLoadState.Failed -> {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Ad load notice",
                                tint = DangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SDK Notice: ${adState.errorMessage}",
                                style = MaterialTheme.typography.bodySmall,
                                color = DangerRed
                            )
                        }
                        AdMobLoadState.Idle -> {
                            Text(
                                text = "Preparing next rewarded ad...",
                                style = MaterialTheme.typography.bodySmall,
                                color = CoolSilver
                            )
                        }
                    }
                }
                OutlinedButton(
                    onClick = onReloadAd,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderSlate),
                    modifier = Modifier.testTag("reload_ad_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reload ad",
                        tint = RoyalGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reload", color = PureWhite)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Eligible Ad Placements",
            style = MaterialTheme.typography.titleLarge,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(10.dp))

        state.adOffers.forEach { offer ->
            val unitState = state.adMobUnitStates[offer.adUnitId] ?: state.adMobState
            val isUnitReady = unitState is AdMobLoadState.Ready && !state.isBusy
            RoyalSectionCard(
                modifier = Modifier.padding(bottom = 12.dp),
                borderColor = if (isUnitReady) RoyalGold.copy(alpha = 0.4f) else BorderSlate
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = RoyalPurple.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = offer.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftViolet,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "+${offer.rewardPoints} DEMO Points",
                        style = MaterialTheme.typography.titleMedium,
                        color = RoyalGold,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = offer.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${offer.advertiser} · Est. ${offer.durationLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolSilver
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Unit: ${offer.adUnitId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = RoyalGold.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = offer.eligibilityNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (unitState is AdMobLoadState.Failed) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Unit status: ${unitState.errorMessage}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DangerRed
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                GoldGradientButton(
                    text = when {
                        unitState is AdMobLoadState.Loading -> if (state.useTestAdUnit) "Loading Test Ad..." else "Loading Rewarded Ad..."
                        unitState is AdMobLoadState.Ready -> if (state.useTestAdUnit) {
                            "Watch Verified Test Ad (+${offer.rewardPoints} DEMO)"
                        } else {
                            "Watch Verified Ad (+${offer.rewardPoints} DEMO)"
                        }
                        else -> "Ad Not Ready — Tap Reload Above"
                    },
                    enabled = isUnitReady && activity != null,
                    icon = Icons.Filled.PlayArrow,
                    onClick = {
                        if (activity != null) onWatchAd(activity, offer)
                    },
                    testTag = "watch_ad_button_${offer.id}"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Verified Advertisement History",
            style = MaterialTheme.typography.titleLarge,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (adHistory.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Filled.PlayArrow,
                title = "No Completed Ads Yet",
                subtitle = "Completed Google AdMob rewarded ads will appear here with reference IDs."
            )
        } else {
            adHistory.take(10).forEach { item ->
                RoyalSectionCard(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PureWhite
                                )
                                Text(
                                    text = "Ref: ${item.referenceId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CoolSilver
                                )
                            }
                        }
                        Text(
                            text = "+${item.pointsDelta} pts",
                            style = MaterialTheme.typography.titleSmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
