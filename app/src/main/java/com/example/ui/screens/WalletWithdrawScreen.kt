package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.LedgerType
import com.example.model.PaymentMethod
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import com.example.ui.KingsEarnsUiState
import com.example.ui.components.DemoModeBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.PaymentMethodSelectionCard
import com.example.ui.components.ResponsiveContentColumn
import com.example.ui.components.RoyalSectionCard
import com.example.ui.components.StatMiniChip
import com.example.ui.components.WithdrawalStatusBadge
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.CoolSilver
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun WalletWithdrawScreen(
    state: KingsEarnsUiState,
    onSavePayoutDetails: (PaymentMethod, String, String) -> Unit,
    onSubmitWithdrawal: (PaymentMethod?, String, String, String, (WithdrawalRequest) -> Unit) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) }
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var holderName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf(state.config.minWithdrawalPoints.toString()) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var lastSubmittedRequest by remember { mutableStateOf<WithdrawalRequest?>(null) }

    val requestedPts = amountText.trim().toLongOrNull() ?: 0L
    val feePts = state.config.feePoints
    val netPts = (requestedPts - feePts).coerceAtLeast(0L)

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
                    text = "My Wallet & Withdraw",
                    style = MaterialTheme.typography.headlineMedium,
                    color = RoyalGold
                )
                Text(
                    text = "Auditable DEMO Ledger · Manual Review",
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolSilver
                )
            }
            DemoModeBadge("DEMO WALLET")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Black & Gold Financial Wallet Card
        Surface(
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.55f)),
            color = DarkNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MidnightBlack,
                                ElevatedNavy,
                                RoyalPurple.copy(alpha = 0.28f)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalanceWallet,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AVAILABLE DEMO POINTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = RoyalGold
                            )
                        }
                        Text(
                            text = "Min ${state.config.minWithdrawalPoints} pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = CoolSilver
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${state.profile.availableDemoPoints} DEMO Points",
                        style = MaterialTheme.typography.displaySmall,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMiniChip(
                            label = "Reserved",
                            value = "${state.profile.reservedDemoPoints} pts",
                            accent = WarningAmber,
                            modifier = Modifier.weight(1f)
                        )
                        StatMiniChip(
                            label = "Lifetime",
                            value = "${state.profile.lifetimeDemoPoints} pts",
                            accent = RoyalGold,
                            modifier = Modifier.weight(1f)
                        )
                        StatMiniChip(
                            label = "Paid Out",
                            value = "${state.profile.totalWithdrawnDemoPoints} pts",
                            accent = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkNavy,
            contentColor = RoyalGold
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Withdraw") },
                modifier = Modifier.testTag("wallet_tab_withdraw")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Requests (${state.withdrawals.size})") },
                modifier = Modifier.testTag("wallet_tab_requests")
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Ledger (${state.ledger.size})") },
                modifier = Modifier.testTag("wallet_tab_ledger")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (activeTab) {
            0 -> {
                if (lastSubmittedRequest != null) {
                    val req = lastSubmittedRequest!!
                    RoyalSectionCard(
                        borderColor = SuccessGreen.copy(alpha = 0.65f),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LockClock,
                                    contentDescription = "Pending review",
                                    tint = WarningAmber
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Request ${req.id}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PureWhite
                                )
                            }
                            WithdrawalStatusBadge(WithdrawalStatus.PENDING)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your withdrawal request has been received. Our administrator will verify your account details and manually process your payment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${req.requestedPoints} DEMO pts via ${req.method} -> ${req.accountHolderName} (${req.mobileNumber})",
                            style = MaterialTheme.typography.bodySmall,
                            color = CoolSilver
                        )
                    }
                }

                // Step 3 & 4: Independent JazzCash & Easypaisa Selection (Never auto-selected)
                Text(
                    text = "Step 1: Select Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose JazzCash or Easypaisa manually for each withdrawal request.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolSilver
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PaymentMethodSelectionCard(
                        method = PaymentMethod.JAZZCASH,
                        selected = selectedMethod == PaymentMethod.JAZZCASH,
                        savedSummary = state.profile.jazzCashNumber,
                        onSelect = {
                            selectedMethod = PaymentMethod.JAZZCASH
                            holderName = state.profile.jazzCashHolder
                            mobileNumber = state.profile.jazzCashNumber
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentMethodSelectionCard(
                        method = PaymentMethod.EASYPAISA,
                        selected = selectedMethod == PaymentMethod.EASYPAISA,
                        savedSummary = state.profile.easypaisaNumber,
                        onSelect = {
                            selectedMethod = PaymentMethod.EASYPAISA
                            holderName = state.profile.easypaisaHolder
                            mobileNumber = state.profile.easypaisaNumber
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedMethod == null) {
                    EmptyStateCard(
                        icon = Icons.Filled.AccountBalanceWallet,
                        title = "Select JazzCash or Easypaisa",
                        subtitle = "Tap one of the two payment cards above to enter your account holder name, mobile number, and DEMO points amount."
                    )
                } else {
                    val activeMethod = selectedMethod!!
                    RoyalSectionCard(borderColor = RoyalGold.copy(alpha = 0.45f)) {
                        Text(
                            text = "Step 2: ${activeMethod.displayName} Account Details",
                            style = MaterialTheme.typography.titleMedium,
                            color = RoyalGold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = holderName,
                            onValueChange = { holderName = it },
                            label = { Text("${activeMethod.displayName} Account Holder Name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalGold,
                                unfocusedBorderColor = BorderSlate
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_holder_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { input ->
                                mobileNumber = input.filter { it.isDigit() }.take(11)
                            },
                            label = { Text("${activeMethod.displayName} Mobile Number (03XXXXXXXXX)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalGold,
                                unfocusedBorderColor = BorderSlate
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_mobile_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { onSavePayoutDetails(activeMethod, holderName, mobileNumber) },
                            border = BorderStroke(1.dp, BorderSlate),
                            modifier = Modifier
                                .align(Alignment.End)
                                .minimumInteractiveComponentSize()
                                .testTag("save_payout_details_button")
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save ${activeMethod.displayName} Details", color = PureWhite)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Step 3: Enter DEMO Points (Min ${state.config.minWithdrawalPoints} pts)",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(100L, 200L, 500L).forEach { preset ->
                                FilterChip(
                                    selected = amountText == preset.toString(),
                                    onClick = { amountText = preset.toString() },
                                    label = { Text("$preset pts") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RoyalGold,
                                        selectedLabelColor = MidnightBlack
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { input -> amountText = input.filter { it.isDigit() }.take(7) },
                            label = { Text("Requested DEMO Points") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalGold,
                                unfocusedBorderColor = BorderSlate
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_amount_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BorderSlate)
                        Spacer(modifier = Modifier.height(10.dp))

                        SummaryRow("Selected Method", activeMethod.displayName)
                        SummaryRow("Destination Account", mobileNumber.ifBlank { "Not entered" })
                        SummaryRow("Requested DEMO Points", "$requestedPts pts")
                        SummaryRow("Applicable Fee (DEMO)", "$feePts pts")
                        SummaryRow("Net DEMO Payout", "$netPts pts", highlight = true)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Note: Points are reserved upon submission. An administrator manually verifies details and sends payments externally — never automatically.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CoolSilver
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        GoldGradientButton(
                            text = "Submit Manual Withdrawal Request",
                            enabled = !state.isBusy,
                            icon = Icons.AutoMirrored.Filled.Send,
                            onClick = { showConfirmDialog = true },
                            testTag = "submit_withdrawal_button"
                        )
                    }
                }
            }

            1 -> {
                if (state.withdrawals.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Filled.History,
                        title = "No Withdrawal Requests Yet",
                        subtitle = "Once you reach ${state.config.minWithdrawalPoints} DEMO Points and submit a request, track its manual review status here."
                    )
                } else {
                    state.withdrawals.forEach { w ->
                        val status = WithdrawalStatus.fromRaw(w.status)
                        RoyalSectionCard(modifier = Modifier.padding(bottom = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${w.method} · ${w.requestedPoints} DEMO pts",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PureWhite
                                )
                                WithdrawalStatusBadge(status)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Request ID: ${w.id}",
                                style = MaterialTheme.typography.labelSmall,
                                color = RoyalGold
                            )
                            Text(
                                text = "Account: ${w.accountHolderName} (${w.mobileNumber})",
                                style = MaterialTheme.typography.bodySmall,
                                color = CoolSilver
                            )
                            if (w.externalTxReference.isNotBlank()) {
                                Text(
                                    text = "External Tx Ref: ${w.externalTxReference}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SuccessGreen
                                )
                            }
                            if (w.rejectionReason.isNotBlank()) {
                                Text(
                                    text = "Rejection Reason: ${w.rejectionReason}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                if (state.ledger.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Filled.CheckCircle,
                        title = "Auditable Ledger Empty",
                        subtitle = "Every verified ad credit, check-in, and withdrawal reserve appears here chronologically."
                    )
                } else {
                    state.ledger.forEach { entry ->
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
                                        color = PureWhite
                                    )
                                    Text(
                                        text = "${type.label} · Balance after: ${entry.balanceAfter} pts · Ref: ${entry.referenceId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CoolSilver
                                    )
                                }
                                Text(
                                    text = if (entry.pointsDelta >= 0) "+${entry.pointsDelta}" else "${entry.pointsDelta}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (entry.pointsDelta >= 0) SuccessGreen else WarningAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = DarkNavy,
            title = {
                Text(
                    text = "Confirm DEMO Withdrawal Request",
                    color = RoyalGold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "Reserve $requestedPts DEMO Points via ${selectedMethod?.displayName ?: "-"} ($mobileNumber)?\n\nThis request will remain PENDING until an authorized adult administrator reviews your account and manually processes it.",
                    color = PureWhite
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onSubmitWithdrawal(selectedMethod, holderName, mobileNumber, amountText) { req ->
                            lastSubmittedRequest = req
                            activeTab = 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = MidnightBlack),
                    modifier = Modifier.testTag("confirm_withdrawal_dialog_button")
                ) {
                    Text("Confirm Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = CoolSilver)
                }
            }
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = CoolSilver
        )
        Text(
            text = value,
            style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            color = if (highlight) RoyalGold else PureWhite,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium
        )
    }
}
