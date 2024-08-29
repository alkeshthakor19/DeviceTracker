package com.devicetracker

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.gson.Gson

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