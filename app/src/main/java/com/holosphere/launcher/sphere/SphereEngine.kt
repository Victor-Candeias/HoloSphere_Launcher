package com.holosphere.launcher.sphere

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Vec3(val x: Float, val y: Float, val z: Float)
data class ProjectedNode(val index: Int, val x: Float, val y: Float, val scale: Float, val alpha: Float, val depth: Float)

object SphereEngine {
    fun fibonacciSphere(count: Int): List<Vec3> {
        if (count <= 0) return emptyList()
        val goldenAngle = Math.PI * (3.0 - sqrt(5.0))
        return (0 until count).map { i ->
            val y = 1.0 - 2.0 * (i + 0.5) / count
            val radius = sqrt(1.0 - y * y)
            val theta = goldenAngle * i
            Vec3((radius * cos(theta)).toFloat(), y.toFloat(), (radius * sin(theta)).toFloat())
        }
    }

    fun project(nodes: List<Vec3>, rotation: Float, radius: Float, focal: Float): List<ProjectedNode> =
        nodes.mapIndexed { index, node ->
            val x = node.x * cos(rotation) - node.z * sin(rotation)
            val z = node.x * sin(rotation) + node.z * cos(rotation)
            val scale = (focal / (focal + z * 0.45f)).coerceIn(0.55f, 1.45f)
            ProjectedNode(index, x * radius, node.y * radius, scale, (0.42f + (z + 1f) * 0.29f).coerceIn(0.25f, 1f), z)
        }.sortedBy { it.depth }
}
