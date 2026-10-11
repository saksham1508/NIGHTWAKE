package com.example.nightwake.core

/**
 * NIGHTWAKE - Global Game Configuration
 *
 * Store working title and global balance constants here so renaming
 * or tuning base mechanics across the entire codebase is trivial.
 */
object GameConfig {
    const val GAME_TITLE = "NIGHTWAKE"
    const val VERSION_NAME = "1.0.0-softlaunch"
    const val VERSION_CODE = 100

    // Performance Targets
    const val TARGET_FPS = 60
    const val FIXED_DELTA_TIME_SEC = 1f / 60f
    const val MAX_ENEMIES_CAPACITY = 250
    const val MAX_PROJECTILES_CAPACITY = 300
    const val MAX_GEMS_CAPACITY = 400
    const val MAX_DAMAGE_NUMBERS_CAPACITY = 100
    const val MAX_PARTICLES_CAPACITY = 200

    // Spatial Hash Grid Tuning
    const val SPATIAL_CELL_SIZE = 120f
    const val WORLD_BOUNDS_HALF_WIDTH = 2500f
    const val WORLD_BOUNDS_HALF_HEIGHT = 2500f

    // Day/Night Cycle Duration in Seconds (for a 15-minute survival run)
    // 1 Full Cycle = 180 seconds (3 minutes): Dawn(20s), Day(60s), Dusk(20s), Night(80s)
    const val DAY_NIGHT_CYCLE_DURATION_SEC = 180f
    const val RUN_SURVIVAL_GOAL_SEC = 900f // 15 minutes to survive until extraction dawn

    // Player Base Constants
    const val PLAYER_BASE_SPEED = 280f
    const val PLAYER_BASE_MAX_HP = 100f
    const val PLAYER_PICKUP_RADIUS = 130f
    const val PLAYER_COLLISION_RADIUS = 26f

    // Night Modifiers
    const val NIGHT_ENEMY_SPEED_MULT = 1.35f
    const val NIGHT_ENEMY_DAMAGE_MULT = 1.30f
    const val NIGHT_SPAWN_RATE_MULT = 1.50f
    const val NIGHT_VISIBILITY_RADIUS = 320f
    const val DAY_VISIBILITY_RADIUS = 1200f

    // Anti-Tamper Salt for JSON save checksums
    const val SAVE_SALT = "NIGHTWAKE_SECRET_SALT_V1_ANTI_TAMPER_2026"
}
