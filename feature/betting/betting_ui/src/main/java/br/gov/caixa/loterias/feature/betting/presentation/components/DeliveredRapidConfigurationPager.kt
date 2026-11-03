package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun RapidPageIndicator(page: Int, onPage: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(40.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(RAPID_PAGE_COUNT) { index ->
            val stepDescription = stringResource(DeliveryR.string.format_step_description, index + 1, RAPID_PAGE_COUNT)
            Box(
                Modifier.width(15.dp).fillMaxHeight()
                    .semantics {
                        contentDescription = stepDescription
                        selected = index == page
                    }
                    .uiId("bet.0155").clickable { onPage(index) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier.size(5.dp)
                        .background(if (index == page) LoteriasCaixaDS.colors.teal else LoteriasCaixaDS.colors.surface, CircleShape)
                        .border(1.dp, LoteriasCaixaDS.colors.teal, CircleShape)
                )
            }
        }
    }
}
