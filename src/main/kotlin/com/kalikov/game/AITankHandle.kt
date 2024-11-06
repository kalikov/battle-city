package com.kalikov.game

interface AITankHandle {
    val x: Pixel
    val y: Pixel

    val hitRect: PixelRect

    val moveFrequency: Int

    var direction: Direction

    var isIdle: Boolean

    fun shoot()
}