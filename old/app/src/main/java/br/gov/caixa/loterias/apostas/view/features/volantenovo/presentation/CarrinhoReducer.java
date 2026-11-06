package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import java.math.BigDecimal;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;

public class CarrinhoReducer {

    public SimulaUiState abrirCarrinho(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setAbrirCarrinho(true);
    }

    public SimulaUiState consumirAbrirCarrinho(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.copy()
                .setAbrirCarrinho(false);
    }

    public SimulaUiState publicarEstadoCarrinho(
            SimulaUiState atual,
            CarrinhoDTO carrinho,
            boolean carregando
    ) {

        if (atual == null) {
            return null;
        }

        BigDecimal valorCarrinho =
                obterValorCarrinho(
                        carrinho
                );

        int quantidadeApostas =
                obterQuantidadeApostas(
                        carrinho
                );

        boolean exibirCarrinho =
                valorCarrinho.compareTo(
                        BigDecimal.ZERO
                ) > 0;

        return atual.copy()
                .setCarrinho(carrinho)
                .setValorCarrinho(valorCarrinho)
                .setQuantidadeApostasCarrinho(
                        quantidadeApostas
                )
                .setExibirCarrinho(exibirCarrinho)
                .setCarregandoCarrinho(carregando);
    }

    public BigDecimal obterValorCarrinho(
            CarrinhoDTO carrinho
    ) {

        if (carrinho == null) {
            return BigDecimal.ZERO;
        }

        if (carrinho.getApostas() == null
                || carrinho.getApostas().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal valorCalculado =
                somarValorDasApostas(
                        carrinho
                );

        if (valorCalculado.compareTo(BigDecimal.ZERO) > 0) {
            return valorCalculado;
        }

        if (carrinho.getValorTotal() != null
                && carrinho.getValorTotal()
                .compareTo(BigDecimal.ZERO) > 0) {
            return carrinho.getValorTotal();
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal somarValorDasApostas(
            CarrinhoDTO carrinho
    ) {

        if (carrinho == null
                || carrinho.getApostas() == null
                || carrinho.getApostas().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total =
                BigDecimal.ZERO;

        List<IdentificaoDeUmaApostaDas8Modalidades> apostas =
                carrinho.getApostas();

        for (IdentificaoDeUmaApostaDas8Modalidades aposta : apostas) {

            if (aposta == null
                    || aposta.getValor() == null) {
                continue;
            }

            total =
                    total.add(
                            aposta.getValor()
                    );
        }

        return total;
    }

    public int obterQuantidadeApostas(
            CarrinhoDTO carrinho
    ) {

        if (carrinho == null
                || carrinho.getApostas() == null) {
            return 0;
        }

        return carrinho.getApostas().size();
    }
}
