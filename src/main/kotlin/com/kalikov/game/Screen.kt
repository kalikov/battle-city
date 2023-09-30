package com.kalikov.game

interface Screen {
    val surface: ScreenSurface

    fun clear()

    fun flip(): Boolean

    fun createSurface(): ScreenSurface

    fun createSurface(width: Int, height: Int): ScreenSurface

    fun createSurface(path: String): ScreenSurface
}