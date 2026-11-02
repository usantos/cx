package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import kotlin.math.roundToInt

/** ConstraintLayout compacto do MP: cabeçalho medido, margens de 20dp e viés vertical de 0.1. */
@Composable
internal fun PrizeMercadoPagoBiasedBody(modifier: Modifier, content: @Composable () -> Unit) {
    BoxWithConstraints(modifier) {
        val viewportHeight = with(LocalDensity.current) { maxHeight.roundToPx() }
        Layout(
            content = { Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { content() } },
            modifier = Modifier.fillMaxWidth().uiId("retail.0091").verticalScroll(rememberScrollState())
        ) { measurables, constraints ->
            val child = measurables.single().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
            val margin = 20.dp.roundToPx()
            val height = maxOf(viewportHeight, child.height + margin * 2)
            val top = margin + ((height - child.height - margin * 2) * BODY_VERTICAL_BIAS).roundToInt()
            layout(constraints.maxWidth, height) { child.placeRelative(0, top) }
        }
    }
}

@Composable
internal fun PrizeMercadoPagoEmailField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val lineColor = LoteriasCaixaDS.colors.line
    val label = androidx.compose.ui.res.stringResource(LocalizedR.string.ui_e_mail_2550d9)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = (
            modifier
                .semantics { contentDescription = label }
                .height(54.dp)
                .drawBehind {
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height - 1.dp.toPx()),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            ).uiId("retail.0092"),
        textStyle = LoteriasCaixaDS.typography.bodyLarge.copy(
            color = LoteriasCaixaDSColorTokens.BetAction,
            fontSize = 20.sp,
            lineHeight = 24.sp
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                innerTextField()
            }
        }
    )
}

private const val BODY_VERTICAL_BIAS = .1f
