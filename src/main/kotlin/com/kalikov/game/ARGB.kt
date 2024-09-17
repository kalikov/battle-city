package com.kalikov.game

@JvmInline
value class ARGB(val value: Int) {
    companion object {
        val TRANSPARENT = ARGB(0)
        val WHITE = rgb(0xFFFFFF)
        val BLACK = rgb(0x000000)

        fun rgb(value: Int): ARGB {
            return ARGB(0xFF000000.toInt() or value)
        }
    }

    constructor(value: Long) : this(value.toInt())

    constructor(alpha: Int, red: Int, green: Int, blue: Int)
            : this(alpha shl 24 or (red shl 16) or (green shl 8) or blue)

    val alpha: Int
        get() {
            return (value shr 24) and 0xFF
        }

    val red: Int
        get() {
            return (value shr 16) and 0xFF
        }

    val green: Int
        get() {
            return (value shr 8) and 0xFF
        }

    val blue: Int
        get() {
            return value and 0xFF
        }

    fun and(argb: ARGB): ARGB {
        return ARGB(value and argb.value)
    }

    fun over(dst: ARGB): ARGB {
        val srcAlpha = alpha
        val dstAlpha = dst.alpha
        if (srcAlpha == 0) {
            return dst
        } else if (dstAlpha == 0 || srcAlpha == 255) {
            return this
        } else if (dstAlpha == 255) {
            return ARGB(
                0xFF,
                (alpha * red + (255 - srcAlpha) * dst.red) / 255,
                (alpha * green + (255 - srcAlpha) * dst.green) / 255,
                (alpha * blue + (255 - srcAlpha) * dst.blue) / 255
            )
        }
        val backAlpha = dstAlpha * (255 - srcAlpha) / 255
        val blendedAlpha = srcAlpha + backAlpha
        return ARGB(
            blendedAlpha,
            (red * alpha + dst.red * backAlpha) / blendedAlpha,
            (green * alpha + dst.green * backAlpha) / blendedAlpha,
            (blue * alpha + dst.blue * backAlpha) / blendedAlpha
        )
    }

    override fun toString(): String {
        return "ARGB(0x${value.toUInt().toString(16)})"
    }
}
