package br.gov.caixa.loterias.feature.support.domain.delivery

import kotlinx.coroutines.flow.Flow

/** Disponibilidade da apresentação opcional; não representa autorização de serviços. */
sealed interface SupportDeliveryState {
    data object Disabled : SupportDeliveryState
    data object Checking : SupportDeliveryState
    data object Pending : SupportDeliveryState
    data object Ready : SupportDeliveryState
    data object Failed : SupportDeliveryState
    data class Downloading(val bytes: Long, val total: Long) : SupportDeliveryState
    data class Confirmation(val sessionId: Int) : SupportDeliveryState
}

interface SupportModuleDelivery {
    fun install(): Flow<SupportDeliveryState>
}
