package br.gov.caixa.loterias.feature.marketplace.navigation

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface MarketplaceNavigationDependencies {
    val addMarketplacePoolToCart: AddMarketplacePoolToCartUseCase
    val bettingParameters: GetBettingParametersUseCase
    val currentCart: GetCurrentCartUseCase
    val dispatchers: CoroutineDispatchers
    val favoriteRetailers: GetFavoriteRetailersUseCase
    val marketplaceDetail: GetMarketplaceDetailUseCase
    val marketplaceFilterParameters: GetMarketplaceFilterParametersUseCase
    val getSpecialContests: br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase?
        get() = null
    val searchMarketplace: SearchMarketplaceUseCase
    val searchRetailers: SearchRetailersUseCase
    val setRetailerFavorite: SetRetailerFavoriteUseCase
}
