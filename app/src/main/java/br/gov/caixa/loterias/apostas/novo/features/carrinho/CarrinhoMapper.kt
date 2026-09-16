package br.gov.caixa.loterias.apostas.novo.features.carrinho

import CarrinhoItem
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO

object CarrinhoMapper {

    fun map(
        carrinho: CarrinhoDTO
    ): List<CarrinhoItem> {

        return buildList {

            if (!carrinho.boloes.isNullOrEmpty()) {

                add(
                    CarrinhoItem.Header(
                        "Cotas Bolão"
                    )
                )

                carrinho.boloes.forEach {
                    add(CarrinhoItem.Bolao(it))
                }
            }

            if (!carrinho.apostasIndividuais.isNullOrEmpty()) {

                add(
                    CarrinhoItem.Header(
                        "Apostas Individuais"
                    )
                )

                carrinho.apostasIndividuais.forEach {
                    add(CarrinhoItem.Aposta(it))
                }
            }

//            if (!carrinho.getCombos().isNullOrEmpty()) {
//
//                add(
//                    CarrinhoItem.Header(
//                        "Combos"
//                    )
//                )
//
//                carrinho.getCombos().forEach {
//                    add(CarrinhoItem.Combo(it))
//                }
//            }
        }
    }
}