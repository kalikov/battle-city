package com.kalikov.game

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ConcurrentSoundManager(
    private val audio: Audio,
) : LoadingSoundManager {
    private val sounds: MutableMap<String, Sound> = ConcurrentHashMap()

    private val executor: ExecutorService = Executors.newFixedThreadPool(8)

    private val playbacks: MutableMap<String, Future<*>> = ConcurrentHashMap()

    override var enabled: Boolean = true
        set(value) {
            field = value
            stopAll()
        }

    override fun load(name: String, path: String) {
        val sound = audio.load(path)
        sounds[name] = sound
    }

    override fun play(name: String) {
        play(name, Sound::play)
    }

    override fun loop(name: String) {
        play(name, Sound::loop)
    }

    override fun isPlaying(name: String): Boolean {
        return playbacks[name]?.let {
            !it.isDone
        } ?: false
    }

    private fun play(name: String, playback: (Sound) -> Unit) {
        val sound = sounds[name] ?: throw SoundNotFoundException(name)

        if (!enabled) {
            return
        }

        playbacks.compute(name) { _, value ->
            value?.cancel(true)
            executor.submit {
                sound.stop()
                playback(sound)
            }
        }
    }

    override fun stop(name: String) {
        playbacks.computeIfPresent(name) { _, value ->
            value.cancel(true)
            null
        }
        sounds[name]?.let {
            if (it.state != Sound.State.STOPPED) {
                executor.submit {
                    it.stop()
                }
            }
        }
    }

    override fun pauseAll() {
        for (key in playbacks.keys) {
            sounds[key]?.let {
                if (it.state == Sound.State.PLAYING) {
                    it.pause()
                }
            }
        }
    }

    override fun resumeAll() {
        for (key in playbacks.keys) {
            sounds[key]?.let {
                if (it.state == Sound.State.PAUSED) {
                    it.resume()
                }
            }
        }
    }

    private fun stopAll() {
        playbacks.values.forEach {
            it.cancel(true)
        }
        playbacks.clear()

        sounds.values.forEach { it.stop() }
    }

    fun destroy() {
        executor.shutdown()

        stopAll()
        sounds.clear()

        executor.awaitTermination(1, TimeUnit.MINUTES)
        executor.shutdownNow()
    }
}