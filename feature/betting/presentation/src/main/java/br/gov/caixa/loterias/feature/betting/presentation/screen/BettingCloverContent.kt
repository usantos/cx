package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID

@Composable
internal fun CloverSelectionContent(state: BettingGameUiState, actions: BettingGameActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val game = requireNotNull(state.game)
    val metrics = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    Text(
        uiStrings.getString(LocalizedR.string.ui_escolha_de_2_a_6_trevos_para_marcar_74f1fb),
        (Modifier.fillMaxWidth().padding(start = 60.dp, end = 60.dp, top = 36.dp)).uiId("bet.0229"),
        fontFamily = CaixaDSFontFamily,
        fontSize = 16.sp,
        color = CaixaDSColorTokens.SocialGraphite,
        textAlign = TextAlign.Center,
        style = metrics
    )
    Column(Modifier.fillMaxWidth().padding(top = 42.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        (1..game.maximumCloverNumber).toList().chunked(3).forEach { row ->
            Row {
                row.forEach { number ->
                    val checked = number in state.selectedClovers
                    val dark = caixaDSLotteryStyle(MAIS_MILIONARIA_ID).dark
                    Box(
                        Modifier.padding(8.dp).size(45.dp)
                            .legacyAutomationId("betting-clover-$number")
                            .semantics {
                                contentDescription = uiStrings.getString(LocalizedR.string.format_clover_description, number)
                                this.selected = checked
                            }
                            .uiId("bet.0230").toggleable(value = checked, role = Role.Checkbox) { actions.toggleClover(number) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                if (checked) {
                                    R.drawable.installed_betting_ic_trevo_selecionado
                                } else {
                                    R.drawable.installed_betting_ic_trevo
                                }
                            ),
                            contentDescription = null,
                            tint = dark,
                            modifier = (Modifier.fillMaxSize()).uiId("bet.0231")
                        )
                        Text(
                            number.toString(),
                            color = if (checked) CaixaDSColorTokens.White else dark,
                            fontFamily = CaixaDSFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.uiId("bet.0232")
                        )
                    }
                }
            }
        }
    }
}
