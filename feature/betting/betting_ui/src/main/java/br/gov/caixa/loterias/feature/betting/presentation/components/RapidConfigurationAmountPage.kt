package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.core.designsystem.components.input.loteriasCaixaDSMoneyInputAmount
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
fun RapidAmountPage(
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
        RapidAmountEditDialog(
            minimum,
            input,
            floor,
            ceiling,
            onValueChange = {
                input = it
                inputError = null
            },
            onSave = {
                val amount = input.takeIf(String::isNotBlank)?.let(::loteriasCaixaDSMoneyInputAmount)
                if (onCustom(amount, minimum)) {
                    editingMinimum = null
                } else {
                    inputError = uiStrings.getString(
                        DeliveryR.string.ui_o_valor_selecionado_tem_que_estar_en_423454,
                        formatBetAmount(floor!!),
                        formatBetAmount(ceiling)
                    )
                }
            },
            onDismiss = { editingMinimum = null }
        )
    }
    RapidAmountInputFeedback(inputError, { inputError = null })
}
