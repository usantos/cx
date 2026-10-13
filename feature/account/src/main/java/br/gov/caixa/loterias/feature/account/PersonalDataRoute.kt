package br.gov.caixa.loterias.feature.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.width
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFooterButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold

@Composable
fun PersonalDataRoute(
    viewModel: PersonalDataViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    // Installed DadosPessoais keeps its update footer hidden while adjustResize exposes the form above IME.
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    var showZipCodeRedirect by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.saved) {
        if (state.saved) onComplete()
    }

    CaixaDSLegacyScaffold(
        title = "Dados Pessoais",
        modifier = Modifier.imePadding(),
        onBack = onBack,
        gradientTopBar = false,
        topBarTitleSize = 20.sp,
        topBarNavigationWidth = 64.dp,
        topBarNavigationContent = {
            Image(painterResource(R.drawable.legacy_account_arrow_back), "Voltar",
                Modifier.padding(start = 32.dp).size(width = 15.dp, height = 23.dp).clickable(onClick = onBack))
        },
        topBarTitleWeight = FontWeight.SemiBold,
        containerColor = CaixaDSColorTokens.White,
        bottomBar = if (imeVisible) null else ({
            LegacyPersonalDataFooter(
                enabled = canUpdatePersonalData(state),
                onUpdate = viewModel::submit,
            )
        }),
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)) {
            Column(Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                .background(legacyAccountBackground).verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)) {
                LegacyPersonalDataIntro(Modifier.fillMaxWidth().padding(top = 16.dp))
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(28.dp))
                    LegacyAccountUnderlineField("CPF", formatCpf(state.cpf), {}, enabled = false, showValidation = true, emptyMask = "012.345.678-98")
                    Spacer(Modifier.height(12.dp))
                    LegacyAccountUnderlineField("Nome", state.name, {}, enabled = false, showValidation = true, emptyMask = "Nome Meio Sobrenome")
                    Spacer(Modifier.height(6.dp))
                    LegacyAccountGenderControl(state.gender, viewModel::changeGender)
                    Spacer(Modifier.height(12.dp))
                    LegacyAccountUnderlineField("CEP", formatZipCode(state.zipCode), viewModel::changeZipCode, keyboardType = KeyboardType.Number, emptyMask = "Somente números", error = state.zipCodeErrorMessage != null)
                    state.zipCodeErrorMessage?.let { LegacyAccountInlineWarning(it) }
                    Box(Modifier.padding(top = 2.dp).height(32.dp).clickable { showZipCodeRedirect = true }, contentAlignment = Alignment.CenterStart) {
                        Text("Não sei o meu CEP", color = CaixaDSColorTokens.LegacyBetAction, fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline, style = legacyAccountTextStyle)
                    }
                    Spacer(Modifier.height(8.dp))
                    LegacyAccountUnderlineField("UF", state.stateCode, {}, enabled = false, showValidation = state.stateCode.isNotBlank(), emptyMask = "UF")
                    Spacer(Modifier.height(12.dp))
                    LegacyAccountUnderlineField("Município", state.city, {}, enabled = false, showValidation = state.city.isNotBlank(), emptyMask = "Município")
                    Spacer(Modifier.height(12.dp))
                    LegacyAccountUnderlineField("E-mail", state.email, {}, enabled = false, showValidation = true, keyboardType = KeyboardType.Email, emptyMask = "nome.sobrenome@email.com")
                    Spacer(Modifier.height(12.dp))
                    LegacyPersonalDailyLimit(state, viewModel::changeDailyLimit)
                    OutlinedButton(
                        onClick = viewModel::recalculateLimit, enabled = !state.loading,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(64.dp), shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        border = BorderStroke(1.dp, CaixaDSColorTokens.LegacyAccountOutline),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CaixaDSColorTokens.LegacyAccountLimitButton,
                            contentColor = CaixaDSColorTokens.LegacyAccountOutline,
                        ),
                    ) {
                        Text("Solicitar limite acima de ${formatAccountCurrency(state.authorizedDailyLimit)}", fontSize = 15.sp, style = legacyAccountTextStyle)
                    }
                }
                Spacer(Modifier.height(16.dp))
                LegacyNotificationRow(state.acceptsNotifications) {
                    val activating = !state.acceptsNotifications
                    viewModel.toggleNotifications()
                    notificationMessage = if (activating) {
                        "Ao ativar esta opção, você receberá notificações importantes de loterias se estiverem habilitadas no seu aparelho. A funcionalidade pode ser ativada ou desativada a qualquer momento. Para salvar, clique em atualizar."
                    } else {
                        "Ao desativar esta opção, você não receberá mais notificações de loterias. Para salvar, clique em atualizar."
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

        }
    }

    if (state.loading || state.saving) CaixaDSAuthenticationLoadingDialog()

    if (showZipCodeRedirect) {
        CaixaDSLegacyDialog(
            title = "Não sei o meu CEP",
            message = "Você será redirecionado para o site dos Correios",
            positiveLabel = "Confirmar",
            onPositive = {
                showZipCodeRedirect = false
                uriHandler.openUri(CORREIOS_ZIP_CODE_URL)
            },
            negativeLabel = "Cancelar",
            onNegative = { showZipCodeRedirect = false },
        )
    }
    notificationMessage?.let { message ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = message,
            positiveLabel = "Entendi",
            onPositive = { notificationMessage = null },
        )
    }
    state.errorMessage?.let { message ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = message,
            positiveLabel = "Entendi",
            onPositive = viewModel::dismissError,
        )
    }
}

/** Preserves the XML TextView's natural font metrics plus its literal 4dp lineSpacingExtra. */
@Composable
private fun LegacyPersonalDataIntro(modifier: Modifier) {
    Text(
        "Estes são seus dados pessoais. Por favor, mantenha-os sempre atualizados.",
        modifier,
        color = CaixaDSColorTokens.LegacyAccountHeading,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        style = legacyAccountTextStyle,
    )
}

@Composable
private fun LegacyPersonalDataFooter(enabled: Boolean, onUpdate: () -> Unit) {
    LegacyAccountSaveFooter("Atualizar", onUpdate, enabled)
}

@Composable
private fun LegacyNotificationRow(checked: Boolean, onToggle: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(48.dp).toggleable(checked, role = Role.Checkbox, onValueChange = { onToggle() }),
        verticalAlignment = Alignment.CenterVertically) {
        LegacyAccountCheckbox(checked, onToggle, 24.dp)
        Text("Aceito receber notificações de Loterias.", Modifier.padding(start = 8.dp).weight(1f),
            color = CaixaDSColorTokens.LegacyAccountOutline, fontSize = 16.sp, style = legacyAccountTextStyle)
    }
}

/** APK content_dados_pessoais: helper16sp; separate warning20dp + text14sp when outside bounds. */
@Composable
internal fun LegacyPersonalDailyLimit(state: PersonalDataUiState, onChange: (String) -> Unit) {
    val message = dailyLimitValidationMessage(state)
    LegacyAccountUnderlineField("Limite Diário de Apostas", state.dailyLimit, onChange,
        keyboardType = KeyboardType.Decimal, error = message != null, currencyInput = true)
    if (message == null) {
        Text("${formatAccountCurrency(state.minimumDailyLimit)} até ${formatAccountCurrency(state.authorizedDailyLimit)}",
            Modifier.padding(start = 10.dp, top = 4.dp, end = 10.dp), fontSize = 16.sp,
            color = CaixaDSColorTokens.LegacyAccountHelper, style = legacyAccountTextStyle)
    } else {
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.legacy_account_warning), null, Modifier.size(20.dp), tint = CaixaDSColorTokens.LegacyAccountError)
            Spacer(Modifier.width(8.dp))
            Text(message, fontSize = 14.sp, color = CaixaDSColorTokens.LegacyAccountError, style = legacyAccountTextStyle)
        }
    }
}

internal fun dailyLimitValidationMessage(state: PersonalDataUiState): String? {
    val value = parseBrazilianCurrency(state.dailyLimit) ?: return null
    return when {
        value < state.minimumDailyLimit -> "O valor mínimo é ${formatAccountCurrency(state.minimumDailyLimit)}"
        value > state.authorizedDailyLimit -> "O valor máximo é ${formatAccountCurrency(state.authorizedDailyLimit)}"
        else -> null
    }
}

internal fun canUpdatePersonalData(state: PersonalDataUiState): Boolean =
    !state.loading && !state.saving && state.zipCode.length == 8 &&
        state.stateCode.isNotBlank() && state.city.isNotBlank() &&
        state.municipalityId.stateId > 0 && parseBrazilianCurrency(state.dailyLimit) != null

internal fun formatCpf(value: String): String {
    val digits = value.filter(Char::isDigit).take(11)
    return buildString {
        digits.forEachIndexed { index, char ->
            if (index == 3 || index == 6) append('.')
            if (index == 9) append('-')
            append(char)
        }
    }
}

internal fun formatZipCode(value: String): String {
    val digits = value.filter(Char::isDigit).take(8)
    return if (digits.length <= 5) digits else digits.take(5) + "-" + digits.drop(5)
}

private const val CORREIOS_ZIP_CODE_URL = "https://buscacepinter.correios.com.br/app/endereco/index.php"
