package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.PlatformConfig
import com.example.model.SupportTicket
import com.example.model.UserWalletProfile
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import com.example.ui.KingsEarnsUiState
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.ResponsiveContentColumn
import com.example.ui.components.RoyalSectionCard
import com.example.ui.components.StatMiniChip
import com.example.ui.components.WithdrawalStatusBadge
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.CoolSilver
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SoftViolet
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun AdminDashboardScreen(
    state: KingsEarnsUiState,
    onBack: () -> Unit,
    onTransitionWithdrawal: (WithdrawalRequest, WithdrawalStatus, String, String) -> Unit,
    onToggleUserFlags: (UserWalletProfile, Boolean, Boolean) -> Unit,
    onSaveConfig: (PlatformConfig) -> Unit,
    onReplyTicket: (SupportTicket, String, String) -> Unit
) {
    if (!state.isAdmin) {
        ResponsiveContentColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            EmptyStateCard(
                icon = Icons.Filled.Security,
                title = "Restricted Administrator Area",
                subtitle = "Only verified authorized adult administrators can view withdrawal accounts or confirm manual payouts."
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onBack) { Text("Back", color = PureWhite) }
        }
        return
    }

    var adminTab by remember { mutableIntStateOf(0) }
    var methodFilter by remember { mutableStateOf("ALL") }
    var selectedWithdrawal by remember { mutableStateOf<WithdrawalRequest?>(null) }

    val pendingCount = state.adminWithdrawals.count {
        it.status == WithdrawalStatus.PENDING.name || it.status == WithdrawalStatus.UNDER_REVIEW.name
    }
    val paidCount = state.adminWithdrawals.count { it.status == WithdrawalStatus.PAID.name }
    val totalAds = state.adminUsers.sumOf { it.completedAdsCount }
    val outstandingLiability = state.adminUsers.sumOf { it.availableDemoPoints + it.reservedDemoPoints }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = RoyalGold)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Admin Control Center", style = MaterialTheme.typography.headlineMedium, color = RoyalGold)
                    Text(state.firebaseUserEmail, style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                }
            }
            OutlinedButton(
                onClick = onBack,
                border = BorderStroke(1.dp, BorderSlate),
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Exit", color = PureWhite)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        ScrollableTabRow(
            selectedTabIndex = adminTab,
            containerColor = DarkNavy,
            contentColor = RoyalGold,
            edgePadding = 4.dp
        ) {
            listOf("Overview", "Withdrawals ($pendingCount)", "Users", "Settings", "Support", "Audit Logs")
                .forEachIndexed { idx, title ->
                    Tab(
                        selected = adminTab == idx,
                        onClick = { adminTab = idx },
                        text = { Text(title) }
                    )
                }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (adminTab) {
            0 -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatMiniChip("Registered Users", state.adminUsers.size.toString(), PureWhite, Modifier.weight(1f))
                    StatMiniChip("Verified Users", state.adminUsers.count { it.accountVerified }.toString(), SuccessGreen, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatMiniChip("Verified Ads", totalAds.toString(), RoyalGold, Modifier.weight(1f))
                    StatMiniChip("Pending Payouts", pendingCount.toString(), WarningAmber, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatMiniChip("Confirmed Paid", paidCount.toString(), SuccessGreen, Modifier.weight(1f))
                    StatMiniChip("DEMO Liability", "$outstandingLiability pts", SoftViolet, Modifier.weight(1f))
                }
            }

            1 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL", "JAZZCASH", "EASYPAISA", "PENDING").forEach { filter ->
                        FilterChip(
                            selected = methodFilter == filter,
                            onClick = { methodFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalGold,
                                selectedLabelColor = MidnightBlack
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                val filtered = state.adminWithdrawals.filter {
                    when (methodFilter) {
                        "JAZZCASH" -> it.method == "JAZZCASH"
                        "EASYPAISA" -> it.method == "EASYPAISA"
                        "PENDING" -> it.status == WithdrawalStatus.PENDING.name || it.status == WithdrawalStatus.UNDER_REVIEW.name
                        else -> true
                    }
                }
                if (filtered.isEmpty()) {
                    EmptyStateCard(title = "No Requests in Filter", subtitle = "Submitted withdrawal requests will appear here for manual review.")
                } else {
                    filtered.forEach { w ->
                        val status = WithdrawalStatus.fromRaw(w.status)
                        RoyalSectionCard(modifier = Modifier.padding(bottom = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${w.method} · ${w.requestedPoints} DEMO pts", style = MaterialTheme.typography.titleMedium, color = PureWhite)
                                WithdrawalStatusBadge(status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("ID: ${w.id} · User: ${w.username}", style = MaterialTheme.typography.labelSmall, color = RoyalGold)
                            Text("Holder: ${w.accountHolderName} · Mobile: ${w.mobileNumber}", style = MaterialTheme.typography.bodySmall, color = CoolSilver)
                            if (w.externalTxReference.isNotBlank()) {
                                Text("External Tx Ref: ${w.externalTxReference}", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                            }
                            if (status != WithdrawalStatus.PAID && status != WithdrawalStatus.REJECTED) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { selectedWithdrawal = w },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = MidnightBlack),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .testTag("admin_review_${w.id}")
                                ) {
                                    Text("Review & Manage Manual Payout", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                if (state.adminUsers.isEmpty()) {
                    EmptyStateCard(title = "No Users Found", subtitle = "Authenticated users appear here.")
                } else {
                    state.adminUsers.forEach { u ->
                        RoyalSectionCard(modifier = Modifier.padding(bottom = 10.dp)) {
                            Text(u.displayName, style = MaterialTheme.typography.titleMedium, color = PureWhite)
                            Text("UID: ${u.userId} · Email: ${u.email.ifBlank { "N/A" }}", style = MaterialTheme.typography.labelSmall, color = CoolSilver)
                            Text(
                                "Avail: ${u.availableDemoPoints} · Reserved: ${u.reservedDemoPoints} · Ads: ${u.completedAdsCount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = RoyalGold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Verified Account", style = MaterialTheme.typography.bodySmall, color = PureWhite)
                                Switch(
                                    checked = u.accountVerified,
                                    onCheckedChange = { onToggleUserFlags(u, it, u.fraudFlag) }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Fraud Flag", style = MaterialTheme.typography.bodySmall, color = DangerRed)
                                Switch(
                                    checked = u.fraudFlag,
                                    onCheckedChange = { onToggleUserFlags(u, u.accountVerified, it) }
                                )
                            }
                        }
                    }
                }
            }

            3 -> {
                var ptsPerAd by remember(state.config) { mutableStateOf(state.config.pointsPerAd.toString()) }
                var minWd by remember(state.config) { mutableStateOf(state.config.minWithdrawalPoints.toString()) }
                var feePts by remember(state.config) { mutableStateOf(state.config.feePoints.toString()) }
                var dailyLimit by remember(state.config) { mutableStateOf(state.config.dailyAdLimit.toString()) }

                RoyalSectionCard {
                    Text("Reward & Threshold Rules", style = MaterialTheme.typography.titleMedium, color = RoyalGold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = ptsPerAd,
                        onValueChange = { ptsPerAd = it.filter(Char::isDigit) },
                        label = { Text("DEMO Points Per Verified Ad (1..25)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold, unfocusedBorderColor = BorderSlate),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = minWd,
                        onValueChange = { minWd = it.filter(Char::isDigit) },
                        label = { Text("Minimum Withdrawal Threshold (Points)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold, unfocusedBorderColor = BorderSlate),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = feePts,
                        onValueChange = { feePts = it.filter(Char::isDigit) },
                        label = { Text("Withdrawal Fee (Points)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold, unfocusedBorderColor = BorderSlate),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dailyLimit,
                        onValueChange = { dailyLimit = it.filter(Char::isDigit) },
                        label = { Text("Daily Verified Ads Cap") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold, unfocusedBorderColor = BorderSlate),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    GoldGradientButton(
                        text = "Save Platform Configuration",
                        onClick = {
                            onSaveConfig(
                                state.config.copy(
                                    pointsPerAd = (ptsPerAd.toLongOrNull() ?: 2L).coerceIn(1L, 25L),
                                    minWithdrawalPoints = (minWd.toLongOrNull() ?: 100L).coerceIn(10L, 100000L),
                                    feePoints = (feePts.toLongOrNull() ?: 0L).coerceIn(0L, 1000L),
                                    dailyAdLimit = (dailyLimit.toLongOrNull() ?: 50L).coerceIn(1L, 500L)
                                )
                            )
                        }
                    )
                }
            }

            4 -> {
                if (state.adminTickets.isEmpty()) {
                    EmptyStateCard(title = "No Support Tickets", subtitle = "User support requests appear here.")
                } else {
                    state.adminTickets.forEach { t ->
                        var replyText by remember(t.id) { mutableStateOf(t.adminReply) }
                        RoyalSectionCard(modifier = Modifier.padding(bottom = 10.dp)) {
                            Text("${t.subject} · ${t.status}", style = MaterialTheme.typography.titleMedium, color = PureWhite)
                            Text("From: ${t.username}", style = MaterialTheme.typography.labelSmall, color = RoyalGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(t.message, style = MaterialTheme.typography.bodySmall, color = CoolSilver)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = replyText,
                                onValueChange = { replyText = it },
                                label = { Text("Admin Reply") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold, unfocusedBorderColor = BorderSlate),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onReplyTicket(t, "RESOLVED", replyText) },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightBlack),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text("Resolve & Reply", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            5 -> {
                if (state.adminAuditLogs.isEmpty()) {
                    EmptyStateCard(title = "No Audit Logs Yet", subtitle = "Every admin action writes an immutable audit log entry.")
                } else {
                    state.adminAuditLogs.forEach { log ->
                        RoyalSectionCard(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(log.action, style = MaterialTheme.typography.titleSmall, color = RoyalGold)
                            Text(log.details, style = MaterialTheme.typography.bodySmall, color = PureWhite)
                            Text("By: ${log.adminEmail} · Target: ${log.targetId}", style = MaterialTheme.typography.labelSmall, color = CoolSilver)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (selectedWithdrawal != null) {
        val w = selectedWithdrawal!!
        var txRef by remember(w.id) { mutableStateOf(w.externalTxReference) }
        var rejectReason by remember(w.id) { mutableStateOf(w.rejectionReason) }

        AlertDialog(
            onDismissRequest = { selectedWithdrawal = null },
            containerColor = DarkNavy,
            title = { Text("Manual Review: ${w.id}", color = RoyalGold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${w.method} · ${w.requestedPoints} DEMO pts\nHolder: ${w.accountHolderName}\nMobile: ${w.mobileNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite
                    )
                    OutlinedTextField(
                        value = txRef,
                        onValueChange = { txRef = it },
                        label = { Text("External JazzCash/Easypaisa Tx Reference") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalGold, unfocusedBorderColor = BorderSlate),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Rejection Reason (if rejecting)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DangerRed, unfocusedBorderColor = BorderSlate),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                onTransitionWithdrawal(w, WithdrawalStatus.UNDER_REVIEW, txRef, "")
                                selectedWithdrawal = null
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Review", color = SoftViolet) }
                        OutlinedButton(
                            onClick = {
                                onTransitionWithdrawal(w, WithdrawalStatus.APPROVED, txRef, "")
                                selectedWithdrawal = null
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Approve", color = RoyalGold) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                onTransitionWithdrawal(w, WithdrawalStatus.PAYMENT_SENT, txRef, "")
                                selectedWithdrawal = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple, contentColor = PureWhite),
                            modifier = Modifier.weight(1f)
                        ) { Text("Sent") }
                        Button(
                            onClick = {
                                onTransitionWithdrawal(w, WithdrawalStatus.PAID, txRef, "")
                                selectedWithdrawal = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightBlack),
                            modifier = Modifier.weight(1f)
                        ) { Text("Confirm Paid", fontWeight = FontWeight.Bold) }
                    }
                    Button(
                        onClick = {
                            onTransitionWithdrawal(w, WithdrawalStatus.REJECTED, "", rejectReason)
                            selectedWithdrawal = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = PureWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reject & Release Reserved Points")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedWithdrawal = null }) {
                    Text("Close", color = CoolSilver)
                }
            }
        )
    }
}
