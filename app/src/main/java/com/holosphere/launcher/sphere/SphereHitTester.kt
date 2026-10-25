package com.holosphere.launcher.sphere

import kotlin.math.hypot

object SphereHitTester {
    fun nearest(projected: List<ProjectedNode>, x: Float, y: Float, radius: Float): Int? =
        projected.asSequence()
            .map { it to hypot(x - it.x, y - it.y) }
            .filter { (node, distance) -> distance <= radius * node.scale }
            .minWithOrNull(compareBy<Pair<ProjectedNode, Float>> { it.second }.thenByDescending { it.first.depth })?.first?.index
}
