package com.kalikov.game

import com.kalikov.engine.Pixel

interface AITankHandle {
    val x: Pixel
    val y: Pixel

    val hitRect: PixelRect

    val moveFrequency: Int

    var direction: Direction

    var isIdle: Boolean

    fun shoot()
}