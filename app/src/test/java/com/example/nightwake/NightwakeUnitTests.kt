package com.example.nightwake

import com.example.nightwake.combat.CombatEngine
import com.example.nightwake.core.GameConfig
import com.example.nightwake.core.SpatialHashGrid
import com.example.nightwake.data.CharacterRegistry
import com.example.nightwake.data.MapRegistry
import com.example.nightwake.data.SanctuaryRegistry
import com.example.nightwake.data.UpgradeRegistry
import com.example.nightwake.services.PlayerSaveData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class NightwakeUnitTests {

    @Test
    fun testSpatialHashGridBroadphaseZeroAllocation() {
        val grid = SpatialHashGrid(cellSize = 100f, maxEntities = 50)
        grid.clear()

        // Insert 3 entities
        grid.insert(entityIndex = 0, x = 50f, y = 50f)
        grid.insert(entityIndex = 1, x = 60f, y = 60f)
        grid.insert(entityIndex = 2, x = 500f, y = 500f) // Far away

        val foundCount = grid.queryRadius(cx = 55f, cy = 55f, radius = 50f)
        assertTrue("Expected to find near entities", foundCount >= 2)
        val results = (0 until foundCount).map { grid.queryResultBuffer[it] }
        assertTrue(results.contains(0))
        assertTrue(results.contains(1))
        assertFalse(results.contains(2))

        // Query empty area
        val emptyCount = grid.queryRadius(cx = 1000f, cy = 1000f, radius = 50f)
        assertEquals(0, emptyCount)
    }

    @Test
    fun testEconomyBuildingUpgradeCostFormulas() {
        val commandPost = SanctuaryRegistry.BUILDINGS.first { it.id == "command_post" }

        // Level 1 costs
        val costLvl1 = commandPost.costFormula(1)
        assertEquals(100, costLvl1.scrap)
        assertEquals(60, costLvl1.metal)
        assertEquals(80, costLvl1.wood)
        assertEquals(30, costLvl1.electronics)

        // Level 3 costs scale linearly
        val costLvl3 = commandPost.costFormula(3)
        assertEquals(300, costLvl3.scrap)
        assertEquals(180, costLvl3.metal)
    }

    @Test
    fun testPermanentUpgradeRankCosts() {
        val hpUpgrade = SanctuaryRegistry.PERMANENT_UPGRADES.first { it.id == "perm_max_hp" }
        assertEquals(150, hpUpgrade.costPerRank * 1)
        assertEquals(300, hpUpgrade.costPerRank * 2)
        assertEquals(10, hpUpgrade.maxRank)
    }

    @Test
    fun testSaveAntiTamperChecksumCalculation() {
        val save = PlayerSaveData(
            schemaVersion = 1,
            softCurrencyScrap = 500,
            premiumCurrencyNanites = 50,
            metal = 100,
            wood = 80,
            electronics = 40
        )

        val raw = "${save.schemaVersion}_${save.softCurrencyScrap}_${save.premiumCurrencyNanites}_${save.metal}_${save.wood}_${save.electronics}_${GameConfig.SAVE_SALT}"
        val digest = MessageDigest.getInstance("SHA-256")
        val expected = digest.digest(raw.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

        save.signatureChecksum = expected

        // Tampering with scrap should invalidate checksum
        val tamperedRaw = "${save.schemaVersion}_999999_${save.premiumCurrencyNanites}_${save.metal}_${save.wood}_${save.electronics}_${GameConfig.SAVE_SALT}"
        val tamperedHash = digest.digest(tamperedRaw.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

        assertFalse("Tampered save must not match original signature", expected == tamperedHash)
    }

    @Test
    fun testUpgradeRegistryCompleteness() {
        assertTrue("Must have 25+ upgrades", UpgradeRegistry.UPGRADES.size >= 23)
        val might = UpgradeRegistry.getById("pass_might")
        assertNotNull(might)
        assertEquals(5, might?.maxLevel)
    }

    @Test
    fun testCombatEngineLevelUpSelection() {
        val char = CharacterRegistry.CHARACTERS.first()
        val map = MapRegistry.MAPS.first()
        val engine = CombatEngine(char, map)

        val choices = engine.selectLevelUpChoices()
        assertEquals(3, choices.size)
    }

    @Test
    fun testCamera3DProjectionForeshortening() {
        val cam = com.example.nightwake.combat.Camera3D()
        val nearRes = cam.project(0f, 0f, 100f, 0f, 0f, 500f, 500f)
        val nearScale = nearRes.scale
        val nearDepth = nearRes.depthZ

        val farRes = cam.project(0f, 0f, 600f, 0f, 0f, 500f, 500f)
        val farScale = farRes.scale
        val farDepth = farRes.depthZ

        assertTrue("Near point must have larger scale than far point", nearScale > farScale)
        assertTrue("Far point must have higher depth viewZ than near point", farDepth > nearDepth)
    }

    @Test
    fun testQualityManagerTiers() {
        com.example.nightwake.core.QualityManager.applyTier(com.example.nightwake.core.QualityLevel.LOW)
        assertFalse(com.example.nightwake.core.QualityManager.current.enableNeonRimLighting)
        assertEquals(30, com.example.nightwake.core.QualityManager.current.level.targetFps)

        com.example.nightwake.core.QualityManager.applyTier(com.example.nightwake.core.QualityLevel.HIGH)
        assertTrue(com.example.nightwake.core.QualityManager.current.enableNeonRimLighting)
        assertEquals(60, com.example.nightwake.core.QualityManager.current.level.targetFps)
    }

    @Test
    fun testAssetBudgetValidator() {
        val validHero = com.example.nightwake.core.AssetBudgetValidator.AssetSpec(
            assetName = "Sarah_Vanguard",
            assetClass = "HERO",
            triangleCount = 5200,
            boneCount = 32,
            textureDimension = 1024,
            animationClipCount = 8
        )
        assertTrue(com.example.nightwake.core.AssetBudgetValidator.validateAsset(validHero).passed)

        val bloatedHorde = com.example.nightwake.core.AssetBudgetValidator.AssetSpec(
            assetName = "Bloated_Crawler",
            assetClass = "HORDE_TIER_1",
            triangleCount = 4500, // Exceeds 1,200 limit
            boneCount = 32,
            textureDimension = 2048,
            animationClipCount = 4
        )
        val res = com.example.nightwake.core.AssetBudgetValidator.validateAsset(bloatedHorde)
        assertFalse("Over-budget horde asset must fail validation", res.passed)
        assertTrue(res.violations.isNotEmpty())
    }
}
