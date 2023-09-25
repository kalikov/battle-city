package com.kalikov.game

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class JavaAudio : Audio {
    override fun load(path: String): Sound {
        return FileInputStream(File(path)).use {
            load(it)
        }
    }

    override fun load(stream: InputStream): Sound {
        val bytes = stream.readAllBytes()
        return JavaSound(bytes)
    }

    override fun destroy() {
    }
}