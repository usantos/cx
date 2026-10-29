package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class SessionCartRepositoryImpl(
    private val remote: CartRepository,
    private val local: VisitorCartRepository,
    private val isAuthenticated: () -> Boolean,
) : CartRepository by remote {
    override suspend fun current() = if (isAuthenticated()) remote.current() else local.current()
    override suspend fun remove(item: CartItem) = if (isAuthenticated()) remote.remove(item) else local.remove(item)
    override suspend fun clear() = if (isAuthenticated()) {
        try {
            remote.clear()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            // CarrinhoActivity recorre à exclusão do carrinho local após uma falha na limpeza.
            coroutineContext.ensureActive()
            local.clear()
            throw failure
        }
    } else {
        local.clear()
    }
    override suspend fun validate() = if (isAuthenticated()) {
        remote.validate()
    } else {
        CartValidationResult.Rejected(ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN))
    }
    override suspend fun validateFavoriteName(name: String) {
        require(isAuthenticated()) { messageText("para_continuar_faca_login_cadast_30e1ca") }
        remote.validateFavoriteName(name)
    }
    override suspend fun saveFavorite(name: String) {
        require(isAuthenticated()) { messageText("para_continuar_faca_login_cadast_30e1ca") }
        remote.saveFavorite(name)
    }
}
