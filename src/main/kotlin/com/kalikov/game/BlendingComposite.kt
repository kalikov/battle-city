package com.kalikov.game

import java.awt.Composite
import java.awt.CompositeContext
import java.awt.RenderingHints
import java.awt.image.ColorModel
import java.awt.image.DataBuffer
import java.awt.image.Raster
import java.awt.image.WritableRaster
import kotlin.math.min

internal class BlendingComposite(private val blending: Blending) : Composite {
    override fun createContext(
        srcColorModel: ColorModel,
        dstColorModel: ColorModel,
        hints: RenderingHints
    ): CompositeContext {
        return if (IntContext.isSupported(srcColorModel, dstColorModel)) {
            IntContext(blending)
        } else {
            GeneralContext(srcColorModel, dstColorModel, blending)
        }
    }

    private class GeneralContext(
        private val srcColorModel: ColorModel,
        private val dstColorModel: ColorModel,
        private val blending: Blending
    ) : CompositeContext {
        override fun dispose() {
        }

        override fun compose(src: Raster, dstIn: Raster, dstOut: WritableRaster) {
            val srcX = src.minX
            val srcY = src.minY
            val dstInX = dstIn.minX
            val dstInY = dstIn.minY
            val dstOutX = dstOut.minX
            val dstOutY = dstOut.minY
            val w = min(min(src.width, dstOut.width), dstIn.width)
            val h = min(min(src.height, dstOut.height), dstIn.height)

            var srcData: Any? = null
            var dstData: Any? = null

            for (y in 0 until h) {
                for (x in 0 until w) {
                    srcData = src.getDataElements(x + srcX, y + srcY, srcData)
                    dstData = dstIn.getDataElements(x + dstInX, y + dstInY, dstData)
                    val srcRgb = srcColorModel.getRGB(srcData)
                    val dstInRgb = dstColorModel.getRGB(dstData)
                    val dstOutRgb = blending.blend(
                        ARGB(dstInRgb),
                        ARGB(srcRgb),
                        px(x - dstIn.sampleModelTranslateX),
                        px(y - dstIn.sampleModelTranslateY),
                    ).value
                    dstData = dstColorModel.getDataElements(dstOutRgb, dstData)
                    dstOut.setDataElements(x + dstOutX, y + dstOutY, dstData)
                }
            }
        }
    }

    private class IntContext(private val blending: Blending) : CompositeContext {
        override fun dispose() {
        }

        override fun compose(src: Raster, dstIn: Raster, dstOut: WritableRaster) {
            val w = min(min(src.width, dstIn.width), dstOut.width)
            val h = min(min(src.height, dstIn.height), dstOut.height)
            if (w < 1 || h < 1) {
                return
            }
            val n = w * h
            val srcData = IntArray(n)
            val dstData = IntArray(n)
            src.getDataElements(src.minX, src.minY, w, h, srcData)
            dstIn.getDataElements(dstIn.minX, dstIn.minY, w, h, dstData)
            var x = 0
            var y = 0
            for (i in 0 until n) {
                dstData[i] = blending.blend(
                    ARGB(dstData[i]),
                    ARGB(srcData[i]),
                    px(x - dstIn.sampleModelTranslateX),
                    px(y - dstIn.sampleModelTranslateY),
                ).value
                x++
                if (x >= w) {
                    x = 0
                    y++
                }
            }
            dstOut.setDataElements(dstOut.minX, dstOut.minY, w, h, dstData)
        }

        companion object {
            @JvmStatic
            fun isSupported(srcColorModel: ColorModel, dstColorModel: ColorModel): Boolean {
                val transferType = srcColorModel.transferType
                return transferType == dstColorModel.transferType && transferType == DataBuffer.TYPE_INT
            }
        }
    }
}