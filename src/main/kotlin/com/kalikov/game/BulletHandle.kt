package com.kalikov.game

interface BulletHandle {
    val tank: Tank

    val x: Pixel
    val y: Pixel

    val center: Pixel
    val middle: Pixel
}