package com.kalikov.game

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.imageio.ImageIO
import kotlin.concurrent.read
import kotlin.concurrent.write

class AwtScreenSurface(
    private val fonts: AwtFonts,
    val image: BufferedImage
) : ScreenSurface {
    private companion object {
        private val colorCache = ColorCache()
    }

    override val width = px(image.width)
    override val height = px(image.height)

    private val gfx: Graphics2D = image.createGraphics()

//    private val lock = ReentrantReadWriteLock()

    init {
        LeaksDetector.add(this)
    }

    constructor(fonts: AwtFonts, width: Pixel, height: Pixel)
            : this(fonts, BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_ARGB))

    constructor(fonts: AwtFonts, stream: InputStream)
            : this(fonts, ImageIO.read(stream))

    override fun clear(color: ARGB) {
        clear(px(0), px(0), width, height, color)
    }

    override fun clear(x: Pixel, y: Pixel, width: Pixel, height: Pixel, color: ARGB) {
//        lock.write {
//            val gfx = image.createGraphics()
//            try {
                gfx.background = colorCache.getColor(color.value, true)
                gfx.clearRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
//            } finally {
//                gfx.dispose()
//            }
//        }
    }

    override fun draw(x: Pixel, y: Pixel, surface: ScreenSurface, blending: Blending?) {
        if (x >= width || y >= height) {
            return
        }
        val surfaceWidth: Pixel = surface.width
        val surfaceHeight: Pixel = surface.height
        if (x + surfaceWidth <= 0 || y + surfaceHeight <= 0) {
            return
        }
        val dx = if (x >= 0) px(0) else -x
        val dy = if (y >= 0) px(0) else -y
        val w = min(surfaceWidth - dx, width - (x + dx))
        val h = min(surfaceHeight - dy, height - (y + dy))
        if (surface !is AwtScreenSurface) {
            drawCompatible(x + dx, y + dy, dx, dy, w, h, surface, blending)
        } else {
            drawImage(x + dx, y + dy, dx, dy, w, h, surface.image, blending)
        }
    }

    override fun draw(
        dstX: Pixel,
        dstY: Pixel,
        surface: ScreenSurface,
        srcX: Pixel,
        srcY: Pixel,
        width: Pixel,
        height: Pixel,
        blending: Blending?
    ) {
        if (dstX >= this.width || dstY >= this.height) {
            return
        }
        val surfaceWidth: Pixel = surface.width
        val surfaceHeight: Pixel = surface.height
        if (dstX + width <= 0 || dstY + height <= 0 || srcX >= surfaceWidth || srcY >= surfaceHeight || srcX + width <= 0 || srcY + height <= 0) {
            return
        }
        if (surface !is AwtScreenSurface) {
            val dstDx = if (dstX >= 0) px(0) else -dstX
            val dstDy = if (dstY >= 0) px(0) else -dstY
            val srcDx = if (srcX >= 0) px(0) else -srcX
            val srcDy = if (srcY >= 0) px(0) else -srcY
            val newW = min(surfaceWidth - dstDx + srcDx, this.width - (dstX + dstDx))
            val newH = min(surfaceHeight - dstDy + srcDy, this.height - (dstY + dstDy))
            drawCompatible(
                dstX + dstDx,
                dstY + dstDy,
                srcX + srcDx + dstDx,
                srcY + srcDy + dstDy,
                newW,
                newH,
                surface,
                blending
            )
        } else {
            drawImage(dstX, dstY, srcX, srcY, width, height, surface.image, blending)
        }
    }

    private fun drawImage(
        dstX: Pixel,
        dstY: Pixel,
        srcX: Pixel,
        srcY: Pixel,
        w: Pixel,
        h: Pixel,
        srcImage: BufferedImage,
        blending: Blending?
    ) {
//        lock.write {
//            val gfx = image.createGraphics()
//            try {
                if (blending != null) {
                    gfx.composite = BlendingComposite.getComposite(blending)
                }
        try {
                gfx.drawImage(
                    srcImage,
                    dstX.toInt(),
                    dstY.toInt(),
                    (dstX + w).toInt(),
                    (dstY + h).toInt(),
                    srcX.toInt(),
                    srcY.toInt(),
                    (srcX + w).toInt(),
                    (srcY + h).toInt(),
                    null
                )
            } finally {
                gfx.composite = AlphaComposite.SrcOver
//                gfx.dispose()
            }
//        }
    }

    private fun drawCompatible(
        dstX: Pixel,
        dstY: Pixel,
        srcX: Pixel,
        srcY: Pixel,
        w: Pixel,
        h: Pixel,
        surface: ScreenSurface,
        blending: Blending?
    ) {
        val pixels = surface.getPixels(srcX, srcY, w, h)
        val srcImage = BufferedImage(w.toInt(), h.toInt(), BufferedImage.TYPE_INT_ARGB)
        srcImage.setRGB(0, 0, w.toInt(), h.toInt(), pixels, 0, w.toInt())
        drawImage(dstX, dstY, px(0), px(0), w, h, srcImage, blending)
    }

    override fun drawRect(x: Pixel, y: Pixel, w: Pixel, h: Pixel, color: ARGB) {
//        lock.write {
//            val gfx = image.createGraphics()
//            try {
                gfx.color = colorCache.getColor(color.value, true)
                gfx.drawRect(x.toInt(), y.toInt(), (w - 1).toInt(), (h - 1).toInt())
//            } finally {
//                gfx.dispose()
//            }
//        }
    }

    override fun fillRect(x: Pixel, y: Pixel, w: Pixel, h: Pixel, color: ARGB) {
//        lock.write {
//            val gfx = image.createGraphics()
//            try {
                gfx.color = colorCache.getColor(color.value, true)
                gfx.fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
//            } finally {
//                gfx.dispose()
//            }
//        }
    }

    override fun drawLine(x1: Pixel, y1: Pixel, x2: Pixel, y2: Pixel, color: ARGB) {
//        lock.write {
//            val gfx = image.createGraphics()
//            try {
                gfx.color = colorCache.getColor(color.value, true)
                gfx.drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
//            } finally {
//                gfx.dispose()
//            }
//        }
    }

    override fun fillText(text: String, x: Pixel, y: Pixel, color: ARGB, font: String, blending: Blending?) {
//        lock.write {
//            val gfx = image.createGraphics()
//            try {
                gfx.color = colorCache.getColor(color.value)
                gfx.font = fonts.getFont(font)
                if (blending != null) {
                    gfx.composite = BlendingComposite.getComposite(blending)
                }
        try {
                gfx.drawString(text, x.toInt(), y.toInt())
            } finally {
                gfx.composite = AlphaComposite.SrcOver
//                gfx.dispose()
            }
//        }
    }

    override fun getPixel(x: Pixel, y: Pixel): ARGB {
//        return lock.read {
          return  ARGB(image.getRGB(x.toInt(), y.toInt()))
//        }
    }

    override val pixels: IntArray get() = getPixels(px(0), px(0), px(image.width), px(image.height))

    override fun getPixels(x: Pixel, y: Pixel, width: Pixel, height: Pixel): IntArray {
//        return lock.read {
            val pixels = IntArray(width * height)
            image.getRGB(x.toInt(), y.toInt(), width.toInt(), height.toInt(), pixels, 0, width.toInt())
            return pixels
//        }
    }

    override fun dispose() {
        gfx.dispose()

        LeaksDetector.remove(this)
    }
}