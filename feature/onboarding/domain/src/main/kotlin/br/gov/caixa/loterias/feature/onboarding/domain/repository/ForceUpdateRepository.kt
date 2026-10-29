package br.gov.caixa.loterias.feature.onboarding.domain.repository

import java.io.Closeable

interface ForceUpdateRepository {
    val active: Boolean
    suspend fun refresh(): Boolean
    fun observe(onChanged: (Boolean) -> Unit): Closeable
}
