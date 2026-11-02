package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountTextStyle
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextFieldVariant
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSOutlinedButton as OutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.LoteriasCaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.formatAccountCurrency

@Composable
internal fun PersonalLimitControl(state: PersonalDataUiState, actions: PersonalDataActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LegacyPersonalDailyLimit(state, actions.changeDailyLimit)
    OutlinedButton(
        onClick = actions.recalculateLimit,
        enabled = !state.loading,
        modifier = (Modifier.fillMaxWidth().padding(top = 8.dp).height(64.dp)).uiId("account.0062"),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        border = BorderStroke(1.dp, LoteriasCaixaDSColorTokens.AccountOutline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = LoteriasCaixaDSColorTokens.AccountLimitButton,
            contentColor = LoteriasCaixaDSColorTokens.AccountOutline
        )
    ) {
        Text(
            if (state.awaitingInitialProfile) {
                uiStrings.getString(LocalizedR.string.ui_solicitar_limite_acima_de_1_s_19ffe1)
            } else {
                uiStrings.getString(
                    LocalizedR.string.ui_solicitar_limite_acima_de_1_s_19ffe1,
                    formatAccountCurrency(state.authorizedDailyLimit)
                )
            },
            fontSize = 15.sp,
            style = LoteriasCaixaDSAccountTextStyle,
            modifier = Modifier.uiId("account.0063")
        )
    }
}

@Composable
internal fun LegacyNotificationRow(checked: Boolean, onToggle: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().height(48.dp)
            .uiId("account.0065").toggleable(checked, role = Role.Checkbox, onValueChange = { onToggle() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoteriasCaixaDSAccountCheckbox(checked, onToggle, 24.dp)
        Text(
            uiStrings.getString(LocalizedR.string.ui_aceito_receber_notificacoes_de_loter_474748),
            (Modifier.padding(start = 8.dp).weight(1f)).uiId("account.0066"),
            color = LoteriasCaixaDSColorTokens.AccountOutline,
            fontSize = 16.sp,
            style = LoteriasCaixaDSAccountTextStyle
        )
    }
}

/** APK content_dados_pessoais: orientação de 16sp; aviso separado de 20dp + texto de 14sp quando fora dos limites. */
@Composable
internal fun LegacyPersonalDailyLimit(state: PersonalDataUiState, onChange: (String) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val message = dailyLimitValidationMessage(state)
    LoteriasCaixaDSTextField(
        variant = LoteriasCaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_limite_diario_de_apostas_5e8560),
        value = state.dailyLimit,
        onValueChange = onChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = message != null,
        currencyInput = true,
        modifier = Modifier.uiId("account.0067")
    )
    if (message == null) {
        Text(
            if (state.awaitingInitialProfile) {
                uiStrings.getString(LocalizedR.string.ui_r_1_s_ate_2_s_813a89)
            } else {
                uiStrings.getString(
                    LocalizedR.string.ui_1_s_ate_2_s_5e0e98,
                    formatAccountCurrency(state.minimumDailyLimit),
                    formatAccountCurrency(state.authorizedDailyLimit)
                )
            },
            (Modifier.padding(start = 10.dp, top = 4.dp, end = 10.dp)).uiId("account.0068"),
            fontSize = 16.sp,
            color = LoteriasCaixaDSColorTokens.AccountHelper,
            style = LoteriasCaixaDSAccountTextStyle
        )
    } else {
        LoteriasCaixaDSAccountInlineWarning(message, (Modifier.padding(top = 4.dp)).uiId("account.0069"))
    }
}
