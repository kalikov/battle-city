package com.kalikov.game

import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min

@JvmInline
@Serializable
value class Pixel(private val value: Int) {
    operator fun plus(delta: Pixel): Pixel {
        return Pixel(value + delta.value)
    }

    operator fun plus(delta: Int): Pixel {
        return Pixel(value + delta)
    }

    operator fun minus(delta: Pixel): Pixel {
        return Pixel(value - delta.value)
    }

    operator fun minus(delta: Int): Pixel {
        return Pixel(value - delta)
    }

    operator fun times(p: Pixel): Int {
        return value * p.value
    }

    operator fun times(n: Int): Pixel {
        return Pixel(value * n)
    }

    operator fun div(n: Int): Pixel {
        return Pixel(value / n)
    }

    operator fun div(p: Pixel): Int {
        return value / p.value
    }

    operator fun rem(n: Int): Pixel {
        return Pixel(value % n)
    }

    operator fun rem(p: Pixel): Pixel {
        return Pixel(value % p.value)
    }

    operator fun unaryMinus(): Pixel {
        return Pixel(-value)
    }

    operator fun inc(): Pixel {
        return Pixel(value + 1)
    }

    operator fun dec(): Pixel {
        return Pixel(value - 1)
    }

    operator fun compareTo(other: Pixel): Int {
        return value.compareTo(other.value)
    }

    operator fun compareTo(v: Int): Int {
        return value.compareTo(v)
    }

    fun toInt() = value

    fun toDouble() = value.toDouble()

    fun toTile() = Tile(value / Globals.TILE_SIZE.value)

    override fun toString(): String {
        return "${value}px"
    }
}

operator fun Int.times(p: Pixel): Pixel {
    return Pixel(this * p.toInt())
}

fun px(value: Int) = Pixel(value)

fun min(a: Pixel, b: Pixel): Pixel {
    return Pixel(min(a.toInt(), b.toInt()))
}

fun max(a: Pixel, b: Pixel): Pixel {
    return Pixel(max(a.toInt(), b.toInt()))
}