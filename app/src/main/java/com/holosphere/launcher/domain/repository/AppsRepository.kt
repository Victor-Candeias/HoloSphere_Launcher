package com.holosphere.launcher.domain.repository

import com.holosphere.launcher.domain.model.LauncherApp
import kotlinx.coroutines.flow.Flow

interface AppsRepository {
    fun observeApps(): Flow<List<LauncherApp>>
    fun refresh()
    fun launch(app: LauncherApp)
}
