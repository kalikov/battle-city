package com.kalikov.game

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.imageio.ImageIO

class AwtScreenSurface(
    private val fonts: AwtFonts,
    val image: BufferedImage
) : ScreenSurface, MutableScreenSurfaceData {

    override val width: Pixel = px(image.width)

    override val height: Pixel = px(image.height)

    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    constructor(fonts: AwtFonts, width: Pixel, height: Pixel)
            : this(fonts, BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_ARGB))

    constructor(fonts: AwtFonts, stream: InputStream)
            : this(fonts, ImageIO.read(stream))

    override fun getFragment(x: Pixel, y: Pixel, width: Pixel, height: Pixel): ScreenSurface {
        return AwtScreenSurface(fonts, image.getSubimage(x.toInt(), y.toInt(), width.toInt(), height.toInt()))
    }

    override fun lock(): MutableScreenSurfaceData {
        lock.writeLock().lock()
        return this
    }

    override fun unlock() {
        lock.writeLock().unlock()
    }

    override fun clear(color: ARGB) {
        clear(px(0), px(0), width, height, color)
    }

    override fun clear(x: Pixel, y: Pixel, width: Pixel, height: Pixel, color: ARGB) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.background = Color(color.value, true)
                gfx.clearRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun draw(x: Pixel, y: Pixel, surface: ScreenSurface, quadrants: Int) {
        val gfx = image.createGraphics()
        try {
            val transform = AffineTransform.getQuadrantRotateInstance(
                quadrants,
                (x + surface.width / 2).toDouble(),
                (y + surface.height / 2).toDouble()
            )
            transform.translate(x.toDouble(), y.toDouble())
            if (surface is AwtScreenSurface) {
                gfx.drawImage(surface.image, transform, null)
            } else {
                val pixels = surface.getPixels(px(0), px(0), surface.width, surface.height)
                val srcImage = BufferedImage(surface.width.toInt(), surface.height.toInt(), BufferedImage.TYPE_INT_ARGB)
                srcImage.setRGB(0, 0, surface.width.toInt(), surface.height.toInt(), pixels, 0, surface.width.toInt())
                gfx.drawImage(srcImage, transform, null)
            }
        } finally {
            gfx.dispose()
        }
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
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                if (blending != null) {
                    gfx.composite = BlendingComposite(blending)
                }
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
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
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
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.color = Color(color.value, true)
                gfx.drawRect(x.toInt(), y.toInt(), (w - 1).toInt(), (h - 1).toInt())
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun fillRect(x: Pixel, y: Pixel, w: Pixel, h: Pixel, color: ARGB) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.color = Color(color.value, true)
                gfx.fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun drawLine(x1: Pixel, y1: Pixel, x2: Pixel, y2: Pixel, color: ARGB) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.color = Color(color.value, true)
                gfx.drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun fillText(text: String, x: Pixel, y: Pixel, color: ARGB, font: String, blending: Blending?) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.color = Color(color.value)
                gfx.font = fonts.getFont(font)
                if (blending != null) {
                    gfx.composite = BlendingComposite(blending)
                }
                gfx.drawString(text, x.toInt(), y.toInt())
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun getPixel(x: Pixel, y: Pixel): ARGB {
        lock.readLock().lock()
        try {
            return ARGB(image.getRGB(x.toInt(), y.toInt()))
        } finally {
            lock.readLock().unlock()
        }
    }

    override val pixels: IntArray get() = getPixels(px(0), px(0), px(image.width), px(image.height))

    override fun getPixels(x: Pixel, y: Pixel, width: Pixel, height: Pixel): IntArray {
        lock.readLock().lock()
        try {
            val pixels = IntArray(width * height)
            image.getRGB(x.toInt(), y.toInt(), width.toInt(), height.toInt(), pixels, 0, width.toInt())
            return pixels
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun setPixel(x: Pixel, y: Pixel, color: ARGB) {
        image.setRGB(x.toInt(), y.toInt(), color.value)
    }

    override fun setPixels(x: Pixel, y: Pixel, width: Pixel, height: Pixel, colors: IntArray) {
        image.setRGB(x.toInt(), y.toInt(), width.toInt(), height.toInt(), colors, 0, width.toInt())
    }
}