package br.gov.caixa.loterias.apostas.model.bo;

import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnCertificadoListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoNovaAPISingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NovaAPISingleton;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;

public class NovaAPIBO extends BaseBO {
    private static NovaAPIBO instance;
    private NovaAPIBO(){
        super(NovaAPISingleton.getInstance().getRequestQueue(),
                SharedPreferencesUtils.getValorString(ConfiguracoesEnum.URL_BASE_BUSCA_APOSTAS.get(), ConfiguracoesDefaultEnum.URL_BASE_BUSCA_APOSTAS.asString()),
                true,
                onCertificadoListener());
    }

    public static NovaAPIBO getInstance(){
        if (instance == null){
            instance = new NovaAPIBO();
        }
        return instance;
    }

    private static OnCertificadoListener onCertificadoListener() {
        return (String urlBase) -> {
            if (CertificadoNovaAPISingleton.getInstance().trocaCertificado()){
                NovaAPISingleton.getInstance().initQueue();
                return NovaAPISingleton.getInstance().getRequestQueue();
            }

            return null;
        };
    }

    public void getApostasConfirmadas(ApostaPageRequest page, RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        final Map<String, String> queryParams = getQueryParams(page);

        final GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_NOVA_API_PATH,
                                                                    queryParams,
                                                                    ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getRecuperarAposta(Long id, final RequestListener<ApostaDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_RECUPERAR_NOVA_API_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = getServiceConnection().buildGetRequest(url,
                queryParams,
                ApostaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    private Map<String, String> getQueryParams(ApostaPageRequest page) {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("mes", String.valueOf(page.getMes()));
        if(page.getAno() > 0){
            queryParams.put("ano", String.valueOf(page.getAno()));
        }
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));
//        if(page.getSituacao() != 0){
//            queryParams.put("situacao", String.valueOf(page.getSituacao()));
//        } else {
//            queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));
//        }
        if(page.getSituacao() != 0) {
            if (page.getSituacao() == 1) {
                queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));
            } else if (page.getSituacao() == 2) {
                queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.PAGAS));
            } else if (page.getSituacao() == 3) {
                queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.PRESCRITAS));
            }
        }
        if(page.getModalidade() != 0){
            queryParams.put("modalidade", String.valueOf(page.getModalidade()));
        }

        if(page.getOrdenarPor() != 0) {
            queryParams.put("ordenacao", String.valueOf(page.getOrdenarPor()));
        }
        if(page.getTipoAposta() != 0) {
            queryParams.put("tipoAposta", String.valueOf(page.getTipoAposta()));
        }

        if(page.isSurpresinha() != 0){
            queryParams.put("surpresinha", String.valueOf(page.isSurpresinha()));
        }

        if(page.isTeimosinha() != 0){
            queryParams.put("teimosinha", String.valueOf(page.isTeimosinha()));
        }

        if(page.isCombo() != 0){
            queryParams.put("comboApostas", String.valueOf(page.isCombo()));
        }

        if(page.getTipoConcurso() != 0) {
            queryParams.put("tipoConcurso", String.valueOf(page.getTipoConcurso()));
        }
        return queryParams;
    }
}