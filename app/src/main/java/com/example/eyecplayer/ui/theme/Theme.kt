package com.example.eyecplayer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.eyecplayer.R

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// Light Theme
private val LightColors = lightColorScheme(
    primary = PrimaryDark,
    secondary = PrimaryRed,
    background = White,
    surface = LightGray4,
    error = ErrorDark,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkGray3,
    onSurface = DarkGray4,
    onError = White
)

// Dark Theme
private val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    secondary = PrimaryRed,
    background = DarkGray1,
    surface = DarkGray2,
    error = ErrorMedium,
    onPrimary = White,
    onSecondary = White,
    onBackground = LightGray1,
    onSurface = LightGray2,
    onError = LightGray3
)
val MontserratFont = FontFamily(
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_medium, FontWeight.Medium)
)
val CustomTypography = Typography(
    displayLarge = TextStyle( // H1
        fontFamily = MontserratFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp
    ),
    displayMedium = TextStyle( // H2
        fontFamily = MontserratFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp
    ),
    displaySmall = TextStyle( // H3
        fontFamily = MontserratFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp
    ),
    headlineMedium = TextStyle( // H4
        fontFamily = MontserratFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp
    ),
    headlineSmall = TextStyle( // H5
        fontFamily = MontserratFont,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    bodyLarge = TextStyle( // Body XL
        fontFamily = MontserratFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle( // Body L
        fontFamily = MontserratFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle( // Body M
        fontFamily = MontserratFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle( // Action L
        fontFamily = MontserratFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle( // Action M
        fontFamily = MontserratFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle( // Action S
        fontFamily = MontserratFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp
    ),
    titleSmall = TextStyle( // Caption
        fontFamily = MontserratFont,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    )
)


@Composable
fun EyeCPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = PrimaryDark,
            secondary = PrimaryRed,
            background = DarkGray1,
            surface = DarkGray2,
            error = ErrorMedium,
            onPrimary = White,
            onSecondary = White,
            onBackground = LightGray1,
            onSurface = LightGray2,
            onError = LightGray3,
        )
        else -> lightColorScheme(
            primary = PrimaryDark,
            secondary = PrimaryRed,
            background = White,
            surface = LightGray4,
            error = ErrorDark,
            onPrimary = White,
            onSecondary = White,
            onBackground = DarkGray3,
            onSurface = DarkGray4,
            onError = White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )
}

