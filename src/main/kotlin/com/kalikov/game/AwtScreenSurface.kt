package com.kalikov.game

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.imageio.ImageIO
import kotlin.math.min

class AwtScreenSurface : ScreenSurface, MutableScreenSurfaceData {
    private val fonts: AwtFonts

    override val width: Int
    override val height: Int

    val image: BufferedImage

    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    constructor(fonts: AwtFonts, width: Int, height: Int) {
        image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        this.fonts = fonts
        this.width = width
        this.height = height
    }

    constructor(fonts: AwtFonts, stream: InputStream) {
        this.fonts = fonts
        image = ImageIO.read(stream)
        width = image.width
        height = image.height
    }

    constructor(fonts: AwtFonts, image: BufferedImage) {
        this.fonts = fonts
        this.image = image
        width = image.width
        height = image.height
    }

    override fun getFragment(x: Int, y: Int, width: Int, height: Int): ScreenSurface {
        return AwtScreenSurface(fonts, image.getSubimage(x, y, width, height))
    }

    override fun lock(): MutableScreenSurfaceData {
        lock.writeLock().lock()
        return this
    }

    override fun unlock() {
        lock.writeLock().unlock()
    }

    override fun clear(color: ARGB) {
        clear(0, 0, width, height, color)
    }

    override fun clear(x: Int, y: Int, width: Int, height: Int, color: ARGB) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.background = Color(color.value, true)
                gfx.clearRect(x, y, width, height)
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun draw(x: Int, y: Int, surface: ScreenSurface, quadrants: Int) {
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
                val pixels = surface.getPixels(0, 0, surface.width, surface.height)
                val srcImage = BufferedImage(surface.width, surface.height, BufferedImage.TYPE_INT_ARGB)
                srcImage.setRGB(0, 0, surface.width, surface.height, pixels, 0, surface.width)
                gfx.drawImage(srcImage, transform, null)
            }
        } finally {
            gfx.dispose()
        }
    }

    override fun draw(x: Int, y: Int, surface: ScreenSurface, blending: Blending?) {
        if (x >= width || y >= height) {
            return
        }
        val surfaceWidth: Int = surface.width
        val surfaceHeight: Int = surface.height
        if (x + surfaceWidth <= 0 || y + surfaceHeight <= 0) {
            return
        }
        val dx = if (x >= 0) 0 else -x
        val dy = if (y >= 0) 0 else -y
        val w = min(surfaceWidth - dx, width - (x + dx))
        val h = min(surfaceHeight - dy, height - (y + dy))
        if (surface !is AwtScreenSurface) {
            drawCompatible(x + dx, y + dy, dx, dy, w, h, surface, blending)
        } else {
            drawImage(x + dx, y + dy, dx, dy, w, h, surface.image, blending)
        }
    }

    override fun draw(
        dstX: Int,
        dstY: Int,
        surface: ScreenSurface,
        srcX: Int,
        srcY: Int,
        width: Int,
        height: Int,
        blending: Blending?
    ) {
        if (dstX >= this.width || dstY >= this.height) {
            return
        }
        val surfaceWidth: Int = surface.width
        val surfaceHeight: Int = surface.height
        if (dstX + width <= 0 || dstY + height <= 0 || srcX >= surfaceWidth || srcY >= surfaceHeight || srcX + width <= 0 || srcY + height <= 0) {
            return
        }
        if (surface !is AwtScreenSurface) {
            val dstDx = if (dstX >= 0) 0 else -dstX
            val dstDy = if (dstY >= 0) 0 else -dstY
            val srcDx = if (srcX >= 0) 0 else -srcX
            val srcDy = if (srcY >= 0) 0 else -srcY
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
        dstX: Int,
        dstY: Int,
        srcX: Int,
        srcY: Int,
        w: Int,
        h: Int,
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
                    dstX,
                    dstY,
                    dstX + w,
                    dstY + h,
                    srcX,
                    srcY,
                    srcX + w,
                    srcY + h,
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
        dstX: Int,
        dstY: Int,
        srcX: Int,
        srcY: Int,
        w: Int,
        h: Int,
        surface: ScreenSurface,
        blending: Blending?
    ) {
        val pixels = surface.getPixels(srcX, srcY, w, h)
        val srcImage = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        srcImage.setRGB(0, 0, w, h, pixels, 0, w)
        drawImage(dstX, dstY, 0, 0, w, h, srcImage, blending)
    }

    override fun drawRect(x: Int, y: Int, w: Int, h: Int, color: ARGB) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.color = Color(color.value, true)
                gfx.drawRect(x, y, w - 1, h - 1)
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun fillRect(x: Int, y: Int, w: Int, h: Int, color: ARGB) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.color = Color(color.value, true)
                gfx.fillRect(x, y, w, h)
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, color: ARGB) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.color = Color(color.value, true)
                gfx.drawLine(x1, y1, x2, y2)
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun fillText(text: String, x: Int, y: Int, color: ARGB, font: String, blending: Blending?) {
        lock.writeLock().lock()
        try {
            val gfx = image.createGraphics()
            try {
                gfx.color = Color(color.value)
                gfx.font = fonts.getFont(font)
                if (blending != null) {
                    gfx.composite = BlendingComposite(blending)
                }
                gfx.drawString(text, x, y)
            } finally {
                gfx.dispose()
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun getPixel(x: Int, y: Int): ARGB {
        lock.readLock().lock()
        try {
            return ARGB(image.getRGB(x, y))
        } finally {
            lock.readLock().unlock()
        }
    }

    override val pixels: IntArray get() = getPixels(0, 0, image.width, image.height)

    override fun getPixels(x: Int, y: Int, width: Int, height: Int): IntArray {
        lock.readLock().lock()
        try {
            val pixels = IntArray(width * height)
            image.getRGB(x, y, width, height, pixels, 0, width)
            return pixels
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun setPixel(x: Int, y: Int, color: Int) {
        image.setRGB(x, y, color)
    }

    override fun setPixels(x: Int, y: Int, width: Int, height: Int, colors: IntArray) {
        image.setRGB(x, y, width, height, colors, 0, width)
    }
}