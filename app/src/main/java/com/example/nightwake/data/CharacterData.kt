package com.example.nightwake.data

/**
 * Playable Character Definitions (Data-driven).
 * 4 Distinct Characters with unique base stats, starting weapons, and passive perks.
 */
data class CharacterDefinition(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val baseHp: Float,
    val baseSpeed: Float,
    val startingWeaponId: String,
    val passiveName: String,
    val passiveDescription: String,
    val damageModifier: Float = 1.0f,
    val pickupRadiusModifier: Float = 1.0f,
    val cooldownReductionModifier: Float = 0.0f
)

object CharacterRegistry {
    val CHARACTERS = listOf(
        CharacterDefinition(
            id = "vanguard",
            name = "Sarah Miller",
            title = "Dawn Vanguard",
            description = "Former garrison commander trained in heavy ballistic skirmishes.",
            baseHp = 120f,
            baseSpeed = 280f,
            startingWeaponId = "assault_rifle",
            passiveName = "Iron Will",
            passiveDescription = "+20% Max HP and +10% Projectile Damage.",
            damageModifier = 1.10f
        ),
        CharacterDefinition(
            id = "ghost",
            name = "Kaelen Chen",
            title = "Scavenger Ghost",
            description = "High-speed scout navigating infected zones with light weaponry.",
            baseHp = 85f,
            baseSpeed = 340f,
            startingWeaponId = "sawed_off_shotgun",
            passiveName = "Agile Footing",
            passiveDescription = "+20% Movement Speed and +30% Pickup Radius.",
            pickupRadiusModifier = 1.30f
        ),
        CharacterDefinition(
            id = "engineer",
            name = "Dr. Elena Rostova",
            title = "Pyro Engineer",
            description = "Technician wielding energy arcs and area-denial devices.",
            baseHp = 100f,
            baseSpeed = 270f,
            startingWeaponId = "tesla_arc",
            passiveName = "Overcharge",
            passiveDescription = "+15% Cooldown Reduction and +10% Area of Effect.",
            cooldownReductionModifier = 0.15f
        ),
        CharacterDefinition(
            id = "medic",
            name = "Marcus Vance",
            title = "Trench Medic",
            description = "Battle-hardened field doctor with toxic bio-weapons and regenerative stamina.",
            baseHp = 110f,
            baseSpeed = 290f,
            startingWeaponId = "toxic_sprayer",
            passiveName = "Cellular Regeneration",
            passiveDescription = "Regenerates 1 HP every 3 seconds; +15% XP Gem Attraction.",
            pickupRadiusModifier = 1.15f
        )
    )

    fun getById(id: String): CharacterDefinition {
        return CHARACTERS.find { it.id == id } ?: CHARACTERS.first()
    }
}
