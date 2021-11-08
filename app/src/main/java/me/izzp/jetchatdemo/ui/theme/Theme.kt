package me.izzp.jetchatdemo.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val blue500 = Color(0xff2196F3)
private val yellow500 = Color(0xffFFC107)

private val LightColors = lightColors(
    surface = Color.White,
    background = Color.White,
    onSurface = Color.Black,
    onBackground = Color.Black,
    primary = blue500,
    primaryVariant = blue500,
    secondary = yellow500,
    secondaryVariant = yellow500,
)

private val DarkColors = darkColors(
    surface = Color.Black,
    background = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White,
    primary = blue500,
    primaryVariant = blue500,
    secondary = yellow500,
    secondaryVariant = yellow500,
)

private val shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
)

@Composable
fun JetChatTheme(
    content: @Composable () -> Unit,
) {
    val ripple = rememberRipple()
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colors = colors,
        shapes = shapes,
    ) {
        CompositionLocalProvider(LocalIndication provides ripple) {
            content()
        }
    }
}

val mtColors: Colors
    @Composable
    get() = MaterialTheme.colors

val mtTypography: Typography
    @Composable
    get() = MaterialTheme.typography

val mtShapes: Shapes
    @Composable
    get() = MaterialTheme.shapes


val Colors.BLUE_1
    @Composable
    get() = if (isLight) Color(240, 240, 250) else Color(40, 40, 40)

val Colors.BLUE_2
    @Composable
    get() = if (isLight) Color(220, 220, 250) else Color(0, 50, 200)


