package br.gov.caixa.loterias.feature.tickets.presentation.mapper

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.RepeatContestOption

internal fun ConfirmedBet.contestType() = if (contestTypeId == 2L) "ESPECIAL" else "NORMAL"

internal fun String.normalizedContestType() = when {
    equals("E", true) || contains("ESPECIAL", true) -> "ESPECIAL"
    else -> "NORMAL"
}

internal fun LotteryGameParameters.toRepeatOption(bet: ConfirmedBet): RepeatContestOption {
    val type = contestType.normalizedContestType()
    val targetName = if (type == "ESPECIAL") modality.specialName.ifBlank { "concurso especial" } else modality.name
    val sourceName = if (bet.contestType() == "ESPECIAL") modality.specialName.ifBlank { "concurso especial" } else modality.name
    return RepeatContestOption(
        contestType = type,
        label = targetName.ifBlank { if (type == "ESPECIAL") "Especial" else "Normal" },
        conversionMessage = messageText("a_modalidade_1_s_nao_esta_vigent_4502e8", sourceName, targetName)
    )
}

internal fun BetDetailUiState.toFavoriteDraft() = FavoriteBetDraft(
    modalityId = bet.modalityId,
    modalityName = bet.modality,
    name = favoriteName.trim(),
    numberColumns = bet.numberColumns.ifEmpty { listOf(bet.numbers) },
    clovers = bet.clovers,
    favoriteTeam = bet.favoriteTeam,
    luckyMonth = bet.luckyMonth
)

internal fun AppError.toReceiptMessage(): String = when (this) {
    AppError.Network -> messageText("nao_foi_possivel_acessar_o_servi_856170")
    is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_gerar_o_comprov_9e2cea") }
    AppError.Unexpected -> messageText("nao_foi_possivel_gerar_o_comprov_4a1d0d")
}

internal fun AppError.toFavoriteMessage(): String = when (this) {
    AppError.Network -> messageText("nao_foi_possivel_acessar_o_servi_856170")
    is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_salvar_a_aposta_eab8cf") }
    AppError.Unexpected -> messageText("nao_foi_possivel_salvar_a_aposta_79ba1d")
}

internal fun AppError.toCartMessage(): String = when (this) {
    AppError.Network -> messageText("nao_foi_possivel_acessar_o_servi_856170")
    is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_adicionar_a_apo_028911") }
    AppError.Unexpected -> messageText("nao_foi_possivel_adicionar_a_apo_072de2")
}

internal fun ConfirmedBet.toRepeatDraft(contestType: String): BetDraft {
    val bet = this
    return BetDraft(
        modalityId = bet.modalityId,
        contestType = contestType,
        numbers = bet.numbers,
        clovers = bet.clovers,
        recurringDraws = bet.recurringDraws,
        surprise = bet.surprise,
        quantity = bet.quantity,
        numberQuantity = bet.numberColumns.sumOf(List<Int>::size).takeIf { it > 0 } ?: bet.numbers.size,
        favoriteTeam = bet.favoriteTeam,
        lotecaSelections = bet.lotecaSelections,
        lotogolSelections = bet.lotogolSelections,
        superSevenColumns = bet.numberColumns.takeIf { it.size > 1 }.orEmpty(),
        luckyMonth = bet.luckyMonth,
        generateMirror = bet.mirror,
        cloverQuantity = bet.clovers.size
    )
}
