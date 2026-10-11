package com.example.nightwake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightwake.combat.CombatEngine
import com.example.nightwake.core.GameConfig

/**
 * Real-time In-Game QA Debug Overlay.
 * Monitored Metrics: FPS, Enemies, Projectiles, Gems, Estimated Draw Calls, Step Time, Day/Night Phase.
 */
@Composable
fun DebugOverlay(
    fps: Int,
    frameTimeMs: Float,
    engine: CombatEngine,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xCC0A0D14), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0x66FF3D00), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        val fpsColor = when {
            fps >= 55 -> Color(0xFF00E676)
            fps >= 40 -> Color(0xFFFFD600)
            else -> Color(0xFFFF1744)
        }

        Text(
            text = "${GameConfig.GAME_TITLE} v${GameConfig.VERSION_NAME} [DEBUG]",
            color = Color(0xFFFF9100),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = "FPS: $fps (${"%.1f".format(frameTimeMs)}ms) | Target: ${GameConfig.TARGET_FPS}",
            color = fpsColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = "Enemies: ${engine.activeEnemiesCount}/${GameConfig.MAX_ENEMIES_CAPACITY} | Proj: ${engine.activeProjectilesCount}",
            color = Color.White,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = "Gems: ${engine.activeGemsCount} | Est. Draws: ${engine.drawCallsEstimate}",
            color = Color(0xFFB0BEC5),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = "Phase: ${engine.currentPhaseName} (${"%.0f".format(engine.dayCycleTimeSec)}s) | Dark: ${"%.0f".format(engine.ambientDarkness * 100)}%",
            color = if (engine.isNight) Color(0xFFFF5252) else Color(0xFFFFD54F),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        if (engine.player.godMode) {
            Text(
                text = ">>> GOD MODE ACTIVE <<<",
                color = Color(0xFF00E5FF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
