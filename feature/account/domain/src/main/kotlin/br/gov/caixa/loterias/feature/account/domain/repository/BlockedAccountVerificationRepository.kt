package br.gov.caixa.loterias.feature.account.domain.repository

fun interface BlockedAccountVerificationRepository {
    /** Conclui somente quando a verificação autenticada de conta bloqueada é bem-sucedida. */
    suspend fun verifyBlockedAccount()
}
