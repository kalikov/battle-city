package com.kalikov.game

interface ShovelWallBuilder {
    fun destroyWall()

    fun buildWall(wallFactory: WallFactory)
}