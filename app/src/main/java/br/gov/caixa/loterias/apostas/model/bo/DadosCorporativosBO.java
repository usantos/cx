package br.gov.caixa.loterias.apostas.model.bo;

import com.android.volley.NetworkResponse;

import org.androidannotations.annotations.EBean;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListDTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaCodigoWrapperResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeiosPagamentosResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepassometroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TermoDeUsoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * Created by igorvilar on 10/11/17.
 */

@EBean(scope = Singleton)
public class DadosCorporativosBO extends BaseBO{

    public void ufs(final RequestListener<UnidadeFederacaoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        getSilceConnection().get(ServerMethods.UFS_PATH,
                            queryParams,
                            UnidadeFederacaoDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>(), listener);
    }

    public void municipios(String idUf, final RequestListener<MunicipioDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("idUF", idUf);
        getSilceConnection().get(ServerMethods.MUNICIPIOS_PATH,
                            queryParams,
                            MunicipioDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>(), listener);
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

        getSilceConnection().get(ServerMethods.BAIRROS_PATH,
                            queryParams,
                            BairroDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>(), listener);
    }

    public void termo(final RequestListener<TermoDeUsoDTOResponse> listener) {
        getSilceConnection().get(ServerMethods.TERMO_PATH,
                            new LinkedHashMap<>(),
                            TermoDeUsoDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>(), listener);
    }

    public void repasses(final RequestListener<RepassometroDTOResponse> listener) {
        getSilceConnection().get(ServerMethods.REPASSOMETRO_PATH,
                            new LinkedHashMap<>(),
                            RepassometroDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>(), listener);
    }

    public void parametrosSimulacao(final RequestListener<ParametrosSimulacaoResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();

        GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.PARAM_SIMULACAO_PATH,
                new LinkedHashMap<>(),
                ParametrosSimulacaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);
        request.setErrorListener(interceptError(request, headers, listener));
        getSilceConnection().request(request, listener);
    }

    public void validarApostaFavorita(final ApostaFavoritaDTO aposta,
                                      final RequestListener<NetworkResponse> listener) {
        GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.VALIDAR_FAVORITA_PATH,
                                                                new LinkedHashMap<>(),
                                                                aposta, NetworkResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void salvarApostaFavorita(final ApostaFavoritaDTO aposta,
                                     final RequestListener<NetworkResponse> listener) {
        GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.INCLUIR_FAVORITA_PATH,
                                                                new LinkedHashMap<>(),
                                                                aposta, NetworkResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void buscarLotericasHabilitadas(final RequestListener<ListDTOEnumIntegerResponse> listener) {
        getSilceConnection().get(ServerMethods.LOTERICAS_HABILITADAS_PATH,
                            new LinkedHashMap<>(),
                            ListDTOEnumIntegerResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>(), listener);
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

        getSilceConnection().get(ServerMethods.LOTERICAS_PATH,
                            queryParams,
                            LotericaDTOResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>(), listener);
    }

    public void buscarLoteriasCodigoCompleto(String codigoCompleto, final RequestListener<LotericaCodigoWrapperResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("codigoCompleto", codigoCompleto);

        getSilceConnection().get(ServerMethods.LOTERICAS_CODIGO_COMPLETO_PATH,
                            queryParams,
                            LotericaCodigoWrapperResponse.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>(), listener);
    }

    public void buscarLoteriasNomeFantasia(String nomeFantasia, final RequestListener<LotericaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("nomeFantasia", nomeFantasia);

        GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.LOTERICAS_NOME_FANTASIA_PATH,
                queryParams,
                LotericaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void buscarLoteriasNomeCodigo(String nomeCodigo, final RequestListener<LotericaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("nomeCodigo", nomeCodigo);

        GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.LOTERICAS_NOME_LOTERICO_PATH,
                queryParams,
                LotericaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void validaTermoAceito(final RequestListener<ResourceResponse> listener) {
        final GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.TERMO_VALIDA_ACEITO_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    ResourceResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void aceiteTermo(final RequestListener<ResourceResponse> listener) {
        GsonRequest request = getSilceConnection().buildPostRequest(ServerMethods.TERMO_ACEITAR_PATH,
                                                                new LinkedHashMap<>(),
                                                                null,
                                                                ResourceResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void secaoDuvidas(final RequestListener<ListSecaoDTOResponse> listener) {
        GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.DUVIDAS_SECOES_PATH,
                                                                new LinkedHashMap<>(),
                                                                ListSecaoDTOResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                new LinkedHashMap<>());
        request.setErrorListener(interceptError(request, new LinkedHashMap<>(), listener));
        getSilceConnection().request(request, listener);
    }

    public void duvidasPerguntasRespostas(Integer secao, final RequestListener<ListSecaoDTOResponse> listener) {
        HashMap queryParams = new LinkedHashMap<String, String>();
        queryParams.put("secao", secao.toString());

        GsonRequest request = getSilceConnection().buildGetRequest(ServerMethods.DUVIDAS_PATH,
                                                                queryParams,
                                                                ListSecaoDTOResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void buscaKeyMercadoPago(Long id, final RequestListener<MeioPagamentoResponse> listener){
        String url = ServerMethods.MEIOS_PAGAMENTOS_ID.replace("{id}", String.valueOf(id));
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                                                                  new HashMap<>(),
                                                                  MeioPagamentoResponse.class,
                                                                  listener.getSilceListener(),
                                                                  listener.getErrorListener(),
                                                                  getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }

    public void buscaMeiosPagamentos(final RequestListener<MeiosPagamentosResponse> listener){
        String url = ServerMethods.MEIOS_PAGAMENTOS;
        final GsonRequest request = getSilceConnection().buildGetRequest(url,
                                                                    new HashMap<>(),
                                                                    MeiosPagamentosResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        getSilceConnection().request(request, listener);
    }
}
