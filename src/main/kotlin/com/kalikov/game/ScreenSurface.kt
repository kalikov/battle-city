package com.kalikov.game

interface ScreenSurface : ScreenSurfaceData {
    fun getFragment(x: Int, y: Int, width: Int, height: Int): ScreenSurface

    fun lock(): MutableScreenSurfaceData
}