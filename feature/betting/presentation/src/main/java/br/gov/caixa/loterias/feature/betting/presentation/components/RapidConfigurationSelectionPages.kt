package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality

@Composable
internal fun RapidModalitiesPage(modalities: List<LotteryModality>, selected: Set<Int>, onToggle: (Int) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxSize().uiId("bet.0156").verticalScroll(rememberScrollState())) {
        RapidSectionHeader(
            "Modalidades",
            uiStrings.getString(LocalizedR.string.ui_selecione_as_opcoes_que_voce_quer_co_09796c),
            titleTop = 40.dp,
            descriptionTop = 20.dp,
            descriptionPadding = 20.dp
        )
        Spacer(Modifier.height(19.dp))
        modalities.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                row.forEach { modality ->
                    val checked = modality.id in selected
                    val style = caixaDSLotteryStyle(modality.id, modality.name)
                    Row(
                        Modifier.weight(1f).padding(4.dp).height(56.dp)
                            .background(if (checked) style.light else CaixaDS.colors.surface)
                            .then(if (checked) Modifier else Modifier.border(1.dp, CaixaDS.colors.graphite, RoundedCornerShape(4.dp)))
                            .uiId("bet.0157").toggleable(value = checked, role = Role.Checkbox) { onToggle(modality.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (modality.id == 14) {
                            Image(
                                painterResource(CaixaDSR.drawable.legacy_trevo_lotogol),
                                null,
                                Modifier.padding(start = 12.dp).size(18.dp)
                            )
                        } else {
                            Image(caixaDSLotteryClover(modality.id, onDark = checked), null, Modifier.padding(start = 12.dp).size(18.dp))
                        }
                        Text(
                            modality.name.lowercase(),
                            (Modifier.padding(start = 8.dp)).uiId("bet.0158"),
                            color = if (checked) style.content else CaixaDS.colors.mutedText,
                            fontFamily = CaixaDSFuturaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
internal fun RapidNumbersPage(
    title: String,
    description: String,
    selected: Set<Int>,
    onToggle: (Int) -> Unit,
    onClear: () -> Unit,
    danger: Boolean = false,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxSize().uiId("bet.0159").verticalScroll(rememberScrollState())) {
        RapidSectionHeader(title, description, titleTop = 40.dp, descriptionTop = 0.dp, descriptionPadding = 20.dp)
        if (selected.isNotEmpty()) {
            TextButton(
                onClick = onClear,
                modifier = (
                    Modifier.align(
                        Alignment.CenterHorizontally
                    ).width(184.dp).height(56.dp).border(1.dp, CaixaDS.colors.graphite, RoundedCornerShape(4.dp))
                    ).uiId("bet.0160")
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_limpar_selecao_33a042),
                    color = CaixaDS.colors.graphite,
                    fontSize = 14.2.sp,
                    fontFamily = CaixaDSFontFamily,
                    modifier = Modifier.uiId("bet.0161")
                )
            }
        }
        RapidNumberCells((1..100).toList(), selected, danger, onToggle, Modifier.padding(horizontal = 8.dp, vertical = 19.dp))
        Spacer(Modifier.height(100.dp))
    }
}
