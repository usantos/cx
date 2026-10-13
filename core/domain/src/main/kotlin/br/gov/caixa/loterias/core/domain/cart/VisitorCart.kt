package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect

const val VISITOR_DUPLICATE_CONFIRMATION = "Existe uma aposta idêntica em seu carrinho. Deseja prosseguir com a inclusão?"

/** Shares the legacy graph and transfer receipt; the editor never uploads or acknowledges. */
interface VisitorCartRepository {
    suspend fun current(): ShoppingCart
    suspend fun add(draft: BetDraft, parameters: BettingParameters): ShoppingCart
    suspend fun remove(item: CartItem): ShoppingCart
    suspend fun clear(): ShoppingCart
}

class SessionBettingRepository(
    private val remote: BettingRepository,
    private val local: VisitorCartRepository,
    private val isAuthenticated: () -> Boolean,
) : BettingRepository by remote {
    override suspend fun addBet(draft: BetDraft): ShoppingCart =
        if (isAuthenticated()) remote.addBet(draft) else local.add(draft, remote.parameters())
}

class SessionCartRepository(
    private val remote: CartRepository,
    private val local: VisitorCartRepository,
    private val isAuthenticated: () -> Boolean,
) : CartRepository by remote {
    override suspend fun current() = if (isAuthenticated()) remote.current() else local.current()
    override suspend fun remove(item: CartItem) = if (isAuthenticated()) remote.remove(item) else local.remove(item)
    override suspend fun clear() = if (isAuthenticated()) remote.clear() else local.clear()
    override suspend fun validate() = if (isAuthenticated()) remote.validate() else
        CartValidationResult.Rejected(ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN))
    override suspend fun validateFavoriteName(name: String) {
        require(isAuthenticated()) { "Para continuar, faça login/cadastre-se." }
        remote.validateFavoriteName(name)
    }
    override suspend fun saveFavorite(name: String) {
        require(isAuthenticated()) { "Para continuar, faça login/cadastre-se." }
        remote.saveFavorite(name)
    }
}
