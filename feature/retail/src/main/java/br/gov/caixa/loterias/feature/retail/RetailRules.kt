package br.gov.caixa.loterias.feature.retail

internal fun formatRemainingTime(totalSeconds: Int): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    return "%02d:%02d".format(safeSeconds / 60, safeSeconds % 60)
}

// ValidacaoUtils.validarEmail, usado no resgate Mercado Pago do legado.
private val mercadoPagoEmailPattern =
    Regex("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

// O Pix usava android.util.Patterns.EMAIL_ADDRESS, com contrato diferente.
private val pixEmailPattern =
    Regex("[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")
private val pixPhonePattern = Regex("^\\d{11}$")
private val pixRandomKeyPattern = Regex("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$")

internal fun isValidPrizeEmail(value: String): Boolean = mercadoPagoEmailPattern.matches(value)

internal fun isValidPixKey(value: String): Boolean {
    return pixPhonePattern.matches(value) || pixRandomKeyPattern.matches(value) || pixEmailPattern.matches(value)
}

internal fun PrizeRedemptionUiState.selectedPixKey(): String = when (pixKeySource) {
    PrizePixKeySource.CPF -> cpf.trim()
    PrizePixKeySource.OTHER -> pixKey
}

internal fun PrizeRedemptionUiState.canContinuePix(): Boolean = when (pixKeySource) {
    PrizePixKeySource.CPF -> cpf.isNotBlank()
    PrizePixKeySource.OTHER -> pixOwnershipAccepted && isValidPixKey(pixKey)
}

internal enum class LotteryQrUrgency { EXPIRED, NORMAL, RED, PULSE, VIBRATE }

internal fun lotteryQrUrgency(remainingSeconds: Int): LotteryQrUrgency = when {
    remainingSeconds <= 0 -> LotteryQrUrgency.EXPIRED
    remainingSeconds <= 3 -> LotteryQrUrgency.VIBRATE
    remainingSeconds <= 10 -> LotteryQrUrgency.PULSE
    remainingSeconds <= 60 -> LotteryQrUrgency.RED
    else -> LotteryQrUrgency.NORMAL
}
