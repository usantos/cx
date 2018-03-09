package caixa.gov.br.loterias.bo;

import android.content.Context;

import com.android.volley.NetworkResponse;

import java.util.LinkedHashMap;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.data.UsuarioLogadoSingleton;
import caixa.gov.br.loterias.bo.silce.dto.ApostadorDTO;
import caixa.gov.br.loterias.bo.silce.dto.AutenticacaoDTO;
import caixa.gov.br.loterias.bo.silce.dto.ListCartaoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.bo.silce.internals.SilceSingleton;

/**
 * Created by cedesbr450 on 07/12/17.
 */

public class DadosUsuarioBO {

    private SilceClient silceClient;
    private Context context;


    public DadosUsuarioBO(Context context) {
        this.context = context;

        setupSilceClient();
    }

    private void setupSilceClient() {
        SilceSingleton queue = new SilceSingleton(context);
        SilceConnection conn = SilceConnection.create(queue.getRequestQueue(), BuildConfig.CAIXA_BASE_URL_SILCE);
        silceClient = SilceClient.create(conn);
    }

    public void cartoes(final SilceClient.Listener<ListCartaoDTOResponse> listener,
                        final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("meioPagamento", "1");

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().get(SilceClient.LIST_CARTOES_PATH, queryParams, ListCartaoDTOResponse.class,
                silceClient.volleyListener(listener, ListCartaoDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), headers);
    }

    public void autenticar(final String email, final String senha,
                           final SilceClient.Listener<UsuarioLogadoResponse> listener,
                           final SilceClient.ErrorListener errorListener) {

        AutenticacaoDTO autenticacaoDTO = new AutenticacaoDTO();
        autenticacaoDTO.setEmail(email);
        autenticacaoDTO.setSenha(senha);

        silceClient.getConnection().post(SilceClient.LOGIN_PATH, new LinkedHashMap<String, String>(),
                autenticacaoDTO, UsuarioLogadoResponse.class,
                silceClient.volleyListener(listener, UsuarioLogadoResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    public void sair(final SilceClient.Listener<NetworkResponse> listener,
                     final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);
;
        silceClient.getConnection().post(SilceClient.SAIR_PATH, queryParams,
                "", NetworkResponse.class, silceClient.volleyListener(listener,
                        NetworkResponse.class),
                silceClient.volleyErrorListener(errorListener),
                headers);
    }

    public void gravarUsuario(ApostadorDTO apostadorDTO, final SilceClient.Listener<UsuarioLogadoResponse> listener,
                              final SilceClient.ErrorListener errorListener) {

        silceClient.getConnection().post(SilceClient.GRAVAR_USUARIO_PATH, new LinkedHashMap<String, String>(), apostadorDTO, UsuarioLogadoResponse.class,
                silceClient.volleyListener(listener, UsuarioLogadoResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());

    }

}
