package caixa.gov.br.loterias.bo;

import android.content.Context;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.bo.silce.internals.SilceSingleton;

public class ApostaBO{

    private SilceClient silceClient;
    private Context context;


    public ApostaBO(Context context){
       this.context = context;

        setupSilceClient();
    }

    private void setupSilceClient(){
        SilceSingleton queue = new SilceSingleton(context);
        SilceConnection conn = SilceConnection.create(queue.getRequestQueue(), BuildConfig.CAIXA_BASE_URL_SILCE);
        silceClient = SilceClient.create(conn);
    }

    public void completaJogo(final int quantidadeTotalNumeros,
                             final List<Integer> numerosMarcados,
                             final int menorPrognostico,
                             final int maiorPrognostico,
                             final SilceClient.Listener<CompletaJogoRsp> listener,
                             final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("quantidadeTotalNumeros", String.valueOf(quantidadeTotalNumeros));
        queryParams.put("numerosMarcados", join(numerosMarcados, ","));
        queryParams.put("menorPrognostico", String.valueOf(menorPrognostico));
        queryParams.put("maiorPrognostico", String.valueOf(maiorPrognostico));

        silceClient.getConnection().get(SilceClient.COMPLETA_JOGO_PATH, queryParams, CompletaJogoRsp.class,
                silceClient.volleyListener(listener, CompletaJogoRsp.class),
                silceClient.volleyErrorListener(errorListener));
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
