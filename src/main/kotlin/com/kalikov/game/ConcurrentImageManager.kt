package com.kalikov.game

import java.util.concurrent.ConcurrentHashMap

class ConcurrentImageManager(private val screen: Screen) : LoadingImageManager {
    private val images: MutableMap<String, ScreenSurface> = ConcurrentHashMap()

    override fun load(name: String, path: String) {
        images[name] = screen.createSurface(path)
    }

    override fun getImage(name: String): ScreenSurface {
        return images[name] ?: throw ImageNotFoundException(name)
    }
}