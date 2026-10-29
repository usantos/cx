package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID

internal class BettingGameNoticeMapper(private val dispatch: (BettingGameAction) -> Unit) {
    fun incompleteBetMessage(state: BettingGameUiState): String = when (state.mode) {
        BetEntryMode.SURPRISE -> messageText("informe_uma_quantidade_valida_de_f055ff")

        BetEntryMode.MANUAL -> {
            when (state.game?.modality?.id) {
                TIMEMANIA_ID -> if (state.selectedNumbers.size == state.numberQuantity) {
                    return messageText("selecione_o_time_do_coracao_7bbd51")
                }

                LOTECA_ID -> return messageText("selecione_todos_os_jogos_e_ao_me_13258f")

                LOTOGOL_ID -> return messageText("informe_o_placar_de_todos_os_jog_1e76a6")

                SUPER_SEVEN_ID -> return messageText("selecione_a_quantidade_escolhida_9aaa6d")

                DIA_DE_SORTE_ID -> if (state.selectedNumbers.size == state.numberQuantity) {
                    return messageText("selecione_o_mes_da_sorte_c559a5")
                }
            }
            val missing = (state.numberQuantity - state.selectedNumbers.size).coerceAtLeast(0)
            if (missing == 1) messageText("selecione_mais_1_numero_96f15b") else messageText("selecione_mais_1_s_numeros_868900", missing)
        }
    }

    fun applyFailure(error: AppError) = dispatch(BettingGameAction.FailureReceived(error, error.userMessage()))

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> messageText("nao_foi_possivel_carregar_a_apos_fa8c4f")
        is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_atualizar_a_apo_da2fb5") }
        AppError.Unexpected -> messageText("ocorreu_um_erro_inesperado_ao_ca_ba37c6")
    }
}
