package com.holosphere.launcher.system

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent

object LauncherRole {
    fun isDefault(context: Context): Boolean =
        context.getSystemService(RoleManager::class.java)?.isRoleHeld(RoleManager.ROLE_HOME) == true

    fun requestIntent(context: Context): Intent? =
        context.getSystemService(RoleManager::class.java)?.takeIf {
            it.isRoleAvailable(RoleManager.ROLE_HOME) && !it.isRoleHeld(RoleManager.ROLE_HOME)
        }?.createRequestRoleIntent(RoleManager.ROLE_HOME)
}
