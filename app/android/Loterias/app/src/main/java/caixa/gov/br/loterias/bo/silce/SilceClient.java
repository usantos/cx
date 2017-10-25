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
    public static final String COMPLETA_JOGO_PATH = "/apostas/completar-jogo";
    public static final String LOGIN_PATH = "/usuarios/autenticar";

    public interface Listener<T> {
        public void onResponse(T response);
    }

    public interface ErrorListener {
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

    public <T> Response.Listener<T> volleyListener(final Listener<T> listener, final Class<T> klass) {
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                listener.onResponse(response);
            }
        };
    }

    public Response.ErrorListener volleyErrorListener(final ErrorListener errorListener) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
            }
        };
    }

    public SilceConnection getConnection() {
        return conn;
    }

    public static SilceClient create(SilceConnection conn) {
        return new SilceClient(conn);
    }
}
