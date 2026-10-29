package br.gov.caixa.loterias.feature.cart.presentation.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private const val CART_BODY_TEXT_SIZE = 14

/** TextView arredonda tamanhos de texto XML para pixels inteiros antes de medir o carrinho atual. */
@Composable
internal fun cartNativeText(size: Int = CART_BODY_TEXT_SIZE, weight: FontWeight = FontWeight.Normal) =
    cartText(size, weight).copy(fontSize = with(LocalDensity.current) { size.sp.toPx().roundToInt().toFloat().toSp() })
