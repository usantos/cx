package br.gov.caixa.loterias.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColors
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSDarkColors
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSElevation
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLightColors
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSRadii
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSSizes
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSSpacing

private val LocalColors = staticCompositionLocalOf { LoteriasCaixaDSLightColors }
private val LocalSpacing = staticCompositionLocalOf { LoteriasCaixaDSSpacing() }
private val LocalRadii = staticCompositionLocalOf { LoteriasCaixaDSRadii() }
private val LocalElevation = staticCompositionLocalOf { LoteriasCaixaDSElevation() }
private val LocalSizes = staticCompositionLocalOf { LoteriasCaixaDSSizes() }

val LoteriasCaixaDSFontFamily = FontFamily(
    Font(R.font.caixa_std_regular, FontWeight.Normal),
    Font(R.font.caixa_std_semi_bold, FontWeight.Medium),
    Font(R.font.caixa_std_semi_bold, FontWeight.SemiBold),
    Font(R.font.caixa_std_bold, FontWeight.Bold),
    Font(R.font.caixa_std_extra_bold, FontWeight.ExtraBold)
)

/** Uma única fonte regular, equivalente a XML @font/caixa_std_regular com síntese de estilo do Android. */
val LoteriasCaixaDSRegularFontFamily = FontFamily(Font(R.font.caixa_std_regular, FontWeight.Normal))

/** Fonte Caixa Std Semi Bold original usada no título em negrito da tela de indisponibilidade. */
val LoteriasCaixaDSSemiboldFontFamily = FontFamily(Font(R.font.caixa_std_semi_bold, FontWeight.Normal))

/** Família usada pelos títulos de cards/listas do aplicativo legado. */
val LoteriasCaixaDSFuturaFontFamily = FontFamily(
    Font(R.font.futura_std_book, FontWeight.Normal),
    Font(R.font.futura_std_bold, FontWeight.Bold)
)

val LoteriasCaixaDSTypography = Typography(
    displaySmall = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(
        fontFamily = LoteriasCaixaDSFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = LoteriasCaixaDSFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp)
)

private val LoteriasCaixaDSShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val LightMaterialColors = lightColorScheme(
    primary = LoteriasCaixaDSLightColors.brand,
    onPrimary = LoteriasCaixaDSLightColors.onBrand,
    primaryContainer = LoteriasCaixaDSColorTokens.Blue100,
    onPrimaryContainer = LoteriasCaixaDSColorTokens.Blue900,
    secondary = LoteriasCaixaDSLightColors.accent,
    onSecondary = LoteriasCaixaDSLightColors.onAccent,
    background = LoteriasCaixaDSLightColors.background,
    onBackground = LoteriasCaixaDSLightColors.textPrimary,
    surface = LoteriasCaixaDSLightColors.surface,
    onSurface = LoteriasCaixaDSLightColors.textPrimary,
    surfaceVariant = LoteriasCaixaDSLightColors.surfaceVariant,
    onSurfaceVariant = LoteriasCaixaDSLightColors.textSecondary,
    outline = LoteriasCaixaDSLightColors.divider,
    error = LoteriasCaixaDSLightColors.error,
    onError = LoteriasCaixaDSLightColors.onError
)

private val DarkMaterialColors = darkColorScheme(
    primary = LoteriasCaixaDSDarkColors.brand,
    onPrimary = LoteriasCaixaDSDarkColors.onBrand,
    primaryContainer = LoteriasCaixaDSColorTokens.Blue900,
    onPrimaryContainer = LoteriasCaixaDSColorTokens.Blue100,
    secondary = LoteriasCaixaDSDarkColors.accent,
    onSecondary = LoteriasCaixaDSDarkColors.onAccent,
    background = LoteriasCaixaDSDarkColors.background,
    onBackground = LoteriasCaixaDSDarkColors.textPrimary,
    surface = LoteriasCaixaDSDarkColors.surface,
    onSurface = LoteriasCaixaDSDarkColors.textPrimary,
    surfaceVariant = LoteriasCaixaDSDarkColors.surfaceVariant,
    onSurfaceVariant = LoteriasCaixaDSDarkColors.textSecondary,
    outline = LoteriasCaixaDSDarkColors.divider,
    error = LoteriasCaixaDSDarkColors.error,
    onError = LoteriasCaixaDSDarkColors.onError
)

/** Ponto único de acesso aos tokens do Design System. */
object LoteriasCaixaDS {
    val colors: LoteriasCaixaDSColors
        @Composable @ReadOnlyComposable
        get() = LocalColors.current
    val spacing: LoteriasCaixaDSSpacing
        @Composable @ReadOnlyComposable
        get() = LocalSpacing.current
    val radii: LoteriasCaixaDSRadii
        @Composable @ReadOnlyComposable
        get() = LocalRadii.current
    val elevation: LoteriasCaixaDSElevation
        @Composable @ReadOnlyComposable
        get() = LocalElevation.current
    val sizes: LoteriasCaixaDSSizes
        @Composable @ReadOnlyComposable
        get() = LocalSizes.current
    val typography: Typography
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.typography
    val shapes: Shapes
        @Composable @ReadOnlyComposable
        get() = MaterialTheme.shapes
}

@Composable
fun LoteriasCaixaDSTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) LoteriasCaixaDSDarkColors else LoteriasCaixaDSLightColors
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalSpacing provides LoteriasCaixaDSSpacing(),
        LocalRadii provides LoteriasCaixaDSRadii(),
        LocalElevation provides LoteriasCaixaDSElevation(),
        LocalSizes provides LoteriasCaixaDSSizes()
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkMaterialColors else LightMaterialColors,
            typography = LoteriasCaixaDSTypography,
            shapes = LoteriasCaixaDSShapes,
            content = content
        )
    }
}
