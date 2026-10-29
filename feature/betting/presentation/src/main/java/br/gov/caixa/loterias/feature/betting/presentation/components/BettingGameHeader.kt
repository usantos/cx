package br.gov.caixa.loterias.feature.betting.presentation.components

import br.gov.caixa.loterias.feature.betting.R as LocalizedR

import br.gov.caixa.loterias.feature.betting.R

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSModalityTopBar
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryKey
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.presentation.mapper.bettingHeaderDrawDate
import br.gov.caixa.loterias.feature.betting.presentation.mapper.fallbackBettingTitle
import br.gov.caixa.loterias.feature.betting.presentation.mapper.specialHeaderTitle
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("CyclomaticComplexMethod")
@Composable
internal fun BettingLegacyGameHeader(state: BettingGameUiState, onBack: () -> Unit, onHelp: () -> Unit, fallbackModalityId: Int?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val game = state.game
    val modalityId = game?.modality?.id ?: fallbackModalityId ?: 0
    val fallbackTitle = if (modalityId == MAIS_MILIONARIA_ID) {
        uiStrings.getString(
            LocalizedR.string.ui_milionaria_c791e4
        )
    } else {
        game?.modality?.name ?: fallbackBettingTitle(modalityId)
    }
    val style = if (modalityId == 14) caixaDSLotteryStyle(2, "mega-sena") else caixaDSLotteryStyle(modalityId, fallbackTitle)
    val special = game?.contestType.equals("ESPECIAL", ignoreCase = true)
    val canonicalId = when (caixaDSLotteryKey(modalityId, fallbackTitle)) {
        "QUINA" -> 3
        else -> modalityId
    }
    val megaThirtyYears = special && canonicalId == 2 && game?.contestNumber == state.specialContests.megaThirtyYearsContest
    val specialTitle = game?.takeIf {
        special
    }?.let { if (megaThirtyYears) "mega-sena\n30 anos" else specialHeaderTitle(canonicalId, it.modality.specialName) }
    val specialArtwork = game?.takeIf { special }?.let { specialHeaderArtwork(canonicalId, megaThirtyYears) }
    val reduceTitle = special && (modalityId == 8 || modalityId == LOTECA_ID)
    CaixaDSModalityTopBar(
        title = specialTitle ?: fallbackTitle.lowercase(),
        backgroundColor = style.light,
        contentColor = style.content,
        contestNumber = game?.contestNumber?.toString().orEmpty(),
        drawLabel = if (modalityId == LOTECA_ID) {
            uiStrings.getString(
                LocalizedR.string.ui_resultado_a71bc3
            )
        } else {
            uiStrings.getString(LocalizedR.string.ui_sorteio_a49bf4)
        },
        drawDate = bettingHeaderDrawDate(game?.drawDate.orEmpty()),
        onBack = onBack, onHelp = onHelp,
        backgroundArtwork = specialArtwork,
        titleScale = if (reduceTitle) .8f else 1f
    )
}

private fun specialHeaderArtwork(modalityId: Int, megaThirtyYears: Boolean): Int? = when (modalityId) {
    2 -> if (megaThirtyYears) {
        CaixaDSR.drawable.caixads_modality_mega_thirty_years
    } else {
        CaixaDSR.drawable.legacy_campaign_cabecalho_mega_da_virada_detalhe_aposta
    }

    3 -> CaixaDSR.drawable.legacy_marketplace_quina_sao_joao_simples

    8 -> CaixaDSR.drawable.legacy_marketplace_lotofacil_indepedencia_simples

    18 -> CaixaDSR.drawable.caixads_modality_easter

    19 -> CaixaDSR.drawable.legacy_marketplace_loteca_copa_simples

    else -> null
}
