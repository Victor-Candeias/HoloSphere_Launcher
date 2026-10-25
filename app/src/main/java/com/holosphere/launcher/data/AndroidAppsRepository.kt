package com.holosphere.launcher.data

import android.content.Context
import android.content.Intent
import com.holosphere.launcher.domain.model.LauncherApp
import com.holosphere.launcher.domain.repository.AppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidAppsRepository(private val context: Context) : AppsRepository {
    private val _apps = MutableStateFlow<List<LauncherApp>>(emptyList())

    override fun observeApps(): Flow<List<LauncherApp>> = _apps.asStateFlow()

    override fun refresh() {
        _apps.value = context.packageManager
            .queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            .map { info -> LauncherApp(info.loadLabel(context.packageManager).toString(), info.activityInfo.packageName, info.activityInfo.name) }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    override fun launch(app: LauncherApp) {
        context.startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setClassName(app.packageName, app.activityName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
