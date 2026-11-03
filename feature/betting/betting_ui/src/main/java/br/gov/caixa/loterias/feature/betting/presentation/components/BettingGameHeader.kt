package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.R as LoteriasCaixaDSR
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSModalityTopBar
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryKey
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.presentation.mapper.bettingHeaderDrawDate
import br.gov.caixa.loterias.feature.betting.presentation.mapper.fallbackBettingTitle
import br.gov.caixa.loterias.feature.betting.presentation.mapper.specialHeaderTitle
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("CyclomaticComplexMethod")
@Composable
fun BettingLegacyGameHeader(state: BettingGameUiState, onBack: () -> Unit, onHelp: () -> Unit, fallbackModalityId: Int?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val game = state.game
    val modalityId = game?.modality?.id ?: fallbackModalityId ?: 0
    val fallbackTitle = if (modalityId == MAIS_MILIONARIA_ID) {
        uiStrings.getString(
            DeliveryR.string.ui_milionaria_c791e4
        )
    } else {
        game?.modality?.name ?: fallbackBettingTitle(modalityId)
    }
    val style = if (modalityId ==
        14
    ) {
        loteriasCaixaDSLotteryStyle(2, "mega-sena")
    } else {
        loteriasCaixaDSLotteryStyle(modalityId, fallbackTitle)
    }
    val special = game?.contestType.equals("ESPECIAL", ignoreCase = true)
    val canonicalId = when (loteriasCaixaDSLotteryKey(modalityId, fallbackTitle)) {
        "QUINA" -> 3
        else -> modalityId
    }
    val megaThirtyYears = special && canonicalId == 2 && game?.contestNumber == state.specialContests.megaThirtyYearsContest
    val specialTitle = game?.takeIf {
        special
    }?.let { if (megaThirtyYears) "mega-sena\n30 anos" else specialHeaderTitle(canonicalId, it.modality.specialName) }
    val specialArtwork = game?.takeIf { special }?.let { specialHeaderArtwork(canonicalId, megaThirtyYears) }
    val reduceTitle = special && (modalityId == 8 || modalityId == LOTECA_ID)
    LoteriasCaixaDSModalityTopBar(
        title = specialTitle ?: fallbackTitle.lowercase(),
        backgroundColor = style.light,
        contentColor = style.content,
        contestNumber = game?.contestNumber?.toString().orEmpty(),
        drawLabel = if (modalityId == LOTECA_ID) {
            uiStrings.getString(
                DeliveryR.string.ui_resultado_a71bc3
            )
        } else {
            uiStrings.getString(DeliveryR.string.ui_sorteio_a49bf4)
        },
        drawDate = bettingHeaderDrawDate(game?.drawDate.orEmpty()),
        onBack = onBack, onHelp = onHelp,
        backgroundArtwork = specialArtwork,
        titleScale = if (reduceTitle) .8f else 1f
    )
}

private fun specialHeaderArtwork(modalityId: Int, megaThirtyYears: Boolean): Int? = when (modalityId) {
    2 -> if (megaThirtyYears) {
        LoteriasCaixaDSR.drawable.loterias_caixa_ds_modality_mega_thirty_years
    } else {
        LoteriasCaixaDSR.drawable.legacy_campaign_cabecalho_mega_da_virada_detalhe_aposta
    }

    3 -> LoteriasCaixaDSR.drawable.legacy_marketplace_quina_sao_joao_simples

    8 -> LoteriasCaixaDSR.drawable.legacy_marketplace_lotofacil_indepedencia_simples

    18 -> LoteriasCaixaDSR.drawable.loterias_caixa_ds_modality_easter

    19 -> LoteriasCaixaDSR.drawable.legacy_marketplace_loteca_copa_simples

    else -> null
}
