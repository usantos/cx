package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField as BasicTextField

@Composable
fun LoteriasCaixaDSMarketplaceFilterLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LoteriasCaixaDSMarketplaceFilterStyle.label,
    fontSize: Int = 12,
) {
    Text(text, modifier, color = color, style = loteriasCaixaDSMarketplaceFilterText(fontSize, FontWeight.Bold))
}

@Composable
fun LoteriasCaixaDSMarketplaceFilterInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    currency: Boolean = false,
    fontSize: Int = 14,
    accessibilityLabel: String = placeholder,
    errorMessage: String? = null,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var editValue by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    val displayedValue = editValue.copy(
        text = value,
        selection = if (currency) TextRange(value.length) else editValue.selection
    )
    BasicTextField(
        value = displayedValue,
        onValueChange = { edited ->
            val input = edited.text
            val limited = if (keyboardType == KeyboardType.Decimal ||
                keyboardType == KeyboardType.Number
            ) {
                input.take(MAX_NUMERIC_INPUT_LENGTH)
            } else {
                input
            }
            val formatted = if (currency && limited.isNotEmpty()) {
                java.text.NumberFormat.getNumberInstance(java.util.Locale.forLanguageTag("pt-BR")).apply {
                    minimumFractionDigits = 2
                    maximumFractionDigits = 2
                }.format(br.gov.caixa.loterias.core.designsystem.components.input.loteriasCaixaDSMoneyInputAmount(limited))
            } else {
                limited
            }
            editValue = edited.copy(
                text = formatted,
                selection = if (currency) TextRange(formatted.length) else edited.selection
            )
            // Mudanças de seleção/composição do teclado não alteram o texto nem devem reiniciar a busca.
            if (formatted != value) onValueChange(formatted)
        },
        modifier = (
            modifier.fillMaxWidth().height(48.dp)
                .border(1.dp, LoteriasCaixaDSMarketplaceFilterStyle.label, RoundedCornerShape(5.dp))
                .semantics {
                    contentDescription = accessibilityLabel
                    if (!errorMessage.isNullOrBlank()) error(errorMessage)
                }
            ).uiId("ds.0029"),
        textStyle = loteriasCaixaDSMarketplaceFilterText(fontSize).copy(color = LoteriasCaixaDSMarketplaceFilterStyle.hint),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        decorationBox = { inner ->
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                if (currency) {
                    Icon(
                        LoteriasCaixaDSMarketplaceFilterCurrencyIcon,
                        uiStrings.getString(LocalizedR.string.ui_r_0d69df),
                        Modifier.width(44.dp).height(49.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(5.dp))
                }
                Box(Modifier.weight(1f).padding(start = if (currency) 0.dp else 8.dp, end = 8.dp)) {
                    if (value.isEmpty()) {
                        Text(
                            placeholder,
                            color = LoteriasCaixaDSMarketplaceFilterStyle.hint,
                            style = loteriasCaixaDSMarketplaceFilterText(fontSize)
                        )
                    }
                    inner()
                }
            }
        }
    )
}

private const val MAX_NUMERIC_INPUT_LENGTH = 14
