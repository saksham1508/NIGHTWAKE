package com.example.nightwake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightwake.services.ServiceLocator
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class PlacedStructure(
    val id: String,
    val name: String,
    var x: Float,
    var y: Float,
    var hp: Float,
    val maxHp: Float,
    val damage: Float,
    val range: Float,
    val colorHex: Long,
    var cooldown: Float = 0f
)

data class RaidEnemy(
    var x: Float,
    var y: Float,
    var hp: Float,
    val maxHp: Float,
    val speed: Float,
    val isBoss: Boolean
)

/**
 * Base-Defense Raid Mode: Real-time defense of Sanctuary Core using placed turrets & barricades.
 */
@Composable
fun RaidScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var coreHp by remember { mutableFloatStateOf(2000f) }
    val maxCoreHp = 2000f
    var waveNumber by remember { mutableIntStateOf(1) }
    var scrapBudget by remember { mutableIntStateOf(300) }
    var selectedStructureToPlace by remember { mutableStateOf("turret") }

    val structures = remember {
        mutableStateListOf(
            PlacedStructure("turret", "Auto-Turret", 180f, 650f, 300f, 300f, 25f, 320f, 0xFFFF9100),
            PlacedStructure("barricade", "Steel Barricade", 320f, 520f, 800f, 800f, 0f, 0f, 0xFF78909C),
            PlacedStructure("tesla", "Tesla Pylon", 460f, 650f, 250f, 250f, 40f, 280f, 0xFF00E5FF)
        )
    }

    val raidEnemies = remember { mutableStateListOf<RaidEnemy>() }
    var isGameOver by remember { mutableStateOf(false) }

    // 60 FPS Raid simulation loop
    LaunchedEffect(isGameOver) {
        var lastNano = System.nanoTime()
        var spawnTimer = 0f

        while (!isGameOver) {
            withFrameNanos { nowNano ->
                val dt = ((nowNano - lastNano) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNano = nowNano

                // Spawn raid horde from top
                spawnTimer -= dt
                if (spawnTimer <= 0f && raidEnemies.size < 40) {
                    spawnTimer = (1.5f - waveNumber * 0.1f).coerceAtLeast(0.4f)
                    val isBoss = Random.nextFloat() < (0.05f + waveNumber * 0.02f)
                    raidEnemies.add(
                        RaidEnemy(
                            x = Random.nextFloat() * 600f + 50f,
                            y = 40f,
                            hp = if (isBoss) 800f else 60f,
                            maxHp = if (isBoss) 800f else 60f,
                            speed = if (isBoss) 55f else 95f,
                            isBoss = isBoss
                        )
                    )
                }

                // Update turrets firing
                for (s in structures) {
                    if (s.damage > 0f) {
                        s.cooldown -= dt
                        if (s.cooldown <= 0f) {
                            // Find closest enemy within range
                            val target = raidEnemies.firstOrNull { e ->
                                val dx = e.x - s.x
                                val dy = e.y - s.y
                                (dx * dx + dy * dy) <= s.range * s.range
                            }
                            if (target != null) {
                                s.cooldown = 0.45f
                                target.hp -= s.damage
                                if (target.hp <= 0f) {
                                    scrapBudget += if (target.isBoss) 50 else 10
                                    raidEnemies.remove(target)
                                }
                            }
                        }
                    }
                }

                // Update raid enemies marching down towards sanctuary core
                val coreTargetY = 820f
                val iterator = raidEnemies.iterator()
                while (iterator.hasNext()) {
                    val e = iterator.next()
                    e.y += e.speed * dt

                    // Check hit against structures
                    val hitStruct = structures.firstOrNull { s ->
                        val dx = s.x - e.x
                        val dy = s.y - e.y
                        dx * dx + dy * dy <= 45f * 45f
                    }

                    if (hitStruct != null) {
                        hitStruct.hp -= 20f * dt
                        e.y -= e.speed * dt * 0.9f // Blocked
                        if (hitStruct.hp <= 0f) {
                            structures.remove(hitStruct)
                        }
                    } else if (e.y >= coreTargetY) {
                        // Impact Sanctuary Core
                        coreHp -= if (e.isBoss) 150f else 35f
                        iterator.remove()
                        if (coreHp <= 0f) {
                            isGameOver = true
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0D14))
    ) {
        // Header
        Surface(
            color = Color(0xFF10151E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExit, modifier = Modifier.testTag("raid_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SANCTUARY DEFENSE RAID",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Core HP: ${coreHp.toInt()}/2000 | Supplies: $scrapBudget",
                        color = Color(0xFFFFD54F),
                        fontSize = 12.sp
                    )
                }
            }
        }

        LinearProgressIndicator(
            progress = { (coreHp / maxCoreHp).coerceIn(0f, 1f) },
            color = if (coreHp > 500f) Color(0xFF00E676) else Color(0xFFFF1744),
            trackColor = Color(0xFF263238),
            modifier = Modifier.fillMaxWidth().height(6.dp)
        )

        // Battlefield Canvas
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Tap to place selected defense structure
                        if (scrapBudget >= 75 && structures.size < 8) {
                            scrapBudget -= 75
                            val newStruct = when (selectedStructureToPlace) {
                                "tesla" -> PlacedStructure("tesla", "Tesla Pylon", offset.x, offset.y, 250f, 250f, 40f, 280f, 0xFF00E5FF)
                                "barricade" -> PlacedStructure("barricade", "Barricade", offset.x, offset.y, 800f, 800f, 0f, 0f, 0xFF78909C)
                                else -> PlacedStructure("turret", "Turret", offset.x, offset.y, 300f, 300f, 25f, 320f, 0xFFFF9100)
                            }
                            structures.add(newStruct)
                        }
                    }
                }
                .testTag("raid_battlefield_canvas")
        ) {
            // Draw Core Base line at bottom
            drawRect(
                color = Color(0x33FF3D00),
                topLeft = Offset(0f, size.height - 70.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width, 70.dp.toPx())
            )

            // Draw Structures
            for (s in structures) {
                drawCircle(
                    color = Color(s.colorHex),
                    radius = 22.dp.toPx(),
                    center = Offset(s.x, s.y)
                )
            }

            // Draw Raid Enemies marching
            for (e in raidEnemies) {
                drawCircle(
                    color = if (e.isBoss) Color(0xFFFF1744) else Color(0xFFFF8A80),
                    radius = if (e.isBoss) 24.dp.toPx() else 12.dp.toPx(),
                    center = Offset(e.x, e.y)
                )
            }
        }

        // Structure Build Selector Bar
        Surface(
            color = Color(0xFF141923),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedStructureToPlace = "turret" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedStructureToPlace == "turret") Color(0xFFFF9100) else Color(0xFF263238)
                    ),
                    modifier = Modifier.testTag("select_turret")
                ) {
                    Text("Auto-Turret (75)", fontSize = 11.sp)
                }

                Button(
                    onClick = { selectedStructureToPlace = "tesla" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedStructureToPlace == "tesla") Color(0xFF00E5FF) else Color(0xFF263238)
                    ),
                    modifier = Modifier.testTag("select_tesla")
                ) {
                    Text("Tesla (75)", color = Color.White, fontSize = 11.sp)
                }

                Button(
                    onClick = { selectedStructureToPlace = "barricade" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedStructureToPlace == "barricade") Color(0xFF78909C) else Color(0xFF263238)
                    ),
                    modifier = Modifier.testTag("select_barricade")
                ) {
                    Text("Wall (75)", fontSize = 11.sp)
                }
            }
        }
    }
}
