package com.example.swimpal.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Displays a network-streamed video using ExoPlayer embedded in
 * a Jetpack Compose UI via AndroidView.
 *
 * The composable properly manages the ExoPlayer lifecycle by creating
 * the player only for the given video URL and releasing all resources
 * when the composable leaves the composition.
 *
 * This implementation is lightweight and safe to use inside a LazyColumn,
 * without custom fullscreen logic or manual recomposition handling.
 *
 * @param videoUrl URL of the video to be streamed (HTTP/HTTPS).
 * @param title Optional text displayed above the video player.
 * @param modifier Modifier used to control layout, size, and positioning
 * of the entire component.
 */

@Composable
fun NetworkVideoPlayer(
    videoUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {

        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
    }
}
