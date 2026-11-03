package br.gov.caixa.loterias.feature.account.presentation.screen

import br.gov.caixa.loterias.feature.account.domain.model.Gender

data class PersonalDataActions(
    val submit: () -> Unit,
    val changeGender: (Gender) -> Unit,
    val changeZipCode: (String) -> Unit,
    val changeDailyLimit: (String) -> Unit,
    val recalculateLimit: () -> Unit,
    val requestZipCodeRedirect: () -> Unit,
    val toggleNotifications: () -> Unit,
)
