package br.gov.caixa.loterias.apostas.model.bo;

import com.android.volley.NetworkResponse;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;

public interface DadosCorporativoRepository {
    void parametrosSimulacao(final RequestListener<ParametrosSimulacaoResponse> listener);
    void validarApostaFavorita(final ApostaFavoritaDTO aposta,final RequestListener<NetworkResponse> listener);
    void salvarApostaFavorita(final ApostaFavoritaDTO aposta,final RequestListener<NetworkResponse> listener);
}
