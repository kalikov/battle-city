package com.kalikov.game

import java.io.File
import java.io.FileInputStream

class TestImageManager(private val fonts: AwtFonts) : ImageManager {
    private val images = HashMap<String, ScreenSurface>()

    override fun getImage(name: String): ScreenSurface {
        return images.computeIfAbsent(name) { key ->
            FileInputStream(File("images/$key.png")).use {
                AwtScreenSurface(fonts, it)
            }
        }
    }
}