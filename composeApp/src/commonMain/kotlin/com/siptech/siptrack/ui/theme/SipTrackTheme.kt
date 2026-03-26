package com.siptech.siptrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// SipTrack brand colors
val SipGreen = Color(0xFF2ECC71)
val SipYellow = Color(0xFFF39C12)
val SipRed = Color(0xFFE74C3C)
val SipDark = Color(0xFF0D0D0D)
val SipDarkSurface = Color(0xFF1A1A1A)
val SipAccent = Color(0xFF3498DB)

private val SipTrackDarkColors = darkColorScheme(
    primary = SipAccent,
    onPrimary = Color.White,
    secondary = SipGreen,
    onSecondary = Color.Black,
    error = SipRed,
    background = SipDark,
    surface = SipDarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun SipTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SipTrackDarkColors,
        content = content,
    )
}

/** Map BAC value to the appropriate status color */
fun bacToColor(bac: Double, driveLimit: Double = 0.08): Color = when {
    bac <= 0.0 -> SipGreen
    bac < 0.04 -> SipGreen
    bac < driveLimit -> SipYellow
    else -> SipRed
}
