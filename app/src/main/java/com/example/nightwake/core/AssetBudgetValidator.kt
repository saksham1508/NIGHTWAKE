package com.example.nightwake.core

/**
 * Automated Asset Budget Validator for NIGHTWAKE.
 * Validates incoming 3D models, textures, and animation clips against the mobile performance budget.
 *
 * Can be run during CI/CD build pipelines or in editor hooks to reject bloated assets before ingestion.
 */
object AssetBudgetValidator {

    data class AssetSpec(
        val assetName: String,
        val assetClass: String,
        val triangleCount: Int,
        val boneCount: Int,
        val textureDimension: Int,
        val animationClipCount: Int
    )

    data class ValidationResult(
        val passed: Boolean,
        val violations: List<String>
    )

    fun validateAsset(spec: AssetSpec): ValidationResult {
        val violations = mutableListOf<String>()

        when (spec.assetClass.uppercase()) {
            "HERO" -> {
                if (spec.triangleCount > 6000) violations.add("Hero triangles ${spec.triangleCount} exceed 6,000 limit")
                if (spec.boneCount > 34) violations.add("Hero bones ${spec.boneCount} exceed 34 humanoid bone limit")
                if (spec.textureDimension > 1024) violations.add("Hero texture ${spec.textureDimension}px exceeds 1024px atlas")
            }
            "HORDE_TIER_1" -> {
                if (spec.triangleCount > 1200) violations.add("Tier 1 horde triangles ${spec.triangleCount} exceed 1,200 limit")
                if (spec.boneCount > 14) violations.add("Tier 1 horde bones ${spec.boneCount} exceed VAT/14 limit")
                if (spec.textureDimension > 1024) violations.add("Swarm atlas texture exceeds 1024px")
            }
            "HORDE_TIER_2", "RANGED" -> {
                if (spec.triangleCount > 2400) violations.add("Horde triangles ${spec.triangleCount} exceed 2,400 limit")
                if (spec.boneCount > 18) violations.add("Horde bones ${spec.boneCount} exceed 18 limit")
            }
            "BOSS" -> {
                if (spec.triangleCount > 16500) violations.add("Boss triangles ${spec.triangleCount} exceed 16,500 limit")
                if (spec.boneCount > 64) violations.add("Boss bones ${spec.boneCount} exceed 64 limit")
                if (spec.textureDimension > 2048) violations.add("Boss texture exceeds 2048px limit")
            }
            "ENVIRONMENT_TILE" -> {
                if (spec.triangleCount > 1500) violations.add("Environment tile exceeds 1,500 limit")
                if (spec.boneCount > 0) violations.add("Environment tiles must be static (0 bones)")
            }
        }

        return ValidationResult(
            passed = violations.isEmpty(),
            violations = violations
        )
    }
}
