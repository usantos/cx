package br.gov.caixa.loterias.core.navigation.destination

import br.gov.caixa.loterias.core.domain.contract.betting.model.PoolEntryRequest

object PoolTutorialRoute {
    const val base = "betting/tutorial"
    const val pattern = "$base?modalityId={modalityId}&contestTypeId={contestTypeId}"
    fun from(request: PoolEntryRequest): String =
        "$base?modalityId=${request.modalityId}&contestTypeId=${if (request.contestType.equals("ESPECIAL", true)) 2 else 1}"
}
