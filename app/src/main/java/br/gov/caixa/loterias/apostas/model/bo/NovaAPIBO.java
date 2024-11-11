package br.gov.caixa.loterias.apostas.model.bo;

import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnCertificadoListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoNovaAPISingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NovaAPISingleton;

public class NovaAPIBO extends BaseBO {
    private static NovaAPIBO instance;
    private NovaAPIBO(){
        super(NovaAPISingleton.getInstance().getRequestQueue(),
                "",
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
        return () -> {
            if (CertificadoNovaAPISingleton.getInstance().trocaCertificado()){
                NovaAPISingleton.getInstance().initQueue();
                return NovaAPISingleton.getInstance().getRequestQueue();
            }

            return null;
        };
    }

    public void getApostasConfirmadas(ApostaPageRequest page, RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        final Map<String, String> queryParams = getQueryParams(page);

        final GsonRequest request = getServiceConnection().buildGetRequest("",
                                                                    queryParams,
                                                                    ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
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
        if(page.getSituacao() != 0){
            queryParams.put("situacao", String.valueOf(page.getSituacao()));
        } else {
            queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));
        }
        if(page.getModalidade() != 0){
            queryParams.put("modalidade", String.valueOf(page.getModalidade()));
        }

        if(page.getOrdenarPor() != 0) {
            queryParams.put("ordenacao", String.valueOf(page.getOrdenarPor()));
        }
        //queryParams.put("situacao", String.valueOf(page.getSituacao()));

        //        queryParams.put("idsApostas", idsApostas);
        return queryParams;
    }
}