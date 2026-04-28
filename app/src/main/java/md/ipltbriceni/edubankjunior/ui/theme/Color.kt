package md.ipltbriceni.edubankjunior.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Primary Branding
val EduPrimary = Color(0xFF169d91)      // Your --primary
val EduPrimaryDark = Color(0xFF0F6B63)

// Backgrounds and Surfaces
val EduBackground = Color(0xFFF5FBFB)  // Your --bg-color
val EduSurface = Color(0xFFFFFFFF)     // White cards

// Typography
val EduTextMain = Color(0xFF2D2D2D)    // Dark gray for titles
val EduTextMuted = Color(0xFF7A7A7A)   // Medium gray for subtitles

val EduHeroGradient = Brush.horizontalGradient(
    colors = listOf(EduPrimary, EduPrimaryDark)
)