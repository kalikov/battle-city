package com.kalikov.game

import java.awt.Color

class ColorCache {
    private val cache = IntMap<Color>()

    fun getColor(value: Int): Color {
        return getColor(value, false)
    }

    fun getColor(value: Int, hasAlpha: Boolean): Color {
        val rgba = if (hasAlpha) {
            value
        } else {
            0xFF000000.toInt() or value
        }
        return cache.computeIfAbsent(rgba) { Color(it, true) }
    }
}