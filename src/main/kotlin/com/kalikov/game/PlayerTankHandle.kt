package com.kalikov.game

interface PlayerTankHandle {
    val player: Player

    val x: Pixel
    val y: Pixel

    val hitRect: PixelRect

    var direction: Direction

    val moveFrequency: Int

    var isIdle: Boolean
    val canMove: Boolean

    fun startShooting()
    fun stopShooting()
}