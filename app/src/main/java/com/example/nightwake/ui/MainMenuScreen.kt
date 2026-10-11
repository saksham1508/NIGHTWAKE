package com.example.nightwake.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.nightwake.core.GameConfig
import com.example.nightwake.data.CharacterDefinition
import com.example.nightwake.data.CharacterRegistry
import com.example.nightwake.services.ServiceLocator

/**
 * Main Title Screen & Character Loadout Selection.
 */
@Composable
fun MainMenuScreen(
    selectedCharacter: CharacterDefinition,
    onSelectCharacter: (CharacterDefinition) -> Unit,
    onEnterSanctuary: () -> Unit,
    onStartExpedition: () -> Unit,
    onOpenStore: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val characters = remember { CharacterRegistry.CHARACTERS }
    val saveState = remember { ServiceLocator.save.loadSave() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F111A),
                        Color(0xFF080A0F),
                        Color(0xFF040508)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "v${GameConfig.VERSION_NAME}",
                    color = Color(0xFF78909C),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                Row {
                    IconButton(
                        onClick = onOpenStore,
                        modifier = Modifier.testTag("menu_store_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Store",
                            tint = Color(0xFFFF9100)
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("menu_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game Logo / Emblem
            Image(
                painter = painterResource(id = R.drawable.nightwake_icon_1791701855508),
                contentDescription = GameConfig.GAME_TITLE,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFFFF3D00), RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = GameConfig.GAME_TITLE,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "SURVIVE THE NIGHT. FORTIFY AT DAWN.",
                color = Color(0xFFFF9100),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Character Selector Ribbon
            Text(
                text = "SELECT SURVIVOR LOADOUT",
                color = Color(0xFF90A4AE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(characters) { charDef ->
                    val isSelected = charDef.id == selectedCharacter.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFF1E2838) else Color(0xFF111722),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFFFF3D00) else Color(0xFF263238),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectCharacter(charDef) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("select_char_${charDef.id}")
                    ) {
                        Text(
                            text = charDef.name,
                            color = if (isSelected) Color.White else Color(0xFFB0BEC5),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = charDef.title,
                            color = Color(0xFFFF9100),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Character Profile Card
            Surface(
                color = Color(0xFF131A26),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF263238), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${selectedCharacter.name} - ${selectedCharacter.title}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedCharacter.description,
                        color = Color(0xFF90A4AE),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "HP: ${selectedCharacter.baseHp.toInt()} | SPD: ${selectedCharacter.baseSpeed.toInt()}",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Perk: ${selectedCharacter.passiveName}",
                            color = Color(0xFF69F0AE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Button(
                onClick = onStartExpedition,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("menu_expedition_button")
            ) {
                Text(
                    text = "DEPLOY EXPEDITION (RUN) ⚔️",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onEnterSanctuary,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("menu_sanctuary_button")
            ) {
                Text(
                    text = "ENTER SANCTUARY (BASE)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
