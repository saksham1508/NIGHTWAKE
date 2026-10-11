package com.example.nightwake.data

/**
 * 4 Playable Maps with distinct hazards, weather, and difficulty modifiers.
 */
data class MapDefinition(
    val id: String,
    val name: String,
    val sector: String,
    val description: String,
    val hazardDescription: String,
    val baseDifficultyMultiplier: Float,
    val weatherType: String,
    val groundColorHex: Long,
    val gridColorHex: Long,
    val ambientDarknessLevel: Float, // 0.0f = bright, 1.0f = pitch black night
    val fuelCost: Int = 10
)

object MapRegistry {
    val MAPS = listOf(
        MapDefinition(
            id = "map_outpost",
            name = "Abandoned Outpost",
            sector = "Sector 01 - Dead Zone",
            description = "Fortified perimeter overrun on Day 1. Standard military grid with sparse barricades.",
            hazardDescription = "None (Standard training zone)",
            baseDifficultyMultiplier = 1.0f,
            weatherType = "Clear Night",
            groundColorHex = 0xFF14191F,
            gridColorHex = 0xFF212B36,
            ambientDarknessLevel = 0.70f,
            fuelCost = 10
        ),
        MapDefinition(
            id = "map_marsh",
            name = "Toxic Marsh",
            sector = "Sector 02 - Chemical Run",
            description = "Industrial drainage swamp infested with bloated swarms and noxious acid geysers.",
            hazardDescription = "Acid bogs deal 5 DPS if standing still; noxious mist reduces view distance by 20%.",
            baseDifficultyMultiplier = 1.25f,
            weatherType = "Toxic Smog",
            groundColorHex = 0xFF111E16,
            gridColorHex = 0xFF1B3322,
            ambientDarknessLevel = 0.85f,
            fuelCost = 15
        ),
        MapDefinition(
            id = "map_metro",
            name = "Sunken Metro",
            sector = "Sector 03 - Sub-Level 4",
            description = "Underground railway terminal with abandoned train cars forming tight choke points.",
            hazardDescription = "Restricted maneuvering lanes and electrical arcing hazards.",
            baseDifficultyMultiplier = 1.50f,
            weatherType = "Tunnel Blackout",
            groundColorHex = 0xFF131118,
            gridColorHex = 0xFF241D30,
            ambientDarknessLevel = 0.92f,
            fuelCost = 20
        ),
        MapDefinition(
            id = "map_highway",
            name = "Scorched Highway",
            sector = "Sector 04 - The Caldera",
            description = "Multi-lane interstate buried beneath burning volcanic ash and high-velocity swarms.",
            hazardDescription = "Periodic fiery ash storms increase enemy speed by +30% for 15 seconds.",
            baseDifficultyMultiplier = 1.85f,
            weatherType = "Ash Storm",
            groundColorHex = 0xFF1F1412,
            gridColorHex = 0xFF36201B,
            ambientDarknessLevel = 0.80f,
            fuelCost = 25
        )
    )

    fun getById(id: String): MapDefinition {
        return MAPS.find { it.id == id } ?: MAPS.first()
    }
}
