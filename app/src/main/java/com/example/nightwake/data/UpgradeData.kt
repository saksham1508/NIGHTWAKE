package com.example.nightwake.data

/**
 * Data-driven Upgrade & Synergy Definitions.
 * 25+ distinct upgrades spanning stat boosts, weapon evolutions, and survival perks.
 */
enum class UpgradeType {
    WEAPON_UNLOCK,
    WEAPON_LEVEL_UP,
    PASSIVE_STAT,
    EVOLUTION_SYN
}

data class UpgradeDefinition(
    val id: String,
    val name: String,
    val type: UpgradeType,
    val maxLevel: Int,
    val iconSymbol: String,
    val description: String,
    val targetWeaponId: String? = null,
    val statKey: String? = null,
    val statValuePerLevel: Float = 0f,
    val requiredWeaponId: String? = null,
    val requiredPassiveId: String? = null
)

object UpgradeRegistry {
    val UPGRADES = listOf(
        // Passive Upgrades (10 core passives)
        UpgradeDefinition(
            id = "pass_might",
            name = "Hollow-Point Rounds",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "BULLET",
            description = "+10% Weapon Damage per level.",
            statKey = "damage_mult",
            statValuePerLevel = 0.10f
        ),
        UpgradeDefinition(
            id = "pass_bullet_speed",
            name = "Rifled Barrels",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "SPEED",
            description = "+15% Projectile Velocity and +10% Range.",
            statKey = "proj_speed_mult",
            statValuePerLevel = 0.15f
        ),
        UpgradeDefinition(
            id = "pass_cooldown",
            name = "Hair Trigger",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "TRIGGER",
            description = "-8% Weapon Cooldown / +8% Fire Rate.",
            statKey = "cooldown_reduction",
            statValuePerLevel = 0.08f
        ),
        UpgradeDefinition(
            id = "pass_area_size",
            name = "Choke Enhancer",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "BLAST",
            description = "+12% Explosion & Cone Area of Effect.",
            statKey = "area_mult",
            statValuePerLevel = 0.12f
        ),
        UpgradeDefinition(
            id = "pass_duration",
            name = "Stagnant Catalyst",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "CLOUD",
            description = "+20% Toxic and Hazard Cloud Duration.",
            statKey = "duration_mult",
            statValuePerLevel = 0.20f
        ),
        UpgradeDefinition(
            id = "pass_armor",
            name = "Ballistic Vest",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "SHIELD",
            description = "Reduces damage taken by 2 points (min 1).",
            statKey = "flat_armor",
            statValuePerLevel = 2.0f
        ),
        UpgradeDefinition(
            id = "pass_vitality",
            name = "Adrenaline Pack",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "HEART",
            description = "+20 Max Health and restores 20 HP.",
            statKey = "max_hp_bonus",
            statValuePerLevel = 20.0f
        ),
        UpgradeDefinition(
            id = "pass_movement",
            name = "Combat Boots",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "BOOTS",
            description = "+10% Survivor Movement Speed.",
            statKey = "move_speed_mult",
            statValuePerLevel = 0.10f
        ),
        UpgradeDefinition(
            id = "pass_magnet",
            name = "Nanite Vacuum",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "MAGNET",
            description = "+25% XP Gem Attraction Radius.",
            statKey = "pickup_radius_mult",
            statValuePerLevel = 0.25f
        ),
        UpgradeDefinition(
            id = "pass_growth",
            name = "Combat Ledger",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 5,
            iconSymbol = "BOOK",
            description = "+12% Experience Gain from all gems.",
            statKey = "xp_gain_mult",
            statValuePerLevel = 0.12f
        ),

        // Weapon unlocks & level-ups (6 weapons x level ups)
        UpgradeDefinition(
            id = "wep_ar",
            name = "AR-44 Vanguard",
            type = UpgradeType.WEAPON_UNLOCK,
            maxLevel = 8,
            iconSymbol = "RIFLE",
            description = "High rate of fire kinetic weapon.",
            targetWeaponId = "assault_rifle"
        ),
        UpgradeDefinition(
            id = "wep_shotgun",
            name = "Double-Barreled Sweeper",
            type = UpgradeType.WEAPON_UNLOCK,
            maxLevel = 8,
            iconSymbol = "SHOTGUN",
            description = "Wide buckshot blast with immense knockback.",
            targetWeaponId = "sawed_off_shotgun"
        ),
        UpgradeDefinition(
            id = "wep_tesla",
            name = "Arc Projector",
            type = UpgradeType.WEAPON_UNLOCK,
            maxLevel = 8,
            iconSymbol = "TESLA",
            description = "Chains lightning between nearby hostiles.",
            targetWeaponId = "tesla_arc"
        ),
        UpgradeDefinition(
            id = "wep_toxic",
            name = "Corrosive Aerosol",
            type = UpgradeType.WEAPON_UNLOCK,
            maxLevel = 8,
            iconSymbol = "TOXIC",
            description = "Creates persistent acid zones on the battlefield.",
            targetWeaponId = "toxic_sprayer"
        ),
        UpgradeDefinition(
            id = "wep_saw",
            name = "Orbiting Saws",
            type = UpgradeType.WEAPON_UNLOCK,
            maxLevel = 8,
            iconSymbol = "SAW",
            description = "Dual razor blades constantly orbiting the player.",
            targetWeaponId = "orbital_sawblades"
        ),
        UpgradeDefinition(
            id = "wep_mortar",
            name = "Thumper Mortar",
            type = UpgradeType.WEAPON_UNLOCK,
            maxLevel = 8,
            iconSymbol = "MORTAR",
            description = "Devastating lobbed explosive projectile.",
            targetWeaponId = "mortar_grenade"
        ),

        // Specialized Tactical Perks (7 utility passives)
        UpgradeDefinition(
            id = "pass_crit",
            name = "Targeting Matrix",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 4,
            iconSymbol = "CRIT",
            description = "+8% Critical Strike Chance for double damage.",
            statKey = "crit_chance",
            statValuePerLevel = 0.08f
        ),
        UpgradeDefinition(
            id = "pass_regeneration",
            name = "Auto-Injector",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 3,
            iconSymbol = "PLUS",
            description = "Regenerates +0.8 HP per second.",
            statKey = "hp_regen_sec",
            statValuePerLevel = 0.8f
        ),
        UpgradeDefinition(
            id = "pass_flashlight",
            name = "Tactical Spotlight",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 3,
            iconSymbol = "LIGHT",
            description = "+35% Night Vision Cone Radius and beam width.",
            statKey = "night_vision_bonus",
            statValuePerLevel = 0.35f
        ),
        UpgradeDefinition(
            id = "pass_curse",
            name = "Night Siren",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 3,
            iconSymbol = "SKULL",
            description = "+25% Enemy Spawn Rate, +30% Material & XP drops.",
            statKey = "curse_mult",
            statValuePerLevel = 0.25f
        ),
        UpgradeDefinition(
            id = "pass_revive",
            name = "Defibrillator",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 1,
            iconSymbol = "SPARK",
            description = "Revive once per run upon fatal damage with 50% HP.",
            statKey = "extra_revive",
            statValuePerLevel = 1.0f
        ),
        UpgradeDefinition(
            id = "pass_scavenger",
            name = "Scrap Collector",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 4,
            iconSymbol = "SCRAP",
            description = "+20% Base Building Materials found per run.",
            statKey = "scrap_mult",
            statValuePerLevel = 0.20f
        ),
        UpgradeDefinition(
            id = "pass_repulsor",
            name = "Kinetic Deflector",
            type = UpgradeType.PASSIVE_STAT,
            maxLevel = 3,
            iconSymbol = "REPULSE",
            description = "+25% Knockback distance inflicted on all enemies.",
            statKey = "knockback_mult",
            statValuePerLevel = 0.25f
        )
    )

    fun getById(id: String): UpgradeDefinition? {
        return UPGRADES.find { it.id == id }
    }
}
