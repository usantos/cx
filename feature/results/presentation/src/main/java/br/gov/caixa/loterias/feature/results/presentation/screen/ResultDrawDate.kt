package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultDateSpokenDescription

@Composable
internal fun ResultDrawDate(result: DrawResult, modality: String, color: Color, bodyStyle: TextStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.clearAndSetSemantics { contentDescription = resultDateSpokenDescription(result.drawDate, modality) }) {
        Text(
            buildAnnotatedString {
                append(
                    if (modality == "loteca") {
                        uiStrings.getString(LocalizedR.string.ui_data_do_resultado_1ae295)
                    } else {
                        uiStrings.getString(LocalizedR.string.ui_data_do_sorteio_088973)
                    }
                )
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(result.drawDate) }
            },
            (Modifier.padding(start = 30.dp, top = 10.dp)).uiId("results.0080"),
            color = color,
            style = bodyStyle
        )
    }
}
