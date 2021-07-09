package com.karleinstein.fastpermissions

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker

class FastPermissionActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FastPermission.getPermissionActivity(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val deniedPermissions = mutableListOf<String>()
        val deniedPermissionsForever = mutableListOf<String>()
        permissions.forEachIndexed { i, permission ->
            Log.d("FastPermissions", "onRequestPermissionsResult: $permission")
            val isShouldShowRationale = shouldShowRequestPermissionRationale(permission)
            when (grantResults[i]) {
                PermissionChecker.PERMISSION_DENIED, PermissionChecker.PERMISSION_DENIED_APP_OP -> {
                    if (!isShouldShowRationale) deniedPermissionsForever.add(permission)
                    else deniedPermissions.add(permission)
                }
                else -> {
                    finish()
                    FastPermission.onSuccess()
                }
            }
        }
        FastPermission.getDeniedPermissions(deniedPermissions, deniedPermissionsForever)
    }

    override fun onDestroy() {
        super.onDestroy()
        FastPermission.destroyPermissionActivity()
    }
}
