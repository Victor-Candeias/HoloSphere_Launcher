package com.holosphere.launcher.sphere

import kotlin.math.abs

data class MotionConfig(
    val sensitivity: Float = 1f / 320f,
    val friction: Float = .92f,
    val maxVelocity: Float = .08f,
    val epsilon: Float = .0005f,
    val reducedMotion: Boolean = false
)

class SphereMotionController(private val config: MotionConfig = MotionConfig()) {
    var rotation: Float = 0f
        private set
    var velocity: Float = 0f
        private set

    val isMoving: Boolean get() = abs(velocity) > config.epsilon

    fun drag(deltaPixels: Float) {
        if (config.reducedMotion) {
            velocity = 0f
            rotation += deltaPixels * config.sensitivity
            return
        }
        velocity = (deltaPixels * config.sensitivity).coerceIn(-config.maxVelocity, config.maxVelocity)
        rotation += deltaPixels * config.sensitivity
    }

    fun release(velocityPixelsPerSecond: Float) {
        velocity = if (config.reducedMotion) 0f else (velocityPixelsPerSecond * config.sensitivity / 60f).coerceIn(-config.maxVelocity, config.maxVelocity)
    }

    fun tick(): Float {
        if (!isMoving) {
            velocity = 0f
            return rotation
        }
        rotation += velocity
        velocity *= config.friction
        if (abs(velocity) <= config.epsilon) velocity = 0f
        return rotation
    }

    fun stop() { velocity = 0f }
}
