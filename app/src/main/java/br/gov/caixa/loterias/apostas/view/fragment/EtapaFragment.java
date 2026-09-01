package br.gov.caixa.loterias.apostas.view.fragment;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public interface EtapaFragment {

    void onCompletarRodapeClicado();

    void onAdicionarRodapeClicado();

    void onLimparRodapeClicado();

    boolean possuiSelecaoValida();
    void onSalvarFavoritoRodapeConfirmado(String nome);
    EtapaAposta getEtapa();
    void onQuantidadeNumerosAlterada();

    default void atualizarQuantidadeTrevos(
            ParametroValorApostaDTO valor
    ) {
    }
}