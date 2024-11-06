package com.kalikov.game

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class JavaAudio : Audio {
    private val sounds = HashSet<JavaSound>()
    private val musics = HashSet<JavaMusic>()
    private val lock = ReentrantReadWriteLock()
    private var destroyed = false

    override fun loadSound(path: String): ManagedSound {
        return FileInputStream(File(path)).use {
            loadSound(it)
        }
    }

    override fun loadSound(stream: InputStream): ManagedSound {
        val bytes = stream.readAllBytes()
        return loadSound { JavaSound(bytes) }
    }

    private fun loadSound(soundFactory: () -> JavaSound): ManagedSound {
        return lock.read {
            check(!destroyed)
            val sound = soundFactory()
            sounds.add(sound)
            sound
        }
    }

    override fun loadMusic(path: String): ManagedMusic {
        return FileInputStream(File(path)).use {
            loadMusic(it)
        }
    }

    override fun loadMusic(stream: InputStream): ManagedMusic {
        val bytes = stream.readAllBytes()
        return loadMusic { JavaMusic(bytes) }
    }

    private fun loadMusic(musicFactory: () -> JavaMusic): ManagedMusic {
        return lock.read {
            check(!destroyed)
            val music = musicFactory()
            musics.add(music)
            music
        }
    }

    override fun destroy() {
        lock.write {
            check(!destroyed)
            destroyed = true

            sounds.forEach { it.destroy() }
            sounds.clear()

            musics.forEach { it.destroy() }
            musics.clear()
        }
    }
}