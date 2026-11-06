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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

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
internal object ExternalFieldTokens {
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
internal fun LoteriasCaixaDSExternalTextField(
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
                color = if (options.error) LoteriasCaixaDSColorTokens.AccountError else LoteriasCaixaDSColorTokens.BetAction,
                fontSize = ExternalFieldTokens.fontSize,
                fontWeight = FontWeight.Bold,
                style = LoteriasCaixaDSAccountTextStyle
            )
        }
        Row(
            Modifier.padding(top = if (options.label.isNotEmpty()) ExternalFieldTokens.labelGap else 0.dp)
                .fillMaxWidth().heightIn(min = ExternalFieldTokens.height)
                .background(
                    if (options.enabled) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDSColorTokens.AccountDisabledField,
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
                    tint = LoteriasCaixaDSColorTokens.AccountOutline
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
                LoteriasCaixaDSBasicTextField(
                    value, onValueChange, (inputModifier).uiId("ds.0011"), enabled = options.enabled,
                    singleLine = options.singleLine, interactionSource = interactions,
                    textStyle = LoteriasCaixaDSAccountTextStyle.copy(
                        color = LoteriasCaixaDSColorTokens.BetAction,
                        fontSize = ExternalFieldTokens.fontSize
                    ),
                    keyboardOptions = options.keyboardOptions, cursorBrush = SolidColor(LoteriasCaixaDSColorTokens.ComboDark),
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
                style = LoteriasCaixaDSAccountTextStyle,
                color = if (options.error) LoteriasCaixaDSColorTokens.AccountError else LoteriasCaixaDSColorTokens.TicketChoiceMuted
            )
        }
    }
}
