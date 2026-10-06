package br.gov.caixa.loterias.apostas.model.bean;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;

/**
 * Created by cedesbr450 on 16/04/18.
 */

public class RetornoListaCarrinho {

    private List<ApostaDTO> apostas;


    public List<ApostaDTO> getApostas() {
        return apostas;
    }

    public void setApostas(List<ApostaDTO> apostas) {
        this.apostas = apostas;
    }

}
