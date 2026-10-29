package br.gov.caixa.loterias.feature.betting.domain.model

import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText

internal fun FavoriteBetDraft.validate() {
    require(modalityId > 0) { messageText("modalidade_invalida_4a3e50") }
    require(name.isNotBlank()) { messageText("informe_um_nome_para_a_aposta_b64e5e") }
    require(numberColumns.any { it.isNotEmpty() }) { messageText("a_aposta_nao_possui_numeros_9e7dc9") }
}
