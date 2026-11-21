package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ads.RewardedAdManager
import com.example.data.DemoSessionStore
import com.example.data.KingsEarnsRepository
import com.example.ui.KingsEarnsViewModel
import com.example.ui.screens.ActivitiesReferralScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileTransparencyScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WalletWithdrawScreen
import com.example.ui.screens.WatchAdsScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.theme.CoolSilver
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.KingsEarnsTheme
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoyalBrushes
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SuccessGreen
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

object KingsEarnsRoutes {
    const val HOME = "home"
    const val WATCH_ADS = "watch_ads"
    const val ACTIVITIES = "activities"
    const val WALLET = "wallet"
    const val PROFILE = "profile"
    const val NOTIFICATIONS = "notifications"
    const val ADMIN = "admin"
    const val WEB_PORTAL = "web_portal"
}

private data class NavTabItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch(Dispatchers.IO) {
            MobileAds.initialize(applicationContext) {}
        }

        setContent {
            KingsEarnsTheme {
                KingsEarnsRoot()
            }
        }
    }
}

@Composable
fun KingsEarnsRoot() {
    val auth = remember { Firebase.auth }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val currentUid = currentUser?.uid
    var showSplash by remember { mutableStateOf(true) }

    key(currentUid ?: "demo_guest") {
        val vmKey = currentUid ?: "demo_guest"
        val viewModel: KingsEarnsViewModel = viewModel(
            key = vmKey,
            factory = viewModelFactory {
                initializer {
                    val app = checkNotNull(this[APPLICATION_KEY])
                    val databaseId = app.getString(R.string.firestore_database_id)
                    val db = FirebaseFirestore.getInstance(databaseId)
                    KingsEarnsViewModel(
                        repository = KingsEarnsRepository(db),
                        demoStore = DemoSessionStore(app.applicationContext),
                        adManager = RewardedAdManager(app.applicationContext),
                        currentUserId = currentUid
                    )
                }
            }
        )

        val state by viewModel.uiState.collectAsStateWithLifecycle()

        when {
            showSplash -> SplashScreen(onFinished = { showSplash = false })
            !state.onboardingSeen && !state.isCloudSignedIn -> OnboardingScreen(
                onEnterDemoMode = { viewModel.markOnboardingSeen() },
                onGoogleSignedIn = { viewModel.markOnboardingSeen() }
            )
            else -> KingsEarnsAppShell(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun KingsEarnsAppShell(
    state: com.example.ui.KingsEarnsUiState,
    viewModel: KingsEarnsViewModel
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: KingsEarnsRoutes.HOME
    var localToastMessage by remember { mutableStateOf<String?>(null) }

    val tabs = remember {
        listOf(
            NavTabItem(KingsEarnsRoutes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
            NavTabItem(KingsEarnsRoutes.WATCH_ADS, "Ads", Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle),
            NavTabItem(KingsEarnsRoutes.ACTIVITIES, "Earn", Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard),
            NavTabItem(KingsEarnsRoutes.WALLET, "Wallet", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
            NavTabItem(KingsEarnsRoutes.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
        )
    }

    LaunchedEffect(state.statusBanner, state.errorMessage, localToastMessage) {
        val msg = state.errorMessage ?: state.statusBanner ?: localToastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissBanner()
            viewModel.dismissError()
            localToastMessage = null
        }
    }

    val isMainTab = tabs.any { it.route == currentRoute }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalBrushes.MidnightRadialGlow)
    ) {
        val useRail = maxWidth >= 720.dp

        Scaffold(
            containerColor = MidnightBlack,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = ElevatedNavy,
                        contentColor = PureWhite,
                        actionColor = RoyalGold
                    )
                }
            },
            bottomBar = {
                if (!useRail && isMainTab) {
                    NavigationBar(
                        containerColor = DarkNavy,
                        contentColor = RoyalGold,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        tabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(KingsEarnsRoutes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MidnightBlack,
                                    selectedTextColor = RoyalGold,
                                    indicatorColor = RoyalGold,
                                    unselectedIconColor = CoolSilver,
                                    unselectedTextColor = CoolSilver
                                ),
                                modifier = Modifier.testTag("nav_${tab.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail && isMainTab) {
                    NavigationRail(
                        containerColor = DarkNavy,
                        contentColor = RoyalGold,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        tabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(KingsEarnsRoutes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MidnightBlack,
                                    selectedTextColor = RoyalGold,
                                    indicatorColor = RoyalGold,
                                    unselectedIconColor = CoolSilver,
                                    unselectedTextColor = CoolSilver
                                )
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    NavHost(
                        navController = navController,
                        startDestination = KingsEarnsRoutes.HOME
                    ) {
                        composable(KingsEarnsRoutes.HOME) {
                            HomeDashboardScreen(
                                state = state,
                                onNavigateWatchAds = { navController.navigate(KingsEarnsRoutes.WATCH_ADS) },
                                onNavigateActivities = { navController.navigate(KingsEarnsRoutes.ACTIVITIES) },
                                onNavigateReferral = { navController.navigate(KingsEarnsRoutes.ACTIVITIES) },
                                onNavigateWithdraw = { navController.navigate(KingsEarnsRoutes.WALLET) },
                                onNavigateNotifications = { navController.navigate(KingsEarnsRoutes.NOTIFICATIONS) },
                                onNavigateProfile = { navController.navigate(KingsEarnsRoutes.PROFILE) },
                                onNavigateWebPortal = { navController.navigate(KingsEarnsRoutes.WEB_PORTAL) },
                                onWatchOffer = { activity, offer -> viewModel.watchRewardedAd(activity, offer) }
                            )
                        }
                        composable(KingsEarnsRoutes.WATCH_ADS) {
                            WatchAdsScreen(
                                state = state,
                                onWatchAd = { activity, offer -> viewModel.watchRewardedAd(activity, offer) },
                                onReloadAd = { viewModel.reloadRewardedAd() },
                                onToggleTestAdUnit = { viewModel.setUseTestAdUnit(it) }
                            )
                        }
                        composable(KingsEarnsRoutes.ACTIVITIES) {
                            ActivitiesReferralScreen(
                                state = state,
                                onClaimDaily = { viewModel.claimDailyCheckIn() },
                                onCompleteActivity = { viewModel.completePromoActivity(it) },
                                onNavigateWatchAds = { navController.navigate(KingsEarnsRoutes.WATCH_ADS) },
                                onApplyReferralCode = { viewModel.applyReferralCode(it) },
                                onCopyMessage = { localToastMessage = it }
                            )
                        }
                        composable(KingsEarnsRoutes.WALLET) {
                            WalletWithdrawScreen(
                                state = state,
                                onSavePayoutDetails = { method, holder, number ->
                                    viewModel.savePayoutDetails(method, holder, number)
                                },
                                onSubmitWithdrawal = { method, holder, number, amount, onDone ->
                                    viewModel.submitWithdrawal(method, holder, number, amount, onDone)
                                }
                            )
                        }
                        composable(KingsEarnsRoutes.PROFILE) {
                            ProfileTransparencyScreen(
                                state = state,
                                onOpenNotifications = { navController.navigate(KingsEarnsRoutes.NOTIFICATIONS) },
                                onOpenAdminPortal = { navController.navigate(KingsEarnsRoutes.ADMIN) },
                                onOpenWebPortal = { navController.navigate(KingsEarnsRoutes.WEB_PORTAL) },
                                onSubmitSupportTicket = { sub, msg -> viewModel.submitSupportTicket(sub, msg) },
                                onSignedIn = { localToastMessage = "Connected to Firebase Cloud Backend." },
                                onSignedOut = { localToastMessage = "Switched to local DEMO Mode." }
                            )
                        }
                        composable(KingsEarnsRoutes.WEB_PORTAL) {
                            BackHandler { navController.popBackStack() }
                            WebPortalScreen(onBack = { navController.popBackStack() })
                        }
                        composable(KingsEarnsRoutes.NOTIFICATIONS) {
                            BackHandler { navController.popBackStack() }
                            NotificationsScreen(
                                state = state,
                                onMarkRead = { viewModel.markNotificationRead(it) },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(KingsEarnsRoutes.ADMIN) {
                            BackHandler { navController.popBackStack() }
                            AdminDashboardScreen(
                                state = state,
                                onBack = { navController.popBackStack() },
                                onTransitionWithdrawal = { wd, status, txRef, reason ->
                                    viewModel.adminTransitionWithdrawal(wd, status, txRef, reason)
                                },
                                onToggleUserFlags = { u, verified, fraud ->
                                    viewModel.adminToggleUserFlags(u, verified, fraud)
                                },
                                onSaveConfig = { viewModel.adminSaveConfig(it) },
                                onReplyTicket = { t, status, reply ->
                                    viewModel.adminReplyTicket(t, status, reply)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
