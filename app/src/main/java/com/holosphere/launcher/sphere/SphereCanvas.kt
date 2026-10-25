package com.holosphere.launcher.sphere

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SphereCanvas(modifier: Modifier = Modifier, onRotation: (Float) -> Unit = {}, onNodeClick: (Int) -> Unit = {}) {
    val rotation = remember { mutableFloatStateOf(0f) }
    val nodes = remember { SphereEngine.fibonacciSphere(20) }
    val motion = remember { SphereMotionController() }
    val scope = rememberCoroutineScope()
    var inertiaJob by remember { mutableStateOf<Job?>(null) }
    Canvas(
        modifier = modifier.fillMaxSize()
            .pointerInput(Unit) {
                val velocity = VelocityTracker()
                detectDragGestures(
                    onDragStart = { inertiaJob?.cancel(); motion.stop(); velocity.resetTracking() },
                    onDragEnd = {
                        motion.release(velocity.calculateVelocity().x)
                        inertiaJob = scope.launch {
                            while (motion.isMoving) {
                                rotation.floatValue = motion.tick()
                                onRotation(rotation.floatValue)
                                delay(16)
                            }
                        }
                    },
                    onDragCancel = { inertiaJob?.cancel(); motion.stop() }
                ) { change, drag ->
                    velocity.addPosition(change.uptimeMillis, change.position)
                    motion.drag(drag.x)
                    rotation.floatValue = motion.rotation
                    onRotation(rotation.floatValue)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { position ->
                    val projected = SphereEngine.project(nodes, Quaternion.fromEuler(0f, rotation.floatValue), size.width.coerceAtMost(size.height) * .34f, 1.8f)
                    val local = position - Offset(size.width / 2f, size.height / 2f)
                    SphereHitTester.nearest(projected, local.x, local.y, 34f)?.let(onNodeClick)
                }
            }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val projected = SphereEngine.project(nodes, Quaternion.fromEuler(0f, rotation.floatValue), size.minDimension * .34f, 1.8f)
        projected.forEach { node ->
            val point = center + Offset(node.x, node.y)
            drawCircle(Color(0xFF7DE2FF).copy(alpha = node.alpha), 18f * node.scale, point)
            drawCircle(Color.White.copy(alpha = node.alpha * .8f), 4f * node.scale, point)
        }
        drawCircle(Color(0xFF223B70).copy(alpha = .45f), size.minDimension * .34f, center, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
    }
}
