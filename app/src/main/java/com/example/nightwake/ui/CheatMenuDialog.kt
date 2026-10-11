package com.example.nightwake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nightwake.combat.CombatEngine
import com.example.nightwake.services.ServiceLocator

/**
 * QA & Balance Testing Cheat Menu.
 * Allows stress-testing horde caps, boss spawns, progression, and save systems on device.
 */
@Composable
fun CheatMenuDialog(
    engine: CombatEngine,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF141923),
            tonalElevation = 8.dp,
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "QA CHEAT & BALANCE PANEL",
                    color = Color(0xFFFF9100),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stress tests
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { engine.debugSpawnHorde(50) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                        modifier = Modifier.weight(1f).testTag("cheat_spawn_50")
                    ) {
                        Text("+50 Crawlers", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { engine.debugSpawnHorde(150) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                        modifier = Modifier.weight(1f).testTag("cheat_spawn_150")
                    ) {
                        Text("+150 Stress", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { engine.debugToggleGodMode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (engine.player.godMode) Color(0xFF00E676) else Color(0xFF263238)
                        ),
                        modifier = Modifier.weight(1f).testTag("cheat_god_mode")
                    ) {
                        Text(if (engine.player.godMode) "God: ON" else "God: OFF", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { engine.debugTriggerLevelUp() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        modifier = Modifier.weight(1f).testTag("cheat_level_up")
                    ) {
                        Text("Force Level Up", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { engine.debugSetNight(!engine.isNight) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E35B1)),
                        modifier = Modifier.weight(1f).testTag("cheat_toggle_night")
                    ) {
                        Text(if (engine.isNight) "Force Dawn" else "Force Night", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { engine.debugKillAllEnemies() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.weight(1f).testTag("cheat_kill_all")
                    ) {
                        Text("Kill Screen", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Economy Grant
                Button(
                    onClick = {
                        val save = ServiceLocator.save.loadSave()
                        save.softCurrencyScrap += 1000
                        save.metal += 200
                        save.wood += 200
                        save.electronics += 100
                        save.premiumCurrencyNanites += 50
                        ServiceLocator.save.saveGame(save)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                    modifier = Modifier.fillMaxWidth().testTag("cheat_grant_resources")
                ) {
                    Text("+1000 Scrap & Building Mats", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("cheat_close_button")
                ) {
                    Text("Close QA Panel", color = Color.White)
                }
            }
        }
    }
}
