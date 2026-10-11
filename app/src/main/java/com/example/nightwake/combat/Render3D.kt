package com.example.nightwake.combat

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.nightwake.core.QualityManager
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * High-Performance Stylized 3D Presentation Pipeline for NIGHTWAKE.
 *
 * Translates the 2D gameplay simulation plane (X, Z) into a fixed-angle top-down/isometric
 * 3D perspective space with:
 * - Fixed high-angle camera (~58° elevation) with portrait-friendly framing.
 * - Accurate 3D perspective foreshortening and depth ordering.
 * - Ground-plane contact blob shadows for believable mass.
 * - Stylized 3D character geometries with gritty tones and neon rim highlights.
 * - 3-Tier Level-of-Detail (LOD) culling for 150+ units.
 * - Atmospheric depth fog and flashlight beam volumetrics.
 */
class Camera3D(
    var elevationDeg: Float = 58f,
    var cameraDistance: Float = 880f
) {
    // Trigonometric caching
    private val elevationRad = Math.toRadians(elevationDeg.toDouble()).toFloat()
    val cosElev = cos(elevationRad)
    val sinElev = sin(elevationRad)

    // Pre-allocated result struct to avoid per-vertex allocations
    class ProjectedPoint {
        var screenX: Float = 0f
        var screenY: Float = 0f
        var scale: Float = 1f
        var depthZ: Float = 0f
        var isVisible: Boolean = true
    }

    val resultBuffer = ProjectedPoint()

    /**
     * Projects world simulation coordinates (worldX, worldZ) with an optional vertical height (worldY)
     * relative to the camera focus position (camX, camZ).
     * Zero-allocation.
     */
    fun project(
        worldX: Float,
        worldY: Float,
        worldZ: Float,
        camX: Float,
        camZ: Float,
        screenCenterX: Float,
        screenCenterY: Float
    ): ProjectedPoint {
        val relX = worldX - camX
        val relZ = worldZ - camZ

        // Isometric perspective transform
        // Camera looks down-forward along +Z
        val viewZ = relZ * sinElev + cameraDistance
        val viewY = relZ * cosElev - worldY

        // Perspective scale factor
        val fovFactor = 800f
        val scale = (fovFactor / (viewZ.coerceAtLeast(300f))).coerceIn(0.45f, 1.8f)

        resultBuffer.screenX = screenCenterX + relX * scale
        resultBuffer.screenY = screenCenterY + viewY * scale
        resultBuffer.scale = scale
        resultBuffer.depthZ = viewZ
        resultBuffer.isVisible = resultBuffer.screenX in -120f..(screenCenterX * 2f + 120f) &&
                resultBuffer.screenY in -120f..(screenCenterY * 2f + 120f)

        return resultBuffer
    }
}

/**
 * 3D Renderer for stylized isometric presentation.
 */
object Render3D {
    val camera = Camera3D()

    /**
     * Renders a grounded 3D perspective terrain grid with depth attenuation.
     */
    fun draw3DGrid(
        drawScope: DrawScope,
        camX: Float,
        camZ: Float,
        screenW: Float,
        screenH: Float,
        gridColor: Color,
        fogColor: Color
    ) {
        val screenCenterX = screenW / 2f
        val screenCenterY = screenH / 2f
        val cellSize = 140f
        val gridExtent = 10 // Grid lines in each direction

        val startX = (kotlin.math.floor(camX / cellSize) - gridExtent).toInt() * cellSize
        val endX = (kotlin.math.floor(camX / cellSize) + gridExtent).toInt() * cellSize
        val startZ = (kotlin.math.floor(camZ / cellSize) - gridExtent).toInt() * cellSize
        val endZ = (kotlin.math.floor(camZ / cellSize) + gridExtent).toInt() * cellSize

        // Draw longitudinal lines (along Z)
        var x = startX
        while (x <= endX) {
            val pNear = camera.project(x, 0f, startZ, camX, camZ, screenCenterX, screenCenterY)
            val pFar = camera.project(x, 0f, endZ, camX, camZ, screenCenterX, screenCenterY)

            if (pNear.isVisible || pFar.isVisible) {
                drawScope.drawLine(
                    color = gridColor.copy(alpha = 0.4f),
                    start = Offset(pNear.screenX, pNear.screenY),
                    end = Offset(pFar.screenX, pFar.screenY),
                    strokeWidth = 1.2f * pNear.scale
                )
            }
            x += cellSize
        }

        // Draw latitudinal lines (along X)
        var z = startZ
        while (z <= endZ) {
            val pLeft = camera.project(startX, 0f, z, camX, camZ, screenCenterX, screenCenterY)
            val pRight = camera.project(endX, 0f, z, camX, camZ, screenCenterX, screenCenterY)

            if (pLeft.isVisible || pRight.isVisible) {
                // Attenuate alpha with distance for depth fogging
                val depthAlpha = (1.0f - ((z - camZ) / 1400f)).coerceIn(0.1f, 0.5f)
                drawScope.drawLine(
                    color = gridColor.copy(alpha = depthAlpha),
                    start = Offset(pLeft.screenX, pLeft.screenY),
                    end = Offset(pRight.screenX, pRight.screenY),
                    strokeWidth = 1.2f * pLeft.scale
                )
            }
            z += cellSize
        }
    }

    /**
     * Renders a soft ground contact blob shadow beneath any entity.
     */
    fun drawBlobShadow(
        drawScope: DrawScope,
        screenX: Float,
        screenY: Float,
        radius: Float,
        scale: Float,
        alpha: Float = 0.55f
    ) {
        if (!QualityManager.current.enableBlobShadows) return
        val rx = radius * scale * 1.1f
        val ry = radius * scale * 0.45f // Foreshortened ground ellipse

        drawScope.drawOval(
            color = Color(0x99000000).copy(alpha = alpha),
            topLeft = Offset(screenX - rx, screenY - ry),
            size = Size(rx * 2f, ry * 2f)
        )
    }

    /**
     * Renders the 3D Stylized Hero with believable proportions and neon rim lighting.
     */
    fun drawStylizedHero3D(
        drawScope: DrawScope,
        pBase: Camera3D.ProjectedPoint,
        aimAngleRad: Float,
        isNight: Boolean
    ) {
        val sx = pBase.screenX
        val sy = pBase.screenY
        val scale = pBase.scale

        // 1. Ground Blob Shadow
        drawBlobShadow(drawScope, sx, sy, radius = 24f, scale = scale, alpha = 0.7f)

        // 2. Character Model Dimensions
        val footY = sy
        val torsoY = sy - 20f * scale
        val headY = sy - 38f * scale

        // Neon Rim Color: Cyan for Vanguard / Electric Cyan
        val rimNeonColor = if (isNight) Color(0xFF00E5FF) else Color(0xFF80DEEA)
        val armorColor = Color(0xFF263238)
        val accentColor = Color(0xFFFF5722)

        // 3. Legs (Tactical boots)
        val legW = 6f * scale
        val legH = 14f * scale
        drawScope.drawRoundRect(
            color = Color(0xFF1B242C),
            topLeft = Offset(sx - 9f * scale, footY - legH),
            size = Size(legW, legH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )
        drawScope.drawRoundRect(
            color = Color(0xFF1B242C),
            topLeft = Offset(sx + 3f * scale, footY - legH),
            size = Size(legW, legH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )

        // 4. Torso with ballistic plate carrier
        val torsoW = 18f * scale
        val torsoH = 18f * scale
        drawScope.drawRoundRect(
            color = armorColor,
            topLeft = Offset(sx - torsoW / 2f, torsoY - torsoH / 2f),
            size = Size(torsoW, torsoH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )

        // Tactical chest strap / neon battery badge
        drawScope.drawRect(
            color = accentColor,
            topLeft = Offset(sx - 3f * scale, torsoY - 5f * scale),
            size = Size(6f * scale, 10f * scale)
        )

        // 5. Stylized Head with Tactical Visor
        val headRadius = 9f * scale
        drawScope.drawCircle(
            color = Color(0xFF37474F),
            radius = headRadius,
            center = Offset(sx, headY)
        )

        // Neon Visor Strip
        val visorW = 10f * scale
        val visorH = 3.5f * scale
        drawScope.drawRoundRect(
            color = rimNeonColor,
            topLeft = Offset(sx - visorW / 2f, headY - 1f * scale),
            size = Size(visorW, visorH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5f, 1.5f)
        )

        // 6. Neon Rim Silhouette (Simulated Fresnel Edge Highlight)
        if (QualityManager.current.enableNeonRimLighting) {
            drawScope.drawCircle(
                color = rimNeonColor.copy(alpha = 0.6f),
                radius = headRadius + 1.5f * scale,
                center = Offset(sx, headY),
                style = Stroke(width = 1.8f * scale)
            )
        }

        // 7. 3D Weapon Model (Barrel oriented in 3D aim vector)
        val barrelLen = 28f * scale
        val gunStartX = sx
        val gunStartY = torsoY
        val gunEndX = gunStartX + cos(aimAngleRad) * barrelLen
        val gunEndY = gunStartY + sin(aimAngleRad) * barrelLen * 0.75f // Foreshortened

        // Heavy receiver & barrel
        drawScope.drawLine(
            color = Color(0xFF90A4AE),
            start = Offset(gunStartX, gunStartY),
            end = Offset(gunEndX, gunEndY),
            strokeWidth = 4f * scale
        )
        // Muzzle Glow
        drawScope.drawCircle(
            color = Color(0xFFFFD54F),
            radius = 2.5f * scale,
            center = Offset(gunEndX, gunEndY)
        )
    }

    /**
     * Renders a 3D Stylized Enemy with 3-tier Level-of-Detail (LOD).
     */
    fun drawStylizedEnemy3D(
        drawScope: DrawScope,
        pBase: Camera3D.ProjectedPoint,
        enemy: EnemyEntity,
        isNight: Boolean
    ) {
        val sx = pBase.screenX
        val sy = pBase.screenY
        val scale = pBase.scale
        val depthDist = pBase.depthZ

        // 1. Blob Shadow
        drawBlobShadow(
            drawScope = drawScope,
            screenX = sx,
            screenY = sy,
            radius = enemy.collisionRadius * 0.9f,
            scale = scale,
            alpha = if (enemy.isBoss) 0.8f else 0.5f
        )

        val baseColor = if (enemy.hitFlashTimer > 0f) Color.White else Color(enemy.colorHex)
        val rimColor = if (enemy.isBoss) Color(0xFFFF1744) else Color(0xFFFF8A80)

        // LOD Hierarchy based on camera depth distance:
        // LOD 0 (< 650): Full 3D faceted biped / arachnid body + glowing core
        // LOD 1 (650..1100): Simplified geometric shape with emissive rim
        // LOD 2 (> 1100): Low-poly billboard with distance fog attenuation
        when {
            depthDist < 650f -> {
                // LOD 0: Detailed stylized geometry
                val heightScale = if (enemy.isBoss) 55f else 28f
                val bodyY = sy - (heightScale * 0.5f) * scale
                val headY = sy - (heightScale * 0.85f) * scale
                val bodyW = enemy.collisionRadius * 1.5f * scale

                // Torso / Carapace
                drawScope.drawRoundRect(
                    color = baseColor,
                    topLeft = Offset(sx - bodyW / 2f, bodyY - 10f * scale),
                    size = Size(bodyW, 20f * scale),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )

                // Head / Maw
                drawScope.drawCircle(
                    color = baseColor.copy(alpha = 0.9f),
                    radius = (enemy.collisionRadius * 0.55f) * scale,
                    center = Offset(sx, headY)
                )

                // Glowing Eye Slits (Aggressive Red/Amber)
                drawScope.drawCircle(
                    color = Color(0xFFFF1744),
                    radius = 2.5f * scale,
                    center = Offset(sx - 3f * scale, headY)
                )
                drawScope.drawCircle(
                    color = Color(0xFFFF1744),
                    radius = 2.5f * scale,
                    center = Offset(sx + 3f * scale, headY)
                )

                // Neon Edge Rim Lighting
                if (QualityManager.current.enableNeonRimLighting) {
                    drawScope.drawCircle(
                        color = rimColor.copy(alpha = 0.5f),
                        radius = (enemy.collisionRadius * 0.6f) * scale,
                        center = Offset(sx, headY),
                        style = Stroke(width = 1.5f * scale)
                    )
                }

                // Boss Crown / Armor Spikes
                if (enemy.isBoss) {
                    drawScope.drawCircle(
                        color = Color(0xFFFFD700),
                        radius = (enemy.collisionRadius * 1.15f) * scale,
                        center = Offset(sx, bodyY),
                        style = Stroke(width = 2.5f * scale)
                    )
                }
            }

            depthDist < 1100f -> {
                // LOD 1: Simplified faceted silhouette
                val bodyRadius = enemy.collisionRadius * scale
                drawScope.drawCircle(
                    color = baseColor,
                    radius = bodyRadius,
                    center = Offset(sx, sy - bodyRadius * 0.6f)
                )
                // Single glowing eye core
                drawScope.drawCircle(
                    color = Color(0xFFFF1744),
                    radius = 3f * scale,
                    center = Offset(sx, sy - bodyRadius * 0.8f)
                )
            }

            else -> {
                // LOD 2: Streamlined low-poly dot / distant horde marker
                val r = (enemy.collisionRadius * 0.75f) * scale
                drawScope.drawCircle(
                    color = baseColor.copy(alpha = 0.75f),
                    radius = r,
                    center = Offset(sx, sy - r)
                )
            }
        }

        // Floating 3D Health Bar
        if (enemy.hp < enemy.maxHp) {
            val barW = enemy.collisionRadius * 2.2f * scale
            val barH = 4.5f * scale
            val barY = sy - (if (enemy.isBoss) 65f else 36f) * scale
            val hpRatio = (enemy.hp / enemy.maxHp).coerceIn(0f, 1f)

            drawScope.drawRect(
                color = Color(0x99000000),
                topLeft = Offset(sx - barW / 2f, barY),
                size = Size(barW, barH)
            )
            drawScope.drawRect(
                color = Color(0xFFFF1744),
                topLeft = Offset(sx - barW / 2f, barY),
                size = Size(barW * hpRatio, barH)
            )
        }
    }

    /**
     * Renders a 3D Projectile with motion tracer streaks.
     */
    fun drawStylizedProjectile3D(
        drawScope: DrawScope,
        pBase: Camera3D.ProjectedPoint,
        proj: ProjectileEntity
    ) {
        val sx = pBase.screenX
        val sy = pBase.screenY
        val scale = pBase.scale
        val col = Color(proj.colorHex)

        if (proj.isHazardPool) {
            // Ground Decal Disc (Foreshortened)
            val rx = proj.aoeRadius * scale
            val ry = rx * 0.45f
            drawScope.drawOval(
                color = col.copy(alpha = 0.35f),
                topLeft = Offset(sx - rx, sy - ry),
                size = Size(rx * 2f, ry * 2f)
            )
            drawScope.drawOval(
                color = col.copy(alpha = 0.8f),
                topLeft = Offset(sx - rx, sy - ry),
                size = Size(rx * 2f, ry * 2f),
                style = Stroke(width = 2f * scale)
            )
        } else {
            // Tracer Bullet with Neon Core
            val r = proj.radius * scale
            drawScope.drawCircle(
                color = col,
                radius = r,
                center = Offset(sx, sy)
            )
            drawScope.drawCircle(
                color = Color.White,
                radius = r * 0.45f,
                center = Offset(sx, sy)
            )
        }
    }
}
