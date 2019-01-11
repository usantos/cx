package br.gov.caixa.loterias.apostar.interno.view.listener;

import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroEquipe;

/**
 * Created by joafilho on 24/01/2018.
 * Interface CartelaFragmentListener
 */

public interface CartelaFragmentListener {
    int verificaNovoQtdDezenasMax(int qtdDezenasAtual);

    void adicionarApostaCarrinho();

    void abreCarrinhoApostas();

    void adicionarTimeSurpresa(ParametroEquipe equipeSelecionada);

}
