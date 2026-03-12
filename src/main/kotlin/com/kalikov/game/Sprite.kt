package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface Sprite {
    val id: Int

    fun draw(surface: ScreenSurface)

    val isDestroyed: Boolean

    companion object {
        val ID_ORDER = Comparator<Sprite> { a, b ->
            a.id - b.id
        }
    }
}