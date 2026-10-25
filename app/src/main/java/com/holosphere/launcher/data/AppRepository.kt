package com.holosphere.launcher.data

import android.content.Context
import android.content.Intent
import com.holosphere.launcher.domain.normalizeForSearch

data class InstalledApp(val label: String, val packageName: String, val activityName: String)

class AppRepository(private val context: Context) {
    fun load(): List<InstalledApp> = context.packageManager
        .queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
        .map { info -> InstalledApp(info.loadLabel(context.packageManager).toString(), info.activityInfo.packageName, info.activityInfo.name) }
        .distinctBy { it.packageName }
        .sortedBy { it.label.normalizeForSearch() }

    fun launch(app: InstalledApp) {
        context.startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setClassName(app.packageName, app.activityName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

