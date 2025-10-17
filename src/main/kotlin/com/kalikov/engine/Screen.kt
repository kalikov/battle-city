package com.kalikov.engine

interface Screen {
    val surface: ScreenSurface

    fun clear()

    fun flip(): Boolean

    fun createSurface(): ScreenSurface

    fun createSurface(width: Pixel, height: Pixel): ScreenSurface

    fun createSurface(path: String): ScreenSurface
}