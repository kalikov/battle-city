package com.kalikov.game

import java.awt.Graphics

inline fun <T : Graphics, R> T.use(block: (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        when {
            exception == null -> dispose()
            else ->
                try {
                    dispose()
                } catch (closeException: Throwable) {
                    exception.addSuppressed(closeException)
                }
        }
    }
}
