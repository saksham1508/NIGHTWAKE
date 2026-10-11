package com.example.nightwake.services

/**
 * Interface for scheduled local notifications (Sanctuary building completion, fuel refill, night raids).
 */
interface INotificationService {
    fun scheduleLocalNotification(title: String, message: String, delaySeconds: Long)
    fun cancelAllNotifications()
}

class LocalNotificationService : INotificationService {
    override fun scheduleLocalNotification(title: String, message: String, delaySeconds: Long) {
        // Scheduled notification trigger using Android WorkManager or AlarmManager
    }

    override fun cancelAllNotifications() {
        // Clear all scheduled notifications
    }
}
