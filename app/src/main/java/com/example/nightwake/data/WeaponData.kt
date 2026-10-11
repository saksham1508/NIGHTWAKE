package com.example.nightwake.data

/**
 * Data-driven Weapon Definitions and Evolution Trees.
 */
data class WeaponDefinition(
    val id: String,
    val name: String,
    val description: String,
    val baseDamage: Float,
    val fireRateSec: Float,
    val projectileSpeed: Float,
    val projectileCount: Int,
    val projectileRange: Float,
    val spreadAngleDeg: Float,
    val pierceCount: Int,
    val areaRadius: Float,
    val colorHex: Long,
    val evolvedWeaponId: String? = null,
    val requiredPassiveIdForEvolution: String? = null
)

object WeaponRegistry {
    val WEAPONS = listOf(
        WeaponDefinition(
            id = "assault_rifle",
            name = "AR-44 Vanguard",
            description = "Rapid kinetic rifle with targeted semi-automatic bursts.",
            baseDamage = 22f,
            fireRateSec = 0.32f,
            projectileSpeed = 650f,
            projectileCount = 1,
            projectileRange = 600f,
            spreadAngleDeg = 6f,
            pierceCount = 1,
            areaRadius = 0f,
            colorHex = 0xFFFFC107,
            evolvedWeaponId = "vulcan_cannon",
            requiredPassiveIdForEvolution = "pass_bullet_speed"
        ),
        WeaponDefinition(
            id = "sawed_off_shotgun",
            name = "Double-Barreled Sweeper",
            description = "Fires a wide cone of high-impact lead buckshot.",
            baseDamage = 16f,
            fireRateSec = 0.85f,
            projectileSpeed = 500f,
            projectileCount = 6,
            projectileRange = 380f,
            spreadAngleDeg = 35f,
            pierceCount = 2,
            areaRadius = 0f,
            colorHex = 0xFFFF5722,
            evolvedWeaponId = "trench_flak",
            requiredPassiveIdForEvolution = "pass_area_size"
        ),
        WeaponDefinition(
            id = "tesla_arc",
            name = "Arc Projector",
            description = "Discharges high-voltage bolts jumping between 3 nearby enemies.",
            baseDamage = 28f,
            fireRateSec = 0.95f,
            projectileSpeed = 800f,
            projectileCount = 1,
            projectileRange = 450f,
            spreadAngleDeg = 0f,
            pierceCount = 3,
            areaRadius = 80f,
            colorHex = 0xFF00E5FF,
            evolvedWeaponId = "thunderstorm_coil",
            requiredPassiveIdForEvolution = "pass_cooldown"
        ),
        WeaponDefinition(
            id = "toxic_sprayer",
            name = "Corrosive Aerosol",
            description = "Sprays persistent toxic puddles damaging all enemies passing through.",
            baseDamage = 8f,
            fireRateSec = 0.45f,
            projectileSpeed = 320f,
            projectileCount = 2,
            projectileRange = 320f,
            spreadAngleDeg = 25f,
            pierceCount = 999,
            areaRadius = 75f,
            colorHex = 0xFF76FF03,
            evolvedWeaponId = "plague_mist",
            requiredPassiveIdForEvolution = "pass_duration"
        ),
        WeaponDefinition(
            id = "orbital_sawblades",
            name = "Orbiting Saws",
            description = "Dual mechanical razor blades constantly circling the survivor.",
            baseDamage = 18f,
            fireRateSec = 0.05f,
            projectileSpeed = 180f, // Rotation speed in deg/sec
            projectileCount = 2,
            projectileRange = 120f, // Orbit distance
            spreadAngleDeg = 180f,
            pierceCount = 999,
            areaRadius = 30f,
            colorHex = 0xFFE040FB,
            evolvedWeaponId = "plasma_buzzsaw",
            requiredPassiveIdForEvolution = "pass_armor"
        ),
        WeaponDefinition(
            id = "mortar_grenade",
            name = "Thumper Mortar",
            description = "Lobs heavy explosive canisters detonating with catastrophic blast radius.",
            baseDamage = 65f,
            fireRateSec = 1.6f,
            projectileSpeed = 420f,
            projectileCount = 1,
            projectileRange = 550f,
            spreadAngleDeg = 0f,
            pierceCount = 1,
            areaRadius = 140f,
            colorHex = 0xFFFF1744,
            evolvedWeaponId = "cluster_nuke",
            requiredPassiveIdForEvolution = "pass_might"
        ),
        // Evolutions
        WeaponDefinition(
            id = "vulcan_cannon",
            name = "Vulcan Overdrive",
            description = "[EVOLVED] Continuous rotary kinetic storm piercing all targets.",
            baseDamage = 35f,
            fireRateSec = 0.12f,
            projectileSpeed = 800f,
            projectileCount = 2,
            projectileRange = 750f,
            spreadAngleDeg = 8f,
            pierceCount = 3,
            areaRadius = 0f,
            colorHex = 0xFFFFD700
        ),
        WeaponDefinition(
            id = "trench_flak",
            name = "Apex Flak Cannon",
            description = "[EVOLVED] 10 exploding flak rounds that shred armor.",
            baseDamage = 26f,
            fireRateSec = 0.65f,
            projectileSpeed = 600f,
            projectileCount = 10,
            projectileRange = 450f,
            spreadAngleDeg = 45f,
            pierceCount = 4,
            areaRadius = 40f,
            colorHex = 0xFFFF6E40
        ),
        WeaponDefinition(
            id = "thunderstorm_coil",
            name = "Cataclysm Coil",
            description = "[EVOLVED] Chain lightning strikes 6 enemies and summons thunderbolts.",
            baseDamage = 50f,
            fireRateSec = 0.70f,
            projectileSpeed = 950f,
            projectileCount = 2,
            projectileRange = 550f,
            spreadAngleDeg = 0f,
            pierceCount = 6,
            areaRadius = 110f,
            colorHex = 0xFF18FFFF
        ),
        WeaponDefinition(
            id = "plague_mist",
            name = "Biohazard Pandemic",
            description = "[EVOLVED] Pervasive necrotic fog that inflicts slowing acid.",
            baseDamage = 16f,
            fireRateSec = 0.30f,
            projectileSpeed = 350f,
            projectileCount = 3,
            projectileRange = 400f,
            spreadAngleDeg = 360f,
            pierceCount = 999,
            areaRadius = 120f,
            colorHex = 0xFF64DD17
        ),
        WeaponDefinition(
            id = "plasma_buzzsaw",
            name = "Singularity Saws",
            description = "[EVOLVED] 4 orbital plasma discs creating a continuous kill zone.",
            baseDamage = 32f,
            fireRateSec = 0.05f,
            projectileSpeed = 240f,
            projectileCount = 4,
            projectileRange = 150f,
            spreadAngleDeg = 90f,
            pierceCount = 999,
            areaRadius = 40f,
            colorHex = 0xFFD500F9
        ),
        WeaponDefinition(
            id = "cluster_nuke",
            name = "Thermobaric Obliterator",
            description = "[EVOLVED] Detonation releases secondary cluster bomblets.",
            baseDamage = 120f,
            fireRateSec = 1.2f,
            projectileSpeed = 480f,
            projectileCount = 1,
            projectileRange = 600f,
            spreadAngleDeg = 0f,
            pierceCount = 1,
            areaRadius = 220f,
            colorHex = 0xFFFF3D00
        )
    )

    fun getById(id: String): WeaponDefinition {
        return WEAPONS.find { it.id == id } ?: WEAPONS.first()
    }
}
