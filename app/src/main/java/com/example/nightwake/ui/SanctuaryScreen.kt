package com.example.nightwake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightwake.data.BuildingDefinition
import com.example.nightwake.data.PermanentUpgrade
import com.example.nightwake.data.SanctuaryRegistry
import com.example.nightwake.data.SurvivorDefinition
import com.example.nightwake.services.PlayerSaveData
import com.example.nightwake.services.ServiceLocator

/**
 * Sanctuary Base-Building Meta-Layer.
 * Session-to-session persistence: base structures, permanent upgrades, survivors, and resource management.
 */
@Composable
fun SanctuaryScreen(
    onStartExpedition: () -> Unit,
    onOpenOverworld: () -> Unit,
    onOpenRaidMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var saveState by remember { mutableStateOf(ServiceLocator.save.loadSave()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Structures", "Upgrades", "Survivors", "Crafting")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E14))
    ) {
        // Top Resource Ribbon
        ResourceRibbon(save = saveState)

        // Meta Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF131922),
            contentColor = Color(0xFFFF9100),
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFFFF3D00)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color.White else Color(0xFF90A4AE)
                        )
                    },
                    modifier = Modifier.testTag("sanctuary_tab_$index")
                )
            }
        }

        // Tab Content
        BoxContent(
            selectedTab = selectedTab,
            saveState = saveState,
            onSaveUpdated = { updated ->
                ServiceLocator.save.saveGame(updated)
                saveState = updated
            },
            modifier = Modifier.weight(1f)
        )

        // Bottom Expedition Launch Bar
        BottomActionBar(
            fuel = saveState.energyFuel,
            maxFuel = saveState.maxEnergyFuel,
            onStartExpedition = onStartExpedition,
            onOpenOverworld = onOpenOverworld,
            onOpenRaidMode = onOpenRaidMode
        )
    }
}

@Composable
fun ResourceRibbon(save: PlayerSaveData) {
    Surface(
        color = Color(0xFF10151E),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResourceItem(label = "Scrap", value = save.softCurrencyScrap, color = Color(0xFFFFD54F))
            ResourceItem(label = "Nanites", value = save.premiumCurrencyNanites, color = Color(0xFF00E5FF))
            ResourceItem(label = "Metal", value = save.metal, color = Color(0xFF90CAF9))
            ResourceItem(label = "Wood", value = save.wood, color = Color(0xFFA1887F))
            ResourceItem(label = "Electro", value = save.electronics, color = Color(0xFF69F0AE))
        }
    }
}

@Composable
fun ResourceItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color(0xFF78909C), fontSize = 10.sp)
        Text(text = "$value", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BoxContent(
    selectedTab: Int,
    saveState: PlayerSaveData,
    onSaveUpdated: (PlayerSaveData) -> Unit,
    modifier: Modifier = Modifier
) {
    when (selectedTab) {
        0 -> BuildingsList(saveState = saveState, onSaveUpdated = onSaveUpdated, modifier = modifier)
        1 -> PermanentUpgradesList(saveState = saveState, onSaveUpdated = onSaveUpdated, modifier = modifier)
        2 -> SurvivorsList(modifier = modifier)
        3 -> CraftingStation(saveState = saveState, onSaveUpdated = onSaveUpdated, modifier = modifier)
    }
}

@Composable
fun BuildingsList(
    saveState: PlayerSaveData,
    onSaveUpdated: (PlayerSaveData) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(SanctuaryRegistry.BUILDINGS) { building ->
            BuildingCard(
                building = building,
                currentLevel = saveState.buildingLevels.getOrDefault(building.id, 1),
                save = saveState,
                onUpgrade = {
                    val lvl = saveState.buildingLevels.getOrDefault(building.id, 1)
                    if (lvl < building.maxLevel) {
                        val cost = building.costFormula(lvl)
                        if (saveState.softCurrencyScrap >= cost.scrap &&
                            saveState.metal >= cost.metal &&
                            saveState.wood >= cost.wood &&
                            saveState.electronics >= cost.electronics
                        ) {
                            saveState.softCurrencyScrap -= cost.scrap
                            saveState.metal -= cost.metal
                            saveState.wood -= cost.wood
                            saveState.electronics -= cost.electronics
                            saveState.buildingLevels[building.id] = lvl + 1
                            onSaveUpdated(saveState)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun BuildingCard(
    building: BuildingDefinition,
    currentLevel: Int,
    save: PlayerSaveData,
    onUpgrade: () -> Unit
) {
    val isMax = currentLevel >= building.maxLevel
    val cost = if (!isMax) building.costFormula(currentLevel) else null
    val canAfford = cost != null &&
            save.softCurrencyScrap >= cost.scrap &&
            save.metal >= cost.metal &&
            save.wood >= cost.wood &&
            save.electronics >= cost.electronics

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF141B26), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF263238), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = building.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rank $currentLevel / ${building.maxLevel}",
                    color = Color(0xFFFF9100),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = Color(0xFFFF5722),
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = building.description,
            color = Color(0xFF90A4AE),
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Active Bonus: ${building.perkDescriptionPerLevel(currentLevel)}",
            color = Color(0xFF00E5FF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!isMax && cost != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cost: ${cost.scrap}S | ${cost.metal}M | ${cost.wood}W | ${cost.electronics}E",
                    color = if (canAfford) Color(0xFFFFD54F) else Color(0xFFFF5252),
                    fontSize = 11.sp
                )

                Button(
                    onClick = onUpgrade,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                    modifier = Modifier.testTag("upgrade_${building.id}")
                ) {
                    Text("Upgrade", fontSize = 12.sp)
                }
            }
        } else {
            Text(text = "MAX RANK REACHED", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PermanentUpgradesList(
    saveState: PlayerSaveData,
    onSaveUpdated: (PlayerSaveData) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(SanctuaryRegistry.PERMANENT_UPGRADES) { upgrade ->
            val rank = saveState.permanentUpgrades.getOrDefault(upgrade.id, 0)
            val isMax = rank >= upgrade.maxRank
            val cost = upgrade.costPerRank * (rank + 1)
            val canAfford = !isMax && saveState.softCurrencyScrap >= cost

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141B26), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = upgrade.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${upgrade.description} (Rank $rank/${upgrade.maxRank})",
                        color = Color(0xFF90A4AE),
                        fontSize = 12.sp
                    )
                    if (!isMax) {
                        Text(
                            text = "Cost: $cost Scrap",
                            color = if (canAfford) Color(0xFFFFD54F) else Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        if (canAfford) {
                            saveState.softCurrencyScrap -= cost
                            saveState.permanentUpgrades[upgrade.id] = rank + 1
                            onSaveUpdated(saveState)
                        }
                    },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    modifier = Modifier.testTag("perm_${upgrade.id}")
                ) {
                    Text(if (isMax) "MAX" else "Train", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SurvivorsList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(SanctuaryRegistry.SURVIVORS) { survivor ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141B26), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = Color(0xFF80CBC4),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = survivor.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${survivor.role}",
                        color = Color(0xFFFF9100),
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = survivor.bio,
                    color = Color(0xFF90A4AE),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Sanctuary Passive: ${survivor.passivePerk}",
                    color = Color(0xFF00E676),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun CraftingStation(
    saveState: PlayerSaveData,
    onSaveUpdated: (PlayerSaveData) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = Color(0xFFFF9100),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "MUNITIONS FABRICATION BENCH",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Synthesize weapon attachments and field repair packs using collected metal and scrap.",
            color = Color(0xFF90A4AE),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (saveState.metal >= 50 && saveState.softCurrencyScrap >= 100) {
                    saveState.metal -= 50
                    saveState.softCurrencyScrap -= 100
                    saveState.energyFuel = (saveState.energyFuel + 20).coerceAtMost(saveState.maxEnergyFuel)
                    onSaveUpdated(saveState)
                }
            },
            enabled = saveState.metal >= 50 && saveState.softCurrencyScrap >= 100,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
            modifier = Modifier.fillMaxWidth().testTag("craft_fuel_pack")
        ) {
            Text("Fabricate Fuel Reserve (+20 Fuel) [50 Metal, 100 Scrap]", fontSize = 12.sp)
        }
    }
}

@Composable
fun BottomActionBar(
    fuel: Int,
    maxFuel: Int,
    onStartExpedition: () -> Unit,
    onOpenOverworld: () -> Unit,
    onOpenRaidMode: () -> Unit
) {
    Surface(
        color = Color(0xFF10151E),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onOpenOverworld,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                modifier = Modifier.weight(1f).testTag("sanctuary_overworld_button")
            ) {
                Text("Map & Quests", fontSize = 12.sp)
            }

            Button(
                onClick = onOpenRaidMode,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4527A0)),
                modifier = Modifier.weight(1f).testTag("sanctuary_raid_button")
            ) {
                Text("Base Raid", fontSize = 12.sp)
            }

            Button(
                onClick = onStartExpedition,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                modifier = Modifier.weight(1.3f).testTag("sanctuary_deploy_button")
            ) {
                Text("DEPLOY ⚔️", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
