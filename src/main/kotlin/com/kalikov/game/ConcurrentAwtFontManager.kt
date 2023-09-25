package com.kalikov.game

import java.awt.Font
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class ConcurrentAwtFontManager : FontManager, AwtFonts {
    private val fonts: MutableMap<String, Font> = ConcurrentHashMap()

    override fun load(name: String, path: String, size: Int) {
        val file = File(path)
        fonts[name] = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(size.toFloat())
    }

    override fun getFont(name: String): Font {
        return fonts.computeIfAbsent(name) { Font.decode(it) }
    }
}