package br.gov.caixa.loterias.feature.onboarding.data.config

import java.io.Closeable

internal interface AppModulesConfigurationSource {
    fun read(): String
    suspend fun initialize()
    suspend fun refresh()
    fun observe(onChanged: () -> Unit): Closeable
}
