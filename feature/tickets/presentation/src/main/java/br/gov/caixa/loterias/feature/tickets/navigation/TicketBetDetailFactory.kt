package br.gov.caixa.loterias.feature.tickets.navigation

import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel

internal fun createTicketBetDetailViewModel(
    bet: ConfirmedBet,
    dependencies: TicketsNavigationDependencies,
    canStartOperation: () -> Boolean,
    results: GetConfirmedBetResultsUseCase? = null,
): BetDetailViewModel = BetDetailViewModel(
    bet,
    dependencies.recoverConfirmedBet,
    dependencies.downloadBetReceipt,
    dependencies.validateFavoriteBet,
    dependencies.saveFavoriteBet,
    dependencies.addBetToCart,
    dependencies.dispatchers,
    getBettingParameters = dependencies.bettingParameters,
    loadConfirmedBet = dependencies.loadConfirmedBet,
    downloadPrizeReceipt = dependencies.downloadPrizeReceipt,
    getConfirmedBetResults = results,
    canStartOperation = canStartOperation
)
