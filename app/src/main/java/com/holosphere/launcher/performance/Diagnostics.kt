package com.holosphere.launcher.performance

import android.os.Debug
import android.os.SystemClock

data class StartupDiagnostics(val elapsedMs: Long, val pssKb: Long)

class Diagnostics {
    private val startedAt = SystemClock.elapsedRealtime()
    fun captureStartup(): StartupDiagnostics = StartupDiagnostics(SystemClock.elapsedRealtime() - startedAt, Debug.getPss())
}
