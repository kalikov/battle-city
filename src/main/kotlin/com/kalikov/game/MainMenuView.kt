package com.kalikov.game

class MainMenuView(private val menu: MainMenu, private val cursorView: MainMenuCursorView) {
    fun draw(surface: ScreenSurface, baseY: Int) {
        val items = menu.getItemsInfo()
        val x = Globals.TILE_SIZE * 11 + 1
        for (i in items.indices) {
            val y = baseY + 17 * Globals.TILE_SIZE + Globals.UNIT_SIZE * i
            surface.fillText(items[i].name, x, y + Globals.FONT_REGULAR_CORRECTION, ARGB.WHITE, Globals.FONT_REGULAR)
            if (items[i].isCurrent) {
                cursorView.draw(
                    surface,
                    8 * Globals.TILE_SIZE,
                    y + (Globals.FONT_REGULAR_SIZE - MainMenuCursorView.SIZE) / 2
                )
            }
        }
    }
}

