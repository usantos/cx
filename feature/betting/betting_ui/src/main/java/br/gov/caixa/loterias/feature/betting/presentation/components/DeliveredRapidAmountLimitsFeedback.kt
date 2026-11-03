package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
internal fun RapidAmountLimitsFeedback(state: RapidConfigurationUiState, available: Boolean, onRetry: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.amountLimitsLoading) CircularProgressIndicator(Modifier.padding(SectionGap), color = LoteriasCaixaDS.colors.teal)
    if (state.amountLimitsError != null || (!available && !state.amountLimitsLoading)) {
        Text(
            state.amountLimitsError ?: uiStrings.getString(DeliveryR.string.ui_nao_foi_possivel_carregar_os_limites_08d889),
            (Modifier.padding(ContentInset)).uiId("bet.0133"),
            color = LoteriasCaixaDS.colors.graphite,
            fontFamily = LoteriasCaixaDSFontFamily,
            textAlign = TextAlign.Center
        )
        TextButton(
            onClick = onRetry,
            modifier = Modifier
                .uiId("bet.0134")
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                modifier = Modifier
                    .uiId("bet.0135")
            )
        }
    }
}
private val ContentInset = 16.dp
private val SectionGap = 20.dp
