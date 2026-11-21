package com.example.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.auth.GoogleSignInHelper
import com.example.ui.components.DemoModeBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.ResponsiveContentColumn
import com.example.ui.components.RoyalSectionCard
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.CoolSilver
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalBrushes
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0.78f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(800L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalBrushes.MidnightRadialGlow)
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(164.dp)
                    .scale(scale.value)
                    .clip(RoundedCornerShape(36.dp))
                    .background(DarkNavy),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_royal_crown_hero),
                    contentDescription = "KINGS EARNS Royal Crown Emblem",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "KINGS EARNS",
                style = MaterialTheme.typography.displayLarge,
                color = RoyalGold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "WATCH. EARN. WITHDRAW.",
                style = MaterialTheme.typography.labelMedium,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(18.dp))
            DemoModeBadge("TRANSPARENT DEMO REWARDS")
        }
    }
}

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val highlight: String
)

@Composable
fun OnboardingScreen(
    onEnterDemoMode: () -> Unit,
    onGoogleSignedIn: () -> Unit
) {
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    var step by remember { mutableIntStateOf(0) }
    var authBusy by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    val pages = remember {
        listOf(
            OnboardingPage(
                icon = Icons.Filled.PlayCircle,
                title = "TURN YOUR TIME INTO REWARDS",
                subtitle = "Discover eligible advertisements, complete approved activities, and collect rewards through a transparent rewards platform.",
                highlight = "2 DEMO Points per verified AdMob test rewarded ad"
            ),
            OnboardingPage(
                icon = Icons.Filled.AccountBalanceWallet,
                title = "FAIR 100-POINT THRESHOLD",
                subtitle = "Complete 50 verified rewarded ads to accumulate 100 DEMO Points. Every credit and deduction is recorded in an auditable ledger.",
                highlight = "50 verified ads = 100 DEMO Points (0% DEMO fee)"
            ),
            OnboardingPage(
                icon = Icons.Filled.VerifiedUser,
                title = "MANUAL ADMIN VERIFICATION",
                subtitle = "Choose JazzCash or Easypaisa for every withdrawal request. Requests remain PENDING until an authorized adult administrator reviews them.",
                highlight = "No guaranteed cash promises · No automatic payouts"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalBrushes.MidnightRadialGlow)
            .safeDrawingPadding()
    ) {
        ResponsiveContentColumn(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KINGS EARNS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = RoyalGold
                )
                DemoModeBadge("DEMO MODE")
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_royal_crown_hero),
                    contentDescription = "Royal crown resting on purple velvet cushion with golden coins",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MidnightBlack.copy(alpha = 0.05f),
                                    MidnightBlack.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(RoyalPurple.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = pages[step].icon,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = pages[step].highlight,
                        style = MaterialTheme.typography.labelLarge,
                        color = RoyalGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            RoyalSectionCard(borderColor = RoyalGold.copy(alpha = 0.35f)) {
                Text(
                    text = pages[step].title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = pages[step].subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CoolSilver
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { idx ->
                        Surface(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (idx == step) 28.dp else 10.dp),
                            shape = RoundedCornerShape(50),
                            color = if (idx == step) RoyalGold else BorderSlate
                        ) {}
                    }
                }
            }

            if (authError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = authError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            if (step < pages.lastIndex) {
                GoldGradientButton(
                    text = "Continue (${step + 1}/${pages.size})",
                    onClick = { step += 1 },
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    testTag = "onboarding_next_button"
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onEnterDemoMode,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderSlate),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CoolSilver),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("onboarding_skip_demo_button")
                ) {
                    Text("Start Earning Now (No Sign-Up Required)")
                }
            } else {
                GoldGradientButton(
                    text = "Start Earning in DEMO Mode",
                    onClick = onEnterDemoMode,
                    icon = Icons.Filled.PlayCircle,
                    testTag = "onboarding_start_demo_button"
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        if (activity == null) return@OutlinedButton
                        authBusy = true
                        authError = null
                        GoogleSignInHelper.onGoogleSignInClicked(
                            activity = activity,
                            scope = scope,
                            onAuthSuccess = {
                                authBusy = false
                                onGoogleSignedIn()
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
                    enabled = !authBusy,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("onboarding_google_signin_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudDone,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (authBusy) "Connecting..." else "Optional: Sync with Google Cloud")
                }
            }
        }
    }
}
