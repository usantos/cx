package br.gov.caixa.loterias.feature.onboarding.presentation.state

enum class QueueStatus { WAITING, NO_INTERNET, PASSED, ERROR }

data class QueueGateState(val status: QueueStatus = QueueStatus.WAITING, val attempt: Int = 0)
