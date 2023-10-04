package com.kalikov.game

interface Entity {
    fun dispose()

    fun toStageObject(stageX: Int, stageY: Int): StageObject
}