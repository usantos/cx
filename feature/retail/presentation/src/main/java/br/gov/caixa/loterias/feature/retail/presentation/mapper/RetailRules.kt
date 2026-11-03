package br.gov.caixa.loterias.feature.retail.presentation.mapper

import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixKeySource
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

// ValidacaoUtils.validarEmail, usado pelo fluxo original de resgate do Mercado Pago.
private val mercadoPagoEmailPattern =
    Regex("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

// Pix usava android.util.Patterns.EMAIL_ADDRESS com um contrato diferente.
private val pixEmailPattern =
    Regex("[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")
private val pixPhonePattern = Regex("^\\d{11}$")
private val pixRandomKeyPattern = Regex("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$")

internal fun isValidPrizeEmail(value: String): Boolean = mercadoPagoEmailPattern.matches(value)

fun isValidPixKey(value: String): Boolean =
    pixPhonePattern.matches(value) || pixRandomKeyPattern.matches(value) || pixEmailPattern.matches(value)

fun PrizeRedemptionUiState.selectedPixKey(): String = when (pixKeySource) {
    PrizePixKeySource.CPF -> cpf.trim()
    PrizePixKeySource.OTHER -> pixKey
}

fun PrizeRedemptionUiState.canContinuePix(): Boolean = when (pixKeySource) {
    PrizePixKeySource.CPF -> cpf.isNotBlank()
    PrizePixKeySource.OTHER -> pixOwnershipAccepted && isValidPixKey(pixKey)
}
