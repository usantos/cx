package br.gov.caixa.loterias.feature.account.domain.repository

fun interface LocalSessionCleanupRecoveryRepository {
    /** True somente quando uma limpeza pendente persistida foi concluída; falhas de leitura ou gravação devem lançar erro. */
    suspend fun resumePending(): Boolean
}
