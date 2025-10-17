package com.kalikov.game

import com.kalikov.engine.SoundManager

interface LoadingSoundManager : SoundManager {
    fun loadSound(name: String, path: String)

    fun loadMusic(name: String, path: String)
}