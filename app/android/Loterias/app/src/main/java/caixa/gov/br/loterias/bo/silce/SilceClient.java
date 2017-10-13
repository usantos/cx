package caixa.gov.br.loterias.bo.silce;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Joiner;

import java.util.LinkedHashMap;
import java.util.List;

import caixa.gov.br.loterias.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;

public class SilceClient {
    private static final String COMPLETA_JOGO_PATH = "/apostas/completar-jogo";

    public interface Listener<T> {
        /** Called when a response is received. */
        public void onResponse(T response);
    }

    /** Callback interface for delivering error responses. */
    public interface ErrorListener {
        /**
         * Callback method that an error has been occurred with the
         * provided error code and optional user-readable message.
         */
        // This abstraction is still leaking. VoleyError must be
        // replaced by a class which does not depend on Volley
        public void onErrorResponse(VolleyError error);
    }

    SilceConnection conn;

    protected SilceClient(SilceConnection conn) {
        this.conn = conn;
    }

    public void completaJogo(final int quantidadeTotalNumeros,
                             final List<Integer> numerosMarcados,
                             final int menorPrognostico,
                             final int maiorPrognostico,
                             final Listener<CompletaJogoRsp> listener,
                             final ErrorListener errorListener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("quantidadeTotalNumeros", String.valueOf(quantidadeTotalNumeros));
        queryParams.put("numerosMarcados", Joiner.on(",").join(numerosMarcados));
        queryParams.put("menorPrognostico", String.valueOf(menorPrognostico));
        queryParams.put("maiorPrognostico", String.valueOf(maiorPrognostico));

        conn.get(COMPLETA_JOGO_PATH, queryParams, CompletaJogoRsp.class,
                volleyListener(listener, CompletaJogoRsp.class),
                volleyErrorListener(errorListener, CompletaJogoRsp.class));
    }

    <T> Response.Listener<T> volleyListener(final Listener<T> listener, final Class<T> klass) {
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                listener.onResponse(response);
            }
        };
    }

    public SilceConnection getConnection() {
        return conn;
    }

    Response.ErrorListener volleyErrorListener(final ErrorListener errorListener,
                                                      final Class klass) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
            }
        };
    }

    public static SilceClient create(SilceConnection conn) {
        return new SilceClient(conn);
    }
}
