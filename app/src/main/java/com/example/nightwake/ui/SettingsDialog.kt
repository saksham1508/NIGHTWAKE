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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import com.example.nightwake.core.AudioVfxSlots
import com.example.nightwake.core.GameConfig
import com.example.nightwake.services.ServiceLocator

/**
 * Game Settings & Audio/Haptics/Performance Configuration.
 */
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit
) {
    var hapticsOn by remember { mutableStateOf(AudioVfxSlots.isHapticsEnabled) }
    var soundOn by remember { mutableStateOf(AudioVfxSlots.isSoundEnabled) }
    var musicOn by remember { mutableStateOf(AudioVfxSlots.isMusicEnabled) }
    var resetPrompt by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF131922),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SETTINGS & PERFORMANCE",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Haptics Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Vibration & Haptics", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = hapticsOn,
                        onCheckedChange = {
                            hapticsOn = it
                            AudioVfxSlots.isHapticsEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF3D00)),
                        modifier = Modifier.testTag("toggle_haptics")
                    )
                }

                // Sound SFX Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sound Effects (SFX)", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = soundOn,
                        onCheckedChange = {
                            soundOn = it
                            AudioVfxSlots.isSoundEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF3D00)),
                        modifier = Modifier.testTag("toggle_sfx")
                    )
                }

                // Music Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Atmospheric Music", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = musicOn,
                        onCheckedChange = {
                            musicOn = it
                            AudioVfxSlots.isMusicEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF3D00)),
                        modifier = Modifier.testTag("toggle_music")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3D Quality Tier Selector
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "3D Graphics Preset: ${com.example.nightwake.core.QualityManager.current.level.displayName}",
                        color = Color(0xFFFF9100),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        com.example.nightwake.core.QualityLevel.entries.forEach { tier ->
                            val isSelected = com.example.nightwake.core.QualityManager.current.level == tier
                            Button(
                                onClick = { com.example.nightwake.core.QualityManager.applyTier(tier) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFFFF3D00) else Color(0xFF263238)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quality_tier_${tier.name.lowercase()}")
                            ) {
                                Text(
                                    text = tier.name.take(3),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${GameConfig.GAME_TITLE} v${GameConfig.VERSION_NAME} (Build ${GameConfig.VERSION_CODE})",
                    color = Color(0xFF90A4AE),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!resetPrompt) {
                    Button(
                        onClick = { resetPrompt = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.fillMaxWidth().testTag("reset_profile_button")
                    ) {
                        Text("Reset Save Profile", fontSize = 12.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                ServiceLocator.save.resetSave()
                                resetPrompt = false
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier.weight(1f).testTag("confirm_reset_button")
                        ) {
                            Text("Confirm Reset", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { resetPrompt = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("close_settings_button")
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}
