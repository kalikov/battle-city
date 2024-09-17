package com.kalikov.game

interface AITankHandle {
    val x: Pixel
    val y: Pixel

    var direction: Direction

    var isIdle: Boolean

    fun shoot()
}