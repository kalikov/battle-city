package com.kalikov.game

import java.io.ByteArrayInputStream
import java.util.concurrent.ConcurrentLinkedDeque
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineListener

class JavaSound(private val bytes: ByteArray) : ManagedSound, LineListener {
    private val clips = ConcurrentLinkedDeque<Clip>()
    private var frameLength = 0

    override val playingCount get() = clips.size

    override fun play() {
        val clip = AudioSystem.getClip()
        clip.addLineListener(this)
        AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes)).use {
            clip.open(it)
        }
        frameLength = clip.frameLength
        clips.add(clip)
        clip.start()
    }

    override fun update(event: LineEvent) {
        if (event.type == LineEvent.Type.STOP && event.framePosition >= frameLength || event.type == LineEvent.Type.CLOSE) {
            if (clips.remove(event.line)) {
                event.line.close()
            }
        }
    }

    override fun pause() {
        clips.forEach { it.stop() }
    }

    override fun resume() {
        val iterator = clips.iterator()
        while (iterator.hasNext()) {
            val clip = iterator.next()
            if (clip.framePosition < clip.frameLength) {
                clip.framePosition = clip.framePosition
                clip.start()
            } else {
                clip.stop()
                clip.close()
                iterator.remove()
            }
        }
    }

    override fun stop() {
        val iterator = clips.iterator()
        while (iterator.hasNext()) {
            val clip = iterator.next()
            clip.stop()
            clip.close()
            iterator.remove()
        }
    }

    fun destroy() {
        stop()
    }
}