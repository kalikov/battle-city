package com.kalikov.game

interface LoadingSoundManager : SoundManager {
    fun load(name: String, path: String)
}