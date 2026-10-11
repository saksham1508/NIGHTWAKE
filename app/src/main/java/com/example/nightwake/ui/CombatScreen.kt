package com.example.nightwake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightwake.combat.CombatEngine
import com.example.nightwake.combat.Render3D
import com.example.nightwake.core.GameConfig
import com.example.nightwake.data.CharacterDefinition
import com.example.nightwake.data.MapDefinition
import com.example.nightwake.services.ServiceLocator
import kotlin.math.cos
import kotlin.math.sin

/**
 * 60 FPS Canvas-driven Combat Screen for NIGHTWAKE.
 * Renders entities, ambient darkness flashlight mask, floating damage numbers,
 * and HUD with zero per-frame collection allocations.
 */
@Composable
fun CombatScreen(
    character: CharacterDefinition,
    map: MapDefinition,
    onExitToSanctuary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val engine = remember(character, map) { CombatEngine(character, map) }

    var isPaused by remember { mutableStateOf(false) }
    var showCheatMenu by remember { mutableStateOf(false) }
    var fps by remember { mutableIntStateOf(60) }
    var frameTimeMs by remember { mutableFloatStateOf(16.6f) }
    var frameCount by remember { mutableIntStateOf(0) }
    var lastFpsTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // 60 FPS Game Loop
    LaunchedEffect(isPaused) {
        var lastNano = System.nanoTime()
        while (true) {
            withFrameNanos { nowNano ->
                val dt = ((nowNano - lastNano) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNano = nowNano

                if (!isPaused) {
                    val startTick = System.nanoTime()
                    engine.update(dt)
                    frameTimeMs = (System.nanoTime() - startTick) / 1_000_000f
                }

                // Calculate moving FPS
                frameCount++
                val nowMs = System.currentTimeMillis()
                if (nowMs - lastFpsTime >= 500) {
                    fps = ((frameCount * 1000f) / (nowMs - lastFpsTime)).toInt()
                    frameCount = 0
                    lastFpsTime = nowMs
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(map.groundColorHex))) {
        // Main 2D Game Canvas
        Canvas(modifier = Modifier.fillMaxSize().testTag("combat_canvas")) {
            val screenWidth = size.width
            val screenHeight = size.height
            val cameraX = engine.player.posX
            val cameraY = engine.player.posY
            val screenCenterX = screenWidth / 2f
            val screenCenterY = screenHeight / 2f

            // 1. Draw 3D Perspective Terrain Grid
            Render3D.draw3DGrid(
                drawScope = this,
                camX = cameraX,
                camZ = cameraY,
                screenW = screenWidth,
                screenH = screenHeight,
                gridColor = Color(map.gridColorHex),
                fogColor = Color(map.groundColorHex)
            )

            // 2. Draw XP Gems with 3D projection & floating bob
            for (i in 0 until engine.gems.size) {
                val g = engine.gems[i]
                if (g.active) {
                    val p = Render3D.camera.project(g.posX, 4f, g.posY, cameraX, cameraY, screenCenterX, screenCenterY)
                    if (p.isVisible) {
                        Render3D.drawBlobShadow(this, p.screenX, p.screenY, radius = 8f, scale = p.scale, alpha = 0.4f)
                        drawCircle(
                            color = Color(g.colorHex),
                            radius = 6.dp.toPx() * p.scale,
                            center = Offset(p.screenX, p.screenY - 6f * p.scale)
                        )
                    }
                }
            }

            // 3. Draw Projectiles & Hazard Pools
            for (i in 0 until engine.projectiles.size) {
                val proj = engine.projectiles[i]
                if (proj.active) {
                    val p = Render3D.camera.project(
                        proj.posX,
                        if (proj.isHazardPool) 0f else 12f,
                        proj.posY,
                        cameraX,
                        cameraY,
                        screenCenterX,
                        screenCenterY
                    )
                    if (p.isVisible) {
                        Render3D.drawStylizedProjectile3D(this, p, proj)
                    }
                }
            }

            // 4. Draw Orbiting Saws in 3D Perspective
            for (w in engine.player.activeWeapons) {
                if (w.definition.id == "orbital_sawblades" || w.definition.id == "plasma_buzzsaw") {
                    val count = w.definition.projectileCount
                    val stepDeg = 360f / count
                    for (c in 0 until count) {
                        val angleRad = Math.toRadians((w.orbitAngleDeg + c * stepDeg).toDouble()).toFloat()
                        val sawDist = w.definition.projectileRange
                        val sawWorldX = cameraX + cos(angleRad) * sawDist
                        val sawWorldZ = cameraY + sin(angleRad) * sawDist
                        val pSaw = Render3D.camera.project(sawWorldX, 10f, sawWorldZ, cameraX, cameraY, screenCenterX, screenCenterY)
                        if (pSaw.isVisible) {
                            drawCircle(
                                color = Color(w.definition.colorHex),
                                radius = 14.dp.toPx() * pSaw.scale,
                                center = Offset(pSaw.screenX, pSaw.screenY)
                            )
                        }
                    }
                }
            }

            // 5. Draw Enemies in 3D with Level-of-Detail (LOD)
            val maxEnemiesToDraw = com.example.nightwake.core.QualityManager.current.maxVisibleEnemies3D
            var drawnEnemies = 0
            for (i in 0 until engine.enemies.size) {
                if (drawnEnemies >= maxEnemiesToDraw) break
                val e = engine.enemies[i]
                if (e.active) {
                    val p = Render3D.camera.project(e.posX, 0f, e.posY, cameraX, cameraY, screenCenterX, screenCenterY)
                    if (p.isVisible) {
                        Render3D.drawStylizedEnemy3D(this, p, e, engine.isNight)
                        drawnEnemies++
                    }
                }
            }

            // 6. Draw 3D Stylized Hero with Believable Proportions and Neon Rim
            val pHero = Render3D.camera.project(cameraX, 0f, cameraY, cameraX, cameraY, screenCenterX, screenCenterY)
            Render3D.drawStylizedHero3D(this, pHero, engine.player.aimAngleRad, engine.isNight)

            // 7. Floating 3D Damage Indicators
            for (i in 0 until engine.damageNumbers.size) {
                val d = engine.damageNumbers[i]
                if (d.active) {
                    val floatHeight = 35f + (0.6f - d.lifeRemaining) * 50f
                    val pDmg = Render3D.camera.project(d.posX, floatHeight, d.posY, cameraX, cameraY, screenCenterX, screenCenterY)
                    if (pDmg.isVisible) {
                        drawCircle(
                            color = if (d.isCrit) Color(0xFFFFD54F) else Color.White,
                            radius = (if (d.isCrit) 5f else 3f) * pDmg.scale,
                            center = Offset(pDmg.screenX, pDmg.screenY)
                        )
                    }
                }
            }

            // 7. Day/Night Darkness and Flashlight Vignette
            if (engine.ambientDarkness > 0.05f) {
                drawNightLighting(
                    screenCenterX = screenCenterX,
                    screenCenterY = screenCenterY,
                    darknessAlpha = engine.ambientDarkness,
                    flashlightAngleRad = engine.flashlightAngleRad,
                    isNight = engine.isNight
                )
            }
        }

        // HUD Top Layer
        CombatHud(
            engine = engine,
            onPauseClick = { isPaused = !isPaused },
            onCheatClick = { showCheatMenu = true },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Debug QA Overlay Top Left
        DebugOverlay(
            fps = fps,
            frameTimeMs = frameTimeMs,
            engine = engine,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 80.dp, start = 12.dp)
        )

        // Bottom Virtual Joystick (One-handed portrait controls)
        VirtualJoystick(
            onMove = { dx, dy ->
                engine.joystickDeltaX = dx
                engine.joystickDeltaY = dy
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .testTag("game_joystick")
        )

        // Level Up Upgrade Selection Modal
        if (engine.isLevelUpPending) {
            LevelUpDialog(
                playerLevel = engine.player.level,
                choices = engine.pendingUpgradeChoices,
                onSelect = { upgrade ->
                    engine.applyUpgradeChoice(upgrade)
                }
            )
        }

        // QA Cheat & Balance Menu
        if (showCheatMenu) {
            CheatMenuDialog(
                engine = engine,
                onDismiss = { showCheatMenu = false }
            )
        }

        // Game Over / Run Complete Screen
        if (!engine.player.isAlive) {
            RunResultDialog(
                victory = false,
                survivedSec = engine.waveDirector.elapsedRunTimeSec,
                kills = engine.player.kills,
                level = engine.player.level,
                scrapEarned = engine.runScrapEarned,
                metalEarned = engine.runMetalEarned,
                onReviveAd = {
                    ServiceLocator.ads.showRewardedAd(com.example.nightwake.services.IAdService.PlacementType.REVIVE_ON_DEATH) { success ->
                        if (success) {
                            engine.player.isAlive = true
                            engine.player.hp = engine.player.maxHp * 0.7f
                        }
                    }
                },
                onConfirm = {
                    // Save collected run resources
                    val save = ServiceLocator.save.loadSave()
                    save.softCurrencyScrap += engine.runScrapEarned
                    save.metal += engine.runMetalEarned
                    save.wood += engine.runWoodEarned
                    save.electronics += engine.runElectronicsEarned
                    ServiceLocator.save.saveGame(save)
                    onExitToSanctuary()
                }
            )
        }
    }
}

private fun DrawScope.drawBackgroundGrid(
    cameraX: Float,
    cameraY: Float,
    screenW: Float,
    screenH: Float,
    gridColor: Color
) {
    val gridSize = 120.dp.toPx()
    val offsetX = -(cameraX % gridSize)
    val offsetY = -(cameraY % gridSize)

    var x = offsetX
    while (x < screenW) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, screenH),
            strokeWidth = 1.dp.toPx()
        )
        x += gridSize
    }

    var y = offsetY
    while (y < screenH) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(screenW, y),
            strokeWidth = 1.dp.toPx()
        )
        y += gridSize
    }
}

private fun DrawScope.drawNightLighting(
    screenCenterX: Float,
    screenCenterY: Float,
    darknessAlpha: Float,
    flashlightAngleRad: Float,
    isNight: Boolean
) {
    // Ambient darkness wash
    val darkColor = Color(0xFF020408).copy(alpha = darknessAlpha.coerceIn(0f, 0.92f))
    drawRect(color = darkColor)

    // Player torchlight circle (radial vision clearing darkness)
    val visionRadius = if (isNight) 160.dp.toPx() else 350.dp.toPx()
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                darkColor.copy(alpha = darknessAlpha * 0.5f),
                darkColor
            ),
            center = Offset(screenCenterX, screenCenterY),
            radius = visionRadius
        ),
        radius = visionRadius,
        center = Offset(screenCenterX, screenCenterY)
    )
}

@Composable
fun CombatHud(
    engine: CombatEngine,
    onPauseClick: () -> Unit,
    onCheatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Top status row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Level and Health
            Column {
                Text(
                    text = "LVL ${engine.player.level} | ${engine.player.hp.toInt()}/${engine.player.maxHp.toInt()} HP",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                // HP Bar
                val hpRatio = (engine.player.hp / engine.player.maxHp).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { hpRatio },
                    color = if (hpRatio > 0.3f) Color(0xFF00E676) else Color(0xFFFF1744),
                    trackColor = Color(0xFF263238),
                    modifier = Modifier
                        .width(130.dp)
                        .height(8.dp)
                )
            }

            // Survival Clock and Phase
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val totalSec = engine.waveDirector.elapsedRunTimeSec.toInt()
                val mins = totalSec / 60
                val secs = totalSec % 60
                Text(
                    text = "%02d:%02d".format(mins, secs),
                    color = Color(0xFFFFD54F),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${engine.currentPhaseName.uppercase()} PHASE",
                    color = if (engine.isNight) Color(0xFFFF5252) else Color(0xFFFFCA28),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Kills and Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💀 ${engine.player.kills}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 6.dp)
                )

                IconButton(
                    onClick = onCheatClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("hud_cheat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "QA Cheats",
                        tint = Color(0xFFFF9100)
                    )
                }

                IconButton(
                    onClick = onPauseClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("hud_pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // XP Progress Bar across full width
        val xpRatio = (engine.player.currentXp.toFloat() / engine.player.xpToNextLevel).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { xpRatio },
            color = Color(0xFF00E5FF),
            trackColor = Color(0xFF102027),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}

@Composable
fun RunResultDialog(
    victory: Boolean,
    survivedSec: Float,
    kills: Int,
    level: Int,
    scrapEarned: Int,
    metalEarned: Int,
    onReviveAd: () -> Unit,
    onConfirm: () -> Unit
) {
    Surface(
        color = Color(0xE60D1117),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (victory) "DAWN EXTRACTION COMPLETE!" else "KILLED IN ACTION",
                color = if (victory) Color(0xFF00E676) else Color(0xFFFF1744),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = if (victory) "You survived until sunrise and safely evacuated." else "The midnight horde overwhelmed your position.",
                color = Color(0xFFB0BEC5),
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Run Stats
            Column(
                modifier = Modifier
                    .background(Color(0xFF161B22), RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val mins = survivedSec.toInt() / 60
                val secs = survivedSec.toInt() % 60
                Text("Time Survived: %02d:%02d".format(mins, secs), color = Color.White, fontSize = 14.sp)
                Text("Infected Slain: $kills", color = Color.White, fontSize = 14.sp)
                Text("Combat Level Reached: $level", color = Color.White, fontSize = 14.sp)
                Text("Scrap Salvaged: +$scrapEarned", color = Color(0xFFFFD54F), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Metal Salvaged: +$metalEarned", color = Color(0xFF80DEEA), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!victory) {
                Button(
                    onClick = onReviveAd,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    modifier = Modifier.fillMaxWidth().testTag("revive_ad_button")
                ) {
                    Text("📺 Revive with 70% HP (Ad)", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                modifier = Modifier.fillMaxWidth().testTag("return_sanctuary_button")
            ) {
                Text("Return to Sanctuary", fontWeight = FontWeight.Bold)
            }
        }
    }
}
