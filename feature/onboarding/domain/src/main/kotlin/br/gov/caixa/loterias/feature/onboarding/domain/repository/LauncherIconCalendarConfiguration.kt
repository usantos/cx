package br.gov.caixa.loterias.feature.onboarding.domain.repository

import java.io.Closeable
import java.time.Instant

interface LauncherIconCalendarConfiguration : LauncherIconConfigurationRepository {
    suspend fun initialize(defaultsResource: Int)
    fun nextEvaluationAt(): Instant
    fun observe(onChanged: () -> Unit): Closeable
}
