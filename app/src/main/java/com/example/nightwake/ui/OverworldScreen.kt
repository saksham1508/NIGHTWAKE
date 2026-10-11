package com.example.nightwake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.example.nightwake.data.MapDefinition
import com.example.nightwake.data.MapRegistry
import com.example.nightwake.data.ProgressionRegistry
import com.example.nightwake.services.ServiceLocator

/**
 * Overworld Grid Map, Chapter Narrative, Energy/Fuel Meter, and Daily Quests.
 */
@Composable
fun OverworldScreen(
    selectedMapId: String,
    onSelectMap: (MapDefinition) -> Unit,
    onBackToSanctuary: () -> Unit,
    modifier: Modifier = Modifier
) {
    var saveState by remember { mutableStateOf(ServiceLocator.save.loadSave()) }
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Sectors, 1: Chapter Lore, 2: Quests & Achievements
    val sections = listOf("Sector Maps", "Narrative Log", "Daily Quests")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D14))
    ) {
        // Header
        Surface(
            color = Color(0xFF10151E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToSanctuary,
                    modifier = Modifier.testTag("overworld_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "SECTOR EXPEDITION RADAR",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Fuel Meter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = null,
                        tint = Color(0xFFFF9100),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${saveState.energyFuel}/${saveState.maxEnergyFuel} Fuel",
                        color = Color(0xFFFF9100),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141923))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            sections.forEachIndexed { idx, title ->
                val isSelected = selectedSection == idx
                Button(
                    onClick = { selectedSection = idx },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFF3D00) else Color.Transparent
                    ),
                    modifier = Modifier.testTag("overworld_tab_$idx")
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else Color(0xFF90A4AE),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Content
        when (selectedSection) {
            0 -> SectorMapsList(
                selectedMapId = selectedMapId,
                onSelectMap = onSelectMap,
                currentFuel = saveState.energyFuel,
                modifier = Modifier.weight(1f)
            )
            1 -> NarrativeLog(modifier = Modifier.weight(1f))
            2 -> QuestsAndAchievementsList(
                save = saveState,
                onClaim = {
                    ServiceLocator.save.saveGame(saveState)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SectorMapsList(
    selectedMapId: String,
    onSelectMap: (MapDefinition) -> Unit,
    currentFuel: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(MapRegistry.MAPS) { mapDef ->
            val isSelected = mapDef.id == selectedMapId
            val canAffordFuel = currentFuel >= mapDef.fuelCost

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131A24), RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFFFF3D00) else Color(0xFF263238),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectMap(mapDef) }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = mapDef.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = mapDef.sector,
                            color = Color(0xFFFF9100),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "Cost: ${mapDef.fuelCost} Fuel",
                        color = if (canAffordFuel) Color(0xFF00E5FF) else Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = mapDef.description,
                    color = Color(0xFFB0BEC5),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Sector Hazard: ${mapDef.hazardDescription}",
                    color = Color(0xFFFF8A80),
                    fontSize = 11.sp
                )

                Text(
                    text = "Weather: ${mapDef.weatherType} | Threat: ${mapDef.baseDifficultyMultiplier}x",
                    color = Color(0xFF90A4AE),
                    fontSize = 11.sp
                )

                if (isSelected) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ">>> TARGET SECTOR MARKED FOR DEPLOYMENT <<<",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun NarrativeLog(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ProgressionRegistry.CHAPTERS) { chapter ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131A24), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF37474F), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = chapter.title,
                    color = Color(0xFFFFD54F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Outpost Objective: ${chapter.targetOutpost}",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = chapter.narrativeIntro,
                    color = Color(0xFFCFD8DC),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reward: +${chapter.rewardScrap} Scrap, +${chapter.rewardNanites} Nanites",
                    color = Color(0xFF81C784),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuestsAndAchievementsList(
    save: com.example.nightwake.services.PlayerSaveData,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quests = remember { ProgressionRegistry.getDefaultQuests() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "DAILY RECON MISSIONS",
                color = Color(0xFFFF9100),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(quests) { quest ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131A24), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = quest.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = quest.requirement, color = Color(0xFF90A4AE), fontSize = 11.sp)
                    Text(text = "Reward: +${quest.rewardScrap} Scrap, +${quest.rewardMetal} Metal", color = Color(0xFFFFD54F), fontSize = 11.sp)
                }
                Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ACHIEVEMENTS",
                color = Color(0xFF00E5FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(ProgressionRegistry.ACHIEVEMENTS) { ach ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131A24), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = ach.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = ach.description, color = Color(0xFF90A4AE), fontSize = 11.sp)
                }
                Text(text = "+${ach.rewardNanites} 💎", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
