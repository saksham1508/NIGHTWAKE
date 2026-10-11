package com.example.nightwake.core

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Decoupled Audio & VFX slot registry.
 * Provides clean hooks for sound effects, ambient cues, screen shake, and haptics.
 * Allows replacing placeholders with final audio assets without altering gameplay systems.
 */
object AudioVfxSlots {

    enum class SoundCue {
        SHOOT_RIFLE,
        SHOOT_SHOTGUN,
        TESLA_ZAP,
        EXPLOSION,
        XP_PICKUP,
        LEVEL_UP,
        PLAYER_HURT,
        BOSS_ROAR,
        NIGHT_SIREN,
        DAWN_CHIME,
        BUTTON_CLICK
    }

    enum class HapticCue {
        LIGHT_TICK,
        GUN_RECOIL,
        HEAVY_IMPACT,
        LEVEL_UP_PULSE,
        DEATH_RATTLE
    }

    private var vibrator: Vibrator? = null
    var isHapticsEnabled: Boolean = true
    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
    var masterVolume: Float = 1.0f

    fun initialize(context: Context) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playSound(cue: SoundCue, pitch: Float = 1.0f, volumeScale: Float = 1.0f) {
        if (!isSoundEnabled) return
        // Production hook: AudioTrack / SoundPool / ExoPlayer can be plugged in here
    }

    fun triggerHaptic(cue: HapticCue) {
        if (!isHapticsEnabled) return
        val vib = vibrator ?: return
        try {
            if (!vib.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (cue) {
                    HapticCue.LIGHT_TICK -> VibrationEffect.createOneShot(12, 50)
                    HapticCue.GUN_RECOIL -> VibrationEffect.createOneShot(25, 120)
                    HapticCue.HEAVY_IMPACT -> VibrationEffect.createOneShot(80, 220)
                    HapticCue.LEVEL_UP_PULSE -> VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 80), intArrayOf(0, 100, 0, 200), -1)
                    HapticCue.DEATH_RATTLE -> VibrationEffect.createOneShot(150, 255)
                }
                vib.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                when (cue) {
                    HapticCue.LIGHT_TICK -> vib.vibrate(15)
                    HapticCue.GUN_RECOIL -> vib.vibrate(25)
                    HapticCue.HEAVY_IMPACT -> vib.vibrate(80)
                    HapticCue.LEVEL_UP_PULSE -> vib.vibrate(120)
                    HapticCue.DEATH_RATTLE -> vib.vibrate(200)
                }
            }
        } catch (_: Throwable) {
            // Gracefully ignore vibration failure if disabled or permission denied
        }
    }
}
