package com.kalikov.game

interface AITankHandle {
    val x: Int
    val y: Int

    var direction: Direction

    var isIdle: Boolean

    fun shoot()
}