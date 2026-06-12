package com.zyroplay.app.player

import androidx.media3.exoplayer.ExoPlayer

object PlayerHolder {
    var exoPlayer: ExoPlayer? = null
        private set

    fun attach(player: ExoPlayer) {
        exoPlayer = player
    }

    fun detach(player: ExoPlayer) {
        if (exoPlayer === player) {
            exoPlayer = null
        }
    }

    fun pause() {
        exoPlayer?.pause()
    }
}
