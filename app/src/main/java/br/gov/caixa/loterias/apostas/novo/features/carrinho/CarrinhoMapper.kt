package br.gov.caixa.loterias.apostas.novo.features.carrinho

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO

object CarrinhoMapper {

    fun map(carrinho: CarrinhoDTO): List<CarrinhoItem> =
        buildList {
            add(CarrinhoItem.HeaderFixo())
            carrinho.boloes
                .orEmpty()
                .takeIf { it.isNotEmpty() }
                ?.let { boloes ->
                    add(CarrinhoItem.Header("Cotas Bolão"))

                    boloes.forEach {
                        add(CarrinhoItem.Bolao(it))
                    }
                }

            carrinho.apostasIndividuais
                .orEmpty()
                .takeIf { it.isNotEmpty() }
                ?.let { apostas ->
                    add(CarrinhoItem.Header("Apostas Individuais"))

                    apostas
                        .filter { it.modalidade != null }
                        .forEach {
                            add(CarrinhoItem.Aposta(it))
                        }
                }

            carrinho.combos
                .orEmpty()
                .takeIf { it.isNotEmpty() }
                ?.let { combos ->
                    add(CarrinhoItem.Header("Combo de Apostas"))

                    combos.forEach {
                        add(CarrinhoItem.Combo(it))
                    }
                }
        }
}