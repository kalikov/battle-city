package com.kalikov.game

interface PlayerTankHandle {
    val player: Player

    var direction: Direction

    var isIdle: Boolean
    val canMove: Boolean

    fun startShooting()
    fun stopShooting()
}