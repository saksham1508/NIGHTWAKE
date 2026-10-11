package com.example.nightwake.combat

import com.example.nightwake.core.GameConfig
import com.example.nightwake.data.CharacterDefinition
import com.example.nightwake.data.CharacterRegistry
import com.example.nightwake.data.WeaponDefinition
import com.example.nightwake.data.WeaponRegistry
import kotlin.math.sqrt

/**
 * Combat Entity Structures optimized for zero-allocation pooling.
 */

class PlayerState {
    var posX: Float = 0f
    var posY: Float = 0f
    var velX: Float = 0f
    var velY: Float = 0f
    var aimAngleRad: Float = 0f

    var hp: Float = GameConfig.PLAYER_BASE_MAX_HP
    var maxHp: Float = GameConfig.PLAYER_BASE_MAX_HP
    var baseSpeed: Float = GameConfig.PLAYER_BASE_SPEED

    var level: Int = 1
    var currentXp: Int = 0
    var xpToNextLevel: Int = 15

    var kills: Int = 0
    var isAlive: Boolean = true
    var godMode: Boolean = false

    // Stat multipliers computed from passives + permanent upgrades
    var damageMult: Float = 1.0f
    var cooldownReduction: Float = 0.0f
    var projSpeedMult: Float = 1.0f
    var areaMult: Float = 1.0f
    var durationMult: Float = 1.0f
    var armorFlat: Float = 0.0f
    var pickupRadius: Float = GameConfig.PLAYER_PICKUP_RADIUS
    var xpGainMult: Float = 1.0f
    var hpRegenSec: Float = 0.0f
    var hasRevive: Boolean = false
    var reviveUsed: Boolean = false

    // Active Weapons & Level maps
    val activeWeapons = mutableListOf<ActiveWeaponSlot>()
    val activePassives = mutableMapOf<String, Int>()

    fun reset(character: CharacterDefinition) {
        posX = 0f
        posY = 0f
        velX = 0f
        velY = 0f
        aimAngleRad = 0f
        maxHp = character.baseHp
        hp = maxHp
        baseSpeed = character.baseSpeed
        level = 1
        currentXp = 0
        xpToNextLevel = 15
        kills = 0
        isAlive = true
        godMode = false
        damageMult = character.damageModifier
        cooldownReduction = character.cooldownReductionModifier
        projSpeedMult = 1.0f
        areaMult = 1.0f
        durationMult = 1.0f
        armorFlat = 0.0f
        pickupRadius = GameConfig.PLAYER_PICKUP_RADIUS * character.pickupRadiusModifier
        xpGainMult = 1.0f
        hpRegenSec = if (character.id == "medic") 0.4f else 0.0f
        hasRevive = false
        reviveUsed = false

        activeWeapons.clear()
        activePassives.clear()

        // Equip starting weapon
        val startingWepDef = WeaponRegistry.getById(character.startingWeaponId)
        activeWeapons.add(ActiveWeaponSlot(startingWepDef, level = 1))
    }
}

class ActiveWeaponSlot(
    var definition: WeaponDefinition,
    var level: Int = 1,
    var cooldownTimer: Float = 0f,
    var orbitAngleDeg: Float = 0f
)

class EnemyEntity {
    var active: Boolean = false
    var id: String = ""
    var posX: Float = 0f
    var posY: Float = 0f
    var hp: Float = 10f
    var maxHp: Float = 10f
    var speed: Float = 100f
    var contactDamage: Float = 10f
    var collisionRadius: Float = 20f
    var xpValue: Int = 1
    var colorHex: Long = 0xFFFF5252
    var isBoss: Boolean = false
    var canShoot: Boolean = false
    var shootCooldownTimer: Float = 0f
    var shootInterval: Float = 2.5f
    var bulletDamage: Float = 10f
    var explodesOnDeath: Boolean = false
    var explosionRadius: Float = 0f
    var hitFlashTimer: Float = 0f
}

class ProjectileEntity {
    var active: Boolean = false
    var posX: Float = 0f
    var posY: Float = 0f
    var velX: Float = 0f
    var velY: Float = 0f
    var damage: Float = 10f
    var radius: Float = 8f
    var rangeRemaining: Float = 500f
    var pierceRemaining: Int = 1
    var colorHex: Long = 0xFFFFD700
    var isHostile: Boolean = false
    var isAoe: Boolean = false
    var aoeRadius: Float = 0f
    var isHazardPool: Boolean = false
    var poolDurationTimer: Float = 0f
}

class GemEntity {
    var active: Boolean = false
    var posX: Float = 0f
    var posY: Float = 0f
    var xpValue: Int = 1
    var colorHex: Long = 0xFF00E5FF
    var isMagnetized: Boolean = false
}

class DamageTextEntity {
    var active: Boolean = false
    var posX: Float = 0f
    var posY: Float = 0f
    var damage: Int = 0
    var isCrit: Boolean = false
    var lifeRemaining: Float = 0f
}
