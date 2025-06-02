package br.gov.caixa.loterias.apostas.model.bo;

import com.android.volley.NetworkResponse;

import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

/**
 * Created by igorvilar on 10/11/17.
 */

public class DadosCorporativosNuvemBO extends NuvemBO implements DadosCorporativoService {
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

    @Override
    public void validarApostaFavorita(final ApostaFavoritaDTO aposta,
                                      final RequestListener<NetworkResponse> listener) {
        GsonRequest request = getServiceConnection().buildPostRequestNuvem(ServerMethodsNuvem.VALIDAR_FAVORITA_NUVEM_PATH,
                new LinkedHashMap<>(),
                aposta, NetworkResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    @Override
    public void salvarApostaFavorita(final ApostaFavoritaDTO aposta,
                                     final RequestListener<NetworkResponse> listener) {
        GsonRequest request = getServiceConnection().buildPostRequestNuvem(ServerMethodsNuvem.INCLUIR_FAVORITA_NUVEM_PATH,
                new LinkedHashMap<>(),
                aposta, NetworkResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());
        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }
}
