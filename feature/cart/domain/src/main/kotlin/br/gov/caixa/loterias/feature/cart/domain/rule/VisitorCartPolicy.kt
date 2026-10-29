package br.gov.caixa.loterias.feature.cart.domain.rule

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.cart.domain.model.VISITOR_DUPLICATE_CONFIRMATION
import br.gov.caixa.loterias.feature.cart.domain.model.VisitorCartEntry
import br.gov.caixa.loterias.feature.cart.domain.model.VisitorCartSelection
import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText
import java.math.BigDecimal

/** Regras de apostas de visitantes. A persistência fornece retratos e aplica todas as mutações atomicamente. */
object VisitorCartPolicy {
    fun gameFor(draft: BetDraft, parameters: BettingParameters): LotteryGameParameters {
        require(draft.quantity in 1..1000) { messageText("quantidade_de_apostas_invalida_f224c8") }
        val game = requireNotNull(
            parameters.games.firstOrNull {
                it.modality.id == draft.modalityId && it.contestType.equals(draft.contestType, true) &&
                    (draft.contestNumber == null || draft.contestNumber == it.contestNumber)
            }
        ) { messageText("modalidade_indisponivel_para_apo_59acb4") }
        require(game.open) { messageText("o_registro_de_apostas_individuai_b546d7", game.contestNumber) }
        return game
    }

    fun requireCapacity(draft: BetDraft, maximumCartBets: Int, existingCount: Int) {
        // A reserva de capacidade legada conta um espelho solicitado para cada modalidade.
        val count = draft.quantity * if (draft.generateMirror) 2 else 1
        require(maximumCartBets <= 0 || existingCount + count <= maximumCartBets) {
            messageText("nao_e_permitido_incluir_uma_nova_f0e027")
        }
    }

    fun unitAmount(draft: BetDraft, game: LotteryGameParameters): BigDecimal {
        val price = if (draft.modalityId == 19) {
            br.gov.caixa.loterias.feature.betting.domain.rule.BetPricing.lotecaPrice(
                game,
                draft.lotecaSelections.count { selection ->
                    listOf(selection.homeWin, selection.draw, selection.awayWin).count { it } == 2
                },
                draft.lotecaSelections.count { selection -> selection.homeWin && selection.draw && selection.awayWin }
            )
        } else {
            br.gov.caixa.loterias.feature.betting.domain.rule.BetPricing.numericPrice(game, draft.numberQuantity, draft.cloverQuantity)
        }
        val unit = requireNotNull(price) { messageText("preco_da_aposta_indisponivel_2b7239") }.amount
        return br.gov.caixa.loterias.feature.betting.domain.rule.BetPricing.total(unit, draft.recurringDraws, 1)
    }

    fun requiresDuplicateCheck(draft: BetDraft): Boolean = !draft.surprise && !draft.allowLocalDuplicate

    fun requireNoDuplicate(draft: BetDraft, candidate: VisitorCartSelection, existing: List<VisitorCartSelection>) {
        if (requiresDuplicateCheck(draft)) require(candidate !in existing) { VISITOR_DUPLICATE_CONFIRMATION }
    }

    fun mirror(draft: BetDraft): BetDraft? = if (draft.generateMirror && draft.modalityId == 16) {
        // Os números da Surpresinha são gerados pelo servidor, incluindo seu espelho.
        draft.copy(numbers = if (draft.surprise) emptyList() else (1..100).filterNot { it in draft.numbers }, generateMirror = false)
    } else {
        null
    }

    fun mirrorLink(localId: Long, contestNumber: Int): Long = localId + contestNumber

    fun removalIds(itemId: Long, entries: List<VisitorCartEntry>): List<Long> {
        val bet = requireNotNull(entries.firstOrNull { it.localId == itemId }) { messageText("aposta_local_nao_encontrada_8ea469") }
        return if (bet.mirrorLink > 0) {
            entries.filter { it.mirrorLink == bet.mirrorLink }.map { it.localId }
        } else {
            listOf(bet.localId)
        }
    }
}
