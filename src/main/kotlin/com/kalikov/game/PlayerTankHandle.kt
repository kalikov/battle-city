package com.kalikov.game

interface PlayerTankHandle {
    var direction: Direction

    var isIdle: Boolean

    fun startShooting()
    fun stopShooting()
}