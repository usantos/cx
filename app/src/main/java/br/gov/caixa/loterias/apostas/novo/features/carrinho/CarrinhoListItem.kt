package br.gov.caixa.loterias.apostas.novo.features.carrinho

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades

sealed interface CarrinhoItem {

    class HeaderFixo : CarrinhoItem
    data class Header(
        val titulo: String
    ) : CarrinhoItem

    data class Bolao(
        val aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) : CarrinhoItem

    data class Aposta(
        val aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) : CarrinhoItem

    data class Combo(
        val combo: ComboApostaDTO
    ) : CarrinhoItem
}