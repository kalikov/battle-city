package com.kalikov.game

import com.kalikov.engine.Pixel

interface BaseHandle {
    val x: Pixel
    val y: Pixel

    fun hit()

    val isHit: Boolean

    val bounds: PixelRect

    var isHidden: Boolean
}