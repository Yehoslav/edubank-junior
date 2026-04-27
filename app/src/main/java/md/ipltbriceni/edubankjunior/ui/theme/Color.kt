package md.ipltbriceni.edubankjunior.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Primary Branding
val EduOrange = Color(0xFFFF8C00)      // Your --primary
val EduOrangeLight = Color(0xFFFFE0B2) // For soft highlights
val EduOrangeDark = Color(0xFFFF5E00)

// Backgrounds and Surfaces
val EduBackground = Color(0xFFF4F7F6)  // Your --bg-color
val EduSurface = Color(0xFFFFFFFF)     // White cards

// Typography
val EduTextMain = Color(0xFF2D2D2D)    // Dark gray for titles
val EduTextMuted = Color(0xFF7A7A7A)   // Medium gray for subtitles

val EduHeroGradient = Brush.horizontalGradient(
    colors = listOf(EduOrange, EduOrangeDark)
)