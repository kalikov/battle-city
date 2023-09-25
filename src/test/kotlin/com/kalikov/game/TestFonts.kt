package com.kalikov.game

import java.awt.Font
import java.io.File

class TestFonts : AwtFonts {
    private val fonts = HashMap<String, Font>()

    override fun getFont(name: String): Font {
        return fonts.computeIfAbsent(name) { key ->
            val index = key.lastIndexOf('-')
            if (index > 0) {
                val size = Integer.parseInt(key.substring(index + 1)).toFloat()
                Font.createFont(Font.TRUETYPE_FONT, File("fonts/${key.substring(0, index)}.ttf")).deriveFont(size)
            } else {
                Font.createFont(Font.TRUETYPE_FONT, File("fonts/$key.ttf")).deriveFont(8f)
            }
        }
    }
}