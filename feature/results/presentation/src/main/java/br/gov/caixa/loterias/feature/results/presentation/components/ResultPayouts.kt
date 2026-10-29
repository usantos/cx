package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultMoney
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultPayoutDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultPayoutSpokenDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultWinners
import br.gov.caixa.loterias.feature.results.presentation.mapper.spokenResultMoney

@Composable
internal fun ResultPayouts(
    result: DrawResult,
    modality: String,
    style: CaixaDSLotteryStyle,
    payoutStyle: TextStyle,
    gap16: Dp,
    gap18: Dp,
    bottom180: Dp,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxWidth().padding(start = 30.dp, top = 10.dp, end = 30.dp, bottom = 84.dp)
            .background(style.light).padding(horizontal = 16.dp).padding(bottom = 30.dp + bottom180)
    ) {
        Text(
            if (result.secondDrawPayouts.isNotEmpty()) {
                uiStrings.getString(
                    LocalizedR.string.ui_premiacao_do_1o_sorteio_7b478e
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_premiacao_b81f73)
            },
            (Modifier.padding(top = gap16)).uiId("results.0045"),
            color = style.content,
            style = payoutStyle.copy(fontWeight = FontWeight.Bold)
        )
        result.payouts.forEach { payout ->
            Column(
                Modifier.clearAndSetSemantics {
                    contentDescription = resultPayoutSpokenDescription(
                        if (result.secondDrawPayouts.isEmpty()) "Premiação" else "Premiação do primeiro sorteio",
                        modality,
                        payout
                    )
                }
            ) {
                Text(
                    resultPayoutDescription(modality, payout.description),
                    (Modifier.padding(top = gap16)).uiId("results.0046"),
                    color = style.content,
                    style = payoutStyle.copy(fontWeight = FontWeight.Bold)
                )
                Text(resultWinners(payout.winners), color = style.content, style = payoutStyle, modifier = Modifier.uiId("results.0047"))
                if (payout.winners > 0) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_premio_1_s_eb3e48, resultMoney(payout.value)),
                        color = style.content,
                        style = payoutStyle,
                        modifier = Modifier.uiId("results.0048")
                    )
                }
            }
        }
        DrawResultSupplement(result, style.content, includeContestSummary = false)
        Column(
            Modifier.clearAndSetSemantics {
                contentDescription = "Arrecadação total: ${spokenResultMoney(result.totalRevenue.ifBlank { "0" })}."
            }
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_arrecadacao_total_968b74),
                (Modifier.padding(top = gap18)).uiId("results.0049"),
                color = style.content,
                style = payoutStyle.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                resultMoney(
                    result.totalRevenue.ifBlank {
                        "0"
                    }
                ),
                (Modifier.padding(top = gap16)).uiId("results.0050"),
                color = style.content,
                style = payoutStyle.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
