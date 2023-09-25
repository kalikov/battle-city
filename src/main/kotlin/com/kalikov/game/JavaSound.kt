package com.kalikov.game

import java.io.ByteArrayInputStream
import java.util.concurrent.locks.ReentrantLock
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.withLock

class JavaSound(private val bytes: ByteArray) : Sound {
    private val buffer = ByteArray(4096)

    private val lock = ReentrantLock()

    @Volatile
    private var playback: SourceDataLine? = null

    override val isPlaying: Boolean
        get() = playback?.isOpen ?: false

    override fun play() {
        start { line, audio -> play(line, audio) }
    }

    override fun loop() {
        start { line, audio -> loop(line, audio) }
    }

    override fun stop() {
        lock.withLock {
            playback?.use {
                it.stop()
                it.flush()
                it.close()
            }
            playback = null
        }
    }

    private fun start(callback: (SourceDataLine, AudioInputStream) -> Unit) {
        lock.lock()
        var locked = true
        try {
            if (playback?.isOpen == true) {
                return
            }
            ByteArrayInputStream(bytes).use { input ->
                AudioSystem.getAudioInputStream(input).use { audio ->
                    val line = getLine(audio.format)
                    line.use {
                        it.open(audio.format)
                        playback = line
                        lock.unlock()
                        locked = false

                        it.start()
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
        var read = 0
        while (!Thread.currentThread().isInterrupted && line.isOpen && read != -1) {
            line.write(buffer, 0, read)
            read = audio.read(buffer, 0, buffer.size)
        }
        line.drain()
    }

    private fun loop(line: SourceDataLine, audio: AudioInputStream) {
        audio.mark(Integer.MAX_VALUE)
        while (!Thread.currentThread().isInterrupted && line.isOpen) {
            var read = 0
            while (!Thread.currentThread().isInterrupted && line.isOpen && read != -1) {
                line.write(buffer, 0, read)
                read = audio.read(buffer, 0, buffer.size)
            }
            audio.reset()
        }
        line.drain()
    }

    private fun getLine(audioFormat: AudioFormat): SourceDataLine {
        val info = DataLine.Info(SourceDataLine::class.java, audioFormat)
        return AudioSystem.getLine(info) as SourceDataLine
    }
}