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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDataCard
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun ManualTicketContent(
    state: TicketsUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    manualCodeValid: Boolean,
    actions: ManualTicketActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_conferir_bilhetes_1465ae),
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("tickets.0169")) {
                Icon(
                    CaixaDSIcons.Help,
                    contentDescription = uiStrings.getString(LocalizedR.string.ui_termos_de_uso_a6f04f),
                    tint = CaixaDSColorTokens.White,
                    modifier = Modifier.uiId("tickets.0170")
                )
            }
        },
        modifier = Modifier.uiId("tickets.0171")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
                .uiId("tickets.0172").verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            ManualTicketFields(state, manualCodeValid, actions)
            Button(
                onClick = { actions.checkTicket() },
                enabled = manualCodeValid,
                modifier = (Modifier.fillMaxWidth().padding(top = 49.dp).height(55.dp)).uiId("tickets.0176"),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CaixaDS.colors.teal,
                    contentColor = CaixaDSColorTokens.White,
                    disabledContainerColor = CaixaDSColorTokens.TicketDisabledBackground,
                    disabledContentColor = CaixaDSColorTokens.TicketDisabledContent
                )
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_continuar_63e2bb),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("tickets.0177")
                )
            }
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))

                state.errorMessage != null -> Text(
                    state.errorMessage.orEmpty(),
                    color = CaixaDS.colors.error,
                    modifier = Modifier
                        .uiId("tickets.0178")
                )

                state.loaded && state.bets.isEmpty() -> Text(
                    uiStrings.getString(
                        LocalizedR.string.ui_nenhuma_aposta_encontrada_neste_bilh_97edde
                    ),
                    color = CaixaDS.colors.mutedText,
                    modifier = Modifier.uiId("tickets.0179")
                )

                else -> state.bets.forEach { BetDataCard(it) {} }
            }
        }
    }
}

internal data class ManualTicketActions(
    val changeTicketCode: (String) -> Unit,
    val changeTicketRetailerCode: (String) -> Unit,
    val checkTicket: () -> Unit,
)

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
            textStyle = CaixaDS.typography.bodyLarge.copy(color = CaixaDS.colors.text, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            decorationBox = { field ->
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        if (value.isBlank()) {
                            Text(
                                hint,
                                color = CaixaDSColorTokens.TealAction,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .uiId("tickets.0181")
                            )
                        }
                        field()
                    }
                    if (valid) {
                        Icon(
                            CaixaDSIcons.Confirm,
                            null,
                            tint = CaixaDS.colors.teal,
                            modifier = (Modifier.size(24.dp))
                                .uiId("tickets.0182")
                        )
                    }
                }
            }
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDS.colors.line))
    }
}

internal fun String.legacyNsbMask(): String = filter(Char::isLetterOrDigit).uppercase().take(23).let { raw ->
    buildString {
        append(raw.take(4))
        if (raw.length > 4) append('-').append(raw.substring(4, minOf(raw.length, 21)))
        if (raw.length > 21) append('-').append(raw.substring(21))
    }
}

internal fun String.legacyRetailerMask(): String = filter(Char::isLetterOrDigit).uppercase().take(9).let { raw ->
    buildString {
        append(raw.take(2))
        if (raw.length > 2) append('.').append(raw.substring(2, minOf(raw.length, 8)))
        if (raw.length > 8) append('-').append(raw.substring(8))
    }
}

@Composable
private fun ManualTicketFields(state: TicketsUiState, manualCodeValid: Boolean, actions: ManualTicketActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_por_favor_informe_o_numero_do_seu_bi_f18e7e),
        modifier = (Modifier.padding(top = 40.dp)).uiId("tickets.0173"),
        color = CaixaDS.colors.brand,
        fontSize = 14.2.sp,
        fontWeight = FontWeight.Bold
    )
    LegacyTicketUnderlineField(
        value = state.ticketCode.legacyNsbMask(),
        onValueChange = actions.changeTicketCode,
        hint = uiStrings.getString(LocalizedR.string.ui_nsb_77c304),
        valid = manualCodeValid,
        modifier = (Modifier.padding(top = 40.dp)).uiId("tickets.0174")
    )
    LegacyTicketUnderlineField(
        value = state.ticketRetailerCode.legacyRetailerMask(),
        onValueChange = actions.changeTicketRetailerCode,
        hint = uiStrings.getString(LocalizedR.string.ui_loterico_c1ae0d),
        valid = state.ticketRetailerCode.length == 9,
        modifier = (Modifier.padding(top = 20.dp)).uiId("tickets.0175")
    )
}
