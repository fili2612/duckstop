// ForegroundAppService.kt
package com.example.duckstop

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class ForegroundAppService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val foregroundApp = event.packageName?.toString()
            showToast("Detected $foregroundApp")
            if (monitoredApps.contains(foregroundApp)) {
                Log.d(TAG, "Monitored app detected: $foregroundApp")
                // Show a popup or perform an action here
                showPopup(foregroundApp)
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
        // Not used
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showPopup(appName: String?) {
        Log.d(TAG, "Showing popup for $appName")
        // Code to show your popup or notification
        // For simplicity, let's show a toast message
        showToast("Detected $appName")
    }

    companion object {
        private const val TAG = "ForegroundAppService"
        private val monitoredApps = setOf("com.instagram.android", "com.snapchat.android", "com.zhiliaoapp.musically")
    }
}
