package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSRadioButton as RadioButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID

@Composable
internal fun LuckyMonthContent(state: BettingGameUiState, actions: BettingGameActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val months = state.game?.luckyMonths.orEmpty()
    Column(Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        months.chunked(2).forEach { rowMonths ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                rowMonths.forEach { month ->
                    val checked = month == state.selectedLuckyMonth
                    Box(
                        Modifier.weight(1f)
                            .legacyAutomationId("lucky-month-${month.name}")
                            .clip(RoundedCornerShape(4.dp))
                            .then(
                                if (checked) {
                                    Modifier.background(caixaDSLotteryStyle(DIA_DE_SORTE_ID).light)
                                } else {
                                    Modifier.border(1.dp, CaixaDSColorTokens.SimulationMuted, RoundedCornerShape(4.dp))
                                }
                            )
                            .uiId("bet.0265").selectable(selected = checked, role = Role.RadioButton) { actions.selectLuckyMonth(month) }
                            .semantics {
                                contentDescription = month.name
                                this.selected = checked
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            month.name,
                            fontFamily = CaixaDSFontFamily,
                            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = CaixaDSColorTokens.SocialGraphite,
                            textAlign = TextAlign.Center,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                            modifier = Modifier
                                .uiId("bet.0266")
                        )
                    }
                }
                repeat(2 - rowMonths.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        if (months.isEmpty()) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_meses_indisponiveis_para_este_concur_d5f08f),
                color = CaixaDS.colors.mutedText,
                modifier = Modifier.uiId("bet.0267")
            )
        }
    }
}
