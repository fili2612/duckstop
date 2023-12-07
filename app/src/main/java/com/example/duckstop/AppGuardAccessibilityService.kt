package com.example.duckstop

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AppGuardAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            if (packageName != null && monitoredApps.contains(packageName)) {
                Log.d(TAG, "Monitored app launched: $packageName")
                showToast("Opened: $packageName")
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
        // Do nothing
    }

    private fun showToast(message: String) {
        Log.d(TAG, "Showing Toast: $message")
        // Displaying a Toast from a background service is not recommended,
        // but it's included here for testing purposes
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "AppGuardAccessibilityService"
        private val monitoredApps = setOf(
            "com.instagram.android",
            "com.snapchat.android",
            "com.zhiliaoapp.musically"
        )
    }
}
