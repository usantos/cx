package br.gov.caixa.loterias.core.navigation.fixture

/** Dados de prévia ficam no aplicativo; destinos reais os solicitam somente com a opção de simulação ativada. */
interface VisualBetNavigationFixtures {
    fun visualConfirmedBet(variant: String, originalRecurringBet: Boolean = false,
        redeemedPrize: Boolean = false,
    ): br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
}
