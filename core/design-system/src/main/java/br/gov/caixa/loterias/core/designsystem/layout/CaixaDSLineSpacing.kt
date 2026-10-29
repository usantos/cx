package br.gov.caixa.loterias.core.designsystem.layout

import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.res.ResourcesCompat
import kotlin.math.roundToInt
import br.gov.caixa.loterias.core.designsystem.R

/** O lineSpacingExtra do TextView usa dp, independentemente da escala de fonte do usuário. */
@Composable
fun caixaDSLineHeight(fontSize: TextUnit, extra: Dp): TextUnit {
    val context = LocalContext.current
    val density = LocalDensity.current
    return remember(context, density.density, density.fontScale, fontSize, extra) {
        val paint = Paint().apply {
            typeface = ResourcesCompat.getFont(context, R.font.caixa_std_regular)
            textSize = with(density) { fontSize.toPx() }
        }
        with(density) { (paint.getFontMetricsInt(null) + extra.toPx().roundToInt()).toFloat().toSp() }
    }
}
