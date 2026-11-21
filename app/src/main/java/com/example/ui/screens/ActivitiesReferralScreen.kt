package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.EarningActivityItem
import com.example.ui.KingsEarnsUiState
import com.example.ui.components.DemoModeBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.ResponsiveContentColumn
import com.example.ui.components.RoyalSectionCard
import com.example.ui.components.StatMiniChip
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.CoolSilver
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SoftViolet
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivitiesReferralScreen(
    state: KingsEarnsUiState,
    onClaimDaily: () -> Unit,
    onCompleteActivity: (EarningActivityItem) -> Unit,
    onNavigateWatchAds: () -> Unit,
    onApplyReferralCode: (String) -> Unit,
    onCopyMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val claimedToday = state.profile.lastDailyClaimDate == today
    var referralInput by remember { mutableStateOf("") }

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
                    text = "Daily Rewards & Referrals",
                    style = MaterialTheme.typography.headlineMedium,
                    color = RoyalGold
                )
                Text(
                    text = "Approved Earning Activities & Program Tracking",
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolSilver
                )
            }
            DemoModeBadge("DEMO")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section B: Daily Participation System
        RoyalSectionCard(borderColor = RoyalGold.copy(alpha = 0.45f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Check-In Streak",
                        style = MaterialTheme.typography.titleMedium,
                        color = PureWhite
                    )
                }
                Text(
                    text = "${state.profile.dailyStreak} Days",
                    style = MaterialTheme.typography.labelLarge,
                    color = SuccessGreen
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val activeDay = (state.profile.dailyStreak % 7).toInt()
                (1..7).forEach { day ->
                    val completedDay = day <= activeDay || (claimedToday && day == activeDay + 1)
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (completedDay) RoyalGold.copy(alpha = 0.2f) else ElevatedNavy,
                        border = BorderStroke(1.dp, if (completedDay) RoyalGold else BorderSlate)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "D$day",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (completedDay) RoyalGold else CoolSilver
                            )
                            Text(
                                text = "+${state.config.dailyCheckInPoints}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            GoldGradientButton(
                text = if (claimedToday) {
                    "Daily Reward Claimed Today"
                } else {
                    "Claim Daily Check-In (+${state.config.dailyCheckInPoints} DEMO pts)"
                },
                enabled = !claimedToday && !state.isBusy,
                onClick = onClaimDaily,
                testTag = "claim_daily_button"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Separate Earning Sections (A, C, E)
        Text(
            text = "Approved Earning Activities",
            style = MaterialTheme.typography.titleLarge,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(10.dp))

        state.activities.forEach { item ->
            RoyalSectionCard(
                modifier = Modifier.padding(bottom = 10.dp),
                borderColor = if (item.completed) SuccessGreen.copy(alpha = 0.5f) else BorderSlate
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.section,
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftViolet
                    )
                    Text(
                        text = "+${item.rewardPoints} DEMO pts",
                        style = MaterialTheme.typography.labelLarge,
                        color = RoyalGold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolSilver
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Requirement: ${item.requirement} · ${item.eligibility}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = if (item.completed) SuccessGreen else BorderSlate,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (item.completed) "Completed" else "Pending",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (item.completed) SuccessGreen else CoolSilver
                        )
                    }
                    when (item.id) {
                        "act_daily_checkin" -> if (!item.completed) {
                            Button(
                                onClick = onClaimDaily,
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = MidnightBlack),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) { Text("Claim", fontWeight = FontWeight.Bold) }
                        }
                        "act_watch_ads_milestone" -> if (!item.completed) {
                            OutlinedButton(
                                onClick = onNavigateWatchAds,
                                border = BorderStroke(1.dp, RoyalGold),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) { Text("Watch Ads", color = RoyalGold) }
                        }
                        else -> if (!item.completed) {
                            Button(
                                onClick = { onCompleteActivity(item) },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple, contentColor = PureWhite),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("complete_${item.id}")
                            ) { Text("Verify & Claim") }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section D: Referral Program
        Text(
            text = "Referral Program",
            style = MaterialTheme.typography.titleLarge,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(10.dp))

        RoyalSectionCard(borderColor = RoyalPurple.copy(alpha = 0.6f)) {
            Text(
                text = "Your Unique Referral Code",
                style = MaterialTheme.typography.titleMedium,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = ElevatedNavy,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.profile.referralCode,
                        style = MaterialTheme.typography.headlineSmall,
                        color = RoyalGold,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(state.profile.referralCode))
                                onCopyMessage("Referral code ${state.profile.referralCode} copied.")
                            },
                            border = BorderStroke(1.dp, BorderSlate),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy code", tint = PureWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", color = PureWhite)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Join KINGS EARNS — WATCH. EARN. WITHDRAW. Use my referral code: ${state.profile.referralCode}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share KINGS EARNS Invite"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = MidnightBlack),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("share_referral_button")
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = "Share invite", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMiniChip("Registered", state.referrals.size.toString(), PureWhite, Modifier.weight(1f))
                StatMiniChip(
                    "Eligible",
                    state.referrals.count { it.status == "ELIGIBLE" || it.status == "APPROVED" }.toString(),
                    RoyalGold,
                    Modifier.weight(1f)
                )
                StatMiniChip(
                    "Approved",
                    state.referrals.count { it.status == "APPROVED" }.toString(),
                    SuccessGreen,
                    Modifier.weight(1f)
                )
            }

            if (state.profile.referredBy.isBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Have an invite code? (Self-referrals blocked)",
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolSilver
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = referralInput,
                        onValueChange = { referralInput = it },
                        label = { Text("Enter Referral Code") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalGold,
                            unfocusedBorderColor = BorderSlate
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("referral_code_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onApplyReferralCode(referralInput) },
                        enabled = referralInput.trim().length >= 4,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple, contentColor = PureWhite),
                        modifier = Modifier
                            .height(54.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("apply_referral_button")
                    ) {
                        Text("Apply")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Linked Referral Code: ${state.profile.referredBy}",
                    style = MaterialTheme.typography.labelLarge,
                    color = SuccessGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (state.referrals.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Filled.Group,
                title = "No Referrals Tracked Yet",
                subtitle = "Genuine eligible referrals appear here after anti-fraud verification."
            )
        } else {
            state.referrals.forEach { ref ->
                RoyalSectionCard(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(ref.referredName, style = MaterialTheme.typography.titleSmall, color = PureWhite)
                            Text("Status: ${ref.status}", style = MaterialTheme.typography.bodySmall, color = CoolSilver)
                        }
                        Text(
                            text = "+${ref.rewardPoints} DEMO",
                            style = MaterialTheme.typography.titleSmall,
                            color = RoyalGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
