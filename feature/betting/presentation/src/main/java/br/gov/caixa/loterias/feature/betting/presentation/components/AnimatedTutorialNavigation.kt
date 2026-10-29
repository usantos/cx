package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R

/** Mudanças de página usam o mesmo callback de rota do paginador; a conclusão permanece uma ação separada. */
@Composable
internal fun AnimatedTutorialNavigation(page: Int, lastPage: Int, onPageSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().background(CaixaDSColorTokens.White).padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CaixaDSOutlinedButton(
            onClick = { onPageSelected(page - 1) },
            enabled = page > 0,
            modifier = (Modifier.weight(1f).heightIn(min = 48.dp)).uiId("bet.0010"),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            CaixaDSText(
                stringResource(R.string.animated_tutorial_previous),
                (Modifier.fillMaxWidth()).uiId("bet.0011"),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
        CaixaDSOutlinedButton(
            onClick = { onPageSelected(page + 1) },
            enabled = page < lastPage,
            modifier = (Modifier.weight(1f).heightIn(min = 48.dp)).uiId("bet.0012"),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            CaixaDSText(
                stringResource(R.string.animated_tutorial_next),
                (Modifier.fillMaxWidth()).uiId("bet.0013"),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
