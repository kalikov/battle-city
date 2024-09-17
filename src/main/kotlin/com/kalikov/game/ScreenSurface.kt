package com.kalikov.game

interface ScreenSurface : ScreenSurfaceData {
    fun getFragment(x: Pixel, y: Pixel, width: Pixel, height: Pixel): ScreenSurface

    fun lock(): MutableScreenSurfaceData
}