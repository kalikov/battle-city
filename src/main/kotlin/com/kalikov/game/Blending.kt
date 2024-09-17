package com.kalikov.game

fun interface Blending {
    fun blend(dst: ARGB, src: ARGB, x: Pixel, y: Pixel): ARGB
}