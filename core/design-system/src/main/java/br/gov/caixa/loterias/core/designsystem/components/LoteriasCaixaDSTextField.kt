package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

/** Variantes visuais compartilham a responsabilidade pelo estado e pelas funções de retorno; a validação de negócio fica com o chamador. */
enum class LoteriasCaixaDSTextFieldVariant { OUTLINED, LABELLED, LEGACY }

@Composable
fun LoteriasCaixaDSTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    variant: LoteriasCaixaDSTextFieldVariant = LoteriasCaixaDSTextFieldVariant.OUTLINED,
    placeholder: String = "",
    showValidation: Boolean = false,
    currencyInput: Boolean = false,
) {
    when (variant) {
        LoteriasCaixaDSTextFieldVariant.OUTLINED -> OutlinedTextField(
            value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = (modifier).uiId("ds.0054"),
            enabled = enabled, singleLine = singleLine, isError = isError,
            supportingText = supportingText?.let { message -> { Text(message) } },
            visualTransformation = visualTransformation, keyboardOptions = keyboardOptions,
            placeholder = placeholder.takeIf { it.isNotEmpty() }?.let { hint -> { Text(hint) } }
        )

        LoteriasCaixaDSTextFieldVariant.LABELLED -> LabelledTextField(
            value, onValueChange, label, (modifier).uiId("ds.0055"), enabled, placeholder,
            visualTransformation, keyboardOptions, singleLine, isError, supportingText
        )

        LoteriasCaixaDSTextFieldVariant.LEGACY -> LoteriasCaixaDSExternalTextField(
            value,
            onValueChange,
            modifier,
            ExternalTextFieldOptions(
                label, enabled, isError, placeholder, showValidation, currencyInput,
                singleLine, keyboardOptions, visualTransformation, supportingText
            )
        )
    }
}

@Composable
private fun LabelledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    enabled: Boolean,
    placeholder: String,
    visualTransformation: VisualTransformation,
    keyboardOptions: KeyboardOptions,
    singleLine: Boolean,
    isError: Boolean,
    supportingText: String?,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(LabelledFieldTokens.gap)) {
        Text(
            label,
            modifier = Modifier.padding(start = LabelledFieldTokens.labelPadding),
            color = LoteriasCaixaDS.colors.mutedText,
            style = LoteriasCaixaDS.typography.titleMedium
        )
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            modifier = (
                Modifier.fillMaxWidth().then(
                    if (LocalDensity.current.fontScale <= 1f) {
                        Modifier.height(LoteriasCaixaDS.sizes.field)
                    } else {
                        Modifier.heightIn(min = LoteriasCaixaDS.sizes.field)
                    }
                ).semantics {
                    contentDescription = label
                    if (isError && !supportingText.isNullOrBlank()) error(supportingText)
                }
                ).uiId("ds.0056"),
            enabled = enabled, singleLine = singleLine, isError = isError,
            placeholder = { Text(placeholder, color = LoteriasCaixaDS.colors.mutedText) },
            visualTransformation = visualTransformation, keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(LabelledFieldTokens.radius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LoteriasCaixaDS.colors.brand,
                unfocusedBorderColor = LoteriasCaixaDS.colors.line,
                disabledBorderColor = LoteriasCaixaDS.colors.line,
                disabledContainerColor = LoteriasCaixaDS.colors.field,
                focusedContainerColor = LoteriasCaixaDS.colors.surface,
                unfocusedContainerColor = LoteriasCaixaDS.colors.surface
            )
        )
        supportingText?.let {
            Text(
                it,
                modifier = Modifier.semantics { if (isError) liveRegion = LiveRegionMode.Polite },
                color = if (isError) LoteriasCaixaDS.colors.error else LoteriasCaixaDS.colors.mutedText,
                style = LoteriasCaixaDS.typography.bodySmall
            )
        }
    }
}

private object LabelledFieldTokens {
    val gap = 4.dp
    val labelPadding = 16.dp
    val radius = 4.dp
}
