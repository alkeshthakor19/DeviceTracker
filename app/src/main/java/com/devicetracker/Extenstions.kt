package com.devicetracker

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import com.devicetracker.core.Constants
import com.google.firebase.Timestamp
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("ModifierFactoryUnreferencedReceiver")
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

private var latestClickTime = 0L
private var intervalThreshold = 1000L

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.noDoubleClick(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    return clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - latestClickTime > intervalThreshold) {
                onClick()
            }
            latestClickTime = currentTime
        },
    )
}

fun singleClick(onClick: () -> Unit): () -> Unit {
    var latest: Long = 0
    return {
        val now = System.currentTimeMillis()
        if (now - latest >= 1000) {
            onClick()
            latest = now
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun getDateStringFromTimestamp(timestamp: Timestamp?): String {
    var stringDate = Constants.EMPTY_STR
    if(timestamp != null){
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
        val netDate = Date(milliseconds)
        stringDate = sdf.format(netDate).toString()
    }
    return stringDate
}

fun Context.createImageFile(): File {
    // Create an image file name
    val imageFileName = "Temp_image_store.jpg"
    val image = File(externalCacheDir, imageFileName)
    return image
}