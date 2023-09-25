package com.kalikov.game

interface Entity {
    fun dispose()

    fun toStageObject(): StageObject
}