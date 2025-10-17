package com.kalikov.engine

fun interface Blending {
    object Src : Blending {
        override fun blend(dst: ARGB, src: ARGB, x: Pixel, y: Pixel) = src
    }

    fun blend(dst: ARGB, src: ARGB, x: Pixel, y: Pixel): ARGB
}