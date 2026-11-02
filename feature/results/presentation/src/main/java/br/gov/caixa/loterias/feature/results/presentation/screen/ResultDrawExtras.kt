package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

@Composable
internal fun ColumnScope.ResultDrawExtras(result: DrawResult, modality: String, style: LoteriasCaixaDSLotteryStyle, bodyStyle: TextStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (result.clovers.isNotEmpty()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_trevos_sorteados_2645f8),
            (Modifier.padding(start = 30.dp, top = 10.dp)).uiId("results.0076"),
            color = style.darkContent,
            style = bodyStyle
        )
        Row(Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)) {
            result.clovers.sorted().forEach { clover ->
                Box(
                    Modifier.width(56.dp).height(40.dp).semantics(mergeDescendants = true) {
                        contentDescription = uiStrings.getString(
                            LocalizedR.string.format_clover_description,
                            clover
                        )
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.legacy_result_ic_item_trevo_selecionado), null, Modifier.size(40.dp))
                    Text(
                        clover.toString().padStart(2, '0'),
                        color = style.light,
                        style = bodyStyle.copy(fontWeight = FontWeight.Bold, fontSize = 14.1.sp),
                        modifier = Modifier.uiId("results.0077")
                    )
                }
            }
        }
    }
    val drawnSelection = when (modality) {
        "timemania" -> uiStrings.getString(LocalizedR.string.ui_time_do_coracao_918360) to result.favoriteTeamReward?.selection

        "diadesorte" -> uiStrings.getString(LocalizedR.string.ui_mes_de_sorte_4fd420) to
            (result.luckyMonthReward?.selection ?: "???")

        else -> null
    }
    drawnSelection?.let { (label, selection) ->
        Row(Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)) {
            if (modality == "timemania") {
                Image(
                    painterResource(R.drawable.legacy_result_ic_timecoracao),
                    null,
                    Modifier.width(41.dp).height(34.dp)
                )
            }
            Column(Modifier.padding(start = 8.dp)) {
                Text(label, color = style.darkContent, style = bodyStyle, modifier = Modifier.uiId("results.0078"))
                Text(
                    selection.orEmpty(),
                    color = style.darkContent,
                    style = bodyStyle.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.uiId("results.0079")
                )
            }
        }
    }
}
