package com.creativedrewy.sharedui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * A muted video player composable designed for live wallpaper usage.
 *
 * @param videoUrl    URL of the video to play.
 * @param onDurationKnown Callback invoked once the video's duration (ms) is resolved.
 */
@Composable
fun VideoWallpaperViewer(
    videoUrl: String,
    onDurationKnown: (Long) -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            volume = 0f
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(it).apply {
                useController = false
                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                player = exoPlayer
            }
        }
    )

    LaunchedEffect(videoUrl) {
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    LaunchedEffect(exoPlayer) {
        while (isActive) {
            if (exoPlayer.playbackState == Player.STATE_READY) {
                val duration = exoPlayer.duration.coerceAtLeast(0L)
                if (duration > 0L) {
                    exoPlayer.repeatMode = if (duration < VIDEO_MIN_PLAY_MS) {
                        Player.REPEAT_MODE_ALL
                    } else {
                        Player.REPEAT_MODE_OFF
                    }
                    onDurationKnown(duration)
                    break
                }
            }
            delay(200)
        }
    }

    // Ensure volume stays muted
    LaunchedEffect(exoPlayer) {
        while (isActive) {
            exoPlayer.volume = 0f
            delay(1000)
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }
}

const val IMAGE_DISPLAY_MS = 5_000L
const val VIDEO_MIN_PLAY_MS = 10_000L
