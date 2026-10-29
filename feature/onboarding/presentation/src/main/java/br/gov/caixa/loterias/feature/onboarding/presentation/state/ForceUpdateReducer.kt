package br.gov.caixa.loterias.feature.onboarding.presentation.state

sealed interface ForceUpdateAction {
    data class ActiveValuePublished(val required: Boolean) : ForceUpdateAction
}

fun reduceForceUpdateState(state: ForceUpdateState, action: ForceUpdateAction): ForceUpdateState = when (action) {
    is ForceUpdateAction.ActiveValuePublished -> {
        val next = if (action.required) ForceUpdateState.REQUIRED else ForceUpdateState.READY
        if (state == next) state else next
    }
}
