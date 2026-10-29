package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR

@Composable
internal fun PrizePixOtherKeyField(value: String, onValueChange: (String) -> Unit, error: Boolean) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val fieldLabel = "Chave Pix"
    val errorMessage = uiStrings.getString(LocalizedR.string.ui_chave_invalida_22ed25)
    val lineColor = if (error) CaixaDSColorTokens.PrizeUrgent else CaixaDS.colors.line
    Column(Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = (
                Modifier.fillMaxWidth().height(
                    maxOf(
                        54.dp,
                        with(LocalDensity.current) { pixKeyFieldLineHeight.toDp() }
                    )
                ).semantics {
                    contentDescription = fieldLabel
                    if (error) error(errorMessage)
                }.drawBehind {
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height - 1.dp.toPx()),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                ).uiId("retail.0017"),
            textStyle = CaixaDS.typography.bodyLarge.copy(color = CaixaDS.colors.text, fontSize = 16.sp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_celular_e_mail_chave_aleatoria_4f5955),
                            color = CaixaDS.colors.mutedText,
                            fontSize = 16.sp,
                            modifier = Modifier.uiId("retail.0018")
                        )
                    }
                    innerTextField()
                }
            }
        )
        if (error) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_chave_invalida_22ed25),
                modifier = (Modifier.padding(top = 4.dp).semantics { liveRegion = LiveRegionMode.Polite }).uiId("retail.0019"),
                color = CaixaDSColorTokens.PrizeUrgent,
                fontSize = 12.sp
            )
        }
    }
}

private val pixKeyFieldLineHeight = 24.sp
