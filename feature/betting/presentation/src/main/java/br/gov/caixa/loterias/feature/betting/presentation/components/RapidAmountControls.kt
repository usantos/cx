package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import java.math.BigDecimal

@Composable
internal fun RapidAmountControls(
    state: RapidConfigurationUiState,
    onMinimum: (BigDecimal) -> Unit,
    onMaximum: (BigDecimal) -> Unit,
    onRetry: () -> Unit,
    onEdit: (Boolean) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val floor = state.minimumAllowedAmount
    val ceiling = state.maximumAllowedAmount
    val available = floor != null && ceiling != null && !state.amountLimitsLoading && state.amountLimitsError == null
    val nearDailyLimit = available && state.minimumAmount.multiply(BigDecimal("4")) > ceiling
    Column(
        Modifier.fillMaxSize()
            .uiId("bet.0132").verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RapidAmountControl(
            uiStrings.getString(LocalizedR.string.ui_valor_maximo_da_aposta_349559),
            uiStrings.getString(LocalizedR.string.ui_escolha_o_valor_maximo_em_reais_da_s_c1e5d7),
            state.maximumAmount,
            if (available) {
                (
                    if (nearDailyLimit) {
                        listOf(ceiling)
                    } else {
                        listOf(
                            state.minimumAmount * BigDecimal("3"),
                            state.minimumAmount * BigDecimal("4"),
                            ceiling
                        )
                    }
                    )
            } else {
                emptyList()
            },
            onMaximum,
            { onEdit(false) },
            available
        )
        Box(Modifier.padding(top = SectionGap).width(DividerWidth).height(1.dp).background(LoteriasCaixaDS.colors.line))
        RapidAmountControl(
            uiStrings.getString(LocalizedR.string.ui_valor_minimo_da_aposta_bcf5d5),
            uiStrings.getString(LocalizedR.string.ui_escolha_o_valor_minimo_em_reais_da_s_e2ba31),
            state.minimumAmount,
            if (available) {
                (
                    if (nearDailyLimit) {
                        listOf(state.minimumAmount)
                    } else {
                        listOf(
                            state.minimumAmount,
                            state.minimumAmount * BigDecimal("2"),
                            state.minimumAmount * BigDecimal("3")
                        )
                    }
                    )
            } else {
                emptyList()
            },
            onMinimum,
            { onEdit(true) },
            available
        )
        RapidAmountLimitsFeedback(state, available, onRetry)
    }
}

private val DividerWidth = 150.dp
private val SectionGap = 20.dp
