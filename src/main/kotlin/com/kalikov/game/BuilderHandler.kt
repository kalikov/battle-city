package com.kalikov.game

interface BuilderHandler {
    fun build(cursor: Cursor)

    fun nextStructure()
}