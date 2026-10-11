package com.example.nightwake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
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
import com.example.nightwake.data.UpgradeDefinition
import com.example.nightwake.data.UpgradeType

/**
 * 3-Choice Level-Up Upgrade Selection Dialog.
 */
@Composable
fun LevelUpDialog(
    playerLevel: Int,
    choices: List<UpgradeDefinition>,
    onSelect: (UpgradeDefinition) -> Unit
) {
    Dialog(onDismissRequest = { /* Must choose an upgrade */ }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F141C),
            tonalElevation = 12.dp,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LEVEL UP! [LEVEL $playerLevel]",
                    color = Color(0xFFFFD54F),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Choose one reinforcement to adapt your arsenal:",
                    color = Color(0xFF90A4AE),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                choices.forEachIndexed { index, upgrade ->
                    UpgradeCard(
                        upgrade = upgrade,
                        onClick = { onSelect(upgrade) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("upgrade_choice_$index")
                    )
                }
            }
        }
    }
}

@Composable
fun UpgradeCard(
    upgrade: UpgradeDefinition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when (upgrade.type) {
        UpgradeType.WEAPON_UNLOCK -> Color(0xFFFF9100)
        UpgradeType.EVOLUTION_SYN -> Color(0xFFD500F9)
        else -> Color(0xFF00E5FF)
    }

    val typeLabel = when (upgrade.type) {
        UpgradeType.WEAPON_UNLOCK -> "NEW WEAPON"
        UpgradeType.EVOLUTION_SYN -> "EVOLUTION"
        else -> "TACTICAL PASSIVE"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color(0xFF19222E), RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        // Icon
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = borderColor.copy(alpha = 0.2f),
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = when (upgrade.type) {
                    UpgradeType.WEAPON_UNLOCK -> Icons.Default.ElectricBolt
                    UpgradeType.EVOLUTION_SYN -> Icons.Default.AutoAwesome
                    else -> Icons.Default.Shield
                },
                contentDescription = upgrade.name,
                tint = borderColor,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = typeLabel,
                color = borderColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = upgrade.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = upgrade.description,
                color = Color(0xFFB0BEC5),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
