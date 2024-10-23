package com.devicetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun FullScreenImage(imageUrl: String?, resourceId: Int, onNavUp: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val minScale = 1f
    val maxScale = 3f

    // Handle scaling and offset, no bound with screen density or size
    /*val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(minScale, maxScale)
        offsetX += offsetChange.x
        offsetY += offsetChange.y
    }*/
    // Handle scaling and offset, It depends on bound of image size
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(minScale, maxScale)
        // Calculate the adjusted offset to keep the image within bounds
        val maxX = (newScale - 1) * screenWidth / 2f
        val maxY = (newScale - 1) * screenHeight / 2f
        val newOffsetX = (offsetX + offsetChange.x).coerceIn(-maxX, maxX)
        val newOffsetY = (offsetY + offsetChange.y).coerceIn(-maxY, maxY)
        scale = newScale
        offsetX = newOffsetX
        offsetY = newOffsetY
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Ensure background is black
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
                .transformable(state = state)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .build(),
                contentDescription = null,
                placeholder = painterResource(id = resourceId),
                error = painterResource(id = resourceId),
                contentScale = ContentScale.Fit, // Ensure the image fits within the bounds
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = onNavUp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clip(CircleShape)
                .background(Color.Red.copy(alpha = 0.7f))
                .padding(8.dp)
                .size(32.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}