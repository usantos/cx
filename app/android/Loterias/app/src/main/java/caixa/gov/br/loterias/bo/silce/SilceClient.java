package caixa.gov.br.loterias.bo.silce;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.LinkedHashMap;

import caixa.gov.br.loterias.bo.silce.dto.AutenticacaoDTO;
import caixa.gov.br.loterias.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;

public class SilceClient {
    public static final String COMPLETA_JOGO_PATH = "/apostas/completar-jogo";
    public static final String VALIDAR_FAVORITA_PATH = "/apostas-favoritas/validar";
    public static final String INCLUIR_FAVORITA_PATH = "/apostas-favoritas";

    public static final String INCLUIR_APOSTA_CARRINHO_PATH = "/carrinhos/incluir-aposta";
    public static final String INCLUIR_SURPRESINHA_CARRINHO_PATH = "/carrinhos/incluir-surpresinha";
    public static final String BUSCA_CARRINHO_PATH = "/carrinhos";
    public static final String DELETE_APOSTA_CARRINHO_PATH = "carrinhos/apostas/";

    public static final String INCLUIR_APOSTAS_CARRINHO_PATH = "/carrinhos/incluir-apostas";

    public static final String UFS_PATH = "/ufs";
    public static final String TERMO_PATH = "/termos-de-uso/termo-vigente";
    public static final String MUNICIPIOS_PATH = "/municipios";
    public static final String REPASSOMETRO_PATH = "/repassometro";
    public static final String PARAM_SIMULACAO_PATH = "/parametros-simulacao";
    public static final String BAIRROS_PATH = "/bairros";

    public static final String LOGIN_PATH = "/usuarios/autenticar";
    public static final String LIST_CARTOES_PATH = "/usuarios/cartoes";
    public static final String SAIR_PATH = "/usuarios/sair";
    public static final String GRAVAR_USUARIO_PATH = "/usuarios";

    public static final String LOTERICAS_PATH = "/lotericas";
    public static final String LOTERICAS_HABILITADAS_PATH = "/lotericas/buscas-habilitadas";
    public static final String LOTERICAS_CODIGO_COMPLETO_PATH = "/lotericas/codigo-completo";
    public static final String GERAR_APOSTAS_RAPIDAO_PATH = "/rapidoes/gerar-apostas";
    public static final String RAPIDOES_PATH = "/rapidoes";
    public static final String MODALIDADES_PATH = "/modalidades";

    public static final String EQUIPES_ESPORTIVAS_PATH = "/equipes-esportivas/{id}/escudo";

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
