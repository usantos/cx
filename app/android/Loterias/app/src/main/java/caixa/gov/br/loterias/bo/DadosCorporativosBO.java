package caixa.gov.br.loterias.bo;

import android.content.Context;

import java.util.Collection;
import java.util.LinkedHashMap;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.MunicipioDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.TermoDeUsoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.UnidadeFederacaoDTOResponse;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.bo.silce.internals.SilceSingleton;

/**
 * Created by igorvilar on 10/11/17.
 */

public class DadosCorporativosBO {

    private SilceClient silceClient;
    private Context context;


    public DadosCorporativosBO(Context context){
        this.context = context;

        setupSilceClient();
    }

    private void setupSilceClient(){
        SilceSingleton queue = new SilceSingleton(context);
        SilceConnection conn = SilceConnection.create(queue.getRequestQueue(), BuildConfig.CAIXA_BASE_URL_SILCE);
        silceClient = SilceClient.create(conn);
    }

    public void ufs(final SilceClient.Listener<UnidadeFederacaoDTOResponse> listener,
                             final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();


        silceClient.getConnection().get(SilceClient.UFS_PATH, queryParams, UnidadeFederacaoDTOResponse.class,
                silceClient.volleyListener(listener, UnidadeFederacaoDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    public void municipios(String idUf, final SilceClient.Listener<MunicipioDTOResponse> listener,
                    final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("idUF", idUf);

        silceClient.getConnection().get(SilceClient.MUNICIPIOS_PATH, queryParams, MunicipioDTOResponse.class,
                silceClient.volleyListener(listener, MunicipioDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    public void termo(final SilceClient.Listener<TermoDeUsoDTOResponse> listener,
                    final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();


        silceClient.getConnection().get(SilceClient.TERMO_PATH, queryParams, TermoDeUsoDTOResponse.class,
                silceClient.volleyListener(listener, TermoDeUsoDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    private static <T> String join(Collection<T> collection, String delimiter){
        StringBuffer result = new StringBuffer("");
        boolean first = true;

        for(T e : collection) {
            if(!first) {
                result.append(delimiter);
                first = true;
            }
            result.append(e);
        }

        return result.toString();
    }


}
