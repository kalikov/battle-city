package com.kalikov.game

import java.awt.image.BufferedImage
import java.io.File
import java.util.Arrays
import javax.imageio.ImageIO
import kotlin.test.fail

fun assertImageEquals(resourceName: String, image: BufferedImage) {
    ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName).use { input ->
        val expected = ImageIO.read(input)

        val time = System.currentTimeMillis()

        fun toOutputFile(postfix: String): File {
            val index = resourceName.lastIndexOf('.')
            val name = if (index >= 0) {
                resourceName.substring(0, index) + "_${time}_${postfix}" + resourceName.substring(index)
            } else {
                resourceName + "_${time}_${postfix}"
            }
            return File(name)
        }

        val message = if (expected.width == image.width && expected.height == image.height) {
            val pixels = IntArray(image.width * image.height)
            image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

            val expectedPixels = IntArray(pixels.size)
            expected.getRGB(0, 0, expected.width, expected.height, expectedPixels, 0, expected.width)

            val mismatch = Arrays.mismatch(expectedPixels, pixels)
            if (mismatch == -1) {
                return
            }
            val xorPixels = IntArray(pixels.size)
            pixels.forEachIndexed { index, pixel ->
                xorPixels[index] = expectedPixels[index] xor pixel
            }

            val x = mismatch % image.width
            val y = mismatch / image.height
            "expected 0x${expectedPixels[mismatch].toUInt().toString(16)}, actual pixel 0x${pixels[mismatch].toUInt().toString(16)} at position ($x, $y)."
        } else {
            "dimensions mismatch"
        }
        ImageIO.write(image, "png", toOutputFile("out"))
        fail("Images are different: $message")
    }
}