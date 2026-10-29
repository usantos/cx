package br.gov.caixa.loterias.feature.account.presentation.screen

import br.gov.caixa.loterias.feature.account.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.formatAccountCurrency
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.parseBrazilianCurrency

internal fun dailyLimitValidationMessage(state: PersonalDataUiState): String? {
    val value = parseBrazilianCurrency(state.dailyLimit) ?: return null
    return when {
        value < state.minimumDailyLimit -> messageText("o_valor_minimo_e_1_s_6adb02", formatAccountCurrency(state.minimumDailyLimit))
        value > state.authorizedDailyLimit -> messageText("o_valor_maximo_e_1_s_5b1314", formatAccountCurrency(state.authorizedDailyLimit))
        else -> null
    }
}

internal fun canUpdatePersonalData(state: PersonalDataUiState): Boolean = !state.loading && !state.saving && state.zipCode.length == 8 &&
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
