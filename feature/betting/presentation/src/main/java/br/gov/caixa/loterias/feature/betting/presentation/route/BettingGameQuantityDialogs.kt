package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSOptionDialog
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.mapper.recurringLabel
import br.gov.caixa.loterias.feature.betting.presentation.mapper.resolveBetPrice
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID

@Composable
internal fun BettingNumberQuantityDialog(state: BettingGameUiState, onSelect: (Int) -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSOptionDialog(
        title = if (state.game?.modality?.id == LOTECA_ID) {
            uiStrings.getString(
                LocalizedR.string.ui_quantidade_de_palpites_ef7895
            )
        } else {
            uiStrings.getString(LocalizedR.string.ui_quantidade_de_numeros_ca55cf)
        },
        options = state.numberQuantityOptions,
        description = if (state.game?.modality?.id == LOTECA_ID) {
            uiStrings.getString(
                LocalizedR.string.ui_escolha_em_quantos_palpites_voce_que_4d2dce
            )
        } else {
            null
        },
        selected = state.numberQuantity,
        subtitle = if (state.game?.modality?.id == MAIS_MILIONARIA_ID) {
            uiStrings.getString(
                LocalizedR.string.ui_valores_para_apostas_com_2_trevos_d4655c
            )
        } else {
            null
        },
        label = { quantity ->
            if (state.game?.modality?.id == LOTECA_ID) {
                uiStrings.getString(LocalizedR.string.ui_1_s_palpites_b6b418, quantity.toString())
            } else {
                val price = state.game?.let {
                    resolveBetPrice(
                        it,
                        quantity,
                        if (state.mode == BetEntryMode.SURPRISE) state.surpriseCloverQuantity else state.selectedClovers.size
                    )
                }
                uiStrings.getString(LocalizedR.string.ui_1_s_numeros_por_2_s_daf587, quantity.toString(), formatBetAmount(price))
            }
        },
        onSelect = onSelect,
        onDismiss = onDismiss
    )
}

@Composable
internal fun BettingCloverQuantityDialog(state: BettingGameUiState, onSelect: (Int) -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSOptionDialog(
        title = uiStrings.getString(LocalizedR.string.ui_quantidade_de_trevos_5dc723),
        subtitle = uiStrings.getString(LocalizedR.string.ui_para_aposta_com_1_s_numeros_aa05ed, state.numberQuantity.toString()),
        options = state.cloverQuantityOptions,
        selected = state.manualCloverQuantity,
        label = { quantity ->
            uiStrings.getString(
                LocalizedR.string.ui_1_s_trevos_por_2_s_427198,
                quantity.toString(),
                formatBetAmount(state.game?.let { resolveBetPrice(it, state.numberQuantity, quantity) })
            )
        },
        onSelect = onSelect,
        onDismiss = onDismiss
    )
}

@Composable
internal fun BettingRecurringDialog(state: BettingGameUiState, onSelect: (Int) -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSOptionDialog(
        title = uiStrings.getString(LocalizedR.string.ui_teimosinhas_7217a4),
        description = uiStrings.getString(LocalizedR.string.ui_escolha_em_quantos_concursos_voce_qu_3736e6),
        options = state.recurringOptions,
        selected = state.recurringDraws,
        label = ::recurringLabel,
        onSelect = onSelect,
        onDismiss = onDismiss
    )
}
