package com.example.youshallnotsavetheprince.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val ColorPrimariaContainer = androidx.compose.ui.graphics.Color(0xFF4C3C27)

private val Esquema = darkColorScheme(
    primary = Ouro,
    onPrimary = FundoEscuro,
    primaryContainer = ColorPrimariaContainer,
    onPrimaryContainer = OuroSuave,
    secondary = VerdeApagado,
    onSecondary = FundoEscuro,
    tertiary = Vinho,
    onTertiary = TextoClaro,
    background = FundoEscuro,
    onBackground = TextoClaro,
    surface = Superficie,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieAlta,
    onSurfaceVariant = TextoSecundario,
    outline = Contorno,
    error = androidx.compose.ui.graphics.Color(0xFFF2A6A2),
    onError = FundoEscuro
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun YouShallNotSaveThePrinceTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = Esquema,
        shapes = Formas,
        typography = Typography,
        content = content
    )
}
