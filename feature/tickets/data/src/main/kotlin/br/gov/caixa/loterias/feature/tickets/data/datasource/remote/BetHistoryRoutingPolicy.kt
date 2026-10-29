package br.gov.caixa.loterias.feature.tickets.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.AppParametersDto

internal enum class BetHistoryBackend { SILCE, MICROSERVICE, NOVA_API }

internal data class BetHistoryRoute(
    val backend: BetHistoryBackend,
    val baseUrl: String? = null,
)

/** Migração exata da implantação gradual realizada por MinhasApostasActivity legada. */
internal object BetHistoryRoutingPolicy {
    fun resolve(parameters: AppParametersDto?, cpf: String, production: Boolean): BetHistoryRoute {
        val novaBaseUrl = if (production) {
            parameters?.productionBetsApiBaseUrl
        } else {
            parameters?.pilotBetsApiBaseUrl
        }
        if (!novaBaseUrl.isNullOrBlank() && cpfIsInNovaApiRollout(cpf, parameters?.novaApiCpfRolloutPercentage)) {
            return BetHistoryRoute(BetHistoryBackend.NOVA_API, novaBaseUrl.appendLegacyV1())
        }

        val microserviceUrl = parameters?.betsHistoryBaseUrl?.trim()
        val validForFlavor = !production || microserviceUrl?.endsWith("/v1") == true ||
            microserviceUrl?.endsWith("/v2") == true
        if (parameters?.microserviceEnabled == true && !microserviceUrl.isNullOrBlank() && validForFlavor) {
            return BetHistoryRoute(BetHistoryBackend.MICROSERVICE, microserviceUrl)
        }
        return BetHistoryRoute(BetHistoryBackend.SILCE)
    }

    private fun cpfIsInNovaApiRollout(cpf: String, percentage: Int?): Boolean {
        val rollout = percentage ?: 0
        if (rollout == 0) return false
        if (rollout == 100) return true
        val digits = cpf.filter(Char::isDigit)
        return digits.takeIf { it.length >= 2 }?.takeLast(2)?.toIntOrNull()?.let { it < rollout } == true
    }

    private fun String.appendLegacyV1(): String = if (endsWith('/')) "${this}v1" else "$this/v1"
}

