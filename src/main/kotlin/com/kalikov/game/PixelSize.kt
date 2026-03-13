package com.kalikov.game

import com.kalikov.engine.Pixel
import com.kalikov.engine.px
import kotlinx.serialization.Serializable

@Serializable
data class PixelSize(val width: Pixel = 0.px, val height: Pixel = 0.px)
