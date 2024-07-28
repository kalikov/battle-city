package com.kalikov.game

interface PlayerTankHandle {
    val player: Player

    var direction: Direction

    var isIdle: Boolean

    fun startShooting()
    fun stopShooting()
}