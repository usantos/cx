package br.gov.caixa.loterias.feature.account.domain.model

/** RETURNED significa um callback correlacionado do protocolo, sem revogar todas as sessões do servidor. */
enum class EndSessionOutcome { RETURNED, CANCELLED, UNCONFIRMED }
