package br.gov.caixa.loterias.feature.account.presentation.route

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountBackground
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountChoice
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountCopy
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountInlineWarning
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountSaveFooter
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountTextStyle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextFieldVariant
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.input.CaixaDSAccountCheckbox
import br.gov.caixa.loterias.core.designsystem.text.withCaixaDSAccessibleLinks
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.presentation.screen.formatCpf
import br.gov.caixa.loterias.feature.account.presentation.screen.formatZipCode
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.RegistrationViewModel
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.canRegister

@Composable
fun RegistrationRoute(viewModel: RegistrationViewModel, onBack: () -> Unit, onComplete: () -> Unit, onTerms: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    var showZipCodeRedirect by remember { mutableStateOf(false) }
    LaunchedEffect(state.registered) {
        if (state.registered) onComplete()
    }
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_loterias_caixa_02d076),
        onBack = onBack,
        topBarAction = { CaixaDSLotteryMark((Modifier.size(24.dp)).uiId("account.0019")) },
        topBarActionWidth = 48.dp,
        containerColor = CaixaDSColorTokens.White,
        gradientTopBar = false,

        bottomBar = {
            CaixaDSAccountSaveFooter(
                uiStrings.getString(LocalizedR.string.ui_finalizar_cadastro_77d482),
                viewModel::submit,
                canRegister(state),
                disabledColor = CaixaDS.colors.disabledButton
            )
        },
        modifier = Modifier.uiId("account.0020")
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CaixaDSColorTokens.White)
                .uiId("account.0021").verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier.fillMaxWidth().padding(
                    horizontal = 6.dp
                ).background(CaixaDSAccountBackground).padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                CaixaDSAccountCopy(
                    uiStrings.getString(LocalizedR.string.ui_que_bom_ver_voce_por_aqui_c7c430),
                    18f,
                    true,
                    (Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("account.0022")
                )
                CaixaDSAccountCopy(
                    uiStrings.getString(LocalizedR.string.ui_o_cadastro_e_bem_rapido_para_comecar_b409e1),
                    16f,
                    false,
                    (Modifier.fillMaxWidth().padding(top = 12.dp)).uiId("account.0023")
                )
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    RegistrationIdentityFields(state, viewModel)
                    RegistrationAddressFields(state, viewModel) { showZipCodeRedirect = true }
                }
                Spacer(Modifier.height(16.dp))
                TermsConsent(state.acceptsTerms, viewModel::toggleTerms, onTerms)
                NotificationConsent(state.acceptsNotifications, viewModel::toggleNotifications)
                state.errorMessage?.let {
                    Text(
                        it,
                        (Modifier.padding(top = 12.dp)).uiId("account.0024"),
                        color = CaixaDS.colors.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
    if (state.loading) CaixaDSAuthenticationLoadingDialog()
    if (showZipCodeRedirect) {
        ZipCodeRedirectDialog(
            onConfirm = {
                showZipCodeRedirect = false
                uriHandler.openUri("https://buscacepinter.correios.com.br/app/endereco/index.php")
            },
            onDismiss = { showZipCodeRedirect = false }
        )
    }
}

@Composable
private fun RegistrationIdentityFields(state: RegistrationUiState, viewModel: RegistrationViewModel) {
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
        modifier = Modifier.uiId("account.0025")
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
        modifier = Modifier.uiId("account.0026")
    )
    Spacer(Modifier.height(6.dp))
    LegacyAccountGenderControl(state.gender, viewModel::changeGender)
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun RegistrationAddressFields(state: RegistrationUiState, viewModel: RegistrationViewModel, onZipCodeRedirect: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_cep_d68636),
        value = formatZipCode(state.zipCode),
        onValueChange = viewModel::changeZipCode,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        placeholder = uiStrings.getString(LocalizedR.string.ui_somente_numeros_a4bde0),
        isError =
            state.zipCodeErrorMessage != null,
        modifier = Modifier.uiId("account.0027")
    )
    state.zipCodeErrorMessage?.let { CaixaDSAccountInlineWarning(it, modifier = Modifier.uiId("account.0028")) }
    Box(
        Modifier.padding(top = 2.dp).height(32.dp).uiId("account.0029").clickable {
            onZipCodeRedirect()
        },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_nao_sei_o_meu_cep_eb7e08),
            color = CaixaDSColorTokens.BetAction,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            style = CaixaDSAccountTextStyle,
            modifier = Modifier.uiId("account.0030")
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
        placeholder = uiStrings.getString(LocalizedR.string.ui_uf_55852e),
        modifier = Modifier.uiId("account.0031")
    )
    Spacer(Modifier.height(12.dp))
    CaixaDSTextField(
        variant = CaixaDSTextFieldVariant.LEGACY,
        label = uiStrings.getString(LocalizedR.string.ui_municipio_8e4309),
        value = state.city,
        onValueChange = {
        },
        enabled = false,
        placeholder = uiStrings.getString(LocalizedR.string.ui_municipio_8e4309),
        modifier = Modifier.uiId("account.0032")
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
        placeholder = uiStrings.getString(LocalizedR.string.ui_nome_sobrenome_email_com_70f17b), modifier = Modifier.uiId("account.0033")
    )
}

@Composable
internal fun LegacyAccountGenderControl(selected: Gender, onSelected: (Gender) -> Unit) {
    CaixaDSAccountChoice(selected, listOf("Masculino" to Gender.MALE, "Feminino" to Gender.FEMALE), onSelected)
}

@Composable
private fun TermsConsent(checked: Boolean, onToggle: () -> Unit, onTerms: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        val consentLabel = uiStrings.getString(LocalizedR.string.ui_li_e_concordo_com_os_3bcaeb) +
            uiStrings.getString(LocalizedR.string.ui_termos_de_uso_3eec48)
        Box(Modifier.semantics(mergeDescendants = true) { contentDescription = consentLabel }) {
            CaixaDSAccountCheckbox(checked, onToggle, 32.dp)
        }
        val text = buildAnnotatedString {
            append(uiStrings.getString(LocalizedR.string.ui_li_e_concordo_com_os_3bcaeb))
            pushStringAnnotation("URL", "terms")
            withStyle(SpanStyle(color = CaixaDSColorTokens.BetAction, textDecoration = TextDecoration.Underline)) {
                append(uiStrings.getString(LocalizedR.string.ui_termos_de_uso_3eec48))
            }
            pop()
            append(".")
        }
        Text(
            text.withCaixaDSAccessibleLinks { onTerms() },
            (Modifier.padding(start = 8.dp).weight(1f)).uiId("account.0034"),
            style = CaixaDSAccountTextStyle.copy(color = CaixaDSColorTokens.AccountTermsText, fontSize = 16.sp)
        )
    }
}

@Composable
private fun NotificationConsent(checked: Boolean, onToggle: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp).uiId("account.0035").toggleable(value = checked, role = Role.Checkbox) { onToggle() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.clearAndSetSemantics { }) { CaixaDSAccountCheckbox(checked, null, 32.dp) }
        Text(
            uiStrings.getString(LocalizedR.string.ui_aceito_receber_notificacoes_de_loter_474748),
            modifier = (Modifier.padding(start = 8.dp).weight(1f)).uiId("account.0036"),
            color = CaixaDSColorTokens.AccountTermsText,
            fontSize = 16.sp,
            style = CaixaDSAccountTextStyle
        )
    }
}

@Composable
private fun ZipCodeRedirectDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSDialog(
        title = uiStrings.getString(LocalizedR.string.ui_nao_sei_o_meu_cep_eb7e08),
        message = uiStrings.getString(LocalizedR.string.ui_voce_sera_redirecionado_para_o_site_61dd1a),
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
        onPositive = onConfirm,
        negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
        onNegative = onDismiss
    )
}
