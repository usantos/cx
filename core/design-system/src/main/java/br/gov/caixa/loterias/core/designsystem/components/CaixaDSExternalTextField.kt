package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import java.text.NumberFormat
import java.util.Locale

internal data class ExternalTextFieldOptions(
    val label: String,
    val enabled: Boolean,
    val error: Boolean,
    val placeholder: String,
    val showValidation: Boolean,
    val currencyInput: Boolean,
    val singleLine: Boolean,
    val keyboardOptions: KeyboardOptions,
    val visualTransformation: VisualTransformation,
    val supportingText: String?,
)

/** Dimensões e métricas de texto compartilhadas com o XML de conta instalado. */
private object ExternalFieldTokens {
    val height = 52.dp
    val labelGap = 4.dp
    val horizontalPadding = 10.dp
    val validationIcon = 24.dp
    val validationGap = 12.dp
    val radius = 4.dp
    val border = 1.dp
    val fontSize = 16.sp
    val helpSize = 14.sp
    const val TEXT_ALPHA = .8f
    const val DECIMAL_PLACES = 2
}

@Composable
internal fun CaixaDSExternalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    options: ExternalTextFieldOptions,
) {
    val interactions = remember { MutableInteractionSource() }
    val focused by interactions.collectIsFocusedAsState()
    Column(modifier.fillMaxWidth()) {
        if (options.label.isNotEmpty()) {
            Text(
                options.label,
                color = if (options.error) CaixaDSColorTokens.AccountError else CaixaDSColorTokens.BetAction,
                fontSize = ExternalFieldTokens.fontSize,
                fontWeight = FontWeight.Bold,
                style = CaixaDSAccountTextStyle
            )
        }
        Row(
            Modifier.padding(top = if (options.label.isNotEmpty()) ExternalFieldTokens.labelGap else 0.dp)
                .fillMaxWidth().heightIn(min = ExternalFieldTokens.height)
                .background(
                    if (options.enabled) CaixaDSColorTokens.White else CaixaDSColorTokens.AccountDisabledField,
                    RoundedCornerShape(ExternalFieldTokens.radius)
                )
                .border(
                    ExternalFieldTokens.border,
                    externalFieldBorderColor(focused, options.error),
                    RoundedCornerShape(ExternalFieldTokens.radius)
                )
                .padding(horizontal = ExternalFieldTokens.horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (options.showValidation && !options.enabled) {
                Icon(
                    painterResource(R.drawable.legacy_account_ic_check),
                    null,
                    Modifier.size(ExternalFieldTokens.validationIcon).alpha(ExternalFieldTokens.TEXT_ALPHA),
                    tint = CaixaDSColorTokens.AccountOutline
                )
                Spacer(Modifier.size(ExternalFieldTokens.validationGap))
            }
            val inputModifier = Modifier.weight(1f).alpha(ExternalFieldTokens.TEXT_ALPHA).semantics {
                contentDescription = options.label
                if (options.error && !options.supportingText.isNullOrBlank()) error(options.supportingText)
            }
            if (options.currencyInput) {
                ExternalCurrencyInput(value, onValueChange, (inputModifier).uiId("ds.0010"), options, interactions)
            } else {
                CaixaDSBasicTextField(
                    value, onValueChange, (inputModifier).uiId("ds.0011"), enabled = options.enabled,
                    singleLine = options.singleLine, interactionSource = interactions,
                    textStyle = CaixaDSAccountTextStyle.copy(color = CaixaDSColorTokens.BetAction, fontSize = ExternalFieldTokens.fontSize),
                    keyboardOptions = options.keyboardOptions, cursorBrush = SolidColor(CaixaDSColorTokens.ComboDark),
                    visualTransformation = options.visualTransformation,
                    decorationBox = { inner -> ExternalFieldDecoration(value, options.placeholder, inner) }
                )
            }
        }
        options.supportingText?.let {
            Text(
                it,
                modifier = Modifier.semantics { if (options.error) liveRegion = LiveRegionMode.Polite },
                fontSize = ExternalFieldTokens.helpSize,
                style = CaixaDSAccountTextStyle,
                color = if (options.error) CaixaDSColorTokens.AccountError else CaixaDSColorTokens.TicketChoiceMuted
            )
        }
    }
}

private fun externalFieldBorderColor(focused: Boolean, error: Boolean) = when {
    focused && error -> CaixaDSColorTokens.AccountError
    focused -> CaixaDSColorTokens.AccountOutline
    else -> CaixaDSColorTokens.AccountUnfocusedOutline
}

@Composable
private fun ExternalFieldDecoration(value: String, placeholder: String, inner: @Composable () -> Unit) {
    if (value.isBlank()) {
        Text(
            placeholder,
            color = CaixaDSColorTokens.AccountOutline,
            fontSize = ExternalFieldTokens.fontSize,
            style = CaixaDSAccountTextStyle
        )
    }
    inner()
}

/** Edições apenas de seleção não notificam o chamador; edições de texto usam centavos e reposicionam o cursor no final. */
@Composable
private fun ExternalCurrencyInput(
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
    CaixaDSBasicTextField(
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
        textStyle = CaixaDSAccountTextStyle.copy(color = CaixaDSColorTokens.BetAction, fontSize = ExternalFieldTokens.fontSize),
        keyboardOptions = options.keyboardOptions.copy(keyboardType = KeyboardType.Decimal),
        cursorBrush = SolidColor(CaixaDSColorTokens.ComboDark), visualTransformation = options.visualTransformation,
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
