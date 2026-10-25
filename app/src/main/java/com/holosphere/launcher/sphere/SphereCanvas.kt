package com.holosphere.launcher.sphere

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize

@Composable
fun SphereCanvas(modifier: Modifier = Modifier, onRotation: (Float) -> Unit = {}) {
    val rotation = remember { mutableFloatStateOf(0f) }
    val nodes = remember { SphereEngine.fibonacciSphere(20) }
    Canvas(
        modifier = modifier.fillMaxSize().pointerInput(Unit) {
            detectDragGestures { _, drag ->
                rotation.floatValue += drag.x / 320f
                onRotation(rotation.floatValue)
            }
        }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val projected = SphereEngine.project(nodes, rotation.floatValue, size.minDimension * .34f, 1.8f)
        projected.forEach { node ->
            val point = center + Offset(node.x, node.y)
            drawCircle(Color(0xFF7DE2FF).copy(alpha = node.alpha), 18f * node.scale, point)
            drawCircle(Color.White.copy(alpha = node.alpha * .8f), 4f * node.scale, point)
        }
        drawCircle(Color(0xFF223B70).copy(alpha = .45f), size.minDimension * .34f, center, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
    }
}
