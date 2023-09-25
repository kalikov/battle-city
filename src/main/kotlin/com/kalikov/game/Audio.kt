package com.kalikov.game

import java.io.InputStream

interface Audio {
    fun destroy()

    fun load(path: String): Sound

    fun load(stream: InputStream): Sound
}