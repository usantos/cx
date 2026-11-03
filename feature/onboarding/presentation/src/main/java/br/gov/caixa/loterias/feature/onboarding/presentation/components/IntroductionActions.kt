package br.gov.caixa.loterias.feature.onboarding.presentation.components

data class IntroductionActions(
    val pageChanged: (Int) -> Unit,
    val complete: () -> Unit,
    val confirmAdult: () -> Unit,
    val rejectAdult: () -> Unit,
)
