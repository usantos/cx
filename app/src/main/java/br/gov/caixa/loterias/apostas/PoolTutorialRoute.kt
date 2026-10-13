package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.betting.PoolEntryRequest

internal object PoolTutorialRoute {
    const val base = "betting/tutorial"
    const val pattern = "$base?modalityId={modalityId}&contestTypeId={contestTypeId}"
    fun from(request: PoolEntryRequest): String =
        "$base?modalityId=${request.modalityId}&contestTypeId=${if (request.contestType.equals("ESPECIAL", true)) 2 else 1}"
}
