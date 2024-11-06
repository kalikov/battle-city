package com.kalikov.game

import java.io.InputStream

interface Audio {
    fun destroy()

    fun loadSound(path: String): ManagedSound

    fun loadSound(stream: InputStream): ManagedSound

    fun loadMusic(path: String): ManagedMusic

    fun loadMusic(stream: InputStream): ManagedMusic
}