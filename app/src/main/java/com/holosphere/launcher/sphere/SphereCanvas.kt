package com.holosphere.launcher.sphere

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SphereCanvas(modifier: Modifier = Modifier, onRotation: (Float) -> Unit = {}, onNodeClick: (Int) -> Unit = {}) {
    val rotation = remember { mutableFloatStateOf(0f) }
    val nodes = remember { SphereEngine.fibonacciSphere(20) }
    val scope = rememberCoroutineScope()
    var inertiaJob: Job? = null
    Canvas(
        modifier = modifier.fillMaxSize()
            .pointerInput(Unit) {
            val velocity = VelocityTracker()
            detectDragGestures(
                onDragStart = { inertiaJob?.cancel(); velocity.resetTracking() },
                onDragEnd = {
                    val initial = velocity.calculateVelocity().x / 18000f
                    inertiaJob = scope.launch {
                        var speed = initial
                        while (kotlin.math.abs(speed) > 0.0005f) {
                            rotation.floatValue += speed
                            onRotation(rotation.floatValue)
                            speed *= 0.92f
                            delay(16)
                        }
                    }
                },
                onDragCancel = { inertiaJob?.cancel() }
            ) { change, drag ->
                velocity.addPosition(change.uptimeMillis, change.position)
                rotation.floatValue += drag.x / 320f
                onRotation(rotation.floatValue)
            }
            .pointerInput(Unit) {
                detectTapGestures { position ->
                    val projected = SphereEngine.project(nodes, rotation.floatValue, size.width.coerceAtMost(size.height) * .34f, 1.8f)
                    val local = position - Offset(size.width / 2f, size.height / 2f)
                    SphereHitTester.nearest(projected, local.x, local.y, 34f)?.let(onNodeClick)
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
