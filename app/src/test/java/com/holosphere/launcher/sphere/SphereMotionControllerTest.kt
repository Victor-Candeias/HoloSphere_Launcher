package com.holosphere.launcher.sphere

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SphereMotionControllerTest {
    @Test fun releaseDeceleratesAndStops() {
        val motion = SphereMotionController(MotionConfig(friction = .8f, epsilon = .001f))
        motion.drag(100f)
        motion.release(1200f)
        repeat(100) { motion.tick() }
        assertFalse(motion.isMoving)
        assertTrue(motion.rotation > 0f)
    }

    @Test fun velocityIsClamped() {
        val motion = SphereMotionController(MotionConfig(maxVelocity = .02f))
        motion.release(100000f)
        assertEquals(.02f, motion.velocity, .0001f)
    }

    @Test fun reducedMotionDoesNotStartInertia() {
        val motion = SphereMotionController(MotionConfig(reducedMotion = true))
        motion.drag(100f)
        motion.release(100000f)
        assertFalse(motion.isMoving)
    }
}
