package br.gov.caixa.loterias.apostas.model.bo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.MicroServicoConnection;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.MicroServicoSingleton;

public class ApostaMicroServicoBO extends BaseMicroServicoBO {
    private static ApostaMicroServicoBO instance;

    private ApostaMicroServicoBO(){}

    public static ApostaMicroServicoBO getInstance(){
        if (instance == null){
            instance = new ApostaMicroServicoBO();
        }
        return instance;
    }

    private static LinkedHashMap<String, String> header;

    private LinkedHashMap<String, String> getHeader(){
        if(header == null){
            this.header = new LinkedHashMap<>();
        }
        this.header.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        return header;
    }

    public void getApostasConfirmadasMicroServico(String urlBase, ApostaPageRequest page, RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        setConnection(MicroServicoConnection.create(MicroServicoSingleton.getInstance().getRequestQueue(), urlBase));

        final Map<String, String> queryParams = getQueryParams(page);

        final GsonRequest request = getConnection().buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_PATH,
                                                                    queryParams,
                                                                    ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        getConnection().request(request, listener);
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
        if(page.getTipoAposta() != 0) {
            queryParams.put("tipoAposta", String.valueOf(page.getTipoAposta()));
        }
        //queryParams.put("situacao", String.valueOf(page.getSituacao()));

        //        queryParams.put("idsApostas", idsApostas);
        return queryParams;
    }
}