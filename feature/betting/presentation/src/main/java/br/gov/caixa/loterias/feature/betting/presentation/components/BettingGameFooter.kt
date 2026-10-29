package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("CyclomaticComplexMethod", "LongMethod")
@Composable
internal fun BettingLegacyFooter(
    state: BettingGameUiState,
    actions: BettingGameActions,
    cartTotal: java.math.BigDecimal,
    cartCount: Int,
    onCart: (() -> Unit)?,
    onClear: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = caixaDSLotteryStyle(state.game?.modality?.id ?: 0)
    val hasSelection = if (state.selectionStep == BetSelectionStep.COMPLEMENT) {
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
    val randomEnabled = !state.loading && state.game?.modality?.id != LOTOGOL_ID &&
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
    val randomLabel = if (state.selectionStep == BetSelectionStep.COMPLEMENT) {
        when (state.game?.modality?.id) {
            MAIS_MILIONARIA_ID -> if (state.selectedClovers.isEmpty()) {
                uiStrings.getString(
                    LocalizedR.string.ui_trevos_aleatorios_56359f
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_completar_trevos_7c6818)
            }

            DIA_DE_SORTE_ID -> uiStrings.getString(LocalizedR.string.ui_mes_aleatorio_a7ecfc)

            TIMEMANIA_ID -> uiStrings.getString(LocalizedR.string.ui_time_aleatorio_972644)

            else -> uiStrings.getString(LocalizedR.string.ui_aposta_aleatoria_9dc3db)
        }
    } else if (hasSelection) {
        uiStrings.getString(LocalizedR.string.ui_completar_jogo_fbd7bf)
    } else {
        uiStrings.getString(LocalizedR.string.ui_aposta_aleatoria_9dc3db)
    }
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).background(CaixaDSColorTokens.White)) {
        if (state.mode == BetEntryMode.MANUAL) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.canFavorite) {
                    CaixaDSBettingButton(
                        if (state.favoriteSaved) {
                            uiStrings.getString(
                                LocalizedR.string.ui_aposta_favoritada_1a9222
                            )
                        } else {
                            uiStrings.getString(LocalizedR.string.ui_favoritar_aposta_a34b1e)
                        },
                        actions.requestFavorite,
                        Modifier.weight(1f).height(52.dp), CaixaDSColorTokens.White, CaixaDSColorTokens.SocialGraphite,
                        stroke = CaixaDSColorTokens.SocialGraphite,
                        icon = if (state.favoriteSaved) R.drawable.ic_favoritado_simula else R.drawable.ic_star_simula, iconAtStart = true,
                        verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
                    )
                } else {
                    CaixaDSBettingButton(
                        randomLabel, { actions.fillShake(false) }, Modifier.weight(1f).height(52.dp),
                        if (randomEnabled) CaixaDSColorTokens.White else CaixaDSColorTokens.FavoriteFilterCanvas,
                        if (randomEnabled) CaixaDSColorTokens.SocialGraphite else CaixaDSColorTokens.SimulationMuted,
                        stroke = if (randomEnabled) CaixaDSColorTokens.SocialGraphite else CaixaDSColorTokens.SimulationMuted,
                        retainNativeAnimator = true,
                        enabled = randomEnabled,
                        verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
                    )
                }
                CaixaDSBettingButton(
                    "Limpar", onClear, Modifier.width(130.dp).height(52.dp),
                    if (hasSelection) CaixaDSColorTokens.White else CaixaDSColorTokens.FavoriteFilterCanvas,
                    if (hasSelection) CaixaDSColorTokens.SocialGraphite else CaixaDSColorTokens.SimulationMuted,
                    stroke = if (hasSelection) CaixaDSColorTokens.SocialGraphite else CaixaDSColorTokens.SimulationMuted,
                    enabled = hasSelection,
                    retainNativeAnimator = true,
                    verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
                )
            }
        }
        CaixaDSBettingButton(
            state.primaryActionLabel, actions.primaryAction, Modifier.fillMaxWidth().height(52.dp),
            if (state.canPerformPrimaryAction) style.light else CaixaDSColorTokens.FavoriteFilterCanvas,
            if (state.canPerformPrimaryAction) style.content else CaixaDSColorTokens.SimulationMuted,
            stroke = if (state.canPerformPrimaryAction) style.light else CaixaDSColorTokens.SimulationMuted,
            enabled = state.canPerformPrimaryAction,
            semiBold = state.canPerformPrimaryAction,
            textSize = if (state.selectionStep == BetSelectionStep.NUMBERS && state.requiresManualComplement) 14f else 13f,
            verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
        )
        Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            BettingDSFooterAmount(
                uiStrings.getString(LocalizedR.string.ui_valor_dessa_aposta_4e1785),
                formatBetAmount(state.totalPrice),
                Modifier.weight(1f)
            )
            Box(Modifier.width(1.dp).height(32.dp).background(CaixaDSColorTokens.SimulationMuted))
            BettingDSFooterAmount(
                uiStrings.getString(LocalizedR.string.ui_total_do_carrinho_b01305),
                formatBetAmount(cartTotal),
                Modifier.weight(1f)
            )
            Box(Modifier.width(1.dp).height(32.dp).background(CaixaDSColorTokens.SimulationMuted))
            BettingDSCartFooter(cartCount, onCart)
        }
    }
}

/** Carrinho e badge desenhados em Compose com dimensões e cores do CaixaDS/rodapé legado. */
@Composable
private fun BettingDSCartFooter(cartCount: Int, onCart: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.width(56.dp).fillMaxHeight().legacyAutomationId("betting-cart-footer")
            .uiId("bet.0032").clickable(enabled = onCart != null, role = Role.Button) { onCart?.invoke() }
            .semantics {
                contentDescription = uiStrings.getString(LocalizedR.string.ui_carrinho_de_apostas_1_s_itens_e4b649, cartCount.toString())
            },
        contentAlignment = Alignment.Center
    ) {
        Image(painterResource(R.drawable.ic_cart), contentDescription = null, modifier = Modifier.size(36.dp, 32.dp))
        Box(
            Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 8.dp)
                .size(15.dp).background(CaixaDSColorTokens.SimulationBadge, CircleShape)
        )
        Box(
            Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 8.dp).size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                cartCount.toString(),
                color = CaixaDSColorTokens.SocialGraphite,
                fontFamily = CaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                modifier = Modifier.uiId("bet.0033")
            )
        }
    }
}

/** Rótulos do rodapé em tipografia e métrica do CaixaDS, com o pixel observado no XCover. */
@Composable
private fun BettingDSFooterAmount(label: String, amount: String, modifier: Modifier) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    fun roundedSp(size: Float) = with(density) { (size.sp.roundToPx().toFloat() / this.density / this.fontScale).sp }
    Column(
        modifier.wrapContentHeight().legacyAutomationId("betting-footer-amount-$label")
            .semantics { contentDescription = "$label $amount" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            label,
            modifier = (Modifier.offset { IntOffset(-1, 0) }).uiId("bet.0034"),
            color = CaixaDSColorTokens.SocialGraphite,
            fontFamily = CaixaDSFontFamily,
            fontSize = roundedSp(11f),
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        Text(
            amount,
            color = CaixaDSColorTokens.SocialGraphite,
            fontFamily = CaixaDSFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = roundedSp(14f),
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
            modifier = Modifier.uiId("bet.0035")
        )
    }
}
