package caixa.gov.br.loterias.view.listener;

import caixa.gov.br.loterias.model.bo.silce.dto.ParametroEquipe;

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
