package com.kalikov.game

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConcurrentSoundManager(
    private val audio: Audio,
) : LoadingSoundManager {
    private val sounds: MutableMap<String, ManagedSound> = ConcurrentHashMap()
    private val musics: MutableMap<String, MusicWrapper> = ConcurrentHashMap()

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    override var enabled: Boolean = true
        set(value) {
            field = value
            stop()
        }

    override fun loadSound(name: String, path: String) {
        val sound = audio.loadSound(path)
        sounds[name] = SoundWrapper(sound)
    }

    override fun loadMusic(name: String, path: String) {
        val music = audio.loadMusic(path)
        musics[name] = MusicWrapper(music)
    }

    override fun sound(name: String): Sound {
        return sounds[name] ?: throw SoundNotFoundException(name)
    }

    override fun music(name: String): Music {
        return musics[name] ?: throw SoundNotFoundException(name)
    }

    override fun pause() {
        sounds.values.forEach { it.pause() }
        musics.values.forEach { it.pause() }
    }

    override fun resume() {
        sounds.values.forEach { it.resume() }
        musics.values.forEach { it.resume() }
    }

    private fun stop() {
        sounds.values.forEach { it.stop() }
        musics.values.forEach { it.stop() }
    }

    fun destroy() {
        stop()
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.MINUTES)
        executor.shutdownNow()
        sounds.clear()
        musics.clear()
    }

    private inner class MusicWrapper(private val music: ManagedMusic) : ManagedMusic {
        override val state get() = music.state

        override fun loop() {
            if (!enabled) {
                return
            }
            executor.submit {
                music.loop()
            }
        }

        override fun pause() {
            if (!enabled) {
                return
            }
            executor.submit {
                music.pause()
            }
        }

        override fun resume() {
            if (!enabled) {
                return
            }
            executor.submit {
                music.resume()
            }
        }

        override fun play() {
            if (!enabled) {
                return
            }
            executor.submit {
                music.play()
            }
        }

        override fun stop() {
            executor.submit {
                music.stop()
            }
        }
    }

    private inner class SoundWrapper(private val sound: ManagedSound) : ManagedSound {
        override val playingCount get() = sound.playingCount

        override fun pause() {
            if (!enabled) {
                return
            }
            sound.pause()
        }

        override fun resume() {
            if (!enabled) {
                return
            }
            sound.resume()
        }

        override fun play() {
            if (!enabled) {
                return
            }
            sound.play()
        }

        override fun stop() {
            sound.stop()
        }
    }
}