package br.gov.caixa.loterias.feature.account.data.datasource.local

internal interface SessionLocalDataSource {
    suspend fun isCleanupPending(): Boolean
    suspend fun setCleanupPending(pending: Boolean)
    suspend fun clearLoginFlag()
    suspend fun clearIdentityAndBiometricRecords()
}
