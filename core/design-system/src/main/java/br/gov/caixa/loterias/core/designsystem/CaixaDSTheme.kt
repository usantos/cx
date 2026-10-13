package br.gov.caixa.loterias.core.designsystem

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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

private val LocalColors = staticCompositionLocalOf { CaixaDSLightColors }
private val LocalSpacing = staticCompositionLocalOf { CaixaDSSpacing() }
private val LocalRadii = staticCompositionLocalOf { CaixaDSRadii() }
private val LocalElevation = staticCompositionLocalOf { CaixaDSElevation() }
private val LocalSizes = staticCompositionLocalOf { CaixaDSSizes() }

val CaixaDSFontFamily = FontFamily(
    Font(R.font.caixa_std_regular, FontWeight.Normal),
    Font(R.font.caixa_std_semi_bold, FontWeight.Medium),
    Font(R.font.caixa_std_semi_bold, FontWeight.SemiBold),
    Font(R.font.caixa_std_bold, FontWeight.Bold),
    Font(R.font.caixa_std_extra_bold, FontWeight.ExtraBold),
)

/** Single regular face, matching XML @font/caixa_std_regular with Android style synthesis. */
val CaixaDSLegacyRegularFontFamily = FontFamily(Font(R.font.caixa_std_regular, FontWeight.Normal))

/** Original Caixa Std Semi Bold face used by the unavailable screen's bold headline. */
val CaixaDSLegacySemiboldFontFamily = FontFamily(Font(R.font.caixa_std_semi_bold, FontWeight.Normal))

/** Original single-face ArialMT used by the legacy help and prize guidance layouts. */
val CaixaDSLegacyArialFontFamily = FontFamily(Font(R.font.arialmt, FontWeight.Normal))

/** Família usada pelos títulos de cards/listas do aplicativo legado. */
val CaixaDSLegacyFuturaFontFamily = FontFamily(
    Font(R.font.futura_std_book, FontWeight.Normal),
    Font(R.font.futura_std_bold, FontWeight.Bold),
)

val CaixaDSTypography = Typography(
    displaySmall = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
)

private val CaixaDSShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

private val LightMaterialColors = lightColorScheme(
    primary = CaixaDSLightColors.brand,
    onPrimary = CaixaDSLightColors.onBrand,
    primaryContainer = CaixaDSColorTokens.Blue100,
    onPrimaryContainer = CaixaDSColorTokens.Blue900,
    secondary = CaixaDSLightColors.accent,
    onSecondary = CaixaDSLightColors.onAccent,
    background = CaixaDSLightColors.background,
    onBackground = CaixaDSLightColors.textPrimary,
    surface = CaixaDSLightColors.surface,
    onSurface = CaixaDSLightColors.textPrimary,
    surfaceVariant = CaixaDSLightColors.surfaceVariant,
    onSurfaceVariant = CaixaDSLightColors.textSecondary,
    outline = CaixaDSLightColors.divider,
    error = CaixaDSLightColors.error,
    onError = CaixaDSLightColors.onError,
)

private val DarkMaterialColors = darkColorScheme(
    primary = CaixaDSDarkColors.brand,
    onPrimary = CaixaDSDarkColors.onBrand,
    primaryContainer = CaixaDSColorTokens.Blue900,
    onPrimaryContainer = CaixaDSColorTokens.Blue100,
    secondary = CaixaDSDarkColors.accent,
    onSecondary = CaixaDSDarkColors.onAccent,
    background = CaixaDSDarkColors.background,
    onBackground = CaixaDSDarkColors.textPrimary,
    surface = CaixaDSDarkColors.surface,
    onSurface = CaixaDSDarkColors.textPrimary,
    surfaceVariant = CaixaDSDarkColors.surfaceVariant,
    onSurfaceVariant = CaixaDSDarkColors.textSecondary,
    outline = CaixaDSDarkColors.divider,
    error = CaixaDSDarkColors.error,
    onError = CaixaDSDarkColors.onError,
)

/** Ponto único de acesso aos tokens do Design System. */
object CaixaDS {
    val colors: CaixaDSColors
        @Composable @ReadOnlyComposable get() = LocalColors.current
    val spacing: CaixaDSSpacing
        @Composable @ReadOnlyComposable get() = LocalSpacing.current
    val radii: CaixaDSRadii
        @Composable @ReadOnlyComposable get() = LocalRadii.current
    val elevation: CaixaDSElevation
        @Composable @ReadOnlyComposable get() = LocalElevation.current
    val sizes: CaixaDSSizes
        @Composable @ReadOnlyComposable get() = LocalSizes.current
    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography
    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes
}

@Composable
fun CaixaDSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) CaixaDSDarkColors else CaixaDSLightColors
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalSpacing provides CaixaDSSpacing(),
        LocalRadii provides CaixaDSRadii(),
        LocalElevation provides CaixaDSElevation(),
        LocalSizes provides CaixaDSSizes(),
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkMaterialColors else LightMaterialColors,
            typography = CaixaDSTypography,
            shapes = CaixaDSShapes,
            content = content,
        )
    }
}

/** Alias de compatibilidade. Novos pontos de entrada devem usar [CaixaDSTheme]. */
@Deprecated("Use CaixaDSTheme")
@Composable
fun CaixaTheme(content: @Composable () -> Unit) = CaixaDSTheme(content = content)
