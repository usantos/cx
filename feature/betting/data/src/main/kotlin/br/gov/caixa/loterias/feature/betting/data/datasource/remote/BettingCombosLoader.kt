package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.data.dto.AddComboRequestDto
import br.gov.caixa.loterias.feature.betting.data.mapper.toBettingCombo
import br.gov.caixa.loterias.feature.cart.data.repository.toDomainCart

internal class BettingCombosLoader(private val remote: BettingRemoteDataSource) {
    suspend fun combos(): List<BetCombo> = remote.combos().orEmpty().map { it.toBettingCombo() }

    suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean): ShoppingCart =
        remote.addCombo(AddComboRequestDto(comboId, quantity, surprise)).toDomainCart()
}
