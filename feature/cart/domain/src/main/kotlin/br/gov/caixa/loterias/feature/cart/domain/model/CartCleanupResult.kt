package br.gov.caixa.loterias.feature.cart.domain.model

sealed interface CartCleanupResult {
    data object Complete : CartCleanupResult
    /** Apostas locais modificadas permanecem armazenadas e devem ser conciliadas antes de outro envio. */
    data class Conflict(val preservedBetIds: Set<Long>) : CartCleanupResult
}
