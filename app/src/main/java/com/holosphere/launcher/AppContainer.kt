package com.holosphere.launcher

import android.content.Context
import com.holosphere.launcher.data.AndroidAppsRepository
import com.holosphere.launcher.data.SettingsRepository

class AppContainer(context: Context) {
    private val applicationContext = context.applicationContext
    val appsRepository = AndroidAppsRepository(applicationContext)
    val settingsRepository = SettingsRepository(applicationContext)
}
