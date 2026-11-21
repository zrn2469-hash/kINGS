package com.example.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.auth.GoogleSignInHelper
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
fun ProfileTransparencyScreen(
    state: KingsEarnsUiState,
    onOpenNotifications: () -> Unit,
    onOpenAdminPortal: () -> Unit,
    onOpenWebPortal: () -> Unit = {},
    onSubmitSupportTicket: (String, String) -> Unit,
    onSignedIn: () -> Unit,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    var authBusy by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    var ticketSubject by remember { mutableStateOf("") }
    var ticketMessage by remember { mutableStateOf("") }
    var expandedPolicy by remember { mutableStateOf<String?>(null) }

    val legalSections = remember {
        listOf(
            "About Us & How It Works" to "KINGS EARNS connects users with eligible advertisements and approved activities. In DEMO Mode, each verified AdMob test rewarded ad awards 2 DEMO Points (50 ads = 100 DEMO Points).",
            "Reward & Advertising Disclosure" to "No earnings are guaranteed. Rewards are credited only after verified provider completion callbacks (OnUserEarnedReward). Currently operating in DEMO Mode with Google AdMob official test units until a live ad network approves cash payouts.",
            "Withdrawal Policy (JazzCash & Easypaisa)" to "Minimum DEMO threshold is 100 DEMO Points. Users select JazzCash or Easypaisa manually. All submitted requests stay PENDING while reserved points are held safely. An authorized adult administrator verifies account details and processes payments manually—never automatically.",
            "Privacy Policy & Terms" to "We collect only essential account, ledger, and payout verification fields. Payment PINs and passwords are never requested or stored. Self-referrals, automated clicking, or duplicate claims violate platform terms and may result in request rejection."
        )
    }

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
            Text(
                text = "Profile & Transparency",
                style = MaterialTheme.typography.headlineMedium,
                color = RoyalGold
            )
            DemoModeBadge("DEMO")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Account & Cloud Sync Card
        RoyalSectionCard(borderColor = RoyalGold.copy(alpha = 0.45f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.profile.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )
                    Text(
                        text = if (state.isCloudSignedIn) {
                            "Cloud Synced: ${state.firebaseUserEmail.ifBlank { state.profile.userId }}"
                        } else {
                            "Local DEMO Guest · ID: ${state.profile.userId}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.isCloudSignedIn) SuccessGreen else CoolSilver
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint = if (state.profile.accountVerified) SuccessGreen else CoolSilver,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (state.profile.accountVerified) "Account Verified by Admin" else "Standard Verification Status",
                    style = MaterialTheme.typography.labelSmall,
                    color = CoolSilver
                )
            }

            if (authError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = authError!!, style = MaterialTheme.typography.bodySmall, color = DangerRed)
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!state.isCloudSignedIn) {
                GoldGradientButton(
                    text = if (authBusy) "Connecting Google Account..." else "Sign in with Google (Cloud Sync & Admin)",
                    enabled = !authBusy && activity != null,
                    icon = Icons.Filled.CloudDone,
                    onClick = {
                        if (activity == null) return@GoldGradientButton
                        authBusy = true
                        authError = null
                        GoogleSignInHelper.onGoogleSignInClicked(
                            activity = activity,
                            scope = scope,
                            onAuthSuccess = {
                                authBusy = false
                                onSignedIn()
                            },
                            onAuthError = { msg ->
                                authBusy = false
                                authError = msg
                            },
                            onAuthCancelled = {
                                authBusy = false
                            }
                        )
                    },
                    testTag = "profile_google_signin_button"
                )
            } else {
                if (state.isAdmin) {
                    GoldGradientButton(
                        text = "Open Administrator Dashboard",
                        icon = Icons.Filled.AdminPanelSettings,
                        onClick = onOpenAdminPortal,
                        testTag = "open_admin_dashboard_button"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                OutlinedButton(
                    onClick = {
                        GoogleSignInHelper.signOut(
                            context = context,
                            scope = scope,
                            onSignOutComplete = onSignedOut
                        )
                    },
                    border = BorderStroke(1.dp, BorderSlate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("profile_signout_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = CoolSilver)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out to Local DEMO Mode", color = PureWhite)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Official Website & APK Download Portal Card
        RoyalSectionCard(
            modifier = Modifier
                .clickable(onClick = onOpenWebPortal)
                .testTag("open_web_portal_card"),
            borderColor = RoyalGold.copy(alpha = 0.55f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Language, contentDescription = null, tint = RoyalGold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Official Website & APK Portal", style = MaterialTheme.typography.titleMedium, color = PureWhite)
                        Text(
                            text = "Preview the official Android app download website",
                            style = MaterialTheme.typography.bodySmall,
                            color = CoolSilver
                        )
                    }
                }
                Text("Open", style = MaterialTheme.typography.labelLarge, color = RoyalGold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Notification Center Shortcut
        RoyalSectionCard(
            modifier = Modifier
                .clickable(onClick = onOpenNotifications)
                .testTag("open_notifications_card"),
            borderColor = RoyalPurple.copy(alpha = 0.55f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = RoyalGold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Notification Center", style = MaterialTheme.typography.titleMedium, color = PureWhite)
                        Text(
                            text = "${state.notifications.count { !it.readStatus }} unread · ${state.notifications.size} total",
                            style = MaterialTheme.typography.bodySmall,
                            color = CoolSilver
                        )
                    }
                }
                Text("Open", style = MaterialTheme.typography.labelLarge, color = RoyalGold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legal, FAQ & Transparency Hub
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Gavel, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Legal, FAQ & Policies",
                style = MaterialTheme.typography.titleLarge,
                color = PureWhite
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        legalSections.forEach { (title, body) ->
            val isExpanded = expandedPolicy == title
            RoyalSectionCard(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable { expandedPolicy = if (isExpanded) null else title }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, style = MaterialTheme.typography.titleSmall, color = PureWhite, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = RoyalGold
                    )
                }
                AnimatedVisibility(visible = isExpanded) {
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodySmall,
                        color = CoolSilver,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Contact & Support Request
        RoyalSectionCard(borderColor = BorderSlate) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = SoftViolet)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Contact & Support", style = MaterialTheme.typography.titleMedium, color = PureWhite)
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = ticketSubject,
                onValueChange = { ticketSubject = it },
                label = { Text("Subject") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalGold,
                    unfocusedBorderColor = BorderSlate
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("support_subject_input")
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ticketMessage,
                onValueChange = { ticketMessage = it },
                label = { Text("Describe your question or withdrawal inquiry") },
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalGold,
                    unfocusedBorderColor = BorderSlate
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("support_message_input")
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    onSubmitSupportTicket(ticketSubject, ticketMessage)
                    ticketSubject = ""
                    ticketMessage = ""
                },
                enabled = ticketSubject.trim().length >= 3 && ticketMessage.trim().length >= 5 && !state.isBusy,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = MidnightBlack),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("support_submit_button")
            ) {
                Text("Send Support Request", fontWeight = FontWeight.Bold)
            }

            if (state.supportTickets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                state.supportTickets.forEach { tkt ->
                    Surface(
                        color = ElevatedNavy,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("${tkt.subject} (${tkt.status})", style = MaterialTheme.typography.titleSmall, color = PureWhite)
                            Text(tkt.message, style = MaterialTheme.typography.bodySmall, color = CoolSilver)
                            if (tkt.adminReply.isNotBlank()) {
                                Text(
                                    text = "Admin Reply: ${tkt.adminReply}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun NotificationsScreen(
    state: KingsEarnsUiState,
    onMarkRead: (String) -> Unit,
    onBack: () -> Unit
) {
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
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineMedium,
                color = RoyalGold
            )
            OutlinedButton(
                onClick = onBack,
                border = BorderStroke(1.dp, BorderSlate),
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Back", color = PureWhite)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (state.notifications.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Filled.Notifications,
                title = "No Notifications",
                subtitle = "Verified reward credits and manual withdrawal review updates appear here."
            )
        } else {
            state.notifications.forEach { notif ->
                RoyalSectionCard(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .clickable { if (!notif.readStatus) onMarkRead(notif.id) },
                    borderColor = if (notif.readStatus) BorderSlate else RoyalGold.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(notif.category, style = MaterialTheme.typography.labelSmall, color = SoftViolet)
                        if (!notif.readStatus) {
                            Icon(Icons.Filled.MarkEmailRead, contentDescription = "Unread", tint = RoyalGold, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(notif.title, style = MaterialTheme.typography.titleMedium, color = PureWhite)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(notif.message, style = MaterialTheme.typography.bodySmall, color = CoolSilver)
                }
            }
        }
    }
}
