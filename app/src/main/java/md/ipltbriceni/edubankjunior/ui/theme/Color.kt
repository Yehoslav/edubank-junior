package md.ipltbriceni.edubankjunior.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Palette
val md_theme_light_primary = Color(0xFF169d91)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFF0F6B63)
val md_theme_light_onPrimaryContainer = Color(0xFFFFFFFF)

val md_theme_light_secondary = Color(0xFF169d91) // Using primary for secondary if no specific color
val md_theme_light_onSecondary = Color(0xFFFFFFFF)

val md_theme_light_tertiary = Color(0xFF4CAF50)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFE8F5E9)
val md_theme_light_onTertiaryContainer = Color(0xFF2E7D32)

val md_theme_light_error = Color(0xFFC62828)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFEBEE)
val md_theme_light_onErrorContainer = Color(0xFFC62828)

val md_theme_light_background = Color(0xFFF5FBFB)
val md_theme_light_onBackground = Color(0xFF2D2D2D)
val md_theme_light_surface = Color(0xFFFFFFFF)
val md_theme_light_onSurface = Color(0xFF2D2D2D)
val md_theme_light_surfaceVariant = Color(0xFFE0E0E0)
val md_theme_light_onSurfaceVariant = Color(0xFF7A7A7A)

// Dark Theme Palette
val md_theme_dark_primary = Color(0xFF5ED4C7) // Lighter version of 169d91 for dark theme
val md_theme_dark_onPrimary = Color(0xFF003733)
val md_theme_dark_primaryContainer = Color(0xFF00504B)
val md_theme_dark_onPrimaryContainer = Color(0xFF7EF1E3)

val md_theme_dark_tertiary = Color(0xFF81C784)
val md_theme_dark_onTertiary = Color(0xFF00390E)
val md_theme_dark_tertiaryContainer = Color(0xFF00531A)
val md_theme_dark_onTertiaryContainer = Color(0xFF9DF49F)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0xFF062825)
val md_theme_dark_onBackground = Color(0xFFE0E3E1)
val md_theme_dark_surface = Color(0xFF093F3B)
val md_theme_dark_onSurface = Color(0xFFE0E3E1)
val md_theme_dark_surfaceVariant = Color(0xFF3F4947)
val md_theme_dark_onSurfaceVariant = Color(0xFFBFC9C6)

// Hero Gradient (Used in both themes, but can be adjusted)
// Moved to DashboardScreen.kt as a Composable to support dynamic themes
