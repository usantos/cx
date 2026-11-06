package br.gov.caixa.loterias.core.presentation.delivery

import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Atualização de configuração com um único trabalho por hospedeiro em primeiro plano. */
class FeatureModuleForegroundRefresh(private val scope: CoroutineScope, private val configuration: AppModulesConfigurationUseCase) :
    AutoCloseable {
    private var job: Job? = null

    fun foreground(active: Boolean) {
        if (!active) {
            close()
        } else if (job == null) {
            job = scope.launch {
                while (isActive) {
                    runCatching { configuration.refresh() }.onFailure { if (it is CancellationException) throw it }
                    delay(CONFIGURATION_REFRESH_MILLIS)
                }
            }
        }
    }

    override fun close() {
        job?.cancel()
        job = null
    }

    private companion object {
        const val CONFIGURATION_REFRESH_MILLIS = 30_000L
    }
}
