package com.example.nightwake.combat

import com.example.nightwake.core.EventBus
import com.example.nightwake.core.GameConfig
import com.example.nightwake.core.GameEvent
import com.example.nightwake.data.EnemyDefinition
import com.example.nightwake.data.EnemyRegistry
import com.example.nightwake.data.MapDefinition
import kotlin.random.Random

/**
 * Wave Director with difficulty curves per map and dynamic day/night scaling.
 * Directs enemy budgets, pacing spikes, and scheduled boss appearances.
 */
class WaveDirector(private val map: MapDefinition) {

    var elapsedRunTimeSec: Float = 0f
        private set
    var spawnCooldownTimer: Float = 0f
        private set

    private var boss5Spawned = false
    private var boss10Spawned = false
    private var boss15Spawned = false

    fun reset() {
        elapsedRunTimeSec = 0f
        spawnCooldownTimer = 0.5f
        boss5Spawned = false
        boss10Spawned = false
        boss15Spawned = false
    }

    fun update(dt: Float, isNight: Boolean, activeEnemiesCount: Int, onSpawnEnemy: (EnemyDefinition, isBoss: Boolean) -> Unit) {
        elapsedRunTimeSec += dt
        spawnCooldownTimer -= dt

        // Check Boss Arrival triggers
        // Minute 5 Boss (300s)
        if (elapsedRunTimeSec >= 300f && !boss5Spawned) {
            boss5Spawned = true
            val boss = EnemyRegistry.getById("boss_gorgon")
            onSpawnEnemy(boss, true)
            EventBus.publish(GameEvent.WaveSpawned(5, isBossWave = true))
        }

        // Minute 10 Boss (600s)
        if (elapsedRunTimeSec >= 600f && !boss10Spawned) {
            boss10Spawned = true
            val boss = EnemyRegistry.getById("boss_terror")
            onSpawnEnemy(boss, true)
            EventBus.publish(GameEvent.WaveSpawned(10, isBossWave = true))
        }

        // Minute 15 Apex Boss (900s)
        if (elapsedRunTimeSec >= 900f && !boss15Spawned) {
            boss15Spawned = true
            val boss = EnemyRegistry.getById("boss_corpse_engine")
            onSpawnEnemy(boss, true)
            EventBus.publish(GameEvent.WaveSpawned(15, isBossWave = true))
        }

        // Standard Horde Spawns
        if (spawnCooldownTimer <= 0f) {
            // Compute dynamic interval based on elapsed time, map difficulty, and night
            val baseInterval = (1.2f - (elapsedRunTimeSec / 900f) * 0.9f).coerceAtLeast(0.12f)
            val nightScale = if (isNight) GameConfig.NIGHT_SPAWN_RATE_MULT else 1.0f
            val adjustedInterval = baseInterval / (map.baseDifficultyMultiplier * nightScale)
            spawnCooldownTimer = adjustedInterval

            // Cap maximum enemies
            if (activeEnemiesCount < GameConfig.MAX_ENEMIES_CAPACITY - 10) {
                val enemyDef = pickEnemyType(elapsedRunTimeSec)
                onSpawnEnemy(enemyDef, false)
            }
        }
    }

    private fun pickEnemyType(timeSec: Float): EnemyDefinition {
        val roll = Random.nextFloat()
        return when {
            timeSec < 60f -> {
                // Early game: Crawlers and Shamblers
                if (roll < 0.70f) EnemyRegistry.getById("crawler") else EnemyRegistry.getById("shambler")
            }
            timeSec < 180f -> {
                // Wave 2: Introduce Spitters and Swarm Leapers
                when {
                    roll < 0.40f -> EnemyRegistry.getById("crawler")
                    roll < 0.70f -> EnemyRegistry.getById("shambler")
                    roll < 0.85f -> EnemyRegistry.getById("spitter")
                    else -> EnemyRegistry.getById("leaper")
                }
            }
            timeSec < 360f -> {
                // Wave 3: Introduce Brutes and Screamers
                when {
                    roll < 0.30f -> EnemyRegistry.getById("crawler")
                    roll < 0.50f -> EnemyRegistry.getById("shambler")
                    roll < 0.65f -> EnemyRegistry.getById("spitter")
                    roll < 0.80f -> EnemyRegistry.getById("leaper")
                    roll < 0.92f -> EnemyRegistry.getById("brute")
                    else -> EnemyRegistry.getById("screamer")
                }
            }
            else -> {
                // Late game: All 8 variants, including Shielded Riot and Toxic Bloats
                when {
                    roll < 0.20f -> EnemyRegistry.getById("crawler")
                    roll < 0.35f -> EnemyRegistry.getById("shambler")
                    roll < 0.50f -> EnemyRegistry.getById("spitter")
                    roll < 0.65f -> EnemyRegistry.getById("leaper")
                    roll < 0.77f -> EnemyRegistry.getById("brute")
                    roll < 0.86f -> EnemyRegistry.getById("screamer")
                    roll < 0.94f -> EnemyRegistry.getById("riot")
                    else -> EnemyRegistry.getById("bloat")
                }
            }
        }
    }
}
