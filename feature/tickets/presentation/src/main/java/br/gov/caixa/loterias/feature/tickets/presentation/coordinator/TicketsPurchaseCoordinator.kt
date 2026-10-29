package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.statusDialog
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.launch

internal class TicketsPurchaseCoordinator(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val scope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getPurchaseBets: GetPurchaseBetsUseCase,
    private val getPurchaseDetails: GetPurchaseDetailsUseCase?,
    private val validateFavoriteName: ValidateFavoriteCartNameUseCase?,
    private val savePurchaseAsFavorite: SavePurchaseAsFavoriteUseCase?,
    private val validateDam: ValidateDamUseCase?,
    private val capturePixDeadline: (Purchase, String) -> Unit,
    private val fail: (String) -> Unit,
) {
    fun loadPurchase(purchase: Purchase) {
        if (mutableState.value.selectedPurchase?.id != purchase.id) {
            mutableState.dispatch(TicketsStateAction.PixPurchaseReset)
        }

        if (mutableState.value.isLoading) return
        mutableState.dispatch(TicketsStateAction.PurchaseLoading(purchase))
        scope.launch(dispatchers.io) {
            val details = getPurchaseDetails
            if (details == null) {
                when (val result = getPurchaseBets(purchase.id)) {
                    is AppResult.Success -> showPurchaseDetails(purchase, result.value)
                    is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_os_dad_bf517e"))
                }
            } else {
                when (val result = details(purchase)) {
                    is AppResult.Success -> showPurchaseDetails(result.value.purchase, result.value.bets, result.value.serverDateTime)
                    is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_os_dad_bf517e"))
                }
            }
        }
    }

    fun openPurchaseBetFilters() {
        mutableState.dispatch(TicketsStateAction.PurchaseBetFiltersOpened)
    }

    fun closePurchaseBetFilters() {
        mutableState.dispatch(TicketsStateAction.PurchaseBetFiltersClosed)
    }

    fun selectPurchaseBetContest(value: Int?) = mutableState.dispatch(TicketsStateAction.PurchaseBetContestSelected(value))
    fun selectPurchaseBetStatus(value: Long?) = mutableState.dispatch(TicketsStateAction.PurchaseBetStatusSelected(value))
    fun selectPurchaseBetModality(value: Int?) = mutableState.dispatch(TicketsStateAction.PurchaseBetModalitySelected(value))

    fun clearPurchaseBetFilters() {
        mutableState.dispatch(TicketsStateAction.PurchaseBetFiltersCleared)
    }

    fun applyPurchaseBetFilters() {
        mutableState.dispatch(TicketsStateAction.PurchaseBetFiltersApplied)
    }

    fun dismissPurchaseStatusDialog() {
        mutableState.dispatch(TicketsStateAction.PurchaseStatusDialogDismissed)
    }

    fun openFavoriteDialog() {
        val current = mutableState.value
        if (current.bets.any(ConfirmedBet::poolShare)) return
        mutableState.dispatch(TicketsStateAction.PurchaseFavoriteOpened)
    }

    fun closeFavoriteDialog() {
        mutableState.dispatch(TicketsStateAction.PurchaseFavoriteClosed)
    }

    fun changeFavoriteName(value: String) {
        mutableState.dispatch(TicketsStateAction.PurchaseFavoriteNameChanged(value))
    }

    fun toggleKeepSurprises() {
        mutableState.dispatch(TicketsStateAction.PurchaseKeepSurprisesToggled)
    }

    fun validateAndSaveFavorite() {
        val current = mutableState.value
        if (current.favoriteName.isBlank()) {
            mutableState.dispatch(TicketsStateAction.PurchaseFavoriteNameRejected())
            return
        }
        val validation = validateFavoriteName
        if (validation == null) return saveFavorite(overwrite = true)
        mutableState.dispatch(TicketsStateAction.PurchaseFavoriteValidationStarted)
        scope.launch(dispatchers.io) {
            when (validation(current.favoriteName)) {
                is AppResult.Success -> saveFavorite(overwrite = true)
                is AppResult.Failure -> mutableState.dispatch(TicketsStateAction.PurchaseFavoriteOverwriteRequired)
            }
        }
    }

    fun confirmFavoriteOverwrite() {
        mutableState.dispatch(TicketsStateAction.PurchaseFavoriteOverwriteConfirmed)
        saveFavorite(overwrite = true)
    }

    fun cancelFavoriteOverwrite() {
        mutableState.dispatch(TicketsStateAction.PurchaseFavoriteOverwriteCancelled)
    }

    fun dismissFavoriteSaved() {
        mutableState.dispatch(TicketsStateAction.PurchaseFavoriteSavedDismissed)
    }

    fun dismissDamMessage() {
        mutableState.dispatch(TicketsStateAction.DamMessageDismissed)
    }

    suspend fun showPurchaseDetails(purchase: Purchase, bets: List<ConfirmedBet>, serverDateTime: String = "") {
        capturePixDeadline(purchase, serverDateTime)

        mutableState.dispatch(
            TicketsStateAction.PurchaseDetailsReceived(purchase, bets, purchase.statusDialog(bets.any(ConfirmedBet::poolShare)))
        )
        if (purchase.statusId == 9L) {
            when (val result = validateDam?.invoke()) {
                is AppResult.Success -> if (result.value.blocked) {
                    mutableState.dispatch(TicketsStateAction.DamBlocked(result.value.message))
                }

                else -> Unit
            }
        }
    }

    private fun saveFavorite(overwrite: Boolean) {
        if (!overwrite) return
        val useCase = savePurchaseAsFavorite
        val current = mutableState.value
        if (useCase == null) {
            mutableState.dispatch(TicketsStateAction.LocalPurchaseFavoriteSaved)
            return
        }
        mutableState.dispatch(TicketsStateAction.PurchaseFavoriteSaveStarted)
        scope.launch(dispatchers.io) {
            when (
                val result = useCase(
                    current.selectedPurchase?.id ?: 0,
                    current.favoriteName,
                    current.keepSurprises
                )
            ) {
                is AppResult.Success -> mutableState.dispatch(TicketsStateAction.PurchaseFavoriteSaved)
                is AppResult.Failure -> fail(messageText("nao_foi_possivel_salvar_o_carrin_1a8fd3"))
            }
        }
    }
}
