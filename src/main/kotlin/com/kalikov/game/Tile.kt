package com.kalikov.game

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Tile(private val value: Int) {
    operator fun times(n: Int): Tile {
        return Tile(value * n)
    }

    operator fun times(pixel: Pixel): Pixel {
        return Pixel(value * pixel.toInt())
    }

    operator fun plus(v: Tile): Tile {
        return Tile(value + v.value)
    }

    operator fun plus(v: Int): Tile {
        return Tile(value + v)
    }

    operator fun minus(v: Tile): Tile {
        return Tile(value - v.value)
    }

    operator fun minus(v: Int): Tile {
        return Tile(value - v)
    }

    operator fun div(n: Int): Tile {
        return Tile(value / n)
    }

    operator fun inc(): Tile {
        return Tile(value + 1)
    }

    operator fun compareTo(v: Int): Int {
        return value.compareTo(v)
    }

    operator fun compareTo(other: Tile): Int {
        return value.compareTo(other.value)
    }

    fun toInt(): Int = value

    fun toPixel(): Pixel {
        return value * Globals.TILE_SIZE
    }

    fun bricksCount(): Int {
        return value * 2
    }

    override fun toString(): String {
        return "${value}t"
    }
}

fun t(value: Int) = Tile(value)
