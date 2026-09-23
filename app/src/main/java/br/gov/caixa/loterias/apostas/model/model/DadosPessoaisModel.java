package br.gov.caixa.loterias.apostas.model.model;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse;

/**
 * Camada de acesso a dados da tela de dados pessoais.

 * Encapsula DadosUsuarioBO e DadosCorporativosSilceBO para que
 * o ViewModel nao dependa diretamente dos BOs nem de Volley.
 */
public class DadosPessoaisModel {

    public interface Callback<T> {

        void onSuccess(T response);

        void onError(VolleyError error);
    }

    public void carregarDadosUsuario(Callback<ApostadorDTOResponse> callback) {
        DadosUsuarioBO.getInstance().getDadosUsuario(criarListener(callback));
    }

    public void carregarUfs(Callback<UnidadeFederacaoDTOResponse> callback) {
        DadosCorporativosSilceBO.getInstance().ufs(criarListener(callback));
    }

    public void carregarMunicipios(
            String idUf,
            Callback<MunicipioDTOResponse> callback
    ) {
        DadosCorporativosSilceBO.getInstance().municipios(
                idUf,
                criarListener(callback)
        );
    }

    public void buscarBairroPorCep(
            String cep,
            Callback<BairroDTOResponse> callback
    ) {
        DadosCorporativosSilceBO.getInstance().bairros(
                null,
                null,
                null,
                cep,
                criarListener(callback)
        );
    }

    public void recalcularLimite(Callback<ApostadorDTOResponse> callback) {
        DadosUsuarioBO.getInstance().recalcularLimite(criarListener(callback));
    }

    public void atualizarDadosUsuario(
            ApostadorAlteracaoDTO apostadorAlteracaoDTO,
            Callback<CadastrarApostadorDTOResponse> callback
    ) {
        DadosUsuarioBO.getInstance().putDadosUsuario(
                apostadorAlteracaoDTO,
                criarListener(callback)
        );
    }

    private <T> RequestListener<T> criarListener(Callback<T> callback) {
        return new RequestListener<>() {

            @Override
            public void onResponse(T response) {
                callback.onSuccess(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        };
    }
}
