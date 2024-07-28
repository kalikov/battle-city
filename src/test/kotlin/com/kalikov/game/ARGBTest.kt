package com.kalikov.game

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ARGBTest {
    companion object {
        @JvmStatic
        fun srcOverColors(): Array<Arguments> {
            return arrayOf(
                argumentsOfColors(0xFFFF0000, 0xFF00FF00, 0xFFFF0000),
                argumentsOfColors(0x7F00FF00, 0xFFFF0000, 0xFF807F00),
                argumentsOfColors(0xFFFF8000, 0xFF003F7F, 0xFFFF8000),
                argumentsOfColors(0xFF000000, 0xFFFFFFFF, 0xFF000000),
                argumentsOfColors(0x7F000000, 0x7FFFFFFF, 0xBE545454),
                argumentsOfColors(0xFFFF0000, 0x8000FF00, 0xFFFF0000),
                argumentsOfColors(0x800000FF, 0x7F000000, 0xBF0000AA),
                argumentsOfColors(0xFFFF0000, 0x00000000, 0xFFFF0000),
                argumentsOfColors(0x0000FF00, 0x00000000, 0x00000000),
                argumentsOfColors(0xFF00FF00, 0x00000000, 0xFF00FF00),
                argumentsOfColors(0x00000000, 0xFF0000FF, 0xFF0000FF),
                argumentsOfColors(0x00000000, 0x0000FFFF, 0x0000FFFF),
                argumentsOfColors(0x00000000, 0xFF00FFFF, 0xFF00FFFF),
                argumentsOfColors(0xFFFF0000, 0xFFFF0000, 0xFFFF0000),
                argumentsOfColors(0x8000FF00, 0x8000FF00, 0xBF00FF00),
            )
        }

        private fun argumentsOfColors(vararg values: Long): Arguments {
            return Arguments.of(*Array(values.size) { values[it].toInt() })
        }
    }

    @ParameterizedTest
    @MethodSource("srcOverColors")
    fun `should blend using src-over`(src: ARGB, dst: ARGB, expected: ARGB) {
        assertEquals(expected, src.over(dst))
    }

    @Test
    fun `should return components correctly`() {
        val argb = ARGB(0x01234567)
        assertEquals(0x01, argb.alpha)
        assertEquals(0x23, argb.red)
        assertEquals(0x45, argb.green)
        assertEquals(0x67, argb.blue)
    }
}