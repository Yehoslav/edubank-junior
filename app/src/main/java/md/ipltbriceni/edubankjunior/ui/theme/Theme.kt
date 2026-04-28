package md.ipltbriceni.edubankjunior.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EduPrimary,
    onPrimary = Color.White,
    primaryContainer = EduPrimaryDark,
    background = DarkEduBackground,
    surface = DarkEduSurface,
    tertiary = EduSuccess,
    tertiaryContainer = EduSuccessContainer,
    onTertiaryContainer = EduOnSuccessContainer,
    error = EduError,
    errorContainer = EduErrorContainer,
    onErrorContainer = EduOnErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = EduPrimary,
    onPrimary = Color.White,
    primaryContainer = EduPrimaryDark, // Mapping EduPrimaryDark here to use via theme
    background = EduBackground,
    surface = EduSurface,
    surfaceContainer = Color.White,
    onSurface = EduTextMain,
    onSurfaceVariant = EduTextMuted,
    tertiary = EduSuccess,
    tertiaryContainer = EduSuccessContainer,
    onTertiaryContainer = EduOnSuccessContainer,
    error = EduError,
    errorContainer = EduErrorContainer,
    onErrorContainer = EduOnErrorContainer
)


@Composable
fun EduBankJuniorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}