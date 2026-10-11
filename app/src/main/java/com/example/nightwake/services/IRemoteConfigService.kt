package com.example.nightwake.services

/**
 * Interface for live-ops tuning and remote feature flags without needing an app update.
 */
interface IRemoteConfigService {
    fun getFloat(key: String, defaultValue: Float): Float
    fun getInt(key: String, defaultValue: Int): Int
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun getString(key: String, defaultValue: String): String
}

class DefaultRemoteConfigService : IRemoteConfigService {
    private val defaults = mapOf(
        "enemy_health_mult" to 1.0f,
        "xp_drop_mult" to 1.0f,
        "ad_revives_daily_cap" to 3,
        "raid_mode_unlocked_by_default" to false,
        "ftue_first_kill_time_sec" to 10f
    )

    override fun getFloat(key: String, defaultValue: Float): Float {
        return (defaults[key] as? Float) ?: defaultValue
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return (defaults[key] as? Int) ?: defaultValue
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return (defaults[key] as? Boolean) ?: defaultValue
    }

    override fun getString(key: String, defaultValue: String): String {
        return (defaults[key] as? String) ?: defaultValue
    }
}
