package com.example.nightwake.data

/**
 * Sanctuary Base-Building Meta-Layer Data.
 * Materials: Metal, Wood, Electronics, Scrap.
 * Base structures with multi-tier upgrade trees and survivor bonuses.
 */
data class BuildingDefinition(
    val id: String,
    val name: String,
    val description: String,
    val maxLevel: Int,
    val iconName: String,
    val costFormula: (level: Int) -> BuildingCost,
    val perkDescriptionPerLevel: (level: Int) -> String
)

data class BuildingCost(
    val scrap: Int,
    val metal: Int,
    val wood: Int,
    val electronics: Int
)

data class SurvivorDefinition(
    val id: String,
    val name: String,
    val role: String,
    val bio: String,
    val passivePerk: String,
    val requiredRescues: Int = 1
)

data class PermanentUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val maxRank: Int,
    val costPerRank: Int, // in Scrap
    val statKey: String,
    val bonusPerRank: Float
)

object SanctuaryRegistry {
    val BUILDINGS = listOf(
        BuildingDefinition(
            id = "command_post",
            name = "Command Bunker",
            description = "Central operations hub. Coordinates expeditions and survivor assignments.",
            maxLevel = 5,
            iconName = "BUNKER",
            costFormula = { lvl ->
                BuildingCost(
                    scrap = 100 * lvl,
                    metal = 60 * lvl,
                    wood = 80 * lvl,
                    electronics = 30 * lvl
                )
            },
            perkDescriptionPerLevel = { lvl ->
                "+${lvl * 15} Max Expedition Fuel; unlocks Sector 0${lvl + 1} operations."
            }
        ),
        BuildingDefinition(
            id = "workshop",
            name = "Munitions Workshop",
            description = "Machining benches for blueprint fabrication and weapon calibrations.",
            maxLevel = 5,
            iconName = "WORKSHOP",
            costFormula = { lvl ->
                BuildingCost(
                    scrap = 80 * lvl,
                    metal = 90 * lvl,
                    wood = 50 * lvl,
                    electronics = 50 * lvl
                )
            },
            perkDescriptionPerLevel = { lvl ->
                "+${lvl * 5}% Weapon Base Damage across all expeditions."
            }
        ),
        BuildingDefinition(
            id = "armory",
            name = "Reinforced Armory",
            description = "Storage depot for ballistic vests, auto-injectors, and heavy plating.",
            maxLevel = 5,
            iconName = "ARMORY",
            costFormula = { lvl ->
                BuildingCost(
                    scrap = 120 * lvl,
                    metal = 110 * lvl,
                    wood = 40 * lvl,
                    electronics = 20 * lvl
                )
            },
            perkDescriptionPerLevel = { lvl ->
                "+${lvl * 10} Starting Survivor Max HP; +${lvl * 2} flat damage mitigation."
            }
        ),
        BuildingDefinition(
            id = "radar",
            name = "Radar Array",
            description = "Long-range sensor dish tracking infected herd migrations.",
            maxLevel = 5,
            iconName = "RADAR",
            costFormula = { lvl ->
                BuildingCost(
                    scrap = 70 * lvl,
                    metal = 50 * lvl,
                    wood = 30 * lvl,
                    electronics = 80 * lvl
                )
            },
            perkDescriptionPerLevel = { lvl ->
                "+${lvl * 10}% Material yield on outpost salvage runs."
            }
        ),
        BuildingDefinition(
            id = "barricade",
            name = "Perimeter Palisade",
            description = "Steel-reinforced outer perimeter defense for midnight horde raids.",
            maxLevel = 5,
            iconName = "WALL",
            costFormula = { lvl ->
                BuildingCost(
                    scrap = 150 * lvl,
                    metal = 120 * lvl,
                    wood = 140 * lvl,
                    electronics = 20 * lvl
                )
            },
            perkDescriptionPerLevel = { lvl ->
                "+${lvl * 500} Sanctuary Defense Core HP during Base Raid assaults."
            }
        )
    )

    val SURVIVORS = listOf(
        SurvivorDefinition(
            id = "surv_jack",
            name = "Jack 'Wrench' Miller",
            role = "Master Machinist",
            bio = "Veteran diesel mechanic found holding out in an engine bay.",
            passivePerk = "+10% Faster Fire Rate on Ballistic Weapons"
        ),
        SurvivorDefinition(
            id = "surv_clara",
            name = "Dr. Clara Voss",
            role = "Bio-Chemist",
            bio = "Researcher synthesizing corrosive anti-viral aerosols.",
            passivePerk = "+15% Chemical & Toxic Hazard Duration"
        ),
        SurvivorDefinition(
            id = "surv_tariq",
            name = "Tariq Al-Mansoor",
            role = "Combat Quartermaster",
            bio = "Ex-logistics officer skilled in supply scavenging.",
            passivePerk = "+20% Soft Currency (Scrap) find rate"
        ),
        SurvivorDefinition(
            id = "surv_maya",
            name = "Maya Lin",
            role = "Scout Sniper",
            bio = "Survivalist archer with keen long-range vision.",
            passivePerk = "+25% Night Flashlight illumination cone"
        )
    )

    val PERMANENT_UPGRADES = listOf(
        PermanentUpgrade(
            id = "perm_max_hp",
            name = "Fortified Physique",
            description = "+10 Max Health per rank.",
            maxRank = 10,
            costPerRank = 150,
            statKey = "hp",
            bonusPerRank = 10f
        ),
        PermanentUpgrade(
            id = "perm_damage",
            name = "Combat Rig Tuning",
            description = "+4% Total Damage per rank.",
            maxRank = 10,
            costPerRank = 200,
            statKey = "dmg",
            bonusPerRank = 0.04f
        ),
        PermanentUpgrade(
            id = "perm_speed",
            name = "Lightweight Exoskeleton",
            description = "+3% Movement Speed per rank.",
            maxRank = 10,
            costPerRank = 120,
            statKey = "spd",
            bonusPerRank = 0.03f
        ),
        PermanentUpgrade(
            id = "perm_pickup",
            name = "Electromagnetic Coil",
            description = "+8% Gem Pickup Radius per rank.",
            maxRank = 10,
            costPerRank = 100,
            statKey = "pick",
            bonusPerRank = 0.08f
        ),
        PermanentUpgrade(
            id = "perm_xp_gain",
            name = "Neural Synthesis",
            description = "+5% XP Acquisition per rank.",
            maxRank = 10,
            costPerRank = 250,
            statKey = "xp",
            bonusPerRank = 0.05f
        )
    )
}
