package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface AdMobLoadState {
    data object Idle : AdMobLoadState
    data object Loading : AdMobLoadState
    data object Ready : AdMobLoadState
    data object Showing : AdMobLoadState
    data class Failed(val errorMessage: String) : AdMobLoadState
}

class RewardedAdManager(private val appContext: Context) {

    companion object {
        private const val TAG = "KingsEarnsAdMob"
        const val LIVE_REWARDED_AD_UNIT_ID = "ca-app-pub-2177974629752041/1735602420"
        const val LIVE_REWARDED_AD_UNIT_ID_2 = "ca-app-pub-2177974629752041/3114143605"
        const val LIVE_REWARDED_AD_UNIT_ID_3 = "ca-app-pub-2177974629752041/1428480664"
        const val LIVE_REWARDED_AD_UNIT_ID_4 = "ca-app-pub-2177974629752041/9013784656"
        val LIVE_REWARDED_AD_UNIT_IDS = listOf(
            LIVE_REWARDED_AD_UNIT_ID,
            LIVE_REWARDED_AD_UNIT_ID_2,
            LIVE_REWARDED_AD_UNIT_ID_3,
            LIVE_REWARDED_AD_UNIT_ID_4
        )
        const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private val loadedAds = mutableMapOf<String, RewardedAd>()
    private val _unitStates = MutableStateFlow<Map<String, AdMobLoadState>>(emptyMap())
    val unitStates: StateFlow<Map<String, AdMobLoadState>> = _unitStates.asStateFlow()

    private val _loadState = MutableStateFlow<AdMobLoadState>(AdMobLoadState.Idle)
    val loadState: StateFlow<AdMobLoadState> = _loadState.asStateFlow()

    private fun updateUnitState(adUnitId: String, state: AdMobLoadState) {
        _unitStates.update { current ->
            val updated = current + (adUnitId to state)
            _loadState.value = when {
                updated.values.any { it is AdMobLoadState.Showing } -> AdMobLoadState.Showing
                updated.values.any { it is AdMobLoadState.Ready } -> AdMobLoadState.Ready
                updated.values.any { it is AdMobLoadState.Loading } -> AdMobLoadState.Loading
                else -> updated.values.firstOrNull { it is AdMobLoadState.Failed } ?: AdMobLoadState.Idle
            }
            updated
        }
    }

    fun preloadAll(
        adUnitIds: List<String> = LIVE_REWARDED_AD_UNIT_IDS,
        forceReload: Boolean = false
    ) {
        adUnitIds.distinct().forEach { unitId ->
            preloadAd(adUnitId = unitId, forceReload = forceReload)
        }
    }

    fun preloadAd(
        adUnitId: String = LIVE_REWARDED_AD_UNIT_ID,
        forceReload: Boolean = false
    ) {
        val currentState = _unitStates.value[adUnitId]
        if (!forceReload) {
            if (currentState is AdMobLoadState.Loading) return
            if (loadedAds[adUnitId] != null) {
                updateUnitState(adUnitId, AdMobLoadState.Ready)
                return
            }
        } else {
            loadedAds.remove(adUnitId)
        }

        updateUnitState(adUnitId, AdMobLoadState.Loading)
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            appContext,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "AdMob RewardedAd loaded for unit $adUnitId.")
                    loadedAds[adUnitId] = ad
                    updateUnitState(adUnitId, AdMobLoadState.Ready)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "AdMob RewardedAd failed to load ($adUnitId): ${loadAdError.message} (code=${loadAdError.code})")
                    loadedAds.remove(adUnitId)
                    updateUnitState(
                        adUnitId,
                        AdMobLoadState.Failed(
                            loadAdError.message.ifBlank { "Unable to load rewarded ad right now." }
                        )
                    )
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity,
        adUnitId: String = LIVE_REWARDED_AD_UNIT_ID,
        onVerifiedRewardEarned: (rewardType: String, rewardAmount: Int) -> Unit,
        onAdDismissedWithoutReward: () -> Unit,
        onAdShowError: (String) -> Unit
    ) {
        val currentAd = loadedAds[adUnitId]
        if (currentAd == null) {
            onAdShowError("Rewarded ad ($adUnitId) is not ready yet. Reloading...")
            preloadAd(adUnitId, forceReload = true)
            return
        }

        var rewardGranted = false
        updateUnitState(adUnitId, AdMobLoadState.Showing)

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "AdMob RewardedAd dismissed ($adUnitId). rewardGranted=$rewardGranted")
                loadedAds.remove(adUnitId)
                updateUnitState(adUnitId, AdMobLoadState.Idle)
                preloadAd(adUnitId, forceReload = true)
                if (!rewardGranted) {
                    onAdDismissedWithoutReward()
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "AdMob RewardedAd failed to show ($adUnitId): ${adError.message}")
                loadedAds.remove(adUnitId)
                updateUnitState(adUnitId, AdMobLoadState.Failed(adError.message))
                preloadAd(adUnitId, forceReload = true)
                onAdShowError(adError.message)
            }
        }

        currentAd.show(activity) { rewardItem ->
            rewardGranted = true
            Log.d(TAG, "Verified OnUserEarnedReward ($adUnitId): type=${rewardItem.type}, amount=${rewardItem.amount}")
            onVerifiedRewardEarned(rewardItem.type, rewardItem.amount)
        }
    }
}
