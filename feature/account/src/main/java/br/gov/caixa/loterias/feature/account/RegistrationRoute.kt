package br.gov.caixa.loterias.feature.account

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.res.ResourcesCompat
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCheckbox
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.domain.account.Gender

@Composable
fun RegistrationRoute(
    viewModel: RegistrationViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onTerms: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    var showZipCodeRedirect by remember { mutableStateOf(false) }
    LaunchedEffect(state.registered) {
        if (state.registered) onComplete()
    }
    CaixaDSLegacyScaffold(
        title = "Loterias Caixa",
        onBack = onBack,
        topBarAction = { CaixaDSLotteryMark(Modifier.size(24.dp)) },
        topBarActionWidth = 48.dp,
        containerColor = CaixaDSColorTokens.White,
        gradientTopBar = false,
        topBarTitleSize = 20.sp,
        topBarTitleWeight = FontWeight.SemiBold,
        bottomBar = { LegacyAccountSaveFooter("Finalizar cadastro", viewModel::submit, canRegister(state), disabledColor = CaixaDS.colors.legacyDisabledButton) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CaixaDSColorTokens.White)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 6.dp).background(legacyAccountBackground).padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)) {
                LegacyAccountCopy("Que bom ver você por aqui", 18f, true, Modifier.fillMaxWidth().padding(top = 16.dp))
                LegacyAccountCopy("O cadastro é bem rápido. Para começar, informe os dados abaixo:", 16f, false,
                    Modifier.fillMaxWidth().padding(top = 12.dp))
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
                    LegacyAccountUnderlineField("UF", state.stateCode, {}, enabled = false, emptyMask = "UF")
                    Spacer(Modifier.height(12.dp))
                    LegacyAccountUnderlineField("Município", state.city, {}, enabled = false, emptyMask = "Município")
                    Spacer(Modifier.height(12.dp))
                    LegacyAccountUnderlineField("E-mail", state.email, {}, enabled = false, showValidation = true, keyboardType = KeyboardType.Email, emptyMask = "nome.sobrenome@email.com")
                }
                Spacer(Modifier.height(16.dp))
                TermsConsent(state.acceptsTerms, viewModel::toggleTerms, onTerms)
                NotificationConsent(state.acceptsNotifications, viewModel::toggleNotifications)
                state.errorMessage?.let { Text(it, Modifier.padding(top = 12.dp), color = CaixaDS.colors.error, fontSize = 14.sp) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
    if (state.loading) CaixaDSAuthenticationLoadingDialog()
    if (showZipCodeRedirect) {
        CaixaDSLegacyDialog(
            title = "Não sei o meu CEP",
            message = "Você será redirecionado para o site dos Correios",
            positiveLabel = "Confirmar",
            onPositive = {
                showZipCodeRedirect = false
                uriHandler.openUri("https://buscacepinter.correios.com.br/app/endereco/index.php")
            },
            negativeLabel = "Cancelar",
            onNegative = { showZipCodeRedirect = false },
        )
    }
}

@Composable
internal fun LegacyAccountUnderlineField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    showValidation: Boolean = false,
    validationEmphasized: Boolean = false,
    emptyMask: String = "",
    error: Boolean = false,
    currencyInput: Boolean = false,
) {
    val interactions = remember { MutableInteractionSource() }
    val focused by interactions.collectIsFocusedAsState()
    Column(modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) Text(label, color = if (error) CaixaDSColorTokens.LegacyAccountError else CaixaDSColorTokens.LegacyBetAction, fontSize = 16.sp, fontWeight = FontWeight.Bold, style = legacyAccountTextStyle)
        Row(
            Modifier.padding(top = if (label.isNotEmpty()) 4.dp else 0.dp).fillMaxWidth().height(52.dp)
                .background(if (enabled) CaixaDSColorTokens.White else CaixaDSColorTokens.LegacyAccountDisabledField, RoundedCornerShape(4.dp))
                .border(1.dp, if (focused && error) CaixaDSColorTokens.LegacyAccountError else if (focused) CaixaDSColorTokens.LegacyAccountOutline else CaixaDSColorTokens.LegacyAccountUnfocusedOutline, RoundedCornerShape(4.dp)).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showValidation && !enabled) {
                Icon(painterResource(R.drawable.legacy_account_ic_check), null, Modifier.size(24.dp).alpha(.8f), tint = CaixaDSColorTokens.LegacyAccountOutline)
                Spacer(Modifier.size(12.dp))
            }
            if (currencyInput) {
                LegacyAccountCurrencyInput(value, onValueChange, Modifier.weight(1f).alpha(.8f), enabled, interactions, emptyMask)
            } else BasicTextField(
                value = value, onValueChange = onValueChange, modifier = Modifier.weight(1f).alpha(.8f),
                enabled = enabled, singleLine = true, interactionSource = interactions,
                textStyle = legacyAccountTextStyle.copy(color = CaixaDSColorTokens.LegacyBetAction, fontSize = 16.sp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType), cursorBrush = SolidColor(CaixaDSColorTokens.LegacyComboDark),
                decorationBox = { inner ->
                    if (value.isBlank()) Text(emptyMask, color = CaixaDSColorTokens.LegacyAccountOutline, fontSize = 16.sp, style = legacyAccountTextStyle)
                    inner()
                },
            )
        }
    }
}

/** The installed currency watcher replaces the text and explicitly selects its end on each edit. */
@Composable
private fun LegacyAccountCurrencyInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    interactions: MutableInteractionSource,
    emptyMask: String,
) {
    var editing by remember(value) { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    val presented = if (editing.text == value) editing else TextFieldValue(value, TextRange(value.length))
    BasicTextField(
        value = presented,
        onValueChange = { next ->
            if (next.text == editing.text) {
                editing = next
            } else {
                val digits = next.text.filter(Char::isDigit)
                val formatted = if (digits.isEmpty()) "" else formatAccountCurrency(digits.toBigDecimal().movePointLeft(2))
                val changed = formatted != editing.text
                editing = TextFieldValue(formatted, TextRange(formatted.length))
                if (changed) onValueChange(formatted)
            }
        },
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        interactionSource = interactions,
        textStyle = legacyAccountTextStyle.copy(color = CaixaDSColorTokens.LegacyBetAction, fontSize = 16.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        cursorBrush = SolidColor(CaixaDSColorTokens.LegacyComboDark),
        decorationBox = { inner ->
            if (value.isBlank()) Text(emptyMask, color = CaixaDSColorTokens.LegacyAccountOutline, fontSize = 16.sp, style = legacyAccountTextStyle)
            inner()
        },
    )
}

/** Installed txt_valida_cep is directly below the field: 14sp, warning20dp and gap8dp. */
@Composable
internal fun LegacyAccountInlineWarning(message: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(R.drawable.legacy_account_warning), null, Modifier.size(20.dp), tint = CaixaDSColorTokens.LegacyAccountError)
        Spacer(Modifier.width(8.dp))
        Text(message, fontSize = 14.sp, color = CaixaDSColorTokens.LegacyAccountError, style = legacyAccountTextStyle)
    }
}

@Composable
internal fun LegacyAccountGenderControl(selected: Gender, onSelected: (Gender) -> Unit) {
    Row(Modifier.fillMaxWidth().height(58.dp).background(CaixaDSColorTokens.White, RoundedCornerShape(18.dp))
        .border(1.dp, CaixaDSColorTokens.LegacyCardOutline, RoundedCornerShape(18.dp)), verticalAlignment = Alignment.CenterVertically) {
        listOf("Masculino" to Gender.MALE, "Feminino" to Gender.FEMALE).forEachIndexed { index, (label, gender) ->
            if (index == 1) Box(Modifier.height(42.dp).size(width = 1.dp, height = 42.dp).background(CaixaDSColorTokens.LegacyGenderDivider))
            Row(Modifier.weight(1f).fillMaxSize().clickable { onSelected(gender) }, verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                // The native inner LinearLayout wraps 20dp children plus the 16dp margin.
                // Its first child is centered with topMargin9, the second has topMargin16.
                Row(Modifier.height(20.dp).offset(y = if (index == 0) (-2).dp else (-3).dp), verticalAlignment = Alignment.CenterVertically) {
                    LegacyAccountGenderLabel(label)
                    Icon(painterResource(if (selected == gender) R.drawable.legacy_account_ic_check_circle else R.drawable.legacy_account_ic_circle_outline),
                        null, Modifier.padding(start = 8.dp).size(20.dp), tint = Color.Unspecified)
                }
            }
        }
    }
}

/** SegmentedButton centers the drawn text bounds, rather than its advance width. */
@Composable
private fun LegacyAccountGenderLabel(label: String) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val textSizePx = with(density) { 14.sp.toPx() }
    val boundsWidth = remember(label, context, textSizePx) {
        val paint = android.graphics.Paint().apply {
            typeface = ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_bold)
            textSize = textSizePx
        }
        val bounds = android.graphics.Rect()
        paint.getTextBounds(label, 0, label.length, bounds)
        bounds.width()
    }
    Box(Modifier.width(with(density) { boundsWidth.toDp() })) {
        Text(label, Modifier.wrapContentWidth(Alignment.Start, unbounded = true),
            color = CaixaDSColorTokens.LegacyBetAction, fontSize = 14.sp, fontWeight = FontWeight.Bold,
            style = legacyAccountTextStyle.copy(platformStyle = PlatformTextStyle(includeFontPadding = false)))
    }
}

@Composable
private fun TermsConsent(checked: Boolean, onToggle: () -> Unit, onTerms: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
        LegacyAccountCheckbox(checked, onToggle, 32.dp)
        val text = buildAnnotatedString {
            append("Li e concordo com os ")
            withStyle(SpanStyle(color = CaixaDSColorTokens.LegacyBetAction, textDecoration = TextDecoration.Underline)) {
                append("termos de uso")
            }
            append(".")
        }
        ClickableText(text, Modifier.padding(start = 8.dp).weight(1f),
            style = legacyAccountTextStyle.copy(color = CaixaDSColorTokens.LegacyAccountTermsText, fontSize = 16.sp),
            onClick = { position -> if (position in 20..32) onTerms() })
    }
}

@Composable
private fun NotificationConsent(checked: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(48.dp).clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegacyAccountCheckbox(checked, onToggle, 32.dp)
        Text(
            "Aceito receber notificações de Loterias.",
            modifier = Modifier.padding(start = 8.dp).weight(1f),
            color = CaixaDSColorTokens.LegacyAccountTermsText,
            fontSize = 16.sp,
            style = legacyAccountTextStyle,
        )
    }
}

internal val legacyAccountTextStyle = TextStyle(fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true))
internal val legacyAccountBackground = Brush.verticalGradient(listOf(CaixaDSColorTokens.White, CaixaDSColorTokens.LegacyAccountGradientMiddle, CaixaDSColorTokens.LegacyAccountGradientEnd))

@Composable
internal fun LegacyAccountSaveFooter(label: String, onClick: () -> Unit, enabled: Boolean, enabledColor: Color = CaixaDSColorTokens.LegacyComboDark, disabledColor: Color = CaixaDSColorTokens.LegacyComboDark) {
    Box(Modifier.fillMaxWidth().height(90.dp).background(CaixaDSColorTokens.White)) {
        Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().padding(start = 32.dp, top = 16.dp, end = 32.dp).height(48.dp),
            shape = RoundedCornerShape(0.dp), colors = ButtonDefaults.buttonColors(containerColor = enabledColor, contentColor = CaixaDSColorTokens.White, disabledContainerColor = disabledColor, disabledContentColor = CaixaDSColorTokens.White),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)) {
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold, style = legacyAccountTextStyle)
        }
    }
}
