package com.zyroplay.app.cast

import android.content.Context
import android.net.Uri
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.common.images.WebImage

object CastManager {

    const val DEFAULT_RECEIVER_ID = "CC1AD845"

    fun initialize(context: Context) {
        runCatching { CastContext.getSharedInstance(context) }
    }

    fun isConnected(context: Context): Boolean {
        return runCatching {
            CastContext.getSharedInstance(context).sessionManager.currentCastSession?.isConnected == true
        }.getOrDefault(false)
    }

    fun castMedia(
        context: Context,
        title: String,
        streamUrl: String,
        subtitle: String = "",
        posterUrl: String? = null
    ): Boolean {
        val castContext = runCatching { CastContext.getSharedInstance(context) }.getOrNull() ?: return false
        val session = castContext.sessionManager.currentCastSession ?: return false
        if (!session.isConnected) return false

        val metadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
            putString(MediaMetadata.KEY_TITLE, title)
            if (subtitle.isNotBlank()) putString(MediaMetadata.KEY_SUBTITLE, subtitle)
            posterUrl?.let { addImage(WebImage(Uri.parse(it))) }
        }

        val contentType = when {
            streamUrl.contains(".m3u8", true) -> "application/x-mpegURL"
            streamUrl.contains(".mpd", true) -> "application/dash+xml"
            else -> "video/mp4"
        }

        val mediaInfo = MediaInfo.Builder(streamUrl)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType(contentType)
            .setMetadata(metadata)
            .build()

        session.remoteMediaClient?.load(mediaInfo, true, 0)
        return true
    }

    fun addSessionListener(context: Context, listener: SessionManagerListener<CastSession>) {
        runCatching {
            CastContext.getSharedInstance(context).sessionManager.addSessionManagerListener(listener, CastSession::class.java)
        }
    }

    fun removeSessionListener(context: Context, listener: SessionManagerListener<CastSession>) {
        runCatching {
            CastContext.getSharedInstance(context).sessionManager.removeSessionManagerListener(listener, CastSession::class.java)
        }
    }
}
