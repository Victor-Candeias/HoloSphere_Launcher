package com.holosphere.launcher.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

enum class BackgroundStyle { SOLID, GRADIENT }

data class AppearanceSettings(
    val backgroundStyle: BackgroundStyle = BackgroundStyle.GRADIENT,
    val primaryColor: Color = Color(0xFF7DE2FF),
    val labelsEnabled: Boolean = true,
    val particlesEnabled: Boolean = false
)

@Composable
fun AppearanceBackground(settings: AppearanceSettings, content: @Composable () -> Unit) {
    val brush = when (settings.backgroundStyle) {
        BackgroundStyle.SOLID -> SolidColor(Color(0xFF080B18))
        BackgroundStyle.GRADIENT -> Brush.radialGradient(listOf(settings.primaryColor.copy(alpha = .38f), Color(0xFF080B18)))
    }
    Box(Modifier.fillMaxSize().background(brush)) { content() }
}
