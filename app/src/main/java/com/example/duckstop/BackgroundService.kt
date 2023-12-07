package com.example.duckstop

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
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.example.duckstop.R

class BackgroundService : Service() {

    private val monitoredApps = setOf(
        "com.instagram.android",
        "com.snapchat.android",
        "com.zhiliaoapp.musically"
    )

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var overlayView: View
    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initOverlay()
        startAppUsageMonitoring()
    }

    private fun initOverlay() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null)

        // Set up layout parameters for the overlay
        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Set the position of the overlay
        layoutParams.gravity = Gravity.CENTER
    }

    private fun startAppUsageMonitoring() {
        Thread {
            while (true) {
                // Monitor app usage in a loop
                checkForegroundApp()
                Thread.sleep(5000) // Adjust the interval as needed
            }
        }.start()
    }

    private fun checkForegroundApp() {
        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val endTime = System.currentTimeMillis()
        val startTime = endTime - 10000 // 10 seconds ago

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)

        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                val packageName = event.packageName
                Log.d(TAG, "Foreground app: $packageName")
                if (monitoredApps.contains(packageName)) {
                    // Show your overlay here
                    handler.post {
                        showOverlay()
                    }
                }
            }
        }
    }

    private fun showOverlay() {
        if (!isOverlayVisible()) {
            windowManager.addView(overlayView, layoutParams)
        }
    }

    private fun hideOverlay() {
        if (isOverlayVisible()) {
            windowManager.removeView(overlayView)
        }
    }

    private fun isOverlayVisible(): Boolean {
        return overlayView.windowToken != null
    }

    companion object {
        private const val TAG = "BackgroundService"
    }
}
