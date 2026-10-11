package com.example.nightwake.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.nightwake.data.CharacterDefinition
import com.example.nightwake.data.CharacterRegistry
import com.example.nightwake.data.MapDefinition
import com.example.nightwake.data.MapRegistry

enum class ScreenState {
    MAIN_MENU,
    SANCTUARY,
    COMBAT,
    OVERWORLD,
    RAID,
    STORE
}

/**
 * Root Navigation and Screen Controller for NIGHTWAKE.
 */
@Composable
fun NightwakeApp(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(ScreenState.MAIN_MENU) }
    var selectedCharacter by remember { mutableStateOf(CharacterRegistry.CHARACTERS.first()) }
    var selectedMap by remember { mutableStateOf(MapRegistry.MAPS.first()) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    when (currentScreen) {
        ScreenState.MAIN_MENU -> {
            MainMenuScreen(
                selectedCharacter = selectedCharacter,
                onSelectCharacter = { selectedCharacter = it },
                onEnterSanctuary = { currentScreen = ScreenState.SANCTUARY },
                onStartExpedition = { currentScreen = ScreenState.COMBAT },
                onOpenStore = { currentScreen = ScreenState.STORE },
                onOpenSettings = { showSettingsDialog = true },
                modifier = modifier
            )
        }

        ScreenState.SANCTUARY -> {
            BackHandler {
                currentScreen = ScreenState.MAIN_MENU
            }
            SanctuaryScreen(
                onStartExpedition = { currentScreen = ScreenState.COMBAT },
                onOpenOverworld = { currentScreen = ScreenState.OVERWORLD },
                onOpenRaidMode = { currentScreen = ScreenState.RAID },
                modifier = modifier
            )
        }

        ScreenState.COMBAT -> {
            BackHandler {
                currentScreen = ScreenState.SANCTUARY
            }
            CombatScreen(
                character = selectedCharacter,
                map = selectedMap,
                onExitToSanctuary = { currentScreen = ScreenState.SANCTUARY },
                modifier = modifier
            )
        }

        ScreenState.OVERWORLD -> {
            BackHandler {
                currentScreen = ScreenState.SANCTUARY
            }
            OverworldScreen(
                selectedMapId = selectedMap.id,
                onSelectMap = { selectedMap = it },
                onBackToSanctuary = { currentScreen = ScreenState.SANCTUARY },
                modifier = modifier
            )
        }

        ScreenState.RAID -> {
            BackHandler {
                currentScreen = ScreenState.SANCTUARY
            }
            RaidScreen(
                onExit = { currentScreen = ScreenState.SANCTUARY },
                modifier = modifier
            )
        }

        ScreenState.STORE -> {
            BackHandler {
                currentScreen = ScreenState.MAIN_MENU
            }
            StoreScreen(
                onBack = { currentScreen = ScreenState.MAIN_MENU },
                modifier = modifier
            )
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(onDismiss = { showSettingsDialog = false })
    }
}
