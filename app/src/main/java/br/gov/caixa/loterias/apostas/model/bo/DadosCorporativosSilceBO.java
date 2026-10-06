package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

/**
 * Created by igorvilar on 10/11/17.
 */

public class DadosCorporativosSilceBO extends SilceBO {
    private static DadosCorporativosSilceBO instance;

    private DadosCorporativosSilceBO(){
        super();
    }

    public static DadosCorporativosSilceBO getInstance(){
        if (instance == null){
            instance = new DadosCorporativosSilceBO();
        }
        return instance;
    }

    public void ufs(final RequestListener<UnidadeFederacaoDTOResponse> listener) {
        LinkedHashMap<String, String> header = getHeader();
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.UFS_PATH,
                queryParams,
                UnidadeFederacaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                header);
        request.setErrorListener(interceptError(request, header, listener));
        getServiceConnection().request(request, listener);
    }

    public void municipios(String idUf, final RequestListener<MunicipioDTOResponse> listener) {
        LinkedHashMap<String, String> header = getHeader();
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("idUF", idUf);
        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.MUNICIPIOS_PATH,
                queryParams,
                MunicipioDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                header);
        request.setErrorListener(interceptError(request, header, listener));
        getServiceConnection().request(request, listener);
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
        LinkedHashMap<String, String> header = getHeader();
        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.BAIRROS_PATH,
                queryParams,
                BairroDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                header);
        request.setErrorListener(interceptError(request, header, listener));
        getServiceConnection().request(request, listener);
    }

    public void termo(final RequestListener<TermoDeUsoDTOResponse> listener) {
        LinkedHashMap<String, String> header = getHeader();
        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.TERMO_PATH,
                new LinkedHashMap<>(),
                TermoDeUsoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                header);
        request.setErrorListener(interceptError(request, header, listener));
        getServiceConnection().request(request, listener);
    }

    public void repasses(final RequestListener<RepassometroDTOResponse> listener) {
        LinkedHashMap<String, String> header = getHeader();
        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.REPASSOMETRO_PATH,
                new LinkedHashMap<>(),
                RepassometroDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                header);
        request.setErrorListener(interceptError(request, header, listener));
        getServiceConnection().request(request, listener);
    }

    public void buscarLoteriasNomeCodigo(String nomeCodigo, final RequestListener<LotericaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("nomeCodigo", nomeCodigo);

        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.LOTERICAS_NOME_LOTERICO_PATH,
                queryParams,
                LotericaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void validaTermoAceito(final RequestListener<ResourceResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.TERMO_VALIDA_ACEITO_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    ResourceResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void aceiteTermo(final RequestListener<ResourceResponse> listener) {
        GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.TERMO_ACEITAR_PATH,
                                                                new LinkedHashMap<>(),
                                                                null,
                                                                ResourceResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void secaoDuvidas(final RequestListener<ListSecaoDTOResponse> listener) {
        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.DUVIDAS_SECOES_PATH,
                                                                new LinkedHashMap<>(),
                                                                ListSecaoDTOResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                new LinkedHashMap<>());
        request.setErrorListener(interceptError(request, new LinkedHashMap<>(), listener));
        getServiceConnection().request(request, listener);
    }

    public void duvidasPerguntasRespostas(Integer secao, final RequestListener<ListSecaoDTOResponse> listener) {
        HashMap queryParams = new LinkedHashMap<String, String>();
        queryParams.put("secao", secao.toString());

        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.DUVIDAS_PATH,
                                                                queryParams,
                                                                ListSecaoDTOResponse.class,
                                                                listener.getSilceListener(),
                                                                listener.getErrorListener(),
                                                                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void buscaKeyMercadoPago(Long id, final RequestListener<MeioPagamentoResponse> listener){
        String url = ServerMethods.MEIOS_PAGAMENTOS_ID.replace("{id}", String.valueOf(id));
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                                                                  new HashMap<>(),
                                                                  MeioPagamentoResponse.class,
                                                                  listener.getSilceListener(),
                                                                  listener.getErrorListener(),
                                                                  getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void buscaMeiosPagamentos(final RequestListener<MeiosPagamentosResponse> listener){
        String url = ServerMethods.MEIOS_PAGAMENTOS;
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                                                                    new HashMap<>(),
                                                                    MeiosPagamentosResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void validaRepresa(final RequestListener<RepresaResponse> listener){
        String url = ServerMethods.VALIDA_REPRESA;
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                new HashMap<>(),
                RepresaResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }
}
