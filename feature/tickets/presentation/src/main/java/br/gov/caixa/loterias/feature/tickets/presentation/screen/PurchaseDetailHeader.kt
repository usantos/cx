package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily

fun purchaseText(size: Float, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = LoteriasCaixaDSFontFamily,
    fontSize = size.sp,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

/* Grupo compacto list_group_compras seguido de compra_processada_layout; sem comportamento financeiro. */
