package com.example.nightwake.data

/**
 * Data-driven Enemy and Boss Definitions.
 * 8 Enemy Types + 3 Major Bosses with distinct behaviors, speeds, and health pools.
 */
data class EnemyDefinition(
    val id: String,
    val name: String,
    val isBoss: Boolean,
    val baseHp: Float,
    val baseSpeed: Float,
    val contactDamage: Float,
    val collisionRadius: Float,
    val xpValue: Int,
    val colorHex: Long,
    val canShoot: Boolean = false,
    val shootCooldownSec: Float = 3.0f,
    val bulletDamage: Float = 0f,
    val explodesOnDeath: Boolean = false,
    val explosionRadius: Float = 0f
)

object EnemyRegistry {
    val ENEMIES = listOf(
        // Standard Horde (8 Types)
        EnemyDefinition(
            id = "crawler",
            name = "Infected Crawler",
            isBoss = false,
            baseHp = 24f,
            baseSpeed = 220f,
            contactDamage = 8f,
            collisionRadius = 16f,
            xpValue = 1,
            colorHex = 0xFFFF8A80
        ),
        EnemyDefinition(
            id = "shambler",
            name = "Ash Shambler",
            isBoss = false,
            baseHp = 48f,
            baseSpeed = 140f,
            contactDamage = 14f,
            collisionRadius = 22f,
            xpValue = 2,
            colorHex = 0xFFB0BEC5
        ),
        EnemyDefinition(
            id = "spitter",
            name = "Bile Spitter",
            isBoss = false,
            baseHp = 42f,
            baseSpeed = 160f,
            contactDamage = 10f,
            collisionRadius = 20f,
            xpValue = 3,
            colorHex = 0xFF76FF03,
            canShoot = true,
            shootCooldownSec = 2.4f,
            bulletDamage = 12f
        ),
        EnemyDefinition(
            id = "brute",
            name = "Garrison Brute",
            isBoss = false,
            baseHp = 180f,
            baseSpeed = 110f,
            contactDamage = 26f,
            collisionRadius = 32f,
            xpValue = 8,
            colorHex = 0xFF8D6E63
        ),
        EnemyDefinition(
            id = "leaper",
            name = "Swarm Leaper",
            isBoss = false,
            baseHp = 35f,
            baseSpeed = 260f,
            contactDamage = 12f,
            collisionRadius = 18f,
            xpValue = 2,
            colorHex = 0xFFFFAB40
        ),
        EnemyDefinition(
            id = "screamer",
            name = "Banshee Screamer",
            isBoss = false,
            baseHp = 60f,
            baseSpeed = 180f,
            contactDamage = 16f,
            collisionRadius = 24f,
            xpValue = 5,
            colorHex = 0xFFEA80FC
        ),
        EnemyDefinition(
            id = "riot",
            name = "Shielded Enforcer",
            isBoss = false,
            baseHp = 140f,
            baseSpeed = 125f,
            contactDamage = 20f,
            collisionRadius = 28f,
            xpValue = 6,
            colorHex = 0xFF37474F
        ),
        EnemyDefinition(
            id = "bloat",
            name = "Toxic Bloat",
            isBoss = false,
            baseHp = 75f,
            baseSpeed = 95f,
            contactDamage = 15f,
            collisionRadius = 26f,
            xpValue = 4,
            colorHex = 0xFF69F0AE,
            explodesOnDeath = true,
            explosionRadius = 90f
        ),

        // Bosses (3 Major Encounters)
        EnemyDefinition(
            id = "boss_gorgon",
            name = "Gorgon Behemoth (Minute 5)",
            isBoss = true,
            baseHp = 2200f,
            baseSpeed = 120f,
            contactDamage = 35f,
            collisionRadius = 60f,
            xpValue = 50,
            colorHex = 0xFFFF1744,
            canShoot = true,
            shootCooldownSec = 3.0f,
            bulletDamage = 22f
        ),
        EnemyDefinition(
            id = "boss_terror",
            name = "Night Terror (Minute 10)",
            isBoss = true,
            baseHp = 4800f,
            baseSpeed = 210f,
            contactDamage = 45f,
            collisionRadius = 55f,
            xpValue = 100,
            colorHex = 0xFF651FFF,
            canShoot = true,
            shootCooldownSec = 1.8f,
            bulletDamage = 28f
        ),
        EnemyDefinition(
            id = "boss_corpse_engine",
            name = "Corpse Engine (Minute 15 Apex)",
            isBoss = true,
            baseHp = 9500f,
            baseSpeed = 100f,
            contactDamage = 60f,
            collisionRadius = 80f,
            xpValue = 250,
            colorHex = 0xFFFF3D00,
            canShoot = true,
            shootCooldownSec = 1.2f,
            bulletDamage = 35f,
            explodesOnDeath = true,
            explosionRadius = 200f
        )
    )

    fun getById(id: String): EnemyDefinition {
        return ENEMIES.find { it.id == id } ?: ENEMIES.first()
    }
}
