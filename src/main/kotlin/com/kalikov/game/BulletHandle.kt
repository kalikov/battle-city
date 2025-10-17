package com.kalikov.game

import com.kalikov.engine.Pixel

interface BulletHandle {
    val tank: Tank

    val x: Pixel
    val y: Pixel

    val center: Pixel
    val middle: Pixel
}