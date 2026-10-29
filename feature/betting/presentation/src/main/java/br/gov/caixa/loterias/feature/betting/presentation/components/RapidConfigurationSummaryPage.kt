package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSRapidEditIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState

@Composable
internal fun RapidSummaryPage(state: RapidConfigurationUiState, onEdit: (Int) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxSize().uiId("bet.0162").verticalScroll(rememberScrollState())) {
        RapidSectionHeader(
            "Resumo",
            uiStrings.getString(LocalizedR.string.ui_verifique_e_confirme_suas_opcoes_de_8815ef),
            descriptionTop = 8.dp
        )
        RapidSummaryCard(
            uiStrings.getString(LocalizedR.string.ui_premio_minimo_4a3c9c),
            if (state.minimumPrize.signum() == 0) {
                uiStrings.getString(
                    LocalizedR.string.ui_qualquer_valor_c92127
                )
            } else {
                formatBetAmount(state.minimumPrize)
            }
        ) { onEdit(1) }
        RapidSummaryCard(
            uiStrings.getString(LocalizedR.string.ui_valor_maximo_da_aposta_349559),
            formatBetAmount(state.maximumAmount)
        ) { onEdit(2) }
        RapidSummaryCard(
            uiStrings.getString(LocalizedR.string.ui_valor_minimo_da_aposta_bcf5d5),
            formatBetAmount(state.minimumAmount)
        ) { onEdit(2) }
        RapidSummaryCard(
            "Modalidades",
            state.availableModalities.filter {
                it.id in state.selectedModalityIds
            }.joinToString { it.name }
        ) { onEdit(3) }
        RapidSummaryNumbers(
            uiStrings.getString(LocalizedR.string.ui_numeros_obrigatorios_621e09),
            state.requiredNumbers,
            false,
            { onEdit(4) }
        )
        RapidSummaryNumbers(uiStrings.getString(LocalizedR.string.ui_numeros_proibidos_09cf4d), state.excludedNumbers, true, { onEdit(5) })
    }
}

@Composable
private fun RapidSummaryCard(label: String, value: String, onEdit: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Card(
        Modifier.fillMaxWidth().padding(15.dp).height(100.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDSColorTokens.RapidSummaryBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center).padding(horizontal = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    label,
                    color = CaixaDS.colors.graphite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CaixaDSFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.uiId("bet.0163")
                )
                Text(
                    value,
                    color = CaixaDSColorTokens.FavoriteCartCard,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CaixaDSFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.uiId("bet.0164")
                )
            }
            IconButton(
                onClick = onEdit,
                modifier = (Modifier.align(Alignment.TopEnd).size(40.dp)).uiId("bet.0165")
            ) {
                Icon(
                    CaixaDSRapidEditIcon,
                    uiStrings.getString(LocalizedR.string.ui_editar_1_s_04ce4e, label),
                    tint = Color.Unspecified,
                    modifier = (Modifier.size(24.dp)).uiId("bet.0166")
                )
            }
        }
    }
}

@Composable
private fun RapidSummaryNumbers(label: String, numbers: Set<Int>, danger: Boolean, onEdit: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(top = 24.dp)) {
        Row(Modifier.padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                color = CaixaDS.colors.graphite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.2.sp,
                fontFamily = CaixaDSFontFamily,
                modifier = Modifier.uiId("bet.0167")
            )
            IconButton(
                onClick = onEdit,
                modifier = (Modifier.size(40.dp)).uiId("bet.0168")
            ) {
                Icon(
                    CaixaDSRapidEditIcon,
                    uiStrings.getString(LocalizedR.string.ui_editar_1_s_04ce4e, label),
                    tint = Color.Unspecified,
                    modifier = Modifier.uiId("bet.0169")
                )
            }
        }
        RapidNumberCells(numbers.sorted(), numbers, danger, null, Modifier.padding(horizontal = 10.dp, vertical = 19.dp))
        CaixaDSDivider()
    }
}
