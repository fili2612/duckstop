package com.example.duckstop

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.core.app.NotificationCompat

class AppUsageService : Service() {

    private lateinit var notificationManager: NotificationManager
    private val monitoredApps = setOf("com.zhiliaoapp.musically")

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAppUsageMonitoring()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startAppUsageMonitoring() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 1000 * 60 // Look back for the last 1 minute

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            beginTime,
            endTime
        )

        if (stats != null) {
            for (usageStats in stats) {
                val packageName = usageStats.packageName
                if (isMonitoredApp(packageName)) {
                    showNotification(packageName)
                    startOverlayService()
                    break
                }
            }
        }
    }

    private fun isMonitoredApp(packageName: String): Boolean {
        return monitoredApps.contains(packageName)
    }

    private fun showNotification(packageName: String) {
        val channelId = "AppUsageServiceChannel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitored App Detected")
            .setContentText("Monitored app detected: $packageName")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder)
    }

    private fun startOverlayService() {
        val overlayIntent = Intent(this, OverlayService::class.java)
        startService(overlayIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Usage Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "AppUsageServiceChannel"
        private const val NOTIFICATION_ID = 1
    }
}