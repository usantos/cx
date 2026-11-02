package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import java.text.NumberFormat
import java.util.Locale

/** Edições apenas de seleção não notificam o chamador; edições de texto usam centavos e reposicionam o cursor no final. */
@Composable
internal fun ExternalCurrencyInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    options: ExternalTextFieldOptions,
    interactions: MutableInteractionSource,
) {
    var editing by remember(value) { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    val presented = if (editing.text == value) editing else TextFieldValue(value, TextRange(value.length))
    // Concilia uma substituição pelo pai ou edição rejeitada com o valor efetivamente apresentado.
    SideEffect { editing = presented }
    LoteriasCaixaDSBasicTextField(
        presented, { next ->
            // Lê a edição mais recente, incluindo eventos entregues antes da próxima composição.
            if (next.text == editing.text) {
                editing = next
            } else {
                val formatted = formatExternalFieldCurrency(next.text)
                val changed = formatted != editing.text
                editing = TextFieldValue(formatted, TextRange(formatted.length))
                if (changed) onValueChange(formatted)
            }
        }, (modifier).uiId("ds.0012"), enabled = options.enabled, singleLine = options.singleLine, interactionSource = interactions,
        textStyle = LoteriasCaixaDSAccountTextStyle.copy(
            color = LoteriasCaixaDSColorTokens.BetAction,
            fontSize = ExternalFieldTokens.fontSize
        ),
        keyboardOptions = options.keyboardOptions.copy(keyboardType = KeyboardType.Decimal),
        cursorBrush = SolidColor(LoteriasCaixaDSColorTokens.ComboDark), visualTransformation = options.visualTransformation,
        decorationBox = { inner -> ExternalFieldDecoration(value, options.placeholder, inner) }
    )
}

private fun formatExternalFieldCurrency(input: String): String {
    val digits = input.filter(Char::isDigit)
    return if (digits.isEmpty()) {
        ""
    } else {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
            .format(digits.toBigDecimal().movePointLeft(ExternalFieldTokens.DECIMAL_PLACES))
            .replace('\u00A0', ' ').replace('\u202F', ' ')
    }
}
