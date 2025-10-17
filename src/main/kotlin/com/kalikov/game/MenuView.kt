package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Menu
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface

class MenuView(private val menu: Menu, private val cursorView: MenuCursorView) {
    fun draw(surface: ScreenSurface, baseY: Pixel) {
        val x = t(11).toPixel() + 1
        for (i in 0 until menu.getItemsCount()) {
            val item = menu.getItem(i)
            val y = baseY + t(17 + 2 * i).toPixel()
            surface.fillText(item.name, x, y + Globals.FONT_REGULAR_CORRECTION, ARGB.WHITE, Globals.FONT_REGULAR)
            if (menu.isCurrent(item)) {
                cursorView.draw(
                    surface,
                    t(8).toPixel(),
                    y + (Globals.FONT_REGULAR_SIZE - MenuCursorView.SIZE) / 2
                )
            }
        }
    }
}

