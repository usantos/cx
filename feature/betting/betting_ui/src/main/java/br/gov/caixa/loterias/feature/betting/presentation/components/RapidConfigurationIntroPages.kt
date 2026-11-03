package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSWheel
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSRapidIcon
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
fun RapidWelcomePage() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    BoxWithConstraints(Modifier.fillMaxSize()) {
        // A diretriz legada fixa o divisor em 45% da altura do paginador.
        val dividerTop = maxHeight * 0.45f
        Icon(
            LoteriasCaixaDSRapidIcon,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = (Modifier.align(Alignment.TopCenter).offset(y = dividerTop - 110.dp).size(100.dp)).uiId("bet.0146")
        )
        Column(Modifier.fillMaxWidth().offset(y = dividerTop), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.width(150.dp).height(1.dp).background(LoteriasCaixaDS.colors.teal))
            Text(
                uiStrings.getString(DeliveryR.string.ui_bem_vindo_ao_78ba61),
                (Modifier.padding(top = 5.dp)).uiId("bet.0147"),
                color = LoteriasCaixaDS.colors.teal,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = LoteriasCaixaDSFontFamily
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_rapidao_658c41),
                (Modifier.padding(top = 2.dp)).uiId("bet.0148"),
                color = LoteriasCaixaDSColorTokens.BurntYellow,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = LoteriasCaixaDSFontFamily
            )
            Text(
                uiStrings.getString(DeliveryR.string.ui_selecione_os_limites_da_sua_compra_e_acc7f2),
                (Modifier.padding(top = 15.dp, start = 30.dp, end = 30.dp)).uiId("bet.0149"),
                color = LoteriasCaixaDS.colors.text,
                fontSize = 15.sp,
                fontFamily = LoteriasCaixaDSFontFamily,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RapidPrizePage(value: BigDecimal, onSelect: (BigDecimal) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().uiId("bet.0150").verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_premio_minimo_4a3c9c),
            (Modifier.padding(top = 20.dp)).uiId("bet.0151"),
            color = LoteriasCaixaDSColorTokens.FavoriteCartCard,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LoteriasCaixaDSFontFamily
        )
        val description = buildAnnotatedString {
            append("O ")
            withStyle(
                SpanStyle(
                    color = LoteriasCaixaDSColorTokens.BurntYellow,
                    fontWeight = FontWeight.Bold
                )
            ) { append(uiStrings.getString(LocalizedR.string.ui_rapidao_658c41)) }
            append(uiStrings.getString(DeliveryR.string.ui_ira_escolher_apenas_concursos_com_pr_4b4fff))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("maior") }
            append(" ou ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("igual") }
            append(uiStrings.getString(DeliveryR.string.ui_ao_valor_selecionado_006200))
        }
        Text(
            description,
            (Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)).uiId("bet.0152"),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            fontFamily = LoteriasCaixaDSFontFamily,
            color = LoteriasCaixaDS.colors.graphite
        )
        Box(Modifier.padding(top = 16.dp).width(150.dp).height(1.dp).background(LoteriasCaixaDSColorTokens.RegistrationSegment))
        RapidPrizeWheel(value, onSelect)
        Box(Modifier.padding(top = 10.dp).width(150.dp).height(1.dp).background(LoteriasCaixaDSColorTokens.RegistrationSegment))
        Text(
            uiStrings.getString(DeliveryR.string.ui_nossas_sugestoes_e9fc54),
            (Modifier.padding(top = 20.dp)).uiId("bet.0153"),
            color = LoteriasCaixaDS.colors.graphite,
            fontSize = 15.sp,
            fontFamily = LoteriasCaixaDSFontFamily
        )
        FlowRow(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Center) {
            listOf(0, 20, 40, 60, 80, 100).forEach { millions ->
                val amount = BigDecimal.valueOf(millions * 1_000_000L)
                RapidPreset(
                    if (millions == 0) uiStrings.getString(DeliveryR.string.ui_qualquer_valor_c92127) else "$millions Mi",
                    value.compareTo(amount) == 0,
                    true,
                    false
                ) { onSelect(amount) }
            }
        }
    }
}

@Composable
private fun RapidPrizeWheel(value: BigDecimal, onSelect: (BigDecimal) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val selected = value.divideToIntegralValue(BigDecimal("1000000")).toInt().coerceIn(0, 100)
    LoteriasCaixaDSWheel(
        options = (0..100).map { if (it == 0) uiStrings.getString(DeliveryR.string.ui_qualquer_valor_c92127) else "R$ $it.000.000" },
        selectedIndex = selected,
        onSelectedIndex = { onSelect(BigDecimal.valueOf(it * 1_000_000L)) },
        modifier = (Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp).height(180.dp)).uiId("bet.0154"),
        itemHeight = 40.dp,
        fontSize = 18.sp,
        wrap = true,
        accessibilityLabel = uiStrings.getString(DeliveryR.string.ui_premio_minimo_4a3c9c)
    )
}
