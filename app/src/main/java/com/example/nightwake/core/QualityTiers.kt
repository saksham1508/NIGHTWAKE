package com.example.nightwake.core

/**
 * Scalable Quality Tiers for Mobile Hardware Targets.
 * Targets:
 * - High/Ultra: 60 FPS on Snapdragon 600-series (3-4GB RAM)
 * - Medium: 45-60 FPS on lower-tier GPUs, blob shadows, simplified neon
 * - Low: 30 FPS battery saver, flat shadows, minimal particle density
 */
enum class QualityLevel(val displayName: String, val targetFps: Int) {
    LOW("Low (Battery Saver - 30 FPS)", 30),
    MEDIUM("Medium (Balanced - 45 FPS)", 45),
    HIGH("High (60 FPS Optimized)", 60),
    ULTRA("Ultra (Full 3D Stylized - 60 FPS)", 60)
}

data class QualitySettings(
    var level: QualityLevel = QualityLevel.HIGH,
    var enableNeonRimLighting: Boolean = true,
    var enableDistanceFog: Boolean = true,
    var enableBlobShadows: Boolean = true,
    var enableHeroDynamicShadow: Boolean = true,
    var maxVisibleEnemies3D: Int = 180,
    var particleCountMultiplier: Float = 1.0f,
    var cameraFovDegrees: Float = 48f,
    var cameraElevationAngleDeg: Float = 55f // Fixed isometric / high-angle top-down
)

object QualityManager {
    var current: QualitySettings = QualitySettings()
        private set

    fun applyTier(level: QualityLevel) {
        current = when (level) {
            QualityLevel.LOW -> QualitySettings(
                level = QualityLevel.LOW,
                enableNeonRimLighting = false,
                enableDistanceFog = false,
                enableBlobShadows = false,
                enableHeroDynamicShadow = false,
                maxVisibleEnemies3D = 90,
                particleCountMultiplier = 0.4f,
                cameraFovDegrees = 45f,
                cameraElevationAngleDeg = 55f
            )
            QualityLevel.MEDIUM -> QualitySettings(
                level = QualityLevel.MEDIUM,
                enableNeonRimLighting = true,
                enableDistanceFog = true,
                enableBlobShadows = true,
                enableHeroDynamicShadow = false,
                maxVisibleEnemies3D = 130,
                particleCountMultiplier = 0.7f,
                cameraFovDegrees = 46f,
                cameraElevationAngleDeg = 55f
            )
            QualityLevel.HIGH -> QualitySettings(
                level = QualityLevel.HIGH,
                enableNeonRimLighting = true,
                enableDistanceFog = true,
                enableBlobShadows = true,
                enableHeroDynamicShadow = true,
                maxVisibleEnemies3D = 180,
                particleCountMultiplier = 1.0f,
                cameraFovDegrees = 48f,
                cameraElevationAngleDeg = 55f
            )
            QualityLevel.ULTRA -> QualitySettings(
                level = QualityLevel.ULTRA,
                enableNeonRimLighting = true,
                enableDistanceFog = true,
                enableBlobShadows = true,
                enableHeroDynamicShadow = true,
                maxVisibleEnemies3D = 250,
                particleCountMultiplier = 1.25f,
                cameraFovDegrees = 50f,
                cameraElevationAngleDeg = 55f
            )
        }
    }
}
