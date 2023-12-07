package com.example.duckstop

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.duckstop.R

class MainActivity : AppCompatActivity() {

    private val DRAW_OVER_OTHER_APPS_REQUEST_CODE = 1002
    private val USAGE_STATS_REQUEST_CODE = 1007

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            requestDrawOverAppsPermission()
        } else {
            checkUsageStatsPermission()
        }
    }

    private fun requestDrawOverAppsPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("We need permission to draw over other apps for the app to work.")
            .setPositiveButton("OK") { _, _ ->
                openDrawOverAppsSettings()
            }
            .setCancelable(false)
            .show()
    }

    private fun openDrawOverAppsSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = android.net.Uri.parse("package:$packageName")
        startActivityForResult(intent, DRAW_OVER_OTHER_APPS_REQUEST_CODE)
    }

    private fun checkUsageStatsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && !hasUsageStatsPermission()) {
            requestUsageStatsPermission()
        } else {
            startAppUsageMonitoringService()
        }
    }

    private fun requestUsageStatsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivityForResult(intent, USAGE_STATS_REQUEST_CODE)
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun startAppUsageMonitoringService() {
        // Start the service to monitor app usage
        startService(Intent(this, AppUsageService::class.java))
    }

    // Override onActivityResult to handle permission results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DRAW_OVER_OTHER_APPS_REQUEST_CODE, USAGE_STATS_REQUEST_CODE -> {
                checkUsageStatsPermission()
            }
        }
    }
}
