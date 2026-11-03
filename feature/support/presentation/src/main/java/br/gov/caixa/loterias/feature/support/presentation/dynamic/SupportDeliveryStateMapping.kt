package br.gov.caixa.loterias.feature.support.presentation.dynamic

import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportDeliveryState

internal fun ModuleDeliveryState.toSupportDeliveryState(): SupportDeliveryState = when (this) {
    ModuleDeliveryState.Disabled -> SupportDeliveryState.Disabled
    ModuleDeliveryState.Checking -> SupportDeliveryState.Checking
    ModuleDeliveryState.Pending -> SupportDeliveryState.Pending
    ModuleDeliveryState.Ready -> SupportDeliveryState.Ready
    ModuleDeliveryState.Failed -> SupportDeliveryState.Failed
    is ModuleDeliveryState.Downloading -> SupportDeliveryState.Downloading(bytes, total)
    is ModuleDeliveryState.Confirmation -> SupportDeliveryState.Confirmation(sessionId)
}
