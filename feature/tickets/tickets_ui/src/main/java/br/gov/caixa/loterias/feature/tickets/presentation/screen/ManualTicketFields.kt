package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
private fun LegacyTicketUnderlineField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    valid: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().heightIn(min = 52.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = (Modifier.fillMaxWidth().heightIn(min = 51.dp).semantics { contentDescription = hint }).uiId("tickets.0180"),
            singleLine = true,
            textStyle = LoteriasCaixaDS.typography.bodyLarge.copy(color = LoteriasCaixaDS.colors.text, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            decorationBox = { field ->
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        if (value.isBlank()) {
                            Text(
                                hint,
                                color = LoteriasCaixaDSColorTokens.TealAction,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .uiId("tickets.0181")
                            )
                        }
                        field()
                    }
                    if (valid) {
                        Icon(
                            LoteriasCaixaDSIcons.Confirm,
                            null,
                            tint = LoteriasCaixaDS.colors.teal,
                            modifier = (Modifier.size(24.dp))
                                .uiId("tickets.0182")
                        )
                    }
                }
            }
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDS.colors.line))
    }
}

fun String.legacyNsbMask(): String = filter(Char::isLetterOrDigit).uppercase().take(NSB_MAXIMUM_LENGTH).let { raw ->
    buildString {
        append(raw.take(NSB_PREFIX_LENGTH))
        if (raw.length > NSB_PREFIX_LENGTH) append('-').append(raw.substring(NSB_PREFIX_LENGTH, minOf(raw.length, NSB_BODY_END)))
        if (raw.length > NSB_BODY_END) append('-').append(raw.substring(NSB_BODY_END))
    }
}

fun String.legacyRetailerMask(): String = filter(Char::isLetterOrDigit).uppercase().take(RETAILER_MAXIMUM_LENGTH).let { raw ->
    buildString {
        append(raw.take(2))
        if (raw.length > 2) append('.').append(raw.substring(2, minOf(raw.length, RETAILER_BODY_END)))
        if (raw.length > RETAILER_BODY_END) append('-').append(raw.substring(RETAILER_BODY_END))
    }
}

@Composable
fun ManualTicketFieldsDelivered(state: TicketsUiState, manualCodeValid: Boolean, actions: ManualTicketActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(DeliveryR.string.ui_por_favor_informe_o_numero_do_seu_bi_f18e7e),
        modifier = (Modifier.padding(top = 40.dp)).uiId("tickets.0173"),
        color = LoteriasCaixaDS.colors.brand,
        fontSize = 14.2.sp,
        fontWeight = FontWeight.Bold
    )
    LegacyTicketUnderlineField(
        value = state.ticketCode.legacyNsbMask(),
        onValueChange = actions.changeTicketCode,
        hint = uiStrings.getString(DeliveryR.string.ui_nsb_77c304),
        valid = manualCodeValid,
        modifier = (Modifier.padding(top = 40.dp)).uiId("tickets.0174")
    )
    LegacyTicketUnderlineField(
        value = state.ticketRetailerCode.legacyRetailerMask(),
        onValueChange = actions.changeTicketRetailerCode,
        hint = uiStrings.getString(DeliveryR.string.ui_loterico_c1ae0d),
        valid = state.ticketRetailerCode.length == RETAILER_MAXIMUM_LENGTH,
        modifier = (Modifier.padding(top = 20.dp)).uiId("tickets.0175")
    )
}

private const val NSB_MAXIMUM_LENGTH = 23

private const val NSB_PREFIX_LENGTH = 4

private const val NSB_BODY_END = 21

private const val RETAILER_MAXIMUM_LENGTH = 9

private const val RETAILER_BODY_END = 8
