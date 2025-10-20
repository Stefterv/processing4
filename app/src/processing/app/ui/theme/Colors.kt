package processing.app.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class ProcessingColors{
    companion object{
        val blue = Color(0xFF0251c8)
        val lightBlue = Color(0xFF82AFFF)

        val deepBlue = Color(0xFF1e32aa)
        val darkBlue = Color(0xFF0F195A)

        val white = Color(0xFFFFFFFF)
        val lightGray = Color(0xFFF5F5F5)
        val gray = Color(0xFFDBDBDB)
        val darkGray = Color(0xFF898989)
        val darkerGray = Color(0xFF727070)
        val veryDarkGray = Color(0xFF1E1E1E)
        val black = Color(0xFF0D0D0D)

        val error = Color(0xFFFF5757)
        val errorContainer = Color(0xFFFFA6A6)

        val p5Light = Color(0xFFfd9db9)
        val p5Mid = Color(0xFFff4077)
        val p5Dark = Color(0xFFaf1f42)

        val foundationLight = Color(0xFFd4b2fe)
        val foundationMid = Color(0xFF9c4bff)
        val foundationDark = Color(0xFF5501a4)

        val downloadInactive = Color(0xFF8890B3)
        val downloadBackgroundActive = Color(0x14508BFF)
    }
}

@Deprecated("Use PDE3LightColor instead")
val PDELightColors = Colors(
    primary = ProcessingColors.blue,
    primaryVariant = ProcessingColors.lightBlue,
    onPrimary = ProcessingColors.white,

    secondary = ProcessingColors.deepBlue,
    secondaryVariant = ProcessingColors.darkBlue,
    onSecondary = ProcessingColors.white,

    background = ProcessingColors.white,
    onBackground = ProcessingColors.darkBlue,

    surface = ProcessingColors.lightGray,
    onSurface = ProcessingColors.darkerGray,

    error = ProcessingColors.error,
    onError = ProcessingColors.white,

    isLight = true,
)

@Deprecated("Use PDE3DarkColor instead")
val PDEDarkColors = Colors(
    primary = ProcessingColors.deepBlue,
    primaryVariant = ProcessingColors.darkBlue,
    onPrimary = ProcessingColors.white,

    secondary = ProcessingColors.lightBlue,
    secondaryVariant = ProcessingColors.blue,
    onSecondary = ProcessingColors.white,

    background = ProcessingColors.veryDarkGray,
    onBackground = ProcessingColors.white,

    surface = ProcessingColors.darkerGray,
    onSurface = ProcessingColors.lightGray,

    error = ProcessingColors.error,
    onError = ProcessingColors.white,

    isLight = false,
)

/*
  *  Generated using Material Theme Builder
  *  https://material-foundation.github.io/material-theme-builder/
*/

val primaryLight = Color(0xFF003B98)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFF0251C8)
val onPrimaryContainerLight = Color(0xFFC1D0FF)
val secondaryLight = Color(0xFF00188B)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFF1E32AA)
val onSecondaryContainerLight = Color(0xFF9DA9FF)
val tertiaryLight = Color(0xFFB8004A)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFDE2360)
val onTertiaryContainerLight = Color(0xFFFFFBFF)
val errorLight = Color(0xFFB6212A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFF5757)
val onErrorContainerLight = Color(0xFF5E000A)
val backgroundLight = Color(0xFFFAF8FF)
val onBackgroundLight = Color(0xFF191B23)
val surfaceLight = Color(0xFFFCF8F8)
val onSurfaceLight = Color(0xFF1C1B1B)
val surfaceVariantLight = Color(0xFFE0E3E3)
val onSurfaceVariantLight = Color(0xFF444748)
val outlineLight = Color(0xFF747878)
val outlineVariantLight = Color(0xFFC4C7C7)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF313030)
val inverseOnSurfaceLight = Color(0xFFF4F0EF)
val inversePrimaryLight = Color(0xFFB2C5FF)
val surfaceDimLight = Color(0xFFDDD9D9)
val surfaceBrightLight = Color(0xFFFCF8F8)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF7F3F2)
val surfaceContainerLight = Color(0xFFF1EDEC)
val surfaceContainerHighLight = Color(0xFFEBE7E7)
val surfaceContainerHighestLight = Color(0xFFE5E2E1)

val primaryLightMediumContrast = Color(0xFF00317F)
val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
val primaryContainerLightMediumContrast = Color(0xFF0251C8)
val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val secondaryLightMediumContrast = Color(0xFF00188B)
val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
val secondaryContainerLightMediumContrast = Color(0xFF1E32AA)
val onSecondaryContainerLightMediumContrast = Color(0xFFD2D6FF)
val tertiaryLightMediumContrast = Color(0xFF71002A)
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFFD31859)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF73000F)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFCB3136)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFFAF8FF)
val onBackgroundLightMediumContrast = Color(0xFF191B23)
val surfaceLightMediumContrast = Color(0xFFFCF8F8)
val onSurfaceLightMediumContrast = Color(0xFF111111)
val surfaceVariantLightMediumContrast = Color(0xFFE0E3E3)
val onSurfaceVariantLightMediumContrast = Color(0xFF333737)
val outlineLightMediumContrast = Color(0xFF4F5354)
val outlineVariantLightMediumContrast = Color(0xFF6A6E6E)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF313030)
val inverseOnSurfaceLightMediumContrast = Color(0xFFF4F0EF)
val inversePrimaryLightMediumContrast = Color(0xFFB2C5FF)
val surfaceDimLightMediumContrast = Color(0xFFC9C6C5)
val surfaceBrightLightMediumContrast = Color(0xFFFCF8F8)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFF7F3F2)
val surfaceContainerLightMediumContrast = Color(0xFFEBE7E7)
val surfaceContainerHighLightMediumContrast = Color(0xFFE0DCDB)
val surfaceContainerHighestLightMediumContrast = Color(0xFFD4D1D0)

val primaryLightHighContrast = Color(0xFF00276A)
val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast = Color(0xFF0042A7)
val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
val secondaryLightHighContrast = Color(0xFF00188B)
val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast = Color(0xFF1E32AA)
val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
val tertiaryLightHighContrast = Color(0xFF5E0022)
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF95003A)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF60000B)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF970117)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFFAF8FF)
val onBackgroundLightHighContrast = Color(0xFF191B23)
val surfaceLightHighContrast = Color(0xFFFCF8F8)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFE0E3E3)
val onSurfaceVariantLightHighContrast = Color(0xFF000000)
val outlineLightHighContrast = Color(0xFF292D2D)
val outlineVariantLightHighContrast = Color(0xFF464A4A)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF313030)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFB2C5FF)
val surfaceDimLightHighContrast = Color(0xFFBBB8B7)
val surfaceBrightLightHighContrast = Color(0xFFFCF8F8)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFF4F0EF)
val surfaceContainerLightHighContrast = Color(0xFFE5E2E1)
val surfaceContainerHighLightHighContrast = Color(0xFFD7D4D3)
val surfaceContainerHighestLightHighContrast = Color(0xFFC9C6C5)

val primaryDark = Color(0xFFB2C5FF)
val onPrimaryDark = Color(0xFF002B73)
val primaryContainerDark = Color(0xFF0251C8)
val onPrimaryContainerDark = Color(0xFFC1D0FF)
val secondaryDark = Color(0xFFBBC3FF)
val onSecondaryDark = Color(0xFF001B96)
val secondaryContainerDark = Color(0xFF1E32AA)
val onSecondaryContainerDark = Color(0xFF9DA9FF)
val tertiaryDark = Color(0xFFFFB2BE)
val onTertiaryDark = Color(0xFF660026)
val tertiaryContainerDark = Color(0xFFFF4E7D)
val onTertiaryContainerDark = Color(0xFF52001C)
val errorDark = Color(0xFFFFB3AF)
val onErrorDark = Color(0xFF68000D)
val errorContainerDark = Color(0xFFFF5757)
val onErrorContainerDark = Color(0xFF5E000A)
val backgroundDark = Color(0xFF11131A)
val onBackgroundDark = Color(0xFFE1E2EC)
val surfaceDark = Color(0xFF141313)
val onSurfaceDark = Color(0xFFE5E2E1)
val surfaceVariantDark = Color(0xFF444748)
val onSurfaceVariantDark = Color(0xFFC4C7C7)
val outlineDark = Color(0xFF8E9192)
val outlineVariantDark = Color(0xFF444748)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE5E2E1)
val inverseOnSurfaceDark = Color(0xFF313030)
val inversePrimaryDark = Color(0xFF1256CD)
val surfaceDimDark = Color(0xFF141313)
val surfaceBrightDark = Color(0xFF3A3939)
val surfaceContainerLowestDark = Color(0xFF0E0E0E)
val surfaceContainerLowDark = Color(0xFF1C1B1B)
val surfaceContainerDark = Color(0xFF201F1F)
val surfaceContainerHighDark = Color(0xFF2A2A2A)
val surfaceContainerHighestDark = Color(0xFF353434)

val primaryDarkMediumContrast = Color(0xFFD1DBFF)
val onPrimaryDarkMediumContrast = Color(0xFF00215D)
val primaryContainerDarkMediumContrast = Color(0xFF5C8CFF)
val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
val secondaryDarkMediumContrast = Color(0xFFD7DAFF)
val onSecondaryDarkMediumContrast = Color(0xFF00147A)
val secondaryContainerDarkMediumContrast = Color(0xFF7587FE)
val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
val tertiaryDarkMediumContrast = Color(0xFFFFD1D7)
val onTertiaryDarkMediumContrast = Color(0xFF52001D)
val tertiaryContainerDarkMediumContrast = Color(0xFFFF4E7D)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFD2CE)
val onErrorDarkMediumContrast = Color(0xFF540008)
val errorContainerDarkMediumContrast = Color(0xFFFF5757)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF11131A)
val onBackgroundDarkMediumContrast = Color(0xFFE1E2EC)
val surfaceDarkMediumContrast = Color(0xFF141313)
val onSurfaceDarkMediumContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkMediumContrast = Color(0xFF444748)
val onSurfaceVariantDarkMediumContrast = Color(0xFFDADDDD)
val outlineDarkMediumContrast = Color(0xFFAFB2B3)
val outlineVariantDarkMediumContrast = Color(0xFF8D9191)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFE5E2E1)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF2A2A2A)
val inversePrimaryDarkMediumContrast = Color(0xFF0041A4)
val surfaceDimDarkMediumContrast = Color(0xFF141313)
val surfaceBrightDarkMediumContrast = Color(0xFF454444)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF070707)
val surfaceContainerLowDarkMediumContrast = Color(0xFF1E1D1D)
val surfaceContainerDarkMediumContrast = Color(0xFF282827)
val surfaceContainerHighDarkMediumContrast = Color(0xFF333232)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF3E3D3D)

val primaryDarkHighContrast = Color(0xFFEDEFFF)
val onPrimaryDarkHighContrast = Color(0xFF000000)
val primaryContainerDarkHighContrast = Color(0xFFACC1FF)
val onPrimaryContainerDarkHighContrast = Color(0xFF000926)
val secondaryDarkHighContrast = Color(0xFFEFEEFF)
val onSecondaryDarkHighContrast = Color(0xFF000000)
val secondaryContainerDarkHighContrast = Color(0xFFB6BEFF)
val onSecondaryContainerDarkHighContrast = Color(0xFF000434)
val tertiaryDarkHighContrast = Color(0xFFFFEBED)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFFFFACB9)
val onTertiaryContainerDarkHighContrast = Color(0xFF210007)
val errorDarkHighContrast = Color(0xFFFFECEA)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFAEA9)
val onErrorContainerDarkHighContrast = Color(0xFF220001)
val backgroundDarkHighContrast = Color(0xFF11131A)
val onBackgroundDarkHighContrast = Color(0xFFE1E2EC)
val surfaceDarkHighContrast = Color(0xFF141313)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF444748)
val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
val outlineDarkHighContrast = Color(0xFFEEF0F1)
val outlineVariantDarkHighContrast = Color(0xFFC0C3C4)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFE5E2E1)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF0041A4)
val surfaceDimDarkHighContrast = Color(0xFF141313)
val surfaceBrightDarkHighContrast = Color(0xFF51504F)
val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
val surfaceContainerLowDarkHighContrast = Color(0xFF201F1F)
val surfaceContainerDarkHighContrast = Color(0xFF313030)
val surfaceContainerHighDarkHighContrast = Color(0xFF3C3B3B)
val surfaceContainerHighestDarkHighContrast = Color(0xFF484646)


val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

