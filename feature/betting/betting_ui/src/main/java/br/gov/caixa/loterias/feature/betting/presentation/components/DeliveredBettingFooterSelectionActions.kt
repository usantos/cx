package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

internal fun bettingFooterHasSelection(state: BettingGameUiState): Boolean = if (state.selectionStep == BetSelectionStep.COMPLEMENT) {
    when (state.game?.modality?.id) {
        MAIS_MILIONARIA_ID -> state.selectedClovers.isNotEmpty()
        DIA_DE_SORTE_ID -> state.selectedLuckyMonth != null
        TIMEMANIA_ID -> state.selectedTeam != null
        else -> false
    }
} else {
    state.selectedNumbers.isNotEmpty() || state.superSevenColumns.any { it.isNotEmpty() } ||
        state.lotecaOutcomes.values.any { it.isNotEmpty() } ||
        state.lotogolScores.isNotEmpty()
}

internal fun bettingFooterRandomEnabled(state: BettingGameUiState): Boolean = !state.loading && state.game?.modality?.id != LOTOGOL_ID &&
    if (state.game?.modality?.id == LOTECA_ID) {
        !state.canAddToCart
    } else if (state.selectionStep == BetSelectionStep.COMPLEMENT) {
        when (state.game?.modality?.id) {
            MAIS_MILIONARIA_ID -> state.selectedClovers.size < state.manualCloverQuantity
            DIA_DE_SORTE_ID -> state.selectedLuckyMonth == null
            TIMEMANIA_ID -> state.selectedTeam == null
            else -> false
        }
    } else {
        !state.numbersAreComplete
    }

internal fun bettingFooterRandomLabel(state: BettingGameUiState, hasSelection: Boolean, uiStrings: android.content.res.Resources): String =
    if (state.selectionStep == BetSelectionStep.COMPLEMENT) {
        when (state.game?.modality?.id) {
            MAIS_MILIONARIA_ID -> if (state.selectedClovers.isEmpty()) {
                uiStrings.getString(
                    DeliveryR.string.ui_trevos_aleatorios_56359f
                )
            } else {
                uiStrings.getString(DeliveryR.string.ui_completar_trevos_7c6818)
            }

            DIA_DE_SORTE_ID -> uiStrings.getString(DeliveryR.string.ui_mes_aleatorio_a7ecfc)

            TIMEMANIA_ID -> uiStrings.getString(DeliveryR.string.ui_time_aleatorio_972644)

            else -> uiStrings.getString(DeliveryR.string.ui_aposta_aleatoria_9dc3db)
        }
    } else if (hasSelection) {
        uiStrings.getString(DeliveryR.string.ui_completar_jogo_fbd7bf)
    } else {
        uiStrings.getString(DeliveryR.string.ui_aposta_aleatoria_9dc3db)
    }

@Composable
internal fun BettingFooterManualActions(
    state: BettingGameUiState,
    actions: BettingGameActions,
    hasSelection: Boolean,
    randomEnabled: Boolean,
    randomLabel: String,
    onClear: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.mode == BetEntryMode.MANUAL) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(ContentGap)) {
            if (state.canFavorite) {
                LoteriasCaixaDSBettingButton(
                    if (state.favoriteSaved) {
                        uiStrings.getString(
                            DeliveryR.string.ui_aposta_favoritada_1a9222
                        )
                    } else {
                        uiStrings.getString(LocalizedR.string.ui_favoritar_aposta_a34b1e)
                    },
                    actions.requestFavorite,
                    Modifier.weight(1f).height(ActionHeight), LoteriasCaixaDSColorTokens.White, LoteriasCaixaDSColorTokens.SocialGraphite,
                    stroke = LoteriasCaixaDSColorTokens.SocialGraphite,
                    icon = if (state.favoriteSaved) DeliveryR.drawable.ic_favoritado_simula else DeliveryR.drawable.ic_star_simula,
                    iconAtStart = true,
                    verticalInset = LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset
                )
            } else {
                LoteriasCaixaDSBettingButton(
                    randomLabel,
                    { actions.fillShake(false) },
                    Modifier.weight(1f).height(ActionHeight),
                    if (randomEnabled) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDSColorTokens.FavoriteFilterCanvas,
                    if (randomEnabled) LoteriasCaixaDSColorTokens.SocialGraphite else LoteriasCaixaDSColorTokens.SimulationMuted,
                    stroke = if (randomEnabled) LoteriasCaixaDSColorTokens.SocialGraphite else LoteriasCaixaDSColorTokens.SimulationMuted,
                    enabled = randomEnabled,
                    verticalInset = LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset
                )
            }
            LoteriasCaixaDSBettingButton(
                "Limpar",
                onClear,
                Modifier.width(ClearActionWidth).height(ActionHeight),
                if (hasSelection) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDSColorTokens.FavoriteFilterCanvas,
                if (hasSelection) LoteriasCaixaDSColorTokens.SocialGraphite else LoteriasCaixaDSColorTokens.SimulationMuted,
                stroke = if (hasSelection) LoteriasCaixaDSColorTokens.SocialGraphite else LoteriasCaixaDSColorTokens.SimulationMuted,
                enabled = hasSelection,
                verticalInset = LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset
            )
        }
    }
}

private val ClearActionWidth = 130.dp
private val ActionHeight = 52.dp
private val ContentGap = 8.dp
