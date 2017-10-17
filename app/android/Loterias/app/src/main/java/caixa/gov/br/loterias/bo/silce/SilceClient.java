package caixa.gov.br.loterias.bo.silce;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Joiner;

import java.util.LinkedHashMap;
import java.util.List;

import caixa.gov.br.loterias.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.bo.silce.data.LoginReq;
import caixa.gov.br.loterias.bo.silce.dto.AutenticacaoDTO;
import caixa.gov.br.loterias.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;

public class SilceClient {
    private static final String COMPLETA_JOGO_PATH = "/apostas/completar-jogo";
    private static final String LOGIN_PATH = "/usuarios/autenticar";

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

    public void autenticar(final String email, final String senha,
                           final Listener<UsuarioLogadoResponse> listener,
                           final ErrorListener errorListener) {
        AutenticacaoDTO autenticacaoDTO = new AutenticacaoDTO();
        autenticacaoDTO.setEmail(email);
        autenticacaoDTO.setSenha(senha);
        /*
        LoginReq req = new LoginReq();
        req.setEmail(email);
        req.setSenha(senha);
        */
        conn.post(LOGIN_PATH, new LinkedHashMap<String, String>(),
                autenticacaoDTO, UsuarioLogadoResponse.class,
                volleyListener(listener, UsuarioLogadoResponse.class),
                volleyErrorListener(errorListener));
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
                volleyErrorListener(errorListener));
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

    Response.ErrorListener volleyErrorListener(final ErrorListener errorListener) {
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
