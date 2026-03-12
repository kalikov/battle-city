package com.kalikov.engine

import com.kalikov.game.ColorCache
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

class AwtScreenSurface(
    private val fonts: AwtFonts,
    val image: BufferedImage
) : ScreenSurface {
    private companion object {
        private val colorCache = ColorCache()

        private val buffers = arrayOf(
            Array(4) { ByteArray(it + 1) },
            Array(4) { ShortArray(it + 1) },
            Array(4) { ShortArray(it + 1) },
            Array(4) { IntArray(it + 1) },
            Array(4) { FloatArray(it + 1) },
            Array(4) { DoubleArray(it + 1) },
        )
    }

    override val width = image.width.px
    override val height = image.height.px

    private val gfx: Graphics2D = image.createGraphics()

    init {
        LeaksDetector.add(this)
    }

    constructor(fonts: AwtFonts, width: Pixel, height: Pixel)
            : this(fonts, BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_ARGB))

    constructor(fonts: AwtFonts, stream: InputStream)
            : this(fonts, ImageIO.read(stream))

    override fun clear(color: ARGB) {
        clear(0.px, 0.px, width, height, color)
    }

    override fun clear(x: Pixel, y: Pixel, width: Pixel, height: Pixel, color: ARGB) {
        gfx.background = colorCache.getColor(color.value, true)
        gfx.clearRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
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
        val dx = if (x >= 0) 0.px else -x
        val dy = if (y >= 0) 0.px else -y
        val w = min(surfaceWidth - dx, width - (x + dx))
        val h = min(surfaceHeight - dy, height - (y + dy))
        require(surface is AwtScreenSurface)
        drawImage(x + dx, y + dy, dx, dy, w, h, surface.image, blending)
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
        require(surface is AwtScreenSurface)
        drawImage(dstX, dstY, srcX, srcY, width, height, surface.image, blending)
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
        if (blending === Blending.Src) {
            gfx.composite = AlphaComposite.Src
        } else if (blending != null) {
            gfx.composite = BlendingComposite.Companion.getComposite(blending)
        } else {
            gfx.composite = AlphaComposite.SrcOver
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
    }

    override fun drawRect(x: Pixel, y: Pixel, w: Pixel, h: Pixel, color: ARGB) {
        gfx.color = colorCache.getColor(color.value, true)
        if (color.alpha == 0xFF) {
            gfx.composite = AlphaComposite.Src
        } else {
            gfx.composite = AlphaComposite.SrcOver
        }
        gfx.drawRect(x.toInt(), y.toInt(), (w - 1).toInt(), (h - 1).toInt())
    }

    override fun fillRect(x: Pixel, y: Pixel, w: Pixel, h: Pixel, color: ARGB) {
        gfx.color = colorCache.getColor(color.value, true)
        if (color.alpha == 0xFF) {
            gfx.composite = AlphaComposite.Src
        } else {
            gfx.composite = AlphaComposite.SrcOver
        }
        gfx.fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun drawLine(x1: Pixel, y1: Pixel, x2: Pixel, y2: Pixel, color: ARGB) {
        gfx.color = colorCache.getColor(color.value, true)
        if (color.alpha == 0xFF) {
            gfx.composite = AlphaComposite.Src
        } else {
            gfx.composite = AlphaComposite.SrcOver
        }
        gfx.drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
    }

    override fun fillText(text: String, x: Pixel, y: Pixel, color: ARGB, font: String, blending: Blending?) {
        gfx.color = colorCache.getColor(color.value)
        gfx.font = fonts.getFont(font)
        if (blending != null) {
            gfx.composite = BlendingComposite.Companion.getComposite(blending)
        } else {
            gfx.composite = AlphaComposite.SrcOver
        }
        gfx.drawString(text, x.toInt(), y.toInt())
    }

    override fun getPixel(x: Pixel, y: Pixel): ARGB {
        val buffer = buffers[image.colorModel.transferType][image.raster.numDataElements - 1]
        image.raster.getDataElements(x.toInt(), y.toInt(), buffer)
        return ARGB(image.colorModel.getRGB(buffer))
    }

    override fun dispose() {
        gfx.dispose()

        LeaksDetector.remove(this)
    }
}