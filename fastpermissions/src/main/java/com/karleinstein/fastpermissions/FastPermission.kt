package com.karleinstein.fastpermissions

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.*

object FastPermission {

    private var remainingPermissions: List<String> = mutableListOf()
    private var onPermissionsListener: PermissionsListener? = null

    fun setOnDeniedPermissionsListener(onPermissionsListener: PermissionsListener?) {
        this.onPermissionsListener = onPermissionsListener
    }

    internal fun destroyPermissionActivity() {
        onPermissionsListener = null
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
        deniedPermissions.forEach {
            Log.d("FastPermissions", "getDeniedPermissions: $it")
        }
        deniedPermissionsForever.forEach {
            Log.d("FastPermissions", "getDeniedPermissions: $it")
        }
        if (deniedPermissionsForever.isNotEmpty()) onPermissionsListener?.onPermissionDeniedForever(
            deniedPermissionsForever
        )
        if (deniedPermissions.isNotEmpty()) onPermissionsListener?.onPermissionDenied(
            deniedPermissions
        )
    }

    internal fun onSuccesed() {
        onPermissionsListener?.onGranted()
        onPermissionsListener = null
    }

    fun check(
        activity: Activity,
        permissions: List<String>,
        onPermissionsListener: PermissionsListener
    ) {
        this.onPermissionsListener=onPermissionsListener
        val isAllPermissionsGranted = activity.isPermissionGranted(permissions)
        Log.d("FastPermissions", "check: ${isAllPermissionsGranted.first}")
        if (isAllPermissionsGranted.first) {
            this.onPermissionsListener?.onGranted()
        } else {
            remainingPermissions = isAllPermissionsGranted.second
            startActivityTransparent(activity)
        }
    }

    private fun startActivityTransparent(activity: Activity) {
        val intent = Intent(activity, FastPermissionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        Log.d("karleinstein", "${activity.isDestroyed}")
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
        dialogExplainListener: DialogExplainListener
    ) {
        AlertDialog.Builder(activity)
            .setMessage(content)
            .setTitle(title)
            .setPositiveButton("OK") { _, _ ->
                dialogExplainListener.onAllowClicked()
            }
            .show()
    }

    interface PermissionsListener {
        fun onGranted()
        fun onPermissionDeniedForever(deniedPermissionsForever: List<String>)
        fun onPermissionDenied(deniedPermissions: List<String>)
    }

    interface DialogExplainListener {
        fun onAllowClicked()
    }
}

