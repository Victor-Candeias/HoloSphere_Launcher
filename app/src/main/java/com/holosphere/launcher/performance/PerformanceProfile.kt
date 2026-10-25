package com.holosphere.launcher.performance

enum class PerformanceProfile { ECONOMICAL, BALANCED, VISUAL }

data class PerformanceOptions(
    val profile: PerformanceProfile,
    val maxVisibleNodes: Int,
    val particles: Boolean,
    val parallax: Boolean,
    val maxFps: Int
)

fun PerformanceProfile.options() = when (this) {
    PerformanceProfile.ECONOMICAL -> PerformanceOptions(this, 12, false, false, 30)
    PerformanceProfile.BALANCED -> PerformanceOptions(this, 24, false, false, 60)
    PerformanceProfile.VISUAL -> PerformanceOptions(this, 48, true, true, 60)
}
