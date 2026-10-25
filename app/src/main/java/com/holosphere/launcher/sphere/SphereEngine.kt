package com.holosphere.launcher.sphere

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Vec3(val x: Float, val y: Float, val z: Float)
data class ProjectedNode(val index: Int, val x: Float, val y: Float, val scale: Float, val alpha: Float, val depth: Float)

data class Quaternion(val w: Float, val x: Float, val y: Float, val z: Float) {
    fun normalized(): Quaternion {
        val length = sqrt(w * w + x * x + y * y + z * z).coerceAtLeast(0.0001f)
        return Quaternion(w / length, x / length, y / length, z / length)
    }

    fun rotate(point: Vec3): Vec3 {
        val q = normalized()
        val ix = q.w * point.x + q.y * point.z - q.z * point.y
        val iy = q.w * point.y + q.z * point.x - q.x * point.z
        val iz = q.w * point.z + q.x * point.y - q.y * point.x
        val iw = -q.x * point.x - q.y * point.y - q.z * point.z
        return Vec3(
            ix * q.w + iw * -q.x + iy * -q.z - iz * -q.y,
            iy * q.w + iw * -q.y + iz * -q.x - ix * -q.z,
            iz * q.w + iw * -q.z + ix * -q.y - iy * -q.x
        )
    }

    companion object {
        fun fromEuler(pitch: Float, yaw: Float, roll: Float = 0f): Quaternion {
            val cy = cos(yaw * .5f); val sy = sin(yaw * .5f)
            val cp = cos(pitch * .5f); val sp = sin(pitch * .5f)
            val cr = cos(roll * .5f); val sr = sin(roll * .5f)
            return Quaternion(cr * cp * cy + sr * sp * sy, sr * cp * cy - cr * sp * sy, cr * sp * cy + sr * cp * sy, cr * cp * sy - sr * sp * cy).normalized()
        }
    }
}

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
        project(nodes, Quaternion.fromEuler(0f, rotation), radius, focal)

    fun project(nodes: List<Vec3>, orientation: Quaternion, radius: Float, focal: Float, minimumAlpha: Float = 0.25f): List<ProjectedNode> =
        nodes.mapIndexed { index, node ->
            val rotated = orientation.rotate(node)
            val scale = (focal / (focal + rotated.z * 0.45f)).coerceIn(0.55f, 1.45f)
            ProjectedNode(index, rotated.x * radius, rotated.y * radius, scale, (0.42f + (rotated.z + 1f) * 0.29f).coerceIn(minimumAlpha, 1f), rotated.z)
        }.sortedBy { it.depth }
}
