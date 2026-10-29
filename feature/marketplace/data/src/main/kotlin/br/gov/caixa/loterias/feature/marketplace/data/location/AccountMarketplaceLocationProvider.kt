package br.gov.caixa.loterias.feature.marketplace.data.location

import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.AccountMunicipalityRepository
import br.gov.caixa.loterias.feature.marketplace.data.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceLocation
import br.gov.caixa.loterias.feature.marketplace.domain.port.MarketplaceLocationProvider

class AccountMarketplaceLocationProvider(
    private val account: AccountMunicipalityRepository,
    private val sessions: AuthenticatedSessionIdentityProvider,
) : MarketplaceLocationProvider {
    override suspend fun current(): MarketplaceLocation {
        val identity = requireNotNull(sessions.captureIdentity()) { messageText("sessao_necessaria_para_buscar_bo_6628ac") }
        val municipality = account.municipality()
        check(sessions.isCurrent(identity)) { messageText("sessao_substituida_durante_a_con_d4be24") }
        return MarketplaceLocation(municipality.number, municipality.stateId)
    }
}
