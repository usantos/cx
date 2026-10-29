package br.gov.caixa.loterias.feature.cart.domain.repository

import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet

/** Retrato de migração somente para leitura. A leitura nunca confirma, envia ou remove apostas locais. */
fun interface LocalCartRepository { suspend fun snapshot(): List<LocalCartBet> }
