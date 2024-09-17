package com.kalikov.game

interface GameFieldHandle {
    val bounds: PixelRect

    val walls: WallsHandle
    val ground: GroundHandle
    val trees: TreesHandle
    val base: BaseHandle
}