package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.automationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.presentation.formatting.localizedMessage as messageText

data class ResultModality(val code: String, val name: String)

val officialModalities = listOf(
    ResultModality("megasena", "Mega-Sena"),
    ResultModality("lotofacil", messageText("lotofacil_d3ed7c")),
    ResultModality("quina", "Quina"),
    ResultModality("lotomania", "Lotomania"),
    ResultModality("timemania", "Timemania"),
    ResultModality("duplasena", "Dupla Sena"),
    ResultModality("diadesorte", messageText("dia_de_sorte_b5c7a5")),
    ResultModality("supersete", "Super Sete"),
    ResultModality("maismilionaria", messageText("milionaria_11be79")),
    ResultModality("loteca", "Loteca"),
    ResultModality("federal", "Federal")
)

private val resultModalityCodes = listOf(
    "diadesorte", "duplasena", "loteca", "lotofacil", "lotomania",
    "maismilionaria", "megasena", "quina", "supersete", "timemania"
)

@Composable
internal fun ResultModalityScreen(onBack: () -> Unit, onSelectModality: (String) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = LocalDensity.current.fontScale > 1f
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resultados_310561),
        onBack = onBack,
        gradientTopBar = false,
        containerColor = CaixaDSColorTokens.White,
        modifier = Modifier.uiId("results.0038")
    ) { padding ->
        LazyColumn(
            (Modifier.fillMaxSize().padding(padding)).automationId("simlo.results.modalidades"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 10.dp,
                top = 40.dp,
                end = 10.dp,
                bottom = 50.dp
            )
        ) {
            item {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_modalidades_6b4baa),
                    (Modifier.fillMaxWidth()).uiId("results.0040"),
                    color = CaixaDS.colors.primaryButton,
                    style = TextStyle(
                        fontFamily = CaixaDSFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    )
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_selecione_a_modalidade_para_ver_os_s_c1f884),
                    (Modifier.fillMaxWidth().padding(top = 20.dp)).uiId("results.0041"),
                    color = CaixaDS.colors.graphite,
                    style = TextStyle(
                        fontFamily = CaixaDSFontFamily,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    )
                )
                Spacer(Modifier.height(19.dp))
            }
            resultModalityCodes.map { code -> officialModalities.first { it.code == code } }
                .chunked(if (largeFont) 1 else 2).forEach { row ->
                    item {
                        Row(Modifier.fillMaxWidth()) {
                            row.forEach { modality ->
                                ResultModalityCard(modality, (Modifier.weight(1f)).uiId("results.0042")) {
                                    onSelectModality(modality.code)
                                }
                            }
                        }
                    }
                }
        }
    }
}

@Composable
private fun ResultModalityCard(modality: ResultModality, modifier: Modifier, onClick: () -> Unit) {
    val style = caixaDSLotteryStyle(0, modality.name)
    val largeFont = LocalDensity.current.fontScale > 1f
    Row(
        modifier.padding(4.dp)
            .then(if (largeFont) Modifier.heightIn(min = 56.dp) else Modifier.height(56.dp))
            .background(style.light).uiId("results.0043").clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            caixaDSLotteryClover(modality.code),
            contentDescription = null,
            modifier = Modifier.padding(start = 12.dp).size(18.dp)
        )
        Text(
            modality.name.lowercase(),
            (
                Modifier.padding(
                    start = 8.dp
                ).then(if (largeFont) Modifier.weight(1f).padding(end = 8.dp, top = 8.dp, bottom = 8.dp) else Modifier)
                ).uiId("results.0044"),
            color = style.content,
            style = TextStyle(
                fontFamily = CaixaDSFuturaFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            ),
            maxLines = if (largeFont) Int.MAX_VALUE else 1
        )
    }
}
