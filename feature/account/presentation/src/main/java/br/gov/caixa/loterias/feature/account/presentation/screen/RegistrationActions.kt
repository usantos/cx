package br.gov.caixa.loterias.feature.account.presentation.screen

import br.gov.caixa.loterias.feature.account.domain.model.Gender

data class RegistrationActions(
    val changeGender: (Gender) -> Unit,
    val changeZipCode: (String) -> Unit,
    val toggleTerms: () -> Unit,
    val toggleNotifications: () -> Unit,
    val submit: () -> Unit,
)
