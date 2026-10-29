package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenu as DropdownMenu
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSDropdownMenuItem as DropdownMenuItem
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.mapper.formatCardNumber
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState

@Composable
internal fun CreditCardFront(state: PaymentCardUiState) = CreditCardFront(
    brand = state.brand,
    number = state.savedCard?.let { "•••• •••• •••• ${state.lastDigits}" }
        ?: formatCardNumber(state.number),
    holderName = state.savedCard?.let { "-" } ?: state.holderName,
    expiration = state.savedCard?.let { "${it.expirationMonth.toString().padStart(2, '0')}/${it.expirationYear}" }
        ?: listOf(state.month, state.year).takeIf { it.all(String::isNotBlank) }?.joinToString("/").orEmpty()
)

@Composable
internal fun LegacyPaymentPicker(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var expanded by remember { mutableStateOf(false) }
    Box(modifier.padding(top = 14.dp)) {
        Row(
            Modifier.fillMaxWidth().height(41.dp).uiId("pay.0020").clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                value.ifBlank { label },
                (Modifier.weight(1f)).uiId("pay.0021"),
                color = if (value.isBlank()) CaixaDS.colors.primaryButton else CaixaDS.colors.text,
                fontSize = 16.sp
            )
            Icon(
                CaixaDSIcons.ChevronDown,
                contentDescription = uiStrings.getString(LocalizedR.string.ui_selecionar_1_s_f97e7d, label),
                tint = CaixaDS.colors.brand,
                modifier = Modifier.uiId("pay.0022")
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).align(Alignment.BottomCenter).background(CaixaDS.colors.line))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.uiId("pay.0023")) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, modifier = Modifier.uiId("pay.0024")) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.uiId("pay.0025")
                )
            }
        }
    }
}

@Composable
internal fun CenteredPaymentProgress(modifier: Modifier, message: String = stringResource(LocalizedR.string.format_payment_loading)) {
    Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = CaixaDS.colors.brand)
        Text(message, (Modifier.padding(top = 16.dp)).uiId("pay.0026"), color = CaixaDS.colors.graphite)
    }
}

internal val legacyCardTextStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

/** Geometria literal de button_rounded_verdeazul usada nas três etapas do cartão. */
@Composable
internal fun LegacyCardActionButton(label: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = (modifier.height(50.dp)).uiId("pay.0027"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.TealAction, contentColor = CaixaDSColorTokens.White),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
    ) {
        Text(label, fontSize = 17.sp, fontWeight = FontWeight.Bold, style = legacyCardTextStyle, modifier = Modifier.uiId("pay.0028"))
    }
}
