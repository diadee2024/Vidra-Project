package com.zyroplay.app.ui.player

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.zyroplay.app.model.PlayerSettings
import com.zyroplay.app.player.PlayerHolder

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView(
    streamUrl: String,
    modifier: Modifier = Modifier,
    startPositionMs: Long = 0L,
    playerSettings: PlayerSettings = PlayerSettings(),
    playWhenReady: Boolean = true,
    onProgress: ((positionMs: Long, durationMs: Long) -> Unit)? = null,
    onPlayerReady: ((ExoPlayer) -> Unit)? = null
) {
    val context = LocalContext.current
    val exoPlayer = remember(playerSettings.bufferSeconds) {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                playerSettings.bufferSeconds * 1000,
                playerSettings.bufferSeconds * 2000,
                playerSettings.bufferSeconds * 500,
                playerSettings.bufferSeconds * 500
            )
            .build()
        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build()
            .apply {
                this.playWhenReady = playWhenReady
            }
    }

    LaunchedEffect(streamUrl, startPositionMs) {
        exoPlayer.setMediaItem(MediaItem.fromUri(streamUrl))
        exoPlayer.prepare()
        if (startPositionMs > 0) exoPlayer.seekTo(startPositionMs)
    }

    LaunchedEffect(playerSettings.subtitlesEnabled) {
        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !playerSettings.subtitlesEnabled)
            .build()
    }

    LaunchedEffect(playerSettings.preferredAudioLanguage) {
        if (playerSettings.preferredAudioLanguage != "auto") {
            val tracks = exoPlayer.currentTracks
            for (group in tracks.groups) {
                if (group.type == C.TRACK_TYPE_AUDIO) {
                    for (i in 0 until group.length) {
                        val format = group.getTrackFormat(i)
                        if (format.language?.startsWith(playerSettings.preferredAudioLanguage) == true) {
                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                .buildUpon()
                                .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, i))
                                .build()
                            break
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(exoPlayer) {
        PlayerHolder.attach(exoPlayer)
        onPlayerReady?.invoke(exoPlayer)
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    val pos = exoPlayer.currentPosition
                    val dur = exoPlayer.duration.coerceAtLeast(0)
                    onProgress?.invoke(pos, dur)
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            PlayerHolder.detach(exoPlayer)
            exoPlayer.release()
        }
    }

    val resizeMode = when (playerSettings.aspectRatio) {
        "fill" -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        "zoom" -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
    }

    AndroidView(
        modifier = modifier.fillMaxSize().background(Color.Black),
        factory = { ctx ->
            PlayerView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                player = exoPlayer
                useController = true
                controllerShowTimeoutMs = 5000
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                this.resizeMode = resizeMode
            }
        },
        update = {
            it.player = exoPlayer
            it.resizeMode = resizeMode
        }
    )
}
