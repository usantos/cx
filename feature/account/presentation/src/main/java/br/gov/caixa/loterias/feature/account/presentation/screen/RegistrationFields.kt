package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAccountTextStyle
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSTextFieldVariant
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.route.LegacyAccountGenderControl
import br.gov.caixa.loterias.feature.account.presentation.screen.formatCpf
import br.gov.caixa.loterias.feature.account.presentation.screen.formatZipCode
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState

@Composable
internal fun RegistrationIdentityFields(state: RegistrationUiState, actions: RegistrationActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Spacer(Modifier.height(28.dp))
    LoteriasCaixaDSTextField(
        variant = LoteriasCaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_cpf_30d436),
        value = formatCpf(
            state.cpf
        ),
        onValueChange = {
        },
        enabled = false,
        showValidation = true,
        placeholder = uiStrings.getString(LocalizedR.string.ui_012_345_678_98_7642fd),
        modifier = Modifier.uiId("account.0025")
    )
    Spacer(Modifier.height(12.dp))
    LoteriasCaixaDSTextField(
        variant = LoteriasCaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_nome_508690),
        value = state.name,
        onValueChange = {
        },
        enabled = false,
        showValidation = true,
        placeholder = uiStrings.getString(LocalizedR.string.ui_nome_meio_sobrenome_a0dea7),
        modifier = Modifier.uiId("account.0026")
    )
    Spacer(Modifier.height(6.dp))
    LegacyAccountGenderControl(state.gender, actions.changeGender)
    Spacer(Modifier.height(12.dp))
}

@Composable
internal fun RegistrationAddressFields(state: RegistrationUiState, actions: RegistrationActions, onZipCodeRedirect: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSTextField(
        variant = LoteriasCaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_cep_d68636),
        value = formatZipCode(state.zipCode),
        onValueChange = actions.changeZipCode,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        placeholder = uiStrings.getString(LocalizedR.string.ui_somente_numeros_a4bde0),
        isError =
            state.zipCodeErrorMessage != null,
        modifier = Modifier.uiId("account.0027")
    )
    state.zipCodeErrorMessage?.let { LoteriasCaixaDSAccountInlineWarning(it, modifier = Modifier.uiId("account.0028")) }
    Box(
        Modifier.padding(top = 2.dp).height(32.dp).uiId("account.0029").clickable {
            onZipCodeRedirect()
        },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_nao_sei_o_meu_cep_eb7e08),
            color = LoteriasCaixaDSColorTokens.BetAction,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            style = LoteriasCaixaDSAccountTextStyle,
            modifier = Modifier.uiId("account.0030")
        )
    }
    Spacer(Modifier.height(8.dp))
    LoteriasCaixaDSTextField(
        variant = LoteriasCaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_uf_55852e),
        value = state.stateCode,
        onValueChange = {
        },
        enabled = false,
        placeholder = uiStrings.getString(LocalizedR.string.ui_uf_55852e),
        modifier = Modifier.uiId("account.0031")
    )
    Spacer(Modifier.height(12.dp))
    LoteriasCaixaDSTextField(
        variant = LoteriasCaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_municipio_8e4309),
        value = state.city,
        onValueChange = {
        },
        enabled = false,
        placeholder = uiStrings.getString(LocalizedR.string.ui_municipio_8e4309),
        modifier = Modifier.uiId("account.0032")
    )
    Spacer(Modifier.height(12.dp))
    LoteriasCaixaDSTextField(
        variant = LoteriasCaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_e_mail_2550d9),
        value = state.email,
        onValueChange = {
        },
        enabled = false,
        showValidation = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        placeholder = uiStrings.getString(LocalizedR.string.ui_nome_sobrenome_email_com_70f17b), modifier = Modifier.uiId("account.0033")
    )
}
