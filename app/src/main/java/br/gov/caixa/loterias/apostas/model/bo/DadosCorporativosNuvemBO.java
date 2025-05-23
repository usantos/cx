package br.gov.caixa.loterias.apostas.model.bo;

import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

/**
 * Created by igorvilar on 10/11/17.
 */

public class DadosCorporativosNuvemBO extends NuvemBO implements DadosCorporativos {
    private static DadosCorporativosNuvemBO instance;

    private DadosCorporativosNuvemBO(){
        super();
    }

    public static DadosCorporativosNuvemBO getInstance(){
        if (instance == null){
            instance = new DadosCorporativosNuvemBO();
        }
        return instance;
    }

    @Override
    public void parametrosSimulacao(final RequestListener<ParametrosSimulacaoResponse> listener) {
        LinkedHashMap<String, String> header = getHeader();
        GsonRequest request = getServiceConnection().buildGetRequestNuvem(ServerMethodsNuvem.PARAM_SIMULACAO_NUVEM_PATH,
                new LinkedHashMap<>(),
                ParametrosSimulacaoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                header);
        request.setErrorListener(interceptError(request, header, listener));
        getServiceConnection().request(request, listener);
    }

}
