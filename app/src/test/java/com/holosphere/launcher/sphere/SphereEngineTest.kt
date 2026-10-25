package com.holosphere.launcher.sphere

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SphereEngineTest {
    @Test fun fibonacciSphereReturnsRequestedCountAndUnitVectors() {
        val nodes = SphereEngine.fibonacciSphere(100)
        assertEquals(100, nodes.size)
        assertTrue(nodes.all { kotlin.math.abs(kotlin.math.sqrt((it.x * it.x + it.y * it.y + it.z * it.z).toDouble()) - 1.0) < .001 })
    }

    @Test fun projectionSortsBackToFrontAndKeepsSafeScale() {
        val projected = SphereEngine.project(SphereEngine.fibonacciSphere(20), Quaternion.fromEuler(.2f, .4f), 100f, 1.8f)
        assertEquals(20, projected.size)
        assertTrue(projected.zipWithNext().all { it.first.depth <= it.second.depth })
        assertTrue(projected.all { it.scale in .55f..1.45f && it.alpha in .25f..1f })
    }

    @Test fun quaternionRotationPreservesVectorLength() {
        val source = Vec3(1f, 0f, 0f)
        val rotated = Quaternion.fromEuler(.4f, .8f, .2f).rotate(source)
        assertEquals(1f, kotlin.math.sqrt(rotated.x * rotated.x + rotated.y * rotated.y + rotated.z * rotated.z), .001f)
    }
}
