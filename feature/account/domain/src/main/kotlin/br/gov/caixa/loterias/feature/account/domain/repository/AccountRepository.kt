package br.gov.caixa.loterias.feature.account.domain.repository

import java.math.BigDecimal
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration

interface AccountRepository {
    suspend fun profile(): AccountProfile
    suspend fun locationForZipCode(zipCode: String): ZipCodeLocation
    suspend fun updatePersonalData(update: PersonalDataUpdate)
    suspend fun register(registration: AccountRegistration)
    suspend fun verifyAccess(): AccountAccess
    suspend fun recalculateAuthorizedLimit(): BigDecimal
}
