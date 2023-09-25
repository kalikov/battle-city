package com.kalikov.game

interface LoadingImageManager : ImageManager {
    fun load(name: String, path: String)
}