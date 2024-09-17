package com.kalikov.game

import java.io.ByteArrayInputStream
import java.util.concurrent.locks.ReentrantLock
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.Unit
import kotlin.concurrent.withLock

class JavaSound(private val bytes: ByteArray) : Sound {
    private val buffer = ByteArray(4096)

    private val lock = ReentrantLock()

    @Volatile
    private var playback: SourceDataLine? = null

    @Volatile
    private var playbackState = PlaybackState.PLAYING

    private enum class PlaybackState(val soundState: Sound.State) {
        PLAYING(Sound.State.PLAYING),
        PAUSING(Sound.State.PAUSED),
        PAUSED(Sound.State.PAUSED),
    }

    override val state get() = if (playback?.isOpen == true) playbackState.soundState else Sound.State.STOPPED

    override fun play() {
        start { line, audio -> play(line, audio) }
    }

    override fun loop() {
        start { line, audio -> loop(line, audio) }
    }

    override fun pause() {
        return lock.withLock {
            if (playback?.isOpen == true) {
                if (playbackState == PlaybackState.PLAYING) {
                    playbackState = PlaybackState.PAUSING
                }
            }
        }
    }

    override fun resume() {
        lock.withLock {
            if (playback?.isOpen == true) {
                if (playbackState == PlaybackState.PAUSING) {
                    playbackState = PlaybackState.PLAYING
                } else if (playbackState == PlaybackState.PAUSED) {
                    playback?.start()
                    playbackState = PlaybackState.PLAYING
                }
            }
        }
    }

    override fun stop() {
        lock.withLock {
            playback?.use {
                it.stop()
                it.flush()
            }
            playback = null
        }
    }

    private fun start(callback: (SourceDataLine, AudioInputStream) -> Unit) {
        lock.lock()
        var locked = true
        try {
            if (playback?.isOpen == true) {
                if (playbackState == PlaybackState.PAUSED) {
                    playback?.start()
                    playbackState = PlaybackState.PLAYING
                }
                return
            }
            ByteArrayInputStream(bytes).use { input ->
                AudioSystem.getAudioInputStream(input).use { audio ->
                    val line = getLine(audio.format)
                    line.use {
                        it.open(audio.format)
                        it.start()
                        playback = it
                        playbackState = PlaybackState.PLAYING
                        lock.unlock()
                        locked = false

                        callback(it, audio)
                        it.stop()
                    }
                }
            }
        } finally {
            if (locked) {
                lock.unlock()
            }
        }
    }

    private fun play(line: SourceDataLine, audio: AudioInputStream) {
        playOnce(line, audio)
        line.drain()
    }

    private fun loop(line: SourceDataLine, audio: AudioInputStream) {
        audio.mark(Integer.MAX_VALUE)
        while (!Thread.currentThread().isInterrupted && line.isOpen) {
            playOnce(line, audio)
            audio.reset()
        }
        line.drain()
    }

    private fun playOnce(line: SourceDataLine, audio: AudioInputStream) {
        var read = 0
        while (!Thread.currentThread().isInterrupted && line.isOpen && read != -1) {
            if (playbackState == PlaybackState.PLAYING) {
                line.write(buffer, 0, read)
                read = audio.read(buffer, 0, buffer.size)
            } else if (playbackState == PlaybackState.PAUSING) {
                lock.withLock {
                    if (playbackState == PlaybackState.PAUSING) {
                        line.stop()
                        playbackState = PlaybackState.PAUSED
                    }
                }
            }
        }
    }

    private fun getLine(audioFormat: AudioFormat): SourceDataLine {
        val info = DataLine.Info(SourceDataLine::class.java, audioFormat)
        return AudioSystem.getLine(info) as SourceDataLine
    }
}