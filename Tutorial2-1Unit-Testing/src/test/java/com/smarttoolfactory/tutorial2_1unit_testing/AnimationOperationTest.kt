package com.smarttoolfactory.tutorial2_1unit_testing

import org.junit.Test
import kotlin.math.max


class AnimationOperationTest {
    @Test
    fun animationTest() {
        val result = valueInRange(0.51f, 0.2f)

        println("🔥 Result: $result")

    }

    fun valueInRange(
        input: Float,
        range: Float,
        minValue: Float = 0f,
        maxValue: Float = 1f
    ): ClosedRange<Float> {

        if (range == 0f || range > 1f) return minValue..maxValue
        val remainder = input % range
        val multiplier = input / range

        return (range * (multiplier) - remainder)
            .coerceAtLeast(0f)..(range * (multiplier + 1f) - remainder)
            .coerceAtMost(maxValue)

    }
}