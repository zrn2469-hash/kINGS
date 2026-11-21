package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ads.RewardedAdManager
import com.example.model.PlatformConfig
import com.example.ui.defaultAdOffers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kings Earns", appName)
  }

  @Test
  fun `default demo config awards 2 pts per ad and requires 100 pts threshold`() {
    val config = PlatformConfig()
    assertEquals(2L, config.pointsPerAd)
    assertEquals(100L, config.minWithdrawalPoints)
    assertEquals(0L, config.feePoints)
    assertEquals(100L, config.pointsPerAd * 50L)
  }

  @Test
  fun `ad placements include all 4 configured admob units and test fallback`() {
    val liveOffers = defaultAdOffers(isTestFallback = false)
    assertEquals(4, liveOffers.size)
    assertEquals(RewardedAdManager.LIVE_REWARDED_AD_UNIT_IDS, liveOffers.map { it.adUnitId })

    val testOffers = defaultAdOffers(isTestFallback = true)
    assertEquals(4, testOffers.size)
    assertTrue(testOffers.all { it.adUnitId == RewardedAdManager.TEST_REWARDED_AD_UNIT_ID })
  }
}
