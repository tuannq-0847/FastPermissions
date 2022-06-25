package com.karleinstein.fastpermissions

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

object FastPermission {

    private var remainingPermissions: List<String> = mutableListOf()

    private var onGranted = {}
    private var onPermissionDeniedForever = fun(_: List<String>) {}
    private var onPermissionDenied = fun(_: List<String>) {}

    internal fun destroyPermissionActivity() {
        remainingPermissions.toMutableList().clear()
    }

    internal fun getPermissionActivity(activity: Activity) {
        Log.d("FastPermissions", "getPermissionActivity: ${remainingPermissions.size}")
        if (remainingPermissions.isEmpty()) return
        ActivityCompat.requestPermissions(activity, remainingPermissions.toTypedArray(), 8080)
    }

    internal fun getDeniedPermissions(
        deniedPermissions: List<String>,
        deniedPermissionsForever: List<String>
    ) {
        Log.d("FastPermissions", "getDeniedPermissions: $deniedPermissions")
        Log.d("FastPermissions", "getDeniedPermissions: $deniedPermissionsForever")
        if (deniedPermissionsForever.isNotEmpty()) onPermissionDeniedForever(
            deniedPermissionsForever
        )
        if (deniedPermissions.isNotEmpty()) onPermissionDenied(
            deniedPermissions
        )
    }

    fun check(
        activity: Activity,
        permissions: List<String>,
        onGranted: () -> Unit,
        onPermissionDenied: (deniedPermissionsForever: List<String>) -> Unit,
        onDontAskAgain: (deniedPermissions: List<String>) -> Unit,
    ) {
        this.onGranted = onGranted
        this.onPermissionDenied = onPermissionDenied
        onPermissionDeniedForever = onDontAskAgain
        val isAllPermissionsGranted = activity.isPermissionGranted(permissions)
        Log.d("FastPermissions", "check: ${isAllPermissionsGranted.first}")
        if (isAllPermissionsGranted.first) {
            onGranted()
        } else {
            remainingPermissions = isAllPermissionsGranted.second
            startActivityTransparent(activity)
        }
    }

    private fun startActivityTransparent(activity: Activity) {
        val intent = Intent(activity, FastPermissionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        if (!activity.isDestroyed)
            activity.startActivity(intent)
    }

    private fun Activity.isPermissionGranted(permissions: List<String>): Pair<Boolean, List<String>> {
        val remainingPermissions = mutableListOf<String>()
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(
                    this@isPermissionGranted,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                remainingPermissions.add(it)
            }
        }
        if (remainingPermissions.isNotEmpty()) return Pair(false, remainingPermissions)
        return Pair(true, listOf())
    }

    fun showDialogExplain(
        activity: Activity,
        title: String,
        content: String,
        titlePositiveButton: String,
        onAllowClicked: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setMessage(content)
            .setTitle(title)
            .setPositiveButton(titlePositiveButton) { _, _ ->
                onAllowClicked()
            }
            .show()
    }

    internal fun onSuccess() {
        onGranted()
    }
}

