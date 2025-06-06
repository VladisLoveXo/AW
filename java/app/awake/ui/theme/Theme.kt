package app.awake.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import app.awake.ConfigurationController
import app.awake.R

class Theme {
    enum class Sizes {
        roundedButton {
            override val dpSizeValue =
                if (configuration.isSmallScreen())
                    DpSize(width = 36.dp, height = 36.dp)
                else
                    DpSize(width = 42.dp, height = 42.dp)
        },

        lineWidth {
            override val dpSizeValue =
                if (configuration.isSmallScreen())
                    DpSize(width = 1.dp, height = 1.dp)
                else
                    DpSize(width = 1.5.dp, height = 1.5.dp)
        },

        modeTab {
            override val dpSizeValue: DpSize =
                if (configuration.isSmallScreen())
                    DpSize(width = 124.dp, height = 198.dp)
                else
                    DpSize(width = 156.dp, height = 248.dp)
        },

        paddingFromTop {
            override val dpSizeValue: DpSize =
                DpSize(width = 80.dp, height = 80.dp)
        },

        paddingFromTabBar {
            override val dpSizeValue: DpSize =
                DpSize(width = 64.dp, height = 64.dp)
        };

        //abstract val size: Size
        abstract val dpSizeValue: DpSize
        var configuration = ConfigurationController.getInstance()
    }

    enum class Colors {
        disabled {
            override val colorValue: Color
                get() = Color.Black.copy(alpha = 0.4f)
        };

        abstract val colorValue: Color
    }

    enum class Fonts {
        cochin {
            override val font: FontFamily = FontFamily(
                Font(R.font.cochin)
            )
        };

        abstract val font: FontFamily
    }
}

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

@Composable
fun AwakeTheme(
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}