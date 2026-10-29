package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.data.mapper.mapCartPayload
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartRemoteOperations
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartValidationOperations

class CartRepositoryImpl(private val remote: CartRemoteDataSource) : CartRepository {
    private val operations = CartRemoteOperations(remote)
    private val validation = CartValidationOperations(remote)
    override suspend fun current(): ShoppingCart = operations.current()
    override suspend fun remove(item: CartItem): ShoppingCart = operations.remove(item)
    override suspend fun clear(): ShoppingCart = operations.clear()
    override suspend fun validate(): CartValidationResult = validation.validate()
    override suspend fun validateFavoriteName(name: String) = operations.validateFavoriteName(name)
    override suspend fun saveFavorite(name: String) = operations.saveFavorite(name)
}

fun CartDto?.toDomainCart(): ShoppingCart = mapCartPayload()
