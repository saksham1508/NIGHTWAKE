package com.example.nightwake.combat

import com.example.nightwake.core.AudioVfxSlots
import com.example.nightwake.core.EventBus
import com.example.nightwake.core.GameConfig
import com.example.nightwake.core.GameEvent
import com.example.nightwake.core.SpatialHashGrid
import com.example.nightwake.data.CharacterDefinition
import com.example.nightwake.data.EnemyDefinition
import com.example.nightwake.data.EnemyRegistry
import com.example.nightwake.data.MapDefinition
import com.example.nightwake.data.UpgradeDefinition
import com.example.nightwake.data.UpgradeRegistry
import com.example.nightwake.data.UpgradeType
import com.example.nightwake.data.WeaponDefinition
import com.example.nightwake.data.WeaponRegistry
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * High-performance Zero-GC Combat Engine for NIGHTWAKE.
 * Coordinates movement, auto-aim, ballistics, spatial partitioning, day/night cycles,
 * and XP progression for 150+ active enemies at 60 FPS.
 */
class CombatEngine(
    val character: CharacterDefinition,
    val map: MapDefinition
) {
    val player = PlayerState()
    val spatialGrid = SpatialHashGrid(GameConfig.SPATIAL_CELL_SIZE, GameConfig.MAX_ENEMIES_CAPACITY)
    val waveDirector = WaveDirector(map)

    // Pre-allocated object pools
    val enemies = Array(GameConfig.MAX_ENEMIES_CAPACITY) { EnemyEntity() }
    val projectiles = Array(GameConfig.MAX_PROJECTILES_CAPACITY) { ProjectileEntity() }
    val gems = Array(GameConfig.MAX_GEMS_CAPACITY) { GemEntity() }
    val damageNumbers = Array(GameConfig.MAX_DAMAGE_NUMBERS_CAPACITY) { DamageTextEntity() }

    // Day/Night and Weather State
    var dayCycleTimeSec: Float = 0f
    var currentPhaseName: String = "Dawn"
    var isNight: Boolean = false
    var ambientDarkness: Float = 0.3f
    var flashlightAngleRad: Float = 0f

    // Pending Level Up choices
    var isLevelUpPending: Boolean = false
    var pendingUpgradeChoices: List<UpgradeDefinition> = emptyList()

    // Scavenged run rewards
    var runScrapEarned: Int = 0
    var runMetalEarned: Int = 0
    var runWoodEarned: Int = 0
    var runElectronicsEarned: Int = 0

    // Virtual Joystick Inputs
    var joystickDeltaX: Float = 0f
    var joystickDeltaY: Float = 0f

    // Performance Profiler Counters
    var activeEnemiesCount: Int = 0
        private set
    var activeProjectilesCount: Int = 0
        private set
    var activeGemsCount: Int = 0
        private set
    var drawCallsEstimate: Int = 0
        private set

    init {
        reset()
    }

    fun reset() {
        player.reset(character)
        waveDirector.reset()
        spatialGrid.clear()

        for (e in enemies) e.active = false
        for (p in projectiles) p.active = false
        for (g in gems) g.active = false
        for (d in damageNumbers) d.active = false

        dayCycleTimeSec = 0f
        currentPhaseName = "Dawn"
        isNight = false
        ambientDarkness = 0.3f
        isLevelUpPending = false
        pendingUpgradeChoices = emptyList()

        runScrapEarned = 0
        runMetalEarned = 0
        runWoodEarned = 0
        runElectronicsEarned = 0
    }

    /**
     * Primary 60 FPS Fixed Physics & Gameplay Tick.
     */
    fun update(dt: Float) {
        if (!player.isAlive || isLevelUpPending) return

        updateDayNightCycle(dt)
        updatePlayerMovement(dt)
        updateWeapons(dt)
        updateProjectiles(dt)
        updateEnemies(dt)
        updateGems(dt)
        updateDamageNumbers(dt)

        // Wave director progression
        waveDirector.update(dt, isNight, activeEnemiesCount) { enemyDef, isBoss ->
            spawnEnemy(enemyDef, isBoss)
        }

        // Count active entities for profiling
        recalculateActiveCounts()
    }

    private fun updateDayNightCycle(dt: Float) {
        dayCycleTimeSec = (dayCycleTimeSec + dt) % GameConfig.DAY_NIGHT_CYCLE_DURATION_SEC

        // Cycle breakdown (180s total):
        // 0..20s = Dawn, 20..80s = Day, 80..100s = Dusk, 100..180s = Night (80s of darkness)
        val wasNight = isNight
        when {
            dayCycleTimeSec < 20f -> {
                currentPhaseName = "Dawn"
                isNight = false
                ambientDarkness = 0.35f
            }
            dayCycleTimeSec < 80f -> {
                currentPhaseName = "Day"
                isNight = false
                ambientDarkness = 0.15f
            }
            dayCycleTimeSec < 100f -> {
                currentPhaseName = "Dusk"
                isNight = false
                ambientDarkness = 0.55f
            }
            else -> {
                currentPhaseName = "Night"
                isNight = true
                ambientDarkness = map.ambientDarknessLevel
            }
        }

        if (wasNight != isNight) {
            EventBus.publish(GameEvent.DayPhaseChanged(currentPhaseName, isNight))
            if (isNight) {
                AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.NIGHT_SIREN)
                AudioVfxSlots.triggerHaptic(AudioVfxSlots.HapticCue.HEAVY_IMPACT)
            } else {
                AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.DAWN_CHIME)
            }
        }
    }

    private fun updatePlayerMovement(dt: Float) {
        // Compute speed and direction from virtual joystick
        val lenSq = joystickDeltaX * joystickDeltaX + joystickDeltaY * joystickDeltaY
        if (lenSq > 0.001f) {
            val len = sqrt(lenSq)
            val dirX = joystickDeltaX / len
            val dirY = joystickDeltaY / len
            val effectiveSpeed = player.baseSpeed * player.activePassives.getOrDefault("pass_movement", 0).let { lvl -> 1f + lvl * 0.10f }

            player.posX += dirX * effectiveSpeed * dt
            player.posY += dirY * effectiveSpeed * dt
            player.aimAngleRad = atan2(dirY, dirX)
            flashlightAngleRad = player.aimAngleRad
        }

        // Clamp player within world bounds
        player.posX = player.posX.coerceIn(-GameConfig.WORLD_BOUNDS_HALF_WIDTH, GameConfig.WORLD_BOUNDS_HALF_WIDTH)
        player.posY = player.posY.coerceIn(-GameConfig.WORLD_BOUNDS_HALF_HEIGHT, GameConfig.WORLD_BOUNDS_HALF_HEIGHT)

        // Health regeneration
        if (player.hpRegenSec > 0f && player.hp < player.maxHp) {
            player.hp = (player.hp + player.hpRegenSec * dt).coerceAtMost(player.maxHp)
        }
    }

    private fun updateWeapons(dt: Float) {
        // Auto-aim: Locate closest enemy in spatial hash grid
        val targetEnemy = findClosestEnemyToPlayer(radius = 700f)
        if (targetEnemy != null) {
            val dx = targetEnemy.posX - player.posX
            val dy = targetEnemy.posY - player.posY
            player.aimAngleRad = atan2(dy, dx)
            flashlightAngleRad = player.aimAngleRad
        }

        for (i in 0 until player.activeWeapons.size) {
            val slot = player.activeWeapons[i]
            val wep = slot.definition
            slot.cooldownTimer -= dt

            if (wep.id == "orbital_sawblades" || wep.id == "plasma_buzzsaw") {
                // Orbital sawblades update angle continuously
                slot.orbitAngleDeg = (slot.orbitAngleDeg + wep.projectileSpeed * dt) % 360f
                continue
            }

            if (slot.cooldownTimer <= 0f) {
                val effectiveFireRate = wep.fireRateSec * (1f - player.cooldownReduction).coerceAtLeast(0.2f)
                slot.cooldownTimer = effectiveFireRate
                fireWeapon(wep, slot.level, targetEnemy)
            }
        }
    }

    private fun fireWeapon(wep: WeaponDefinition, level: Int, target: EnemyEntity?) {
        val baseDamage = wep.baseDamage * player.damageMult * (1f + (level - 1) * 0.15f)
        val aimAngle = player.aimAngleRad

        when (wep.id) {
            "assault_rifle", "vulcan_cannon" -> {
                spawnProjectile(
                    x = player.posX,
                    y = player.posY,
                    angle = aimAngle,
                    speed = wep.projectileSpeed * player.projSpeedMult,
                    damage = baseDamage,
                    radius = 8f,
                    range = wep.projectileRange,
                    pierce = wep.pierceCount,
                    colorHex = wep.colorHex
                )
                AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.SHOOT_RIFLE)
                AudioVfxSlots.triggerHaptic(AudioVfxSlots.HapticCue.LIGHT_TICK)
            }
            "sawed_off_shotgun", "trench_flak" -> {
                val count = wep.projectileCount
                val halfSpread = Math.toRadians(wep.spreadAngleDeg.toDouble()).toFloat() / 2f
                val step = if (count > 1) (halfSpread * 2f) / (count - 1) else 0f
                for (j in 0 until count) {
                    val angle = (aimAngle - halfSpread) + (j * step) + Random.nextFloat() * 0.05f
                    spawnProjectile(
                        x = player.posX,
                        y = player.posY,
                        angle = angle,
                        speed = wep.projectileSpeed * (0.85f + Random.nextFloat() * 0.3f),
                        damage = baseDamage,
                        radius = 7f,
                        range = wep.projectileRange,
                        pierce = wep.pierceCount,
                        colorHex = wep.colorHex
                    )
                }
                AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.SHOOT_SHOTGUN)
                AudioVfxSlots.triggerHaptic(AudioVfxSlots.HapticCue.GUN_RECOIL)
            }
            "tesla_arc", "thunderstorm_coil" -> {
                if (target != null) {
                    spawnProjectile(
                        x = player.posX,
                        y = player.posY,
                        angle = aimAngle,
                        speed = wep.projectileSpeed,
                        damage = baseDamage,
                        radius = 12f,
                        range = wep.projectileRange,
                        pierce = wep.pierceCount,
                        colorHex = wep.colorHex
                    )
                    AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.TESLA_ZAP)
                }
            }
            "toxic_sprayer", "plague_mist" -> {
                val poolAngle = aimAngle + (Random.nextFloat() - 0.5f) * 0.4f
                spawnHazardPool(
                    x = player.posX + cos(poolAngle) * 90f,
                    y = player.posY + sin(poolAngle) * 90f,
                    damage = baseDamage,
                    radius = wep.areaRadius * player.areaMult,
                    duration = 3.5f * player.durationMult,
                    colorHex = wep.colorHex
                )
            }
            "mortar_grenade", "cluster_nuke" -> {
                val dist = if (target != null) {
                    val dx = target.posX - player.posX
                    val dy = target.posY - player.posY
                    sqrt(dx * dx + dy * dy).coerceAtMost(wep.projectileRange)
                } else 280f

                spawnExplosiveRocket(
                    startX = player.posX,
                    startY = player.posY,
                    targetX = player.posX + cos(aimAngle) * dist,
                    targetY = player.posY + sin(aimAngle) * dist,
                    damage = baseDamage,
                    aoeRadius = wep.areaRadius * player.areaMult,
                    colorHex = wep.colorHex
                )
            }
        }
    }

    private fun spawnProjectile(
        x: Float,
        y: Float,
        angle: Float,
        speed: Float,
        damage: Float,
        radius: Float,
        range: Float,
        pierce: Int,
        colorHex: Long,
        isHostile: Boolean = false
    ) {
        for (i in 0 until projectiles.size) {
            val p = projectiles[i]
            if (!p.active) {
                p.active = true
                p.posX = x
                p.posY = y
                p.velX = cos(angle) * speed
                p.velY = sin(angle) * speed
                p.damage = damage
                p.radius = radius
                p.rangeRemaining = range
                p.pierceRemaining = pierce
                p.colorHex = colorHex
                p.isHostile = isHostile
                p.isAoe = false
                p.isHazardPool = false
                break
            }
        }
    }

    private fun spawnHazardPool(x: Float, y: Float, damage: Float, radius: Float, duration: Float, colorHex: Long) {
        for (i in 0 until projectiles.size) {
            val p = projectiles[i]
            if (!p.active) {
                p.active = true
                p.posX = x
                p.posY = y
                p.velX = 0f
                p.velY = 0f
                p.damage = damage
                p.radius = radius
                p.rangeRemaining = 9999f
                p.pierceRemaining = 999
                p.colorHex = colorHex
                p.isHostile = false
                p.isAoe = true
                p.aoeRadius = radius
                p.isHazardPool = true
                p.poolDurationTimer = duration
                break
            }
        }
    }

    private fun spawnExplosiveRocket(startX: Float, startY: Float, targetX: Float, targetY: Float, damage: Float, aoeRadius: Float, colorHex: Long) {
        val dx = targetX - startX
        val dy = targetY - startY
        val dist = sqrt(dx * dx + dy * dy)
        val angle = atan2(dy, dx)

        for (i in 0 until projectiles.size) {
            val p = projectiles[i]
            if (!p.active) {
                p.active = true
                p.posX = startX
                p.posY = startY
                p.velX = cos(angle) * 440f
                p.velY = sin(angle) * 440f
                p.damage = damage
                p.radius = 12f
                p.rangeRemaining = dist
                p.pierceRemaining = 1
                p.colorHex = colorHex
                p.isHostile = false
                p.isAoe = true
                p.aoeRadius = aoeRadius
                p.isHazardPool = false
                break
            }
        }
    }

    private fun updateProjectiles(dt: Float) {
        for (i in 0 until projectiles.size) {
            val p = projectiles[i]
            if (!p.active) continue

            if (p.isHazardPool) {
                p.poolDurationTimer -= dt
                if (p.poolDurationTimer <= 0f) {
                    p.active = false
                    continue
                }

                // Hazard damage tick against enemies inside radius
                val count = spatialGrid.queryRadius(p.posX, p.posY, p.aoeRadius)
                for (j in 0 until count) {
                    val enemyIdx = spatialGrid.queryResultBuffer[j]
                    val enemy = enemies[enemyIdx]
                    if (enemy.active) {
                        applyDamageToEnemy(enemy, p.damage * dt * 3f, isCrit = false)
                    }
                }
                continue
            }

            // Normal flying projectile
            p.posX += p.velX * dt
            p.posY += p.velY * dt
            val travel = sqrt(p.velX * p.velX + p.velY * p.velY) * dt
            p.rangeRemaining -= travel

            if (p.rangeRemaining <= 0f) {
                if (p.isAoe) {
                    detonateAoe(p)
                }
                p.active = false
                continue
            }

            if (p.isHostile) {
                // Hostile bullet check against player
                val dx = p.posX - player.posX
                val dy = p.posY - player.posY
                if (dx * dx + dy * dy < (p.radius + GameConfig.PLAYER_COLLISION_RADIUS) * (p.radius + GameConfig.PLAYER_COLLISION_RADIUS)) {
                    damagePlayer(p.damage)
                    p.active = false
                }
            } else {
                // Player projectile check against enemies
                val count = spatialGrid.queryRadius(p.posX, p.posY, p.radius + 30f)
                for (j in 0 until count) {
                    val enemyIdx = spatialGrid.queryResultBuffer[j]
                    val enemy = enemies[enemyIdx]
                    if (enemy.active) {
                        val edx = p.posX - enemy.posX
                        val edy = p.posY - enemy.posY
                        val hitDistSq = (p.radius + enemy.collisionRadius) * (p.radius + enemy.collisionRadius)
                        if (edx * edx + edy * edy <= hitDistSq) {
                            if (p.isAoe) {
                                detonateAoe(p)
                                p.active = false
                                break
                            } else {
                                applyDamageToEnemy(enemy, p.damage, isCrit = Random.nextFloat() < 0.15f)
                                p.pierceRemaining--
                                if (p.pierceRemaining <= 0) {
                                    p.active = false
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun detonateAoe(p: ProjectileEntity) {
        val count = spatialGrid.queryRadius(p.posX, p.posY, p.aoeRadius)
        for (j in 0 until count) {
            val enemyIdx = spatialGrid.queryResultBuffer[j]
            val enemy = enemies[enemyIdx]
            if (enemy.active) {
                applyDamageToEnemy(enemy, p.damage, isCrit = false)
            }
        }
        AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.EXPLOSION)
        AudioVfxSlots.triggerHaptic(AudioVfxSlots.HapticCue.HEAVY_IMPACT)
    }

    private fun updateEnemies(dt: Float) {
        spatialGrid.clear()

        val nightSpeedMult = if (isNight) GameConfig.NIGHT_ENEMY_SPEED_MULT else 1.0f
        val nightDamageMult = if (isNight) GameConfig.NIGHT_ENEMY_DAMAGE_MULT else 1.0f

        for (i in 0 until enemies.size) {
            val e = enemies[i]
            if (!e.active) continue

            // Insert into spatial grid for broadphase queries
            spatialGrid.insert(i, e.posX, e.posY)

            // Hit flash timer decay
            if (e.hitFlashTimer > 0f) {
                e.hitFlashTimer -= dt
            }

            // Path towards player
            val dx = player.posX - e.posX
            val dy = player.posY - e.posY
            val dist = sqrt(dx * dx + dy * dy)

            if (dist > 0.001f) {
                val dirX = dx / dist
                val dirY = dy / dist
                val speed = e.speed * nightSpeedMult

                e.posX += dirX * speed * dt
                e.posY += dirY * speed * dt
            }

            // Ranged shooting check
            if (e.canShoot && dist < 500f) {
                e.shootCooldownTimer -= dt
                if (e.shootCooldownTimer <= 0f) {
                    e.shootCooldownTimer = e.shootInterval
                    val shootAngle = atan2(dy, dx)
                    spawnProjectile(
                        x = e.posX,
                        y = e.posY,
                        angle = shootAngle,
                        speed = 260f,
                        damage = e.bulletDamage * nightDamageMult,
                        radius = 9f,
                        range = 450f,
                        pierce = 1,
                        colorHex = 0xFFFF1744,
                        isHostile = true
                    )
                }
            }

            // Contact damage to player
            if (dist <= e.collisionRadius + GameConfig.PLAYER_COLLISION_RADIUS) {
                damagePlayer(e.contactDamage * nightDamageMult * dt)
            }
        }
    }

    private fun applyDamageToEnemy(enemy: EnemyEntity, damage: Float, isCrit: Boolean) {
        val finalDamage = if (isCrit) damage * 2.0f else damage
        enemy.hp -= finalDamage
        enemy.hitFlashTimer = 0.08f

        spawnDamageNumber(enemy.posX, enemy.posY, finalDamage.toInt(), isCrit)

        if (enemy.hp <= 0f) {
            killEnemy(enemy)
        }
    }

    private fun killEnemy(enemy: EnemyEntity) {
        enemy.active = false
        player.kills++

        // Spawn XP Gem
        spawnGem(enemy.posX, enemy.posY, enemy.xpValue)

        // Scavenge materials (soft economy)
        runScrapEarned += (enemy.xpValue * 2)
        if (Random.nextFloat() < 0.25f) runMetalEarned += 1
        if (Random.nextFloat() < 0.20f) runWoodEarned += 1
        if (enemy.isBoss) {
            runElectronicsEarned += 5
            runScrapEarned += 50
        }

        if (enemy.explodesOnDeath && enemy.explosionRadius > 0f) {
            // Explode damage around death location
            val count = spatialGrid.queryRadius(enemy.posX, enemy.posY, enemy.explosionRadius)
            for (j in 0 until count) {
                val otherIdx = spatialGrid.queryResultBuffer[j]
                val other = enemies[otherIdx]
                if (other.active && other != enemy) {
                    applyDamageToEnemy(other, 40f, isCrit = false)
                }
            }
        }

        EventBus.publish(GameEvent.EnemyKilled(enemy.id, enemy.posX, enemy.posY, enemy.xpValue, enemy.isBoss))
    }

    private fun spawnEnemy(def: EnemyDefinition, isBoss: Boolean) {
        for (i in 0 until enemies.size) {
            val e = enemies[i]
            if (!e.active) {
                e.active = true
                e.id = def.id
                e.maxHp = def.baseHp * map.baseDifficultyMultiplier
                e.hp = e.maxHp
                e.speed = def.baseSpeed
                e.contactDamage = def.contactDamage
                e.collisionRadius = def.collisionRadius
                e.xpValue = def.xpValue
                e.colorHex = def.colorHex
                e.isBoss = isBoss
                e.canShoot = def.canShoot
                e.shootInterval = def.shootCooldownSec
                e.shootCooldownTimer = def.shootCooldownSec
                e.bulletDamage = def.bulletDamage
                e.explodesOnDeath = def.explodesOnDeath
                e.explosionRadius = def.explosionRadius
                e.hitFlashTimer = 0f

                // Spawn on a circle 550-700px away from player
                val spawnAngle = Random.nextFloat() * 6.28318f
                val spawnDist = 600f + Random.nextFloat() * 150f
                e.posX = player.posX + cos(spawnAngle) * spawnDist
                e.posY = player.posY + sin(spawnAngle) * spawnDist
                break
            }
        }
    }

    private fun spawnGem(x: Float, y: Float, xpValue: Int) {
        for (i in 0 until gems.size) {
            val g = gems[i]
            if (!g.active) {
                g.active = true
                g.posX = x
                g.posY = y
                g.xpValue = xpValue
                g.isMagnetized = false
                g.colorHex = when {
                    xpValue >= 20 -> 0xFFFF1744 // Crimson
                    xpValue >= 5 -> 0xFFFFD700  // Gold
                    else -> 0xFF00E5FF         // Cyan
                }
                break
            }
        }
    }

    private fun updateGems(dt: Float) {
        val pickupDistSq = player.pickupRadius * player.pickupRadius

        for (i in 0 until gems.size) {
            val g = gems[i]
            if (!g.active) continue

            val dx = player.posX - g.posX
            val dy = player.posY - g.posY
            val distSq = dx * dx + dy * dy

            if (distSq <= pickupDistSq || g.isMagnetized) {
                g.isMagnetized = true
                val dist = sqrt(distSq)
                if (dist < 20f) {
                    // Collect gem
                    g.active = false
                    gainXp((g.xpValue * player.xpGainMult).toInt().coerceAtLeast(1))
                    AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.XP_PICKUP)
                } else {
                    val magnetSpeed = 480f * dt
                    g.posX += (dx / dist) * magnetSpeed
                    g.posY += (dy / dist) * magnetSpeed
                }
            }
        }
    }

    private fun gainXp(amount: Int) {
        player.currentXp += amount
        if (player.currentXp >= player.xpToNextLevel) {
            player.currentXp -= player.xpToNextLevel
            player.level++
            // Scaling XP formula: 15 * level^1.2
            player.xpToNextLevel = (15f * Math.pow(player.level.toDouble(), 1.25)).toInt()
            triggerLevelUp()
        }
    }

    private fun triggerLevelUp() {
        isLevelUpPending = true
        AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.LEVEL_UP)
        AudioVfxSlots.triggerHaptic(AudioVfxSlots.HapticCue.LEVEL_UP_PULSE)
        EventBus.publish(GameEvent.PlayerLeveledUp(player.level))

        // Select 3 random valid upgrades
        pendingUpgradeChoices = selectLevelUpChoices()
    }

    fun selectLevelUpChoices(): List<UpgradeDefinition> {
        val available = UpgradeRegistry.UPGRADES.filter { up ->
            when (up.type) {
                UpgradeType.WEAPON_UNLOCK -> {
                    val alreadyEquipped = player.activeWeapons.any { it.definition.id == up.targetWeaponId }
                    !alreadyEquipped && player.activeWeapons.size < 6
                }
                UpgradeType.PASSIVE_STAT -> {
                    val currentLvl = player.activePassives.getOrDefault(up.id, 0)
                    currentLvl < up.maxLevel
                }
                else -> true
            }
        }.shuffled()

        return available.take(3)
    }

    fun applyUpgradeChoice(upgrade: UpgradeDefinition) {
        when (upgrade.type) {
            UpgradeType.WEAPON_UNLOCK -> {
                upgrade.targetWeaponId?.let { wepId ->
                    val def = WeaponRegistry.getById(wepId)
                    player.activeWeapons.add(ActiveWeaponSlot(def, level = 1))
                }
            }
            UpgradeType.PASSIVE_STAT -> {
                val currentLvl = player.activePassives.getOrDefault(upgrade.id, 0) + 1
                player.activePassives[upgrade.id] = currentLvl

                // Recalculate player multipliers
                when (upgrade.id) {
                    "pass_might" -> player.damageMult += 0.10f
                    "pass_bullet_speed" -> player.projSpeedMult += 0.15f
                    "pass_cooldown" -> player.cooldownReduction = (player.cooldownReduction + 0.08f).coerceAtMost(0.6f)
                    "pass_area_size" -> player.areaMult += 0.12f
                    "pass_duration" -> player.durationMult += 0.20f
                    "pass_armor" -> player.armorFlat += 2.0f
                    "pass_vitality" -> {
                        player.maxHp += 20f
                        player.hp = (player.hp + 20f).coerceAtMost(player.maxHp)
                    }
                    "pass_magnet" -> player.pickupRadius += 30f
                    "pass_growth" -> player.xpGainMult += 0.12f
                    "pass_regeneration" -> player.hpRegenSec += 0.8f
                    "pass_revive" -> player.hasRevive = true
                }
            }
            else -> {}
        }
        isLevelUpPending = false
    }

    private fun damagePlayer(amount: Float) {
        if (player.godMode) return
        val effectiveDamage = (amount - player.armorFlat).coerceAtLeast(1.0f)
        player.hp -= effectiveDamage

        AudioVfxSlots.playSound(AudioVfxSlots.SoundCue.PLAYER_HURT)
        AudioVfxSlots.triggerHaptic(AudioVfxSlots.HapticCue.LIGHT_TICK)
        EventBus.publish(GameEvent.PlayerDamaged(player.hp, player.maxHp, effectiveDamage))

        if (player.hp <= 0f) {
            if (player.hasRevive && !player.reviveUsed) {
                player.reviveUsed = true
                player.hp = player.maxHp * 0.5f
                AudioVfxSlots.triggerHaptic(AudioVfxSlots.HapticCue.LEVEL_UP_PULSE)
            } else {
                player.isAlive = false
                AudioVfxSlots.triggerHaptic(AudioVfxSlots.HapticCue.DEATH_RATTLE)
            }
        }
    }

    private fun spawnDamageNumber(x: Float, y: Float, damage: Int, isCrit: Boolean) {
        for (i in 0 until damageNumbers.size) {
            val d = damageNumbers[i]
            if (!d.active) {
                d.active = true
                d.posX = x
                d.posY = y
                d.damage = damage
                d.isCrit = isCrit
                d.lifeRemaining = 0.6f
                break
            }
        }
    }

    private fun updateDamageNumbers(dt: Float) {
        for (i in 0 until damageNumbers.size) {
            val d = damageNumbers[i]
            if (!d.active) continue
            d.posY -= 45f * dt
            d.lifeRemaining -= dt
            if (d.lifeRemaining <= 0f) {
                d.active = false
            }
        }
    }

    private fun findClosestEnemyToPlayer(radius: Float): EnemyEntity? {
        val count = spatialGrid.queryRadius(player.posX, player.posY, radius)
        var closest: EnemyEntity? = null
        var closestDistSq = radius * radius

        for (i in 0 until count) {
            val idx = spatialGrid.queryResultBuffer[i]
            val e = enemies[idx]
            if (e.active) {
                val dx = e.posX - player.posX
                val dy = e.posY - player.posY
                val dSq = dx * dx + dy * dy
                if (dSq < closestDistSq) {
                    closestDistSq = dSq
                    closest = e
                }
            }
        }
        return closest
    }

    private fun recalculateActiveCounts() {
        var eCount = 0
        for (e in enemies) if (e.active) eCount++
        activeEnemiesCount = eCount

        var pCount = 0
        for (p in projectiles) if (p.active) pCount++
        activeProjectilesCount = pCount

        var gCount = 0
        for (g in gems) if (g.active) gCount++
        activeGemsCount = gCount

        // Estimated draw calls: terrain grid + gems batch + enemies batch + projectiles batch + damage text
        drawCallsEstimate = 5 + (activeEnemiesCount / 20) + (activeProjectilesCount / 20)
    }

    /**
     * Debug cheats for QA balance testing.
     */
    fun debugSpawnHorde(count: Int) {
        val def = EnemyRegistry.getById("crawler")
        for (i in 0 until count) {
            spawnEnemy(def, false)
        }
    }

    fun debugKillAllEnemies() {
        for (e in enemies) {
            if (e.active) killEnemy(e)
        }
    }

    fun debugTriggerLevelUp() {
        triggerLevelUp()
    }

    fun debugToggleGodMode() {
        player.godMode = !player.godMode
    }

    fun debugSetNight(night: Boolean) {
        dayCycleTimeSec = if (night) 110f else 30f
        updateDayNightCycle(0f)
    }
}
