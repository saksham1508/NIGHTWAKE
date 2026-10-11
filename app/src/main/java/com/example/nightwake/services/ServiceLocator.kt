package com.example.nightwake.services

import android.content.Context
import com.example.nightwake.core.AudioVfxSlots

/**
 * Service Locator & Bootstrapper.
 * Central access point for all platform and gameplay services.
 * Allows effortless unit-testing mock replacement.
 */
object ServiceLocator {
    lateinit var ads: IAdService
        private set
    lateinit var purchases: IPurchaseService
        private set
    lateinit var analytics: IAnalyticsService
        private set
    lateinit var save: ISaveService
        private set
    lateinit var notifications: INotificationService
        private set
    lateinit var remoteConfig: IRemoteConfigService
        private set

    private var initialized = false

    fun init(context: Context) {
        if (initialized) return

        ads = MockAdService()
        purchases = MockPurchaseService()
        analytics = DebugAnalyticsService()
        save = JsonSaveService(context.applicationContext)
        notifications = LocalNotificationService()
        remoteConfig = DefaultRemoteConfigService()

        AudioVfxSlots.initialize(context.applicationContext)
        initialized = true
    }

    /**
     * Testing override for unit tests.
     */
    fun setServicesForTesting(
        saveService: ISaveService? = null,
        adService: IAdService? = null,
        purchaseService: IPurchaseService? = null,
        analyticsService: IAnalyticsService? = null
    ) {
        saveService?.let { save = it }
        adService?.let { ads = it }
        purchaseService?.let { purchases = it }
        analyticsService?.let { analytics = it }
    }
}
