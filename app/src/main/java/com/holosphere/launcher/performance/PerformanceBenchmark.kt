package com.holosphere.launcher.performance

import com.holosphere.launcher.sphere.Quaternion
import com.holosphere.launcher.sphere.SphereEngine

data class ProjectionBenchmarkResult(val nodeCount: Int, val iterations: Int, val elapsedMs: Long, val averageFrameMs: Double)

object PerformanceBenchmark {
    fun measure(nodeCount: Int, iterations: Int = 120): ProjectionBenchmarkResult {
        val nodes = SphereEngine.fibonacciSphere(nodeCount)
        val start = System.nanoTime()
        repeat(iterations) { SphereEngine.project(nodes, Quaternion.fromEuler(0f, it * .01f), 300f, 1.8f) }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000L
        return ProjectionBenchmarkResult(nodeCount, iterations, elapsedMs, elapsedMs.toDouble() / iterations.coerceAtLeast(1))
    }
}
