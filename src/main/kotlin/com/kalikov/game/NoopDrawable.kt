package com.kalikov.game

import com.kalikov.engine.ScreenSurface

object NoopDrawable : Drawable {
    override fun draw(surface: ScreenSurface) = Unit
}