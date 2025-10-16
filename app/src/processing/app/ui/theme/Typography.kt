package processing.app.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

val processingFont = FontFamily(
    Font(
        resource = "ProcessingSans-Regular.ttf",
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        resource = "ProcessingSans-Bold.ttf",
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    )
)
val spaceGroteskFont = FontFamily(
    Font(
        resource = "SpaceGrotesk-Bold.ttf",
        weight = FontWeight.Bold,
    ),
    Font(
        resource = "SpaceGrotesk-Regular.ttf",
        weight = FontWeight.Normal,
    ),
    Font(
        resource = "SpaceGrotesk-Medium.ttf",
        weight = FontWeight.Medium,
    ),
    Font(
        resource = "SpaceGrotesk-SemiBold.ttf",
        weight = FontWeight.SemiBold,
    ),
    Font(
        resource = "SpaceGrotesk-Light.ttf",
        weight = FontWeight.Light,
    )
)

val PDETypography = Typography(
    defaultFontFamily = spaceGroteskFont,
    h1 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 42.725.sp,
        lineHeight = 48.sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 34.18.sp,
        lineHeight = 40.sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 27.344.sp,
        lineHeight = 32.sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 21.875.sp,
        lineHeight = 28.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 17.5.sp,
        lineHeight = 22.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.8.sp,
        lineHeight = 16.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 13.824.sp,
        lineHeight = 16.sp,
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.2.sp,
        lineHeight = 14.sp
    ),
    overline = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 8.96.sp,
        lineHeight = 10.sp
    )
)