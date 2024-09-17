package com.kalikov.game

class MainMenuView(private val menu: MainMenu, private val cursorView: MainMenuCursorView) {
    fun draw(surface: ScreenSurface, baseY: Pixel) {
        val items = menu.getItemsInfo()
        val x = t(11).toPixel() + 1
        for (i in items.indices) {
            val y = baseY + t(17 + 2 * i).toPixel()
            surface.fillText(items[i].name, x, y + Globals.FONT_REGULAR_CORRECTION, ARGB.WHITE, Globals.FONT_REGULAR)
            if (items[i].isCurrent) {
                cursorView.draw(
                    surface,
                    t(8).toPixel(),
                    y + (Globals.FONT_REGULAR_SIZE - MainMenuCursorView.SIZE) / 2
                )
            }
        }
    }
}

