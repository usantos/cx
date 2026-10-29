package br.gov.caixa.loterias.feature.marketplace.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import javax.inject.Inject

class MarketplaceNavigationDependenciesAdapter @Inject constructor(
    override val addMarketplacePoolToCart: AddMarketplacePoolToCartUseCase,
    override val bettingParameters: GetBettingParametersUseCase,
    override val currentCart: GetCurrentCartUseCase,
    override val dispatchers: CoroutineDispatchers,
    override val favoriteRetailers: GetFavoriteRetailersUseCase,
    override val marketplaceDetail: GetMarketplaceDetailUseCase,
    override val marketplaceFilterParameters: GetMarketplaceFilterParametersUseCase,
    override val searchMarketplace: SearchMarketplaceUseCase,
    override val searchRetailers: SearchRetailersUseCase,
    override val setRetailerFavorite: SetRetailerFavoriteUseCase,
    override val getSpecialContests: br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase,
) : MarketplaceNavigationDependencies {

}
