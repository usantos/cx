package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSRapidAmountDialog
import br.gov.caixa.loterias.core.designsystem.components.input.caixaDSMoneyInputAmount
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import java.math.BigDecimal

@Composable
internal fun RapidAmountPage(
    state: RapidConfigurationUiState,
    onMinimum: (BigDecimal) -> Unit,
    onMaximum: (BigDecimal) -> Unit,
    onCustom: (BigDecimal?, Boolean) -> Boolean,
    onRetry: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var editingMinimum by remember { mutableStateOf<Boolean?>(null) }
    var input by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    val floor = state.minimumAllowedAmount
    val ceiling = state.maximumAllowedAmount
    RapidAmountControls(state, onMinimum, onMaximum, onRetry) { minimum ->
        editingMinimum = minimum
        input = ""
        inputError = null
    }
    editingMinimum?.let { minimum ->
        CaixaDSRapidAmountDialog(
            title = if (minimum) {
                uiStrings.getString(
                    LocalizedR.string.ui_valor_minimo_da_aposta_bcf5d5
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_valor_maximo_da_aposta_349559)
            },
            value = input,
            hint = if (minimum) {
                uiStrings.getString(
                    LocalizedR.string.ui_digite_o_valor_minimo_25e16d
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_digite_o_valor_maximo_e1efe1)
            },
            limits = if (minimum) {
                uiStrings.getString(
                    LocalizedR.string.ui_minimo_de_1_s_e_maximo_de_2_s_6f07c8,
                    formatBetAmount(floor!!),
                    formatBetAmount(ceiling!!)
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_o_limite_e_de_1_s_82167e, formatBetAmount(ceiling!!))
            },
            errorMessage = null,
            onValueChange = {
                input = it
                inputError = null
            },
            onSave = {
                val amount = input.takeIf(String::isNotBlank)?.let(::caixaDSMoneyInputAmount)
                if (onCustom(amount, minimum)) {
                    editingMinimum = null
                } else {
                    inputError = uiStrings.getString(
                        LocalizedR.string.ui_o_valor_selecionado_tem_que_estar_en_423454,
                        formatBetAmount(floor!!),
                        formatBetAmount(ceiling)
                    )
                }
            },
            onDismiss = { editingMinimum = null }
        )
    }
    inputError?.let { message ->
        br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            { inputError = null }
        )
    }
}

@Composable
private fun RapidAmountControl(
    label: String,
    description: String,
    value: BigDecimal,
    presets: List<BigDecimal>,
    onSelect: (BigDecimal) -> Unit,
    onCustom: () -> Unit,
    enabled: Boolean,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxWidth().padding(top = if (label.contains("mínimo")) 20.dp else 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            color = CaixaDSColorTokens.FavoriteCartCard,
            fontSize = 20.sp,
            fontFamily = CaixaDSFontFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("bet.0136")
        )
        Text(
            buildAnnotatedString {
                val word = if (label.contains("mínimo")) {
                    uiStrings.getString(
                        LocalizedR.string.ui_minimo_5aeed3
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_maximo_e7013b)
                }
                val start = description.indexOf(word)
                append(description.substring(0, start))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(word) }
                append(description.substring(start + word.length))
            },
            (Modifier.padding(start = 50.dp, end = 50.dp, top = 10.dp)).uiId("bet.0137"),
            fontSize = if (label.contains("mínimo")) 16.sp else 15.sp,
            fontFamily = CaixaDSFontFamily,
            color = CaixaDS.colors.graphite,
            textAlign = TextAlign.Center
        )
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.Center) {
            presets.forEach { amount ->
                RapidAmountPreset(amount.toInt().toString(), value.compareTo(amount) == 0, true) { onSelect(amount) }
            }
            RapidAmountPreset("...", enabled && presets.none { it.compareTo(value) == 0 }, enabled, onCustom)
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_valor_selecionado_8db7ec),
            (Modifier.padding(top = 10.dp)).uiId("bet.0138"),
            fontSize = 15.sp,
            fontFamily = CaixaDSFontFamily,
            color = CaixaDS.colors.graphite
        )
        Text(
            formatBetAmount(value),
            (Modifier.padding(top = 10.dp)).uiId("bet.0139"),
            color = CaixaDSColorTokens.BurntYellow,
            fontSize = 15.sp,
            fontFamily = CaixaDSFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RapidAmountPreset(label: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    RapidPreset(label, selected, enabled, true, onClick)
}

@Composable
private fun RapidAmountControls(
    state: RapidConfigurationUiState,
    onMinimum: (BigDecimal) -> Unit,
    onMaximum: (BigDecimal) -> Unit,
    onRetry: () -> Unit,
    onEdit: (Boolean) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val floor = state.minimumAllowedAmount
    val ceiling = state.maximumAllowedAmount
    val available = floor != null && ceiling != null && !state.amountLimitsLoading && state.amountLimitsError == null
    val nearDailyLimit = available && state.minimumAmount.multiply(BigDecimal("4")) > ceiling
    Column(
        Modifier.fillMaxSize()
            .uiId("bet.0132").verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RapidAmountControl(
            uiStrings.getString(LocalizedR.string.ui_valor_maximo_da_aposta_349559),
            uiStrings.getString(LocalizedR.string.ui_escolha_o_valor_maximo_em_reais_da_s_c1e5d7),
            state.maximumAmount,
            if (available) {
                (
                    if (nearDailyLimit) {
                        listOf(ceiling)
                    } else {
                        listOf(
                            state.minimumAmount * BigDecimal("3"),
                            state.minimumAmount * BigDecimal("4"),
                            ceiling
                        )
                    }
                    )
            } else {
                emptyList()
            },
            onMaximum,
            { onEdit(false) },
            available
        )
        Box(Modifier.padding(top = 20.dp).width(150.dp).height(1.dp).background(CaixaDS.colors.line))
        RapidAmountControl(
            uiStrings.getString(LocalizedR.string.ui_valor_minimo_da_aposta_bcf5d5),
            uiStrings.getString(LocalizedR.string.ui_escolha_o_valor_minimo_em_reais_da_s_e2ba31),
            state.minimumAmount,
            if (available) {
                (
                    if (nearDailyLimit) {
                        listOf(state.minimumAmount)
                    } else {
                        listOf(
                            state.minimumAmount,
                            state.minimumAmount * BigDecimal("2"),
                            state.minimumAmount * BigDecimal("3")
                        )
                    }
                    )
            } else {
                emptyList()
            },
            onMinimum,
            { onEdit(true) },
            available
        )
        RapidAmountLimitsFeedback(state, available, onRetry)
    }
}

@Composable
private fun RapidAmountLimitsFeedback(state: RapidConfigurationUiState, available: Boolean, onRetry: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.amountLimitsLoading) CircularProgressIndicator(Modifier.padding(20.dp), color = CaixaDS.colors.teal)
    if (state.amountLimitsError != null || (!available && !state.amountLimitsLoading)) {
        Text(
            state.amountLimitsError ?: uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_carregar_os_limites_08d889),
            (Modifier.padding(16.dp)).uiId("bet.0133"),
            color = CaixaDS.colors.graphite,
            fontFamily = CaixaDSFontFamily,
            textAlign = TextAlign.Center
        )
        TextButton(
            onClick = onRetry,
            modifier = Modifier
                .uiId("bet.0134")
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                modifier = Modifier
                    .uiId("bet.0135")
            )
        }
    }
}
