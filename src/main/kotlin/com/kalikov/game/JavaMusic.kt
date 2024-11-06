package com.kalikov.game

import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineListener

class JavaMusic(bytes: ByteArray) : ManagedMusic, LineListener {
    private val clip: Clip = AudioSystem.getClip()

    @Volatile
    private var controlState = ControlState.STOPPED

    @Volatile
    private var playbackState = PlaybackState.NONE

    override val state: Music.State
        get() {
            return when (controlState) {
                ControlState.STOPPED -> Music.State.STOPPED
                ControlState.PLAYING -> {
                    if (playbackState == PlaybackState.STOPPED) {
                        Music.State.STOPPED
                    } else {
                        Music.State.PLAYING
                    }
                }

                ControlState.LOOPING -> Music.State.PLAYING
                ControlState.PAUSED_PLAY -> Music.State.PAUSED
                ControlState.PAUSED_LOOP -> Music.State.PAUSED
            }
        }

    init {
        AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes)).use {
            clip.open(it)
        }
        clip.addLineListener(this)
    }

    override fun play() {
        if (playbackState == PlaybackState.STOPPED) {
            playbackState = PlaybackState.NONE
        }
        controlState = ControlState.PLAYING
        clip.framePosition = 0
        clip.start()
    }

    override fun loop() {
        controlState = ControlState.LOOPING
        clip.loop(Clip.LOOP_CONTINUOUSLY)
    }

    override fun pause() {
        if (controlState == ControlState.PLAYING && playbackState != PlaybackState.STOPPED) {
            controlState = ControlState.PAUSED_PLAY
            clip.stop()
        } else if (controlState == ControlState.LOOPING) {
            controlState = ControlState.PAUSED_LOOP
            clip.stop()
        }
    }

    override fun resume() {
        if (controlState == ControlState.PAUSED_PLAY) {
            controlState = ControlState.PLAYING
            clip.framePosition = clip.framePosition
            clip.start()
        } else if (controlState == ControlState.PAUSED_LOOP) {
            controlState = ControlState.LOOPING
            clip.loop(Clip.LOOP_CONTINUOUSLY)
        }
    }

    override fun stop() {
        controlState = ControlState.STOPPED
        clip.stop()
        clip.framePosition = 0
    }

    override fun update(event: LineEvent) {
        playbackState = if (event.type === LineEvent.Type.START) {
            PlaybackState.PLAYING
        } else if (event.type === LineEvent.Type.STOP && event.framePosition >= clip.frameLength || event.type === LineEvent.Type.CLOSE) {
            PlaybackState.STOPPED
        } else {
            PlaybackState.NONE
        }
    }

    fun destroy() {
        controlState = ControlState.STOPPED
        clip.stop()
        clip.close()
    }

    enum class ControlState {
        STOPPED,
        PLAYING,
        LOOPING,
        PAUSED_PLAY,
        PAUSED_LOOP,
    }

    enum class PlaybackState {
        NONE,
        STOPPED,
        PLAYING
    }
}