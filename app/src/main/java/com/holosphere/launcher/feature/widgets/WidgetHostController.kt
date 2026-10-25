package com.holosphere.launcher.feature.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.Context

class WidgetHostController(context: Context) {
    private val host = AppWidgetHost(context, HOST_ID)
    val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
    fun start() = host.startListening()
    fun stop() = host.stopListening()
    fun allocateId(): Int = host.allocateAppWidgetId()
    fun createView(context: Context, appWidgetId: Int, info: AppWidgetProviderInfo): AppWidgetHostView = host.createView(context, appWidgetId, info)

    companion object { const val HOST_ID = 1427 }
}
