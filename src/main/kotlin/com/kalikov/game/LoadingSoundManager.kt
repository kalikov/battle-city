package com.kalikov.game

interface LoadingSoundManager : SoundManager {
    fun loadSound(name: String, path: String)

    fun loadMusic(name: String, path: String)
}