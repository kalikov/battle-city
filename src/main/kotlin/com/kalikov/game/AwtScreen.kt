package com.kalikov.game

import java.awt.Color
import java.awt.Graphics2D
import java.io.File
import java.io.FileInputStream
import javax.swing.JFrame
import kotlin.math.floor
import kotlin.math.max

class AwtScreen(private val frame: JFrame, private val fonts: AwtFonts) : Screen {
    companion object {
        val SCREEN_BG_COLOR: Color = Color.BLACK
    }

    override val surface: AwtScreenSurface

    private var scaleX = 0.0
    private var scaleY = 0.0

    private var topBlackBand = 0
    private var leftBlackBand = 0
    private var rightBlackBand = 0
    private var bottomBlackBand = 0

    private val bufferStrategy = frame.bufferStrategy

    init {
        surface = createSurface(Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT)

        updateScale(surface.width, surface.height)
    }

    fun resize(): Boolean {
        return updateScale(surface.width, surface.height)
    }

    private fun updateScale(baseWidth: Int, baseHeight: Int): Boolean {
        val insets = frame.insets
        val width = frame.width - insets.right - insets.left
        val height = frame.height - insets.bottom - insets.top

        val newScaleX = width / baseWidth.toDouble()
        val newScaleY = height / baseHeight.toDouble()
        if (scaleX != newScaleX || scaleY != newScaleY) {
            scaleX = newScaleX
            scaleY = newScaleY
            if (scaleX > scaleY) {
                scaleX = scaleY
                val targetWidth = floor(scaleY * baseWidth.toDouble()).toInt()
                bottomBlackBand = height
                topBlackBand = 0
                leftBlackBand = max(0, (width - targetWidth) / 2)
                rightBlackBand = leftBlackBand + targetWidth
            } else if (scaleY > scaleX) {
                scaleY = scaleX
                val targetHeight = floor(scaleX * baseHeight.toDouble()).toInt()
                rightBlackBand = width
                leftBlackBand = 0
                topBlackBand = max(0, (height - targetHeight) / 2)
                bottomBlackBand = topBlackBand + targetHeight
            } else {
                rightBlackBand = 0
                leftBlackBand = 0
                bottomBlackBand = 0
                topBlackBand = 0
            }
            return true
        }
        return false
    }

    fun destroy() {
        bufferStrategy.dispose()
        frame.dispose()
    }

    override fun clear() {
        surface.clear(ARGB.rgb(SCREEN_BG_COLOR.rgb))
    }

    override fun flip(): Boolean {
        val insets = frame.insets
        try {
            val width = frame.width - insets.horizontal
            val height = frame.height - insets.vertical
            do {
                do {
                    val gfx = bufferStrategy.drawGraphics as Graphics2D
                    try {
                        gfx.background = SCREEN_BG_COLOR
                        gfx.translate(insets.left, insets.top)
                        if (leftBlackBand > 0) {
                            gfx.clearRect(0, 0, leftBlackBand, height)
                            gfx.clearRect(rightBlackBand, 0, width - rightBlackBand, height)
                        }
                        if (topBlackBand > 0) {
                            gfx.clearRect(0, 0, width, topBlackBand)
                            gfx.clearRect(0, bottomBlackBand, width, height - bottomBlackBand)
                        }
                        gfx.translate(leftBlackBand, topBlackBand)
                        gfx.scale(scaleX, scaleY)
                        gfx.drawImage(surface.image, 0, 0, null)
                    } finally {
                        gfx.dispose()
                    }
                } while (bufferStrategy.contentsRestored())
                bufferStrategy.show()
            } while (bufferStrategy.contentsLost())
            return true
        } catch (e: IllegalStateException) {
            e.printStackTrace(System.err)
            return false
        }
    }

    override fun createSurface(): AwtScreenSurface {
        return AwtScreenSurface(fonts, surface.width, surface.height)
    }

    override fun createSurface(width: Int, height: Int): AwtScreenSurface {
        return AwtScreenSurface(fonts, width, height)
    }

    override fun createSurface(path: String): AwtScreenSurface {
        return FileInputStream(File(path)).use { AwtScreenSurface(fonts, it) }
    }
}