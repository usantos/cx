package br.gov.caixa.loterias.core.domain.contract.account.model

/** RETURNED significa um callback correlacionado do protocolo, sem revogar todas as sessões do servidor. */
enum class EndSessionOutcome { RETURNED, CANCELLED, UNCONFIRMED }
