package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun NumericPurchaseNumbers(numbers: List<Int>) {
    numbers.chunked(NUMBERS_PER_ROW).forEach { row ->
        Row(Modifier.fillMaxWidth()) {
            repeat(NUMBERS_PER_ROW) { index ->
                Box(Modifier.weight(1f).height(51.dp), contentAlignment = Alignment.Center) {
                    row.getOrNull(index)?.let { number ->
                        // O padrão de DezenaConfig é cinza110, mesmo em uma linha roxa.
                        Box(
                            Modifier.size(
                                35.dp
                            ).border(
                                (NUMBER_DIAMETER / LEGACY_NUMBER_REFERENCE_DIAMETER).dp,
                                LoteriasCaixaDSColorTokens.SocialGraphite,
                                CircleShape
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                number.toString(),
                                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                                style = purchaseText(NUMBER_TEXT_SIZE, FontWeight.Bold),
                                modifier = Modifier.uiId("tickets.0236")
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val NUMBERS_PER_ROW = 6

private const val NUMBER_DIAMETER = 35f

private const val LEGACY_NUMBER_REFERENCE_DIAMETER = 40.1168f

private const val NUMBER_TEXT_SIZE = 14f
