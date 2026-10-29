package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountBackground
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountTextStyle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextFieldVariant
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSOutlinedButton as OutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.R
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.presentation.route.LegacyAccountGenderControl
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.formatAccountCurrency

internal data class PersonalDataActions(
    val submit: () -> Unit,
    val changeGender: (Gender) -> Unit,
    val changeZipCode: (String) -> Unit,
    val changeDailyLimit: (String) -> Unit,
    val recalculateLimit: () -> Unit,
    val requestZipCodeRedirect: () -> Unit,
    val toggleNotifications: () -> Unit,
)

@Composable
internal fun PersonalDataScreen(state: PersonalDataUiState, imeVisible: Boolean, onBack: () -> Unit, actions: PersonalDataActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_dados_pessoais_f10a9c),
        modifier = (Modifier.imePadding()).uiId("account.0050"),
        onBack = onBack,
        gradientTopBar = false,

        containerColor = CaixaDSColorTokens.White,
        bottomBar = if (imeVisible) {
            null
        } else {
            (
                {
                    PersonalDataFooter(
                        enabled = canUpdatePersonalData(state),
                        onUpdate = actions.submit
                    )
                }
                )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)) {
            Column(
                Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                    .background(CaixaDSAccountBackground).uiId("account.0051").verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                LegacyPersonalDataIntro((Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("account.0052"))
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    PersonalIdentityFields(state, actions)
                    PersonalAddressFields(state, actions)
                    PersonalLimitControl(state, actions)
                }
                Spacer(Modifier.height(16.dp))
                LegacyNotificationRow(state.acceptsNotifications, actions.toggleNotifications)
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PersonalIdentityFields(state: PersonalDataUiState, actions: PersonalDataActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Spacer(Modifier.height(28.dp))
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_cpf_30d436),
        value = formatCpf(
            state.cpf
        ),
        onValueChange = {
        },
        enabled = false,
        showValidation = true,
        placeholder = uiStrings.getString(LocalizedR.string.ui_012_345_678_98_7642fd),
        modifier = Modifier.uiId("account.0053")
    )
    Spacer(Modifier.height(12.dp))
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_nome_508690),
        value = state.name,
        onValueChange = {
        },
        enabled = false,
        showValidation = true,
        placeholder = uiStrings.getString(LocalizedR.string.ui_nome_meio_sobrenome_a0dea7),
        modifier = Modifier.uiId("account.0054")
    )
    Spacer(Modifier.height(6.dp))
    LegacyAccountGenderControl(state.gender, actions.changeGender)
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun PersonalAddressFields(state: PersonalDataUiState, actions: PersonalDataActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_cep_d68636),
        value = formatZipCode(state.zipCode),
        onValueChange = actions.changeZipCode,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        placeholder = uiStrings.getString(LocalizedR.string.ui_somente_numeros_a4bde0),
        isError =
            state.zipCodeErrorMessage != null,
        modifier = Modifier.uiId("account.0055")
    )
    state.zipCodeErrorMessage?.let { CaixaDSAccountInlineWarning(it, modifier = Modifier.uiId("account.0056")) }
    Box(
        Modifier.padding(top = 2.dp).height(32.dp).uiId("account.0057").clickable {
            actions.requestZipCodeRedirect()
        },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_nao_sei_o_meu_cep_eb7e08),
            color = CaixaDSColorTokens.BetAction,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            style = CaixaDSAccountTextStyle,
            modifier = Modifier.uiId("account.0058")
        )
    }
    Spacer(Modifier.height(8.dp))
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_uf_55852e),
        value = state.stateCode,
        onValueChange = {
        },
        enabled = false,
        showValidation = state.stateCode.isNotBlank(),
        placeholder = uiStrings.getString(LocalizedR.string.ui_uf_55852e),
        modifier = Modifier.uiId("account.0059")
    )
    Spacer(Modifier.height(12.dp))
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_municipio_8e4309),
        value = state.city,
        onValueChange = {
        },
        enabled = false,
        showValidation = state.city.isNotBlank(),
        placeholder = uiStrings.getString(LocalizedR.string.ui_municipio_8e4309),
        modifier = Modifier.uiId("account.0060")
    )
    Spacer(Modifier.height(12.dp))
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_e_mail_2550d9),
        value = state.email,
        onValueChange = {
        },
        enabled = false,
        showValidation = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        placeholder = uiStrings.getString(LocalizedR.string.ui_nome_sobrenome_email_com_70f17b), modifier = Modifier.uiId("account.0061")
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun PersonalLimitControl(state: PersonalDataUiState, actions: PersonalDataActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LegacyPersonalDailyLimit(state, actions.changeDailyLimit)
    OutlinedButton(
        onClick = actions.recalculateLimit,
        enabled = !state.loading,
        modifier = (Modifier.fillMaxWidth().padding(top = 8.dp).height(64.dp)).uiId("account.0062"),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        border = BorderStroke(1.dp, CaixaDSColorTokens.AccountOutline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CaixaDSColorTokens.AccountLimitButton,
            contentColor = CaixaDSColorTokens.AccountOutline
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
            style = CaixaDSAccountTextStyle,
            modifier = Modifier.uiId("account.0063")
        )
    }
}

/** Preserva as métricas naturais de fonte do TextView XML mais seus 4dp literais de lineSpacingExtra. */
@Composable
private fun LegacyPersonalDataIntro(modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_estes_sao_seus_dados_pessoais_por_fa_7b7c92),
        (modifier).uiId("account.0064"),
        color = CaixaDSColorTokens.AccountHeading,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        style = CaixaDSAccountTextStyle
    )
}

@Composable
private fun PersonalDataFooter(enabled: Boolean, onUpdate: () -> Unit) {
    CaixaDSAccountSaveFooter("Atualizar", onUpdate, enabled)
}

@Composable
private fun LegacyNotificationRow(checked: Boolean, onToggle: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().height(48.dp)
            .uiId("account.0065").toggleable(checked, role = Role.Checkbox, onValueChange = { onToggle() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CaixaDSAccountCheckbox(checked, onToggle, 24.dp)
        Text(
            uiStrings.getString(LocalizedR.string.ui_aceito_receber_notificacoes_de_loter_474748),
            (Modifier.padding(start = 8.dp).weight(1f)).uiId("account.0066"),
            color = CaixaDSColorTokens.AccountOutline,
            fontSize = 16.sp,
            style = CaixaDSAccountTextStyle
        )
    }
}

/** APK content_dados_pessoais: orientação de 16sp; aviso separado de 20dp + texto de 14sp quando fora dos limites. */
@Composable
internal fun LegacyPersonalDailyLimit(state: PersonalDataUiState, onChange: (String) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val message = dailyLimitValidationMessage(state)
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
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
            color = CaixaDSColorTokens.AccountHelper,
            style = CaixaDSAccountTextStyle
        )
    } else {
        CaixaDSAccountInlineWarning(message, (Modifier.padding(top = 4.dp)).uiId("account.0069"))
    }
}
