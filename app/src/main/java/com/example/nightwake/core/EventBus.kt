package com.example.nightwake.core

/**
 * Lightweight, typed in-memory Event Bus for decoupling gameplay systems.
 * Supports listening to key lifecycle events without tight coupling.
 */
sealed interface GameEvent {
    data class EnemyKilled(val enemyType: String, val x: Float, val y: Float, val xpValue: Int, val isBoss: Boolean) : GameEvent
    data class PlayerDamaged(val currentHp: Float, val maxHp: Float, val damageTaken: Float) : GameEvent
    data class PlayerLeveledUp(val newLevel: Int) : GameEvent
    data class WaveSpawned(val waveNumber: Int, val isBossWave: Boolean) : GameEvent
    data class DayPhaseChanged(val phaseName: String, val isNight: Boolean) : GameEvent
    data class SanctuaryUpgraded(val buildingId: String, val newLevel: Int) : GameEvent
    data class RaidWaveCompleted(val wave: Int, val rewardScrap: Int) : GameEvent
}

typealias GameEventListener = (GameEvent) -> Unit

object EventBus {
    private val listeners = mutableListOf<GameEventListener>()

    fun subscribe(listener: GameEventListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun unsubscribe(listener: GameEventListener) {
        listeners.remove(listener)
    }

    fun publish(event: GameEvent) {
        for (i in 0 until listeners.size) {
            listeners[i].invoke(event)
        }
    }

    fun clear() {
        listeners.clear()
    }
}
