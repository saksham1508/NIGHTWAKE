package com.example.nightwake.services

import android.util.Log

/**
 * Analytics event abstraction for soft-launch KPIs.
 * Tracks tutorial funnel, retention milestones, run durations, and economy sinks/sources.
 */
interface IAnalyticsService {
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())
    fun logRunStarted(characterId: String, mapId: String)
    fun logRunCompleted(mapId: String, survivedSec: Float, kills: Int, level: Int, victory: Boolean)
    fun logEconomySink(currency: String, amount: Int, itemBought: String)
    fun logEconomySource(currency: String, amount: Int, reason: String)
}

class DebugAnalyticsService : IAnalyticsService {
    private val tag = "NightwakeAnalytics"

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        Log.d(tag, "EVENT: $eventName | PARAMS: $params")
    }

    override fun logRunStarted(characterId: String, mapId: String) {
        logEvent("run_started", mapOf("character" to characterId, "map" to mapId))
    }

    override fun logRunCompleted(
        mapId: String,
        survivedSec: Float,
        kills: Int,
        level: Int,
        victory: Boolean
    ) {
        logEvent(
            "run_completed",
            mapOf(
                "map" to mapId,
                "survived_sec" to survivedSec,
                "kills" to kills,
                "level" to level,
                "victory" to victory
            )
        )
    }

    override fun logEconomySink(currency: String, amount: Int, itemBought: String) {
        logEvent("economy_sink", mapOf("currency" to currency, "amount" to amount, "item" to itemBought))
    }

    override fun logEconomySource(currency: String, amount: Int, reason: String) {
        logEvent("economy_source", mapOf("currency" to currency, "amount" to amount, "reason" to reason))
    }
}
