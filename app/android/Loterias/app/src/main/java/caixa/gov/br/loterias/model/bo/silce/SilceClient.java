package caixa.gov.br.loterias.model.bo.silce;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import caixa.gov.br.loterias.model.bo.silce.internals.SilceConnection;

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
    public static final String TERMO_VALIDA_ACEITO_PATH = "/termos-de-uso/valida-termo-aceito";
    public static final String TERMO_ACEITAR_PATH = "/termos-de-uso/aceitar-termo-vigente";
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

    public static final String APOSTAS_FAVORITAS_PATH = "/apostas-favoritas/";
    public static final String APOSTAS_FAVORITAS_LISTA_UNICA_PATH = "/apostas-favoritas/lista-unica";

    public static final String APOSTAS_MES_ANO_PATH = "/apostas/meses";
    public static final String APOSTAS_CONFIRMADAS_PATH = "/apostas";
    public static final String APOSTAS_HISTORICO_MES_ANO_PATH = "/historico-apostas/meses";
    public static final String APOSTAS_CONFIRMADAS_HISTORICO_PATH = "/historico-apostas";

    public static final String EQUIPES_ESPORTIVAS_ESCUDO_PATH = "/equipes-esportivas/{id}/escudo";
    public static final String BILHETES_CONFERIR_BILHETE_PATH = "/bilhetes/conferir-bilhete";

    public static final String COMPRAS_PATH = "/compras";
    public static final String COMPRAS_MESES_PATH = "/compras/meses";
    public static final String DETALHES_COMPRAS_PATH = "/compras/{id}/apostas";

    public static final String DUVIDAS_SECOES_PATH = "/duvidas/secoes";
    public static final String DUVIDAS_PATH = "/duvidas";

    public static final String EQUIPES_ESPORTIVAS_POR_MODALIDADE_EQUIPE_PATH = "/equipes-esportivas/{idEquipe}/{idModalidade}/escudo";

    public interface Listener<T> {
        void onResponse(T response);
    }

    public interface ErrorListener {
        void onErrorResponse(VolleyError error);
    }

    private SilceConnection conn;

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
