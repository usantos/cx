package caixa.gov.br.loterias.model.bo;

import com.android.volley.NetworkResponse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.silce.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.RequestListenerHelper;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.BairroDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.BairroDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ListDTOEnumIntegerResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ListSecaoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.LotericaCodigoWrapperResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.LotericaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.MunicipioDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametrosSimulacaoResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.RepassometroDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResourceResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.TermoDeUsoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.UnidadeFederacaoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.model.bo.silce.internals.SilceSingleton;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * Created by igorvilar on 10/11/17.
 */

@EBean(scope = Singleton)
public class DadosCorporativosBO extends BaseBO{

    public void ufs(final RequestListener<UnidadeFederacaoDTOResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        RequestListenerHelper<UnidadeFederacaoDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.UFS_PATH, queryParams, UnidadeFederacaoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), UnidadeFederacaoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());
    }

    public void municipios(String idUf, final RequestListener<MunicipioDTOResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("idUF", idUf);

        RequestListenerHelper<MunicipioDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.MUNICIPIOS_PATH, queryParams, MunicipioDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), MunicipioDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());
    }

    public void bairros(String idUf, String numeroCidade, String digitoVerificador, String cep, final RequestListener<BairroDTOResponse> listener) {

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

        RequestListenerHelper<BairroDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.BAIRROS_PATH, queryParams, BairroDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), BairroDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());
    }

    public void termo(final RequestListener<TermoDeUsoDTOResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        RequestListenerHelper<TermoDeUsoDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.TERMO_PATH, queryParams, TermoDeUsoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), TermoDeUsoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());
    }

    public void repasses(final RequestListener<RepassometroDTOResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

        RequestListenerHelper<RepassometroDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.REPASSOMETRO_PATH, queryParams, RepassometroDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), RepassometroDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);
    }

    public void parametrosSimulacao(final RequestListener<ParametrosSimulacaoResponse> listener) {
        LinkedHashMap<String, String> headers;
        if (keycloakBO.getAccessToken() != null){
            headers = new LinkedHashMap<String, String>();
            headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());
        }else{
            headers = new LinkedHashMap<String, String>();
        }
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        RequestListenerHelper<ParametrosSimulacaoResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.PARAM_SIMULACAO_PATH, queryParams, ParametrosSimulacaoResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ParametrosSimulacaoResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);
    }

    public void validarApostaFavorita(final ApostaFavoritaDTO aposta,
                                      final RequestListener<NetworkResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<NetworkResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().post(SilceClient.VALIDAR_FAVORITA_PATH, new LinkedHashMap<String, String>(),
                aposta, NetworkResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), NetworkResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);
    }

    public void salvarApostaFavorita(final ApostaFavoritaDTO aposta,
                                     final RequestListener<NetworkResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<NetworkResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().post(SilceClient.INCLUIR_FAVORITA_PATH, new LinkedHashMap<String, String>(),
                aposta, NetworkResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), NetworkResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);
    }

    public void buscarLotericasHabilitadas(final RequestListener<ListDTOEnumIntegerResponse> listener) {

        RequestListenerHelper<ListDTOEnumIntegerResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.LOTERICAS_HABILITADAS_PATH, new LinkedHashMap<String, String>(), ListDTOEnumIntegerResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ListDTOEnumIntegerResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());
    }

    public void buscarLotericasCepOrBairroDTO(String cep, BairroDTO bairroDTO, final RequestListener<LotericaDTOResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(cep)) {
            queryParams.put("cep", cep);
            queryParams.put("pesquisaInteligente", "true");
        }
        if (bairroDTO != null) {
            queryParams.put("idBairro", bairroDTO.getId().toString());
            //queryParams.put("codigoUF", bairroDTO.getMunicipio().getUf().ge);
            queryParams.put("numero", bairroDTO.getMunicipio().getId().getNumero().toString());
            queryParams.put("digitoVerificador", bairroDTO.getMunicipio().getId().getDigitoVerificador().toString());
            queryParams.put("idUF", bairroDTO.getMunicipio().getUf().getId().toString());
            queryParams.put("pesquisaInteligente", "true");
        }

        RequestListenerHelper<LotericaDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.LOTERICAS_PATH, queryParams, LotericaDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), LotericaDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());
    }

    public void buscarLoteriasCodigoCompleto(String codigoCompleto, final RequestListener<LotericaCodigoWrapperResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("codigoCompleto", codigoCompleto);

        RequestListenerHelper<LotericaCodigoWrapperResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.LOTERICAS_CODIGO_COMPLETO_PATH, queryParams, LotericaCodigoWrapperResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), LotericaCodigoWrapperResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());
    }

    public void validaTermoAceito(final RequestListener<ResourceResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<ResourceResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.TERMO_VALIDA_ACEITO_PATH, new LinkedHashMap<String, String>(), ResourceResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResourceResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);

    }

    public void aceiteTermo(final RequestListener<ResourceResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<ResourceResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().post(SilceClient.TERMO_ACEITAR_PATH, new LinkedHashMap<String, String>(), null, ResourceResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ResourceResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);
    }

    public void secaoDuvidas(final RequestListener<ListSecaoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<ListSecaoDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.DUVIDAS_SECOES_PATH, new LinkedHashMap<String, String>(), ListSecaoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ListSecaoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);
    }

    public void duvidasPerguntasRespostas(Integer secao, final RequestListener<ListSecaoDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        HashMap queryParams = new LinkedHashMap<String, String>();
        queryParams.put("secao", secao.toString());

        RequestListenerHelper<ListSecaoDTOResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().get(SilceClient.DUVIDAS_PATH, queryParams, ListSecaoDTOResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), ListSecaoDTOResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), headers);
    }

    private static <T> String join(Collection<T> collection, String delimiter) {
        StringBuffer result = new StringBuffer("");
        boolean first = true;

        for (T e : collection) {
            if (!first) {
                result.append(delimiter);
                first = true;
            }
            result.append(e);
        }

        return result.toString();
    }


}
