package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;

public interface DadosCorporativos {
    void parametrosSimulacao(final RequestListener<ParametrosSimulacaoResponse> listener);
}
