package com.kalikov.game

import com.kalikov.engine.px
import com.kalikov.engine.times

object Globals {
    val TILE_SIZE = 8.px

    val CANVAS_WIDTH = TILE_SIZE * 32

    val CANVAS_HEIGHT = TILE_SIZE * 30

    const val FONT_REGULAR = "prstart"
    val FONT_REGULAR_CORRECTION = TILE_SIZE - 1
    val FONT_REGULAR_SIZE = TILE_SIZE

    const val FONT_BIG = "prstart-32"
    val FONT_BIG_CORRECTION = 4 * FONT_REGULAR_CORRECTION
    val FONT_BIG_SIZE = 4 * TILE_SIZE

    const val DATA_DIR = "data"

    const val IDENTITY_GAME = 1
    const val IDENTITY_MENU_SCENE = 2
    const val IDENTITY_CONSTRUCTION_SCENE = 3
    const val IDENTITY_STAGE_SCENE = 4
    const val IDENTITY_DEMO_SCENE = 5

    const val IDENTITY_LEVEL = 6
    const val IDENTITY_POINTS_MANAGER = 7
    const val IDENTITY_POWER_UP_MANAGER = 8
    const val IDENTITY_BULLETS_MANAGER = 9
    const val IDENTITY_EXPLOSION_MANAGER = 10
    const val IDENTITY_ENEMY_MANAGER = 11
    const val IDENTITY_PLAYERS_MANAGER = 12
    const val IDENTITY_PAUSE_MANAGER = 13

    const val IDENTITY_CONSTRUCTION_CONTROLLER = 14
    const val IDENTITY_PLAYER_CONTROLLER = 100
    const val IDENTITY_AI_PLAYER_CONTROLLER = 200
    const val IDENTITY_AI_ENEMY_CONTROLLER = 17
    const val IDENTITY_MOVEMENT_CONTROLLER = 18

    const val IDENTITY_FREEZE_HANDLER = 19
    const val IDENTITY_SHOVEL_HANDLER = 20
}
