package com.example.nightwake.services

/**
 * Interface for in-app purchases and Battle Pass unlocking.
 * Adheres strictly to the no-P2W design policy.
 */
interface IPurchaseService {
    data class ProductCatalogItem(
        val productId: String,
        val title: String,
        val description: String,
        val priceFormatted: String,
        val nanitesReward: Int = 0,
        val isBattlePass: Boolean = false,
        val isNoAdsBundle: Boolean = false
    )

    fun getCatalog(): List<ProductCatalogItem>
    fun purchaseProduct(productId: String, onResult: (Boolean, String?) -> Unit)
    fun restorePurchases(onComplete: (Boolean) -> Unit)
    fun hasBattlePass(): Boolean
    fun hasNoAds(): Boolean
}

class MockPurchaseService : IPurchaseService {
    private var isBattlePassOwned: Boolean = false
    private var isNoAdsOwned: Boolean = false

    private val catalog = listOf(
        IPurchaseService.ProductCatalogItem(
            productId = "nightwake.starter_pack",
            title = "Survivor Supply Drop",
            description = "100 Nanites + 500 Scrap + 5 Medkits (One-time value)",
            priceFormatted = "$1.99",
            nanitesReward = 100
        ),
        IPurchaseService.ProductCatalogItem(
            productId = "nightwake.nanites_pouch",
            title = "Nanite Battery Pack",
            description = "300 Premium Nanites for cosmetics and blueprints",
            priceFormatted = "$4.99",
            nanitesReward = 300
        ),
        IPurchaseService.ProductCatalogItem(
            productId = "nightwake.battlepass_s1",
            title = "Nightwatch Pass: Season 1",
            description = "Unlock 30 tiers of cosmetic weapon skins and blueprints",
            priceFormatted = "$9.99",
            isBattlePass = true
        ),
        IPurchaseService.ProductCatalogItem(
            productId = "nightwake.ad_free",
            title = "Ad-Free Radio Jammer",
            description = "Permanently removes all non-rewarded ad prompts and auto-claims daily crates",
            priceFormatted = "$2.99",
            isNoAdsBundle = true
        )
    )

    override fun getCatalog(): List<IPurchaseService.ProductCatalogItem> = catalog

    override fun purchaseProduct(productId: String, onResult: (Boolean, String?) -> Unit) {
        when (productId) {
            "nightwake.battlepass_s1" -> isBattlePassOwned = true
            "nightwake.ad_free" -> isNoAdsOwned = true
        }
        onResult(true, null)
    }

    override fun restorePurchases(onComplete: (Boolean) -> Unit) {
        onComplete(true)
    }

    override fun hasBattlePass(): Boolean = isBattlePassOwned

    override fun hasNoAds(): Boolean = isNoAdsOwned
}
