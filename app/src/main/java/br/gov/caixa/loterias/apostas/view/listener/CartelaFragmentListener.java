package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;

/**
 * Created by joafilho on 24/01/2018.
 * Interface CartelaFragmentListener
 */

public interface CartelaFragmentListener {
    int verificaNovoQtdDezenasMax(int qtdDezenasAtual);

    void adicionarTimeSurpresa(ParametroEquipe equipeSelecionada);

}
