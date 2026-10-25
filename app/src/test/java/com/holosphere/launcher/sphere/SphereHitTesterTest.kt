package com.holosphere.launcher.sphere

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SphereHitTesterTest {
    @Test fun ignoresTouchesOutsideScaledHitArea() {
        val node = ProjectedNode(index = 4, x = 10f, y = 10f, scale = .5f, alpha = 1f, depth = .2f)
        assertNull(SphereHitTester.nearest(listOf(node), 30f, 10f, 20f))
    }

    @Test fun selectsNearestNode() {
        val nodes = listOf(
            ProjectedNode(1, 10f, 10f, 1f, 1f, -.2f),
            ProjectedNode(2, 20f, 10f, 1f, 1f, .8f)
        )
        assertEquals(1, SphereHitTester.nearest(nodes, 11f, 10f, 20f))
    }
}
