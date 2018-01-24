package caixa.gov.br.loterias.utils.interfaces;

import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;

/**
 * Created by joafilho on 24/01/2018.
 */

public interface CartelaFragmentListener {
    int verificaNovoQtdDezenasMax(int qtdDezenasAtual);

    void adicionarApostaCarrinho();

    void abreCarrinhoApostas();

    void adicionarTimeSurpresa(ParametroEquipe equipeSelecionada);
}
