package com.kalikov.engine

interface Scene {
    fun update()

    fun draw(surface: ScreenSurface)

    fun activate()

    fun deactivate()

    fun destroy()
}