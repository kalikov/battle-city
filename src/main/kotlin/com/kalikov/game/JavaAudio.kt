package com.kalikov.game

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class JavaAudio : Audio {
    private val sounds = HashSet<Sound>()
    private val lock = ReentrantReadWriteLock()
    private var destroyed = false

    override fun load(path: String): Sound {
        return FileInputStream(File(path)).use {
            load(it)
        }
    }

    override fun load(stream: InputStream): Sound {
        return lock.read {
            check(!destroyed)
            val bytes = stream.readAllBytes()
            val sound = JavaSound(bytes)
            sounds.add(sound)
            sound
        }
    }

    override fun destroy() {
        lock.write {
            check(!destroyed)
            destroyed = true

            sounds.forEach { it.stop() }
            sounds.clear()
        }
    }
}