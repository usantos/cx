package br.gov.caixa.loterias.apostas.model.bo;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;

public interface DadosUsuarioRepository {
    void postVerificarCadastro(final List<IdentificaoDeUmaApostaDas8Modalidades> listaApostas, final RequestListener<UsuarioLogadoResponse> listener);
}
