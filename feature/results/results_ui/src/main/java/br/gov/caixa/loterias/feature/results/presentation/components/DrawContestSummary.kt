package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.presentation.accessibility.PortugueseSpeech
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultMoney
import br.gov.caixa.loterias.feature.results.presentation.mapper.spokenResultMoney
import br.gov.caixa.loterias.feature.results.ui.R as DeliveryR

@Composable
internal fun DrawContestSummary(result: DrawResult, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    // VisualizarResultadosActivity renderiza somente detalhes dos prêmios e arrecadação.
    // O resumo do serviço pertence a outras apresentações de resultado na CX.
    if (result.specialContest) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_concurso_especial_f9b7c4),
            (Modifier.padding(top = 12.dp)).uiId("results.0026"),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
    if (result.drawLocation.isNotBlank()) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_local_do_sorteio_1_s_101c6d, result.drawLocation),
            (Modifier.padding(top = 16.dp)).uiId("results.0027"),
            color = color
        )
    }
    if (result.notice.isNotBlank()) Text(result.notice, (Modifier.padding(top = 12.dp)).uiId("results.0028"), color = color)
    if (result.accumulated) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_acumulou_d5cf3d),
            (Modifier.padding(top = 12.dp)).uiId("results.0029"),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
    if (result.accumulatedNextContest.isNotBlank()) {
        Text(
            uiStrings.getString(
                DeliveryR.string.ui_acumulado_para_o_proximo_concurso_1_942144,
                resultMoney(result.accumulatedNextContest)
            ),
            color = color,
            modifier = Modifier.uiId("results.0030")
        )
    }
    listOf(
        uiStrings.getString(DeliveryR.string.ui_proximo_concurso_aab095) to result.nextContest,
        uiStrings.getString(DeliveryR.string.ui_proximo_concurso_final_zero_7ffe7d) to result.nextFinalZeroContest,
        uiStrings.getString(DeliveryR.string.ui_proximo_concurso_final_cinco_be1e62) to result.nextFinalFiveContest,
        uiStrings.getString(DeliveryR.string.ui_proximo_concurso_especial_3276ac) to result.nextSpecialContest
    ).forEach { (
        title,
        contest,
    ),
        ->
        contest?.let {
            Column(
                Modifier.clearAndSetSemantics {
                    contentDescription = buildList {
                        add("${PortugueseSpeech.expand(title)}: ${PortugueseSpeech.number(it.number)}")
                        if (it.date.isNotBlank()) add(PortugueseSpeech.date(it.date))
                        if (it.estimatedPrize.isNotBlank()) add("Estimativa de prêmio: ${spokenResultMoney(it.estimatedPrize)}")
                    }.joinToString(". ", postfix = ".")
                }
            ) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_1_s_2_s_13326c, title, it.number.toString()),
                    (Modifier.padding(top = 12.dp)).uiId("results.0031"),
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                if (it.date.isNotBlank()) Text(it.date, color = color, modifier = Modifier.uiId("results.0032"))
                if (it.estimatedPrize.isNotBlank()) {
                    Text(
                        uiStrings.getString(
                            DeliveryR.string.ui_estimativa_de_premio_1_s_aa5411,
                            resultMoney(it.estimatedPrize)
                        ),
                        color = color,
                        modifier = Modifier.uiId("results.0033")
                    )
                }
            }
        }
    }
}
