package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.testTag as semanticTestTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** SuperSeteResultadoLayout: duas linhas centralizadas de sete células de 30×24dp, cada uma com espaçamento de 3dp. */
@Composable
internal fun DrawPublicSuperSevenResult(columns: List<List<Int>>, digitColor: Color) {
    Column(
        Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 56.dp)
            .uiId("results.0009").horizontalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            (1..7).forEach { column ->
                SuperSevenResultCell(
                    column.toString(),
                    "result_super_seven_header_$column",
                    CaixaDSColorTokens.SuperSeteContent,
                    CaixaDSColorTokens.SuperSeteLight,
                    decorative = true
                )
            }
        }
        Row {
            (1..7).forEach { column ->
                SuperSevenResultCell(
                    columns.getOrNull(column - 1)?.firstOrNull()?.toString().orEmpty(),
                    "result_super_seven_digit_$column",
                    digitColor,
                    description = "Coluna $column: ${columns.getOrNull(column - 1)?.firstOrNull()?.toString() ?: "não informado"}"
                )
            }
        }
    }
}

@Composable
private fun SuperSevenResultCell(
    text: String,
    tag: String,
    color: Color,
    background: Color = Color.Transparent,
    description: String? = null,
    decorative: Boolean = false,
) {
    val density = LocalDensity.current
    val viewportWidth = LocalWindowInfo.current.containerSize.width.coerceAtLeast(1)
    val textStyle = TextStyle(
        fontFamily = CaixaDSFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
    // O contêiner de rolagem mede horizontalmente sem limite máximo. Limita o parágrafo
    // antes de consultar a largura natural dos dígitos, incluindo a escala de fonte do sistema.
    val measured = rememberTextMeasurer().measure(
        text = text,
        style = textStyle,
        softWrap = false,
        maxLines = 1,
        constraints = Constraints(maxWidth = viewportWidth)
    )
    val cellWidth = maxOf(30.dp, with(density) { measured.size.width.toDp() })
    Box(Modifier.padding(3.dp)) {
        Box(
            Modifier.width(cellWidth).heightIn(min = 24.dp).then(
                if (decorative) {
                    Modifier.clearAndSetSemantics { semanticTestTag = tag }
                } else {
                    Modifier.clearAndSetSemantics {
                        semanticTestTag = tag
                        contentDescription =
                            description.orEmpty()
                    }
                }
            ).background(background, RoundedCornerShape(4.dp)).legacyAutomationId(tag),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                modifier = (Modifier.fillMaxWidth()).uiId("results.0010"),
                textAlign = TextAlign.Center,
                color = color,
                style = textStyle,
                softWrap = false,
                maxLines = 1
            )
        }
    }
}
