package com.holosphere.launcher.sphere

import kotlin.math.hypot

object SphereHitTester {
    fun nearest(projected: List<ProjectedNode>, x: Float, y: Float, radius: Float): Int? =
        projected.asSequence()
            .map { it to hypot(x - it.x, y - it.y) }
            .filter { (_, distance) -> distance <= radius }
            .minByOrNull { it.second }?.first?.index
}
