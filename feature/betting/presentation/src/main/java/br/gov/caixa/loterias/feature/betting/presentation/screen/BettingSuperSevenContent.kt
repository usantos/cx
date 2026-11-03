package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment

// GridView centraliza por divisão inteira; o alinhamento por viés do Compose arredonda restos ímpares.
val LegacyGridHorizontalAlignment = object : Alignment.Horizontal {
    override fun align(size: Int, space: Int, layoutDirection: androidx.compose.ui.unit.LayoutDirection): Int = (space - size) / 2
}

/*
 * LinearLayout atribui a cada peso sucessivo sua parcela dos pixels restantes. Row.weight distribui os pixels
 * restantes a partir da primeira coluna, deslocando as bolas.
 */
