package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.home.presentation.components.ResultCard
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

@Composable
internal fun HomeV2Presentation.LatestResults(cards: List<HomeLottery>, results: Map<String, DrawResult>) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Section(uiStrings.getString(DeliveryR.string.ui_ultimos_resultados_efecc0), action = actions::results)
    Row(
        Modifier.fillMaxWidth().height(IntrinsicSize.Min).uiId("home.0148").horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cards.filter {
            it.tipoModalidade in listOf(
                HomeV2Modality.MEGA_SENA,
                HomeV2Modality.MAIS_MILIONARIA,
                HomeV2Modality.QUINA
            )
        }.forEach { ResultCard(it, results[it.resultCode]) }
    }
}
