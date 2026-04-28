package md.ipltbriceni.edubankjunior.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Primary Branding
val EduPrimary = Color(0xFF169d91)      // Your --primary
val EduPrimaryDark = Color(0xFF0F6B63)

// Backgrounds and Surfaces
val EduBackground = Color(0xFFF5FBFB)  // Your --bg-color
val DarkEduBackground = Color(0xFF062825)
val EduSurface = Color(0xFFFFFFFF)     // White cards
val DarkEduSurface = Color(0xFF093F3B)

// Typography
val EduTextMain = Color(0xFF2D2D2D)    // Dark gray for titles
val EduTextMuted = Color(0xFF7A7A7A)   // Medium gray for subtitles

// Success / Completed Colors
val EduSuccess = Color(0xFF4CAF50)
val EduSuccessContainer = Color(0xFFE8F5E9)
val EduOnSuccessContainer = Color(0xFF2E7D32)

// Error / Failure Colors
val EduError = Color(0xFFC62828)
val EduErrorContainer = Color(0xFFFFEBEE)
val EduOnErrorContainer = Color(0xFFC62828)

val EduHeroGradient = Brush.horizontalGradient(
    colors = listOf(EduPrimary, EduPrimaryDark)
)