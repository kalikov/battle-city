package com.kalikov.game

class MainMenuView(private val menu: MainMenu, private val cursorView: MainMenuCursorView) {
    fun draw(surface: ScreenSurface, baseY: Int) {
        val items = menu.getItemsInfo()
        for (i in items.indices) {
            val y = baseY + 143 + 16 * i
            surface.fillText(items[i].name, 89, y, ARGB.WHITE, Globals.FONT_REGULAR)
            if (items[i].isCurrent) {
                cursorView.draw(surface, 64, y - 11)
            }
        }
    }
}

