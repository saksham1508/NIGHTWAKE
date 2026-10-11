package com.example.nightwake.services

import android.content.Context
import com.example.nightwake.core.GameConfig
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

/**
 * Game Save Data Representation (v1.0.0)
 */
data class PlayerSaveData(
    var schemaVersion: Int = 1,
    var playerLevel: Int = 1,
    var softCurrencyScrap: Int = 250,
    var premiumCurrencyNanites: Int = 25,
    var energyFuel: Int = 60,
    var maxEnergyFuel: Int = 60,
    var metal: Int = 100,
    var wood: Int = 80,
    var electronics: Int = 40,
    var unlockedCharacters: MutableList<String> = mutableListOf("vanguard"),
    var selectedCharacterId: String = "vanguard",
    var buildingLevels: MutableMap<String, Int> = mutableMapOf(
        "command_post" to 1,
        "workshop" to 1,
        "armory" to 1,
        "radar" to 1,
        "barricade" to 1
    ),
    var permanentUpgrades: MutableMap<String, Int> = mutableMapOf(
        "perm_max_hp" to 0,
        "perm_damage" to 0,
        "perm_speed" to 0,
        "perm_pickup" to 0,
        "perm_xp_gain" to 0
    ),
    var unlockedChapters: Int = 1,
    var highScores: MutableMap<String, Int> = mutableMapOf(),
    var isTutorialCompleted: Boolean = false,
    var signatureChecksum: String = ""
)

/**
 * Interface for Offline-First Save System with Schema Migrations and Anti-Tamper Checksum.
 */
interface ISaveService {
    fun loadSave(): PlayerSaveData
    fun saveGame(data: PlayerSaveData): Boolean
    fun verifyChecksum(data: PlayerSaveData): Boolean
    fun generateChecksum(data: PlayerSaveData): String
    fun resetSave(): PlayerSaveData
}

class JsonSaveService(private val context: Context) : ISaveService {
    private val saveFileName = "nightwake_save_v1.json"
    private var cachedData: PlayerSaveData? = null

    override fun generateChecksum(data: PlayerSaveData): String {
        // Hash all critical progression fields with GameConfig.SAVE_SALT
        val raw = "${data.schemaVersion}_${data.softCurrencyScrap}_${data.premiumCurrencyNanites}_${data.metal}_${data.wood}_${data.electronics}_${GameConfig.SAVE_SALT}"
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override fun verifyChecksum(data: PlayerSaveData): Boolean {
        if (data.signatureChecksum.isEmpty()) return true // Fresh profile
        val expected = generateChecksum(data)
        return expected == data.signatureChecksum
    }

    override fun loadSave(): PlayerSaveData {
        cachedData?.let { return it }

        val file = File(context.filesDir, saveFileName)
        if (!file.exists()) {
            val fresh = PlayerSaveData()
            fresh.signatureChecksum = generateChecksum(fresh)
            saveGame(fresh)
            cachedData = fresh
            return fresh
        }

        return try {
            val jsonStr = file.readText(Charsets.UTF_8)
            val json = JSONObject(jsonStr)
            val data = parseAndMigrate(json)
            if (!verifyChecksum(data)) {
                // Anti-tamper violation fallback: clamp cheated currency or log flag
                data.softCurrencyScrap = minOf(data.softCurrencyScrap, 5000)
                data.premiumCurrencyNanites = minOf(data.premiumCurrencyNanites, 100)
            }
            cachedData = data
            data
        } catch (e: Exception) {
            val fresh = PlayerSaveData()
            fresh.signatureChecksum = generateChecksum(fresh)
            cachedData = fresh
            fresh
        }
    }

    private fun parseAndMigrate(json: JSONObject): PlayerSaveData {
        val version = json.optInt("schemaVersion", 1)
        val data = PlayerSaveData(schemaVersion = version)

        // Schema migration hooks:
        if (version < 1) {
            // e.g. Migrate legacy alpha save
        }

        data.playerLevel = json.optInt("playerLevel", 1)
        data.softCurrencyScrap = json.optInt("softCurrencyScrap", 250)
        data.premiumCurrencyNanites = json.optInt("premiumCurrencyNanites", 25)
        data.energyFuel = json.optInt("energyFuel", 60)
        data.maxEnergyFuel = json.optInt("maxEnergyFuel", 60)
        data.metal = json.optInt("metal", 100)
        data.wood = json.optInt("wood", 80)
        data.electronics = json.optInt("electronics", 40)
        data.selectedCharacterId = json.optString("selectedCharacterId", "vanguard")
        data.unlockedChapters = json.optInt("unlockedChapters", 1)
        data.isTutorialCompleted = json.optBoolean("isTutorialCompleted", false)
        data.signatureChecksum = json.optString("signatureChecksum", "")

        val charsArray = json.optJSONArray("unlockedCharacters")
        if (charsArray != null) {
            data.unlockedCharacters.clear()
            for (i in 0 until charsArray.length()) {
                data.unlockedCharacters.add(charsArray.getString(i))
            }
        }

        val bObj = json.optJSONObject("buildingLevels")
        if (bObj != null) {
            bObj.keys().forEach { key ->
                data.buildingLevels[key] = bObj.getInt(key)
            }
        }

        val pObj = json.optJSONObject("permanentUpgrades")
        if (pObj != null) {
            pObj.keys().forEach { key ->
                data.permanentUpgrades[key] = pObj.getInt(key)
            }
        }

        return data
    }

    override fun saveGame(data: PlayerSaveData): Boolean {
        return try {
            data.signatureChecksum = generateChecksum(data)
            val json = JSONObject()
            json.put("schemaVersion", data.schemaVersion)
            json.put("playerLevel", data.playerLevel)
            json.put("softCurrencyScrap", data.softCurrencyScrap)
            json.put("premiumCurrencyNanites", data.premiumCurrencyNanites)
            json.put("energyFuel", data.energyFuel)
            json.put("maxEnergyFuel", data.maxEnergyFuel)
            json.put("metal", data.metal)
            json.put("wood", data.wood)
            json.put("electronics", data.electronics)
            json.put("selectedCharacterId", data.selectedCharacterId)
            json.put("unlockedChapters", data.unlockedChapters)
            json.put("isTutorialCompleted", data.isTutorialCompleted)
            json.put("signatureChecksum", data.signatureChecksum)

            val charsArray = JSONArray()
            data.unlockedCharacters.forEach { charsArray.put(it) }
            json.put("unlockedCharacters", charsArray)

            val bObj = JSONObject()
            data.buildingLevels.forEach { (k, v) -> bObj.put(k, v) }
            json.put("buildingLevels", bObj)

            val pObj = JSONObject()
            data.permanentUpgrades.forEach { (k, v) -> pObj.put(k, v) }
            json.put("permanentUpgrades", pObj)

            val file = File(context.filesDir, saveFileName)
            file.writeText(json.toString(), Charsets.UTF_8)
            cachedData = data
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun resetSave(): PlayerSaveData {
        val fresh = PlayerSaveData()
        saveGame(fresh)
        return fresh
    }
}
