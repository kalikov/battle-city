package com.kalikov.game

class MainMenu(private vararg var items: MainMenuItem = emptyArray()) {
    var item = 0
        set(value) {
            require(value >= 0 && value < items.size)
            field = value
        }

    fun getCurrentItem(): MainMenuItem {
        return items[item]
    }

    fun isCurrent(item: MainMenuItem): Boolean {
        return item === getCurrentItem()
    }

    fun nextItem() {
        item = if (item + 1 >= items.size) 0 else item + 1
    }

    fun executeCurrentItem() {
        getCurrentItem().execute()
    }

    fun getItemsInfo(): Array<MainMenuItemInfo> {
        return Array(items.size) { i -> MainMenuItemInfo(items[i].name, isCurrent(items[i])) }
    }
}