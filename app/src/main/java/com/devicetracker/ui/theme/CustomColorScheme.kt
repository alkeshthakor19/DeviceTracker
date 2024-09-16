package com.devicetracker.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColors(
    val textColor: Color,
    val cardBackgroundColor: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        textColor = Color.Unspecified,
        cardBackgroundColor = Color.Unspecified
    )
}