package br.gov.caixa.loterias.apostar.interno.model.bo;

import com.android.volley.NetworkResponse;

import org.androidannotations.annotations.EBean;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.BairroDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.BairroDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListDTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListSecaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.LotericaCodigoWrapperResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.LotericaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.MunicipioDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RepassometroDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.TermoDeUsoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.UnidadeFederacaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.GsonRequest;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * Created by igorvilar on 10/11/17.
 */

@EBean(scope = Singleton)
public class DadosCorporativosBO extends BaseBO{

    private static LinkedHashMap<String, String> header;

    private LinkedHashMap<String, String> getHeader(){
        if(header == null){
            this.header = new LinkedHashMap<>();
            this.header.put("Authorization", "Bearer " + keycloakBO.getAccessToken());
        }
        return header;
    }


    public void ufs(final RequestListener<UnidadeFederacaoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        silceConnection.get(ServerMethods.UFS_PATH,
                            queryParams,
                            UnidadeFederacaoDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void municipios(String idUf, final RequestListener<MunicipioDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("idUF", idUf);
        silceConnection.get(ServerMethods.MUNICIPIOS_PATH,
                            queryParams,
                            MunicipioDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void bairros(String idUf, String numeroCidade,
                        String digitoVerificador,
                        String cep,
                        final RequestListener<BairroDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(idUf)) {
            queryParams.put("idUF", idUf);
        }
        if (StringUtils.isNotEmpty(numeroCidade)) {
            queryParams.put("numero", numeroCidade);
        }
        if (StringUtils.isNotEmpty(digitoVerificador)) {
            queryParams.put("digitoVerificador", digitoVerificador);
        }
        if (StringUtils.isNotEmpty(cep)) {
            queryParams.put("cep", cep);
        }

        silceConnection.get(ServerMethods.BAIRROS_PATH,
                            queryParams,
                            BairroDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void termo(final RequestListener<TermoDeUsoDTOResponse> listener) {
        silceConnection.get(ServerMethods.TERMO_PATH,
                            new LinkedHashMap<>(),
                            TermoDeUsoDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void repasses(final RequestListener<RepassometroDTOResponse> listener) {
        silceConnection.get(ServerMethods.REPASSOMETRO_PATH,
                            new LinkedHashMap<>(),
                            RepassometroDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void parametrosSimulacao(final RequestListener<ParametrosSimulacaoResponse> listener) {
        if (keycloakBO.getAccessToken() != null){
            GsonRequest request = silceConnection.buildGetRequest(ServerMethods.PARAM_SIMULACAO_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    ParametrosSimulacaoResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
            request.setErrorListener(interceptError(request, getHeader(), listener));
            silceConnection.request(request);
        }else{
            silceConnection.get(ServerMethods.PARAM_SIMULACAO_PATH,
                                new LinkedHashMap<>(),
                                ParametrosSimulacaoResponse.class,
                                listener.getSilceListener(),
                                listener.getErrorListener(),
                                getHeader());
        }
    }

    public void validarApostaFavorita(final ApostaFavoritaDTO aposta,
                                      final RequestListener<NetworkResponse> listener) {
        GsonRequest request = silceConnection.buildPostRequest(ServerMethods.VALIDAR_FAVORITA_PATH,
                                                                new LinkedHashMap<>(),
                                                                aposta, NetworkResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void salvarApostaFavorita(final ApostaFavoritaDTO aposta,
                                     final RequestListener<NetworkResponse> listener) {
        GsonRequest request = silceConnection.buildPostRequest(ServerMethods.INCLUIR_FAVORITA_PATH,
                                                                new LinkedHashMap<>(),
                                                                aposta, NetworkResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void buscarLotericasHabilitadas(final RequestListener<ListDTOEnumIntegerResponse> listener) {
        silceConnection.get(ServerMethods.LOTERICAS_HABILITADAS_PATH,
                            new LinkedHashMap<>(),
                            ListDTOEnumIntegerResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void buscarLotericasCepOrBairroDTO(String cep,
                                              BairroDTO bairroDTO,
                                              final RequestListener<LotericaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(cep)) {
            queryParams.put("cep", cep);
            queryParams.put("pesquisaInteligente", "true");
        }
        if (bairroDTO != null) {
            queryParams.put("idBairro", bairroDTO.getId().toString());
            queryParams.put("numero", bairroDTO.getMunicipio().getId().getNumero().toString());
            queryParams.put("digitoVerificador", bairroDTO.getMunicipio().getId().getDigitoVerificador().toString());
            queryParams.put("idUF", bairroDTO.getMunicipio().getUf().getId().toString());
            queryParams.put("pesquisaInteligente", "true");
        }

        silceConnection.get(ServerMethods.LOTERICAS_PATH,
                            queryParams,
                            LotericaDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void buscarLoteriasCodigoCompleto(String codigoCompleto, final RequestListener<LotericaCodigoWrapperResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("codigoCompleto", codigoCompleto);

        silceConnection.get(ServerMethods.LOTERICAS_CODIGO_COMPLETO_PATH,
                            queryParams,
                            LotericaCodigoWrapperResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void validaTermoAceito(final RequestListener<ResourceResponse> listener) {
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.TERMO_VALIDA_ACEITO_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    ResourceResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void aceiteTermo(final RequestListener<ResourceResponse> listener) {
        GsonRequest request = silceConnection.buildPostRequest(ServerMethods.TERMO_ACEITAR_PATH,
                                                                new LinkedHashMap<>(),
                                                                null,
                                                                ResourceResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void secaoDuvidas(final RequestListener<ListSecaoDTOResponse> listener) {
        GsonRequest request = silceConnection.buildGetRequest(ServerMethods.DUVIDAS_SECOES_PATH,
                                                                new LinkedHashMap<>(),
                                                                ListSecaoDTOResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                new LinkedHashMap<>());
        request.setErrorListener(interceptError(request, new LinkedHashMap<>(), listener));
        silceConnection.request(request);
    }

    public void duvidasPerguntasRespostas(Integer secao, final RequestListener<ListSecaoDTOResponse> listener) {
        HashMap queryParams = new LinkedHashMap<String, String>();
        queryParams.put("secao", secao.toString());

        GsonRequest request = silceConnection.buildGetRequest(ServerMethods.DUVIDAS_PATH,
                                                                queryParams,
                                                                ListSecaoDTOResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }
}
