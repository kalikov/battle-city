package com.kalikov.engine

open class Menu(private vararg var items: MenuItem = emptyArray()) {
    var item = 0
        set(value) {
            require(value >= 0 && value < items.size)
            field = value
        }

    fun getItemsCount() = items.size

    fun getItem(index: Int) = items[index]

    fun getCurrentItem(): MenuItem {
        return items[item]
    }

    fun isCurrent(item: MenuItem): Boolean {
        return item === getCurrentItem()
    }

    fun nextItem() {
        item = if (item + 1 >= items.size) 0 else item + 1
    }

    fun executeCurrentItem() {
        getCurrentItem().execute()
    }
}