package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class PixelSize(val width: Pixel = px(0), val height: Pixel = px(0))
