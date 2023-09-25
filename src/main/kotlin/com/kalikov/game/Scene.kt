package com.kalikov.game

interface Scene {
    data class Start(val sceneFactory: () -> Scene) : Event()

    fun update()

    fun draw(surface: ScreenSurface)

    fun destroy()
}