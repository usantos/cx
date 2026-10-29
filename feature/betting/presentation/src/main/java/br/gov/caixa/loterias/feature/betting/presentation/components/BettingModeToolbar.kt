package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.recurringLabel
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("CyclomaticComplexMethod", "LongMethod")
@Composable
internal fun BettingModeToolbar(
    state: BettingGameUiState,
    onManual: () -> Unit,
    onSurprise: () -> Unit,
    onNumberQuantity: () -> Unit,
    onRecurring: () -> Unit,
    onTutorial: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val graphite = CaixaDSColorTokens.SocialGraphite
    val modalityStyle = caixaDSLotteryStyle(state.game?.modality?.id ?: 0)
    val surprise = state.mode == BetEntryMode.SURPRISE
    // SwitchMaterial mede 48dp; seu contêiner nativo adiciona 4dp de cada lado.
    val nativeRowHeight = with(androidx.compose.ui.platform.LocalDensity.current) {
        (48.dp.roundToPx() + 2 * 4.dp.roundToPx()).toDp()
    }
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = nativeRowHeight).legacyAutomationId("betting-mode-toolbar").background(
                if (surprise) {
                    modalityStyle.light.copy(
                        alpha =
                            26f / 255f
                    )
                } else {
                    CaixaDSColorTokens.SimulationToolbar
                }
            ).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier.padding(4.dp).uiId("bet.0040").toggleable(
                    value = surprise,
                    role = Role.Switch,
                    onValueChange = { checked -> if (checked) onSurprise() else onManual() }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(width = 45.dp, height = 26.dp)
                        .legacyAutomationId("betting-surprise-switch-control")
                        .background(
                            color = if (surprise) modalityStyle.light else CaixaDSColorTokens.White,
                            shape = RoundedCornerShape(13.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (surprise) modalityStyle.light else graphite,
                            shape = RoundedCornerShape(13.dp)
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        Modifier.offset(x = if (surprise) 21.dp else 0.dp)
                            .size(24.dp)
                            .background(
                                color = if (surprise) CaixaDSColorTokens.White else graphite,
                                shape = CircleShape
                            )
                            .then(
                                if (surprise) Modifier.border(2.dp, graphite, CircleShape) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_surpresinha),
                            contentDescription = null,
                            tint = if (surprise) modalityStyle.light else CaixaDSColorTokens.White,
                            modifier = (Modifier.size(width = 14.dp, height = 13.dp)).uiId("bet.0041")
                        )
                    }
                }
                // O interruptor nativo ocupa 48dp; a trilha desenhada acima ocupa 45dp.
                Text(
                    uiStrings.getString(LocalizedR.string.ui_surpresinha_34302e),
                    (Modifier.padding(start = 11.dp)).uiId("bet.0042"),
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 16.sp,
                    color = graphite,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                Modifier.uiId("bet.0043").clickable(enabled = onTutorial != null) {
                    onTutorial?.invoke()
                }.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_como_jogar_ceb452),
                    (Modifier.padding(end = 8.dp)).uiId("bet.0044"),
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 16.sp,
                    color = graphite,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
                )
                Icon(painterResource(R.drawable.ic_info), null, (Modifier.size(24.dp)).uiId("bet.0045"), tint = graphite)
            }
        }
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val style = caixaDSLotteryStyle(state.game?.modality?.id ?: 0)
            val cloverSelector =
                state.game?.modality?.id == MAIS_MILIONARIA_ID && state.selectionStep == BetSelectionStep.COMPLEMENT &&
                    state.mode == BetEntryMode.MANUAL
            CaixaDSBettingButton(
                if (cloverSelector) {
                    "${state.manualCloverQuantity} trevos"
                } else {
                    uiStrings.getString(
                        LocalizedR.string.ui_1_s_numeros_f145a3,
                        state.numberQuantity.toString()
                    )
                },
                onNumberQuantity,
                Modifier.width(
                    if (cloverSelector) 100.dp else 112.dp
                ).height(45.dp),
                color = style.light, content = style.content,
                stroke = style.light,
                textSize = 14f,
                corner = 12f,
                icon = R.drawable.ic_arrow_down,
                semiBold = !cloverSelector,
                verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
            )
            CaixaDSBettingButton(
                if (state.recurringDraws == 0) "Teimosinha" else recurringLabel(state.recurringDraws), onRecurring,
                Modifier.width(112.dp).height(45.dp), color = CaixaDSColorTokens.White, content = graphite,
                stroke = CaixaDS.colors.graphite,
                textSize = 14f,
                corner = 12f,
                icon = R.drawable.ic_arrow_down,
                retainNativeAnimator = true,
                verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
            )
            if (state.requiresManualComplement) {
                Spacer(Modifier.weight(1f))
                Text(
                    if (state.selectionStep ==
                        BetSelectionStep.COMPLEMENT
                    ) {
                        uiStrings.getString(LocalizedR.string.format_selection_step, 2, 2)
                    } else {
                        uiStrings.getString(LocalizedR.string.format_selection_step, 1, 2)
                    },
                    (Modifier.padding(end = 16.dp).height(40.dp).wrapContentHeight(Alignment.CenterVertically)).uiId("bet.0046"),
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 14.sp,
                    color = graphite,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
                )
            }
        }
    }
}
