package com.example.nightwake.services

/**
 * Interface for rewarded and interstitial ads.
 * Wraps mediation networks (Google Mobile Ads / Unity Ads / AppLovin) behind clean abstractions.
 */
interface IAdService {
    enum class PlacementType {
        REVIVE_ON_DEATH,
        DOUBLE_RUN_REWARDS,
        REFILL_ENERGY,
        DAILY_FREE_CRATE
    }

    fun isAdReady(placement: PlacementType): Boolean
    fun showRewardedAd(placement: PlacementType, onRewardEarned: (Boolean) -> Unit)
    fun getRemainingCooldownSec(placement: PlacementType): Long
    fun getDailyViewsRemaining(placement: PlacementType): Int
}

class MockAdService : IAdService {
    private val viewCounts = mutableMapOf<IAdService.PlacementType, Int>()
    private val lastViewTime = mutableMapOf<IAdService.PlacementType, Long>()

    // Frequency cap: max 3 per placement per day
    private val maxDaily = 3
    // Cooldown: 60s between rewarded ads
    private val cooldownMs = 60_000L

    override fun isAdReady(placement: IAdService.PlacementType): Boolean {
        val views = viewCounts[placement] ?: 0
        if (views >= maxDaily) return false
        val last = lastViewTime[placement] ?: 0L
        return (System.currentTimeMillis() - last) >= cooldownMs
    }

    override fun showRewardedAd(
        placement: IAdService.PlacementType,
        onRewardEarned: (Boolean) -> Unit
    ) {
        val views = viewCounts[placement] ?: 0
        viewCounts[placement] = views + 1
        lastViewTime[placement] = System.currentTimeMillis()
        // Immediately grant reward in mock/development mode
        onRewardEarned(true)
    }

    override fun getRemainingCooldownSec(placement: IAdService.PlacementType): Long {
        val last = lastViewTime[placement] ?: 0L
        val elapsed = System.currentTimeMillis() - last
        return if (elapsed < cooldownMs) (cooldownMs - elapsed) / 1000L else 0L
    }

    override fun getDailyViewsRemaining(placement: IAdService.PlacementType): Int {
        val views = viewCounts[placement] ?: 0
        return (maxDaily - views).coerceAtLeast(0)
    }
}
