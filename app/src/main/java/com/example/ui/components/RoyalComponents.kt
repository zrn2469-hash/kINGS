package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.PaymentMethod
import com.example.model.WithdrawalStatus
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DeepRoyalPurple
import com.example.ui.theme.EasypaisaDark
import com.example.ui.theme.EasypaisaGreen
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.JazzCashGold
import com.example.ui.theme.JazzCashRed
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalBrushes
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SoftViolet
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun ResponsiveContentColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp),
            content = content
        )
    }
}

@Composable
fun DemoModeBadge(
    label: String = "DEMO MODE",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = RoyalPurple.copy(alpha = 0.22f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, SoftViolet.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "Demo verification status",
                tint = RoyalGold,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GoldGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = ""
) {
    val shape = RoundedCornerShape(14.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = ElevatedNavy,
            contentColor = MidnightBlack,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .height(52.dp)
            .minimumInteractiveComponentSize()
            .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(
                    brush = if (enabled) RoyalBrushes.GoldGradient else Brush.linearGradient(
                        listOf(ElevatedNavy, ElevatedNavy)
                    ),
                    shape = shape
                )
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) MidnightBlack else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) MidnightBlack else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RoyalSectionCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderSlate,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavy),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun WithdrawalStatusBadge(status: WithdrawalStatus) {
    val (bg, fg) = when (status) {
        WithdrawalStatus.PENDING -> WarningAmber.copy(alpha = 0.16f) to WarningAmber
        WithdrawalStatus.UNDER_REVIEW -> RoyalPurple.copy(alpha = 0.24f) to SoftViolet
        WithdrawalStatus.APPROVED -> RoyalGold.copy(alpha = 0.18f) to RoyalGold
        WithdrawalStatus.PAYMENT_SENT -> Color(0xFF38BDF8).copy(alpha = 0.18f) to Color(0xFF38BDF8)
        WithdrawalStatus.PAID -> SuccessGreen.copy(alpha = 0.18f) to SuccessGreen
        WithdrawalStatus.REJECTED -> DangerRed.copy(alpha = 0.18f) to DangerRed
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, fg.copy(alpha = 0.45f))
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PaymentMethodSelectionCard(
    method: PaymentMethod,
    selected: Boolean,
    savedSummary: String,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = if (method == PaymentMethod.JAZZCASH) JazzCashRed else EasypaisaGreen
    val secondaryAccent = if (method == PaymentMethod.JAZZCASH) JazzCashGold else EasypaisaDark
    val borderBrush = if (selected) {
        Brush.linearGradient(listOf(RoyalGold, accent))
    } else {
        Brush.linearGradient(listOf(BorderSlate, BorderSlate))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (selected) ElevatedNavy else DarkNavy
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onSelect)
            .minimumInteractiveComponentSize()
            .testTag("payment_card_${method.name.lowercase()}")
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(accent, secondaryAccent))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (method == PaymentMethod.JAZZCASH) "JC" else "ep",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PureWhite
                    )
                }
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "${method.displayName} selected",
                        tint = RoyalGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = method.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = method.tagline,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (savedSummary.isNotBlank()) "Saved: $savedSummary" else "Tap to choose",
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) RoyalGold else accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: ImageVector = Icons.Filled.WorkspacePremium,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    RoyalSectionCard(modifier = modifier, borderColor = BorderSlate) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(DeepRoyalPurple.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatMiniChip(
    label: String,
    value: String,
    accent: Color = RoyalGold,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ElevatedNavy,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun WalletSummaryHeroCard(
    availablePoints: Long,
    reservedPoints: Long,
    lifetimePoints: Long,
    completedAds: Long,
    minThreshold: Long,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (availablePoints.toFloat() / minThreshold.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF281D4D),
                            Color(0xFF151933),
                            Color(0xFF2B200B)
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
                            contentDescription = "Available DEMO wallet",
                            tint = RoyalGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AVAILABLE DEMO BALANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = RoyalGold
                        )
                    }
                    DemoModeBadge("DEMO POINTS")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "$availablePoints pts",
                            style = MaterialTheme.typography.displaySmall,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$completedAds ads verified · $lifetimePoints lifetime pts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onWithdrawClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalGold,
                            contentColor = MidnightBlack
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("hero_withdraw_button")
                    ) {
                        Text(
                            text = "Withdraw",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 50-Ad / 100-Point Threshold Meter
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MidnightBlack.copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(RoyalBrushes.GoldGradient)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (availablePoints >= minThreshold) {
                            "Eligible for manual withdrawal review"
                        } else {
                            "${minThreshold - availablePoints} more DEMO pts to reach $minThreshold-pt threshold"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (availablePoints >= minThreshold) SuccessGreen else RoyalGold
                    )
                    Text(
                        text = "Reserved: $reservedPoints pts",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
