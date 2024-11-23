package com.kalikov.game

class MainMenuView(private val menu: MainMenu, private val cursorView: MainMenuCursorView) {
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
                    y + (Globals.FONT_REGULAR_SIZE - MainMenuCursorView.SIZE) / 2
                )
            }
        }
    }
}

