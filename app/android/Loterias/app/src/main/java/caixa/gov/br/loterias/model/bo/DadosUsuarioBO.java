package caixa.gov.br.loterias.model.bo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.NetworkResponse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.LinkedHashMap;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.silce.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.RequestListenerHelper;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostadorDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.AutenticacaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ListCartaoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.model.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.model.bo.silce.internals.SilceSingleton;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * Created by cedesbr450 on 07/12/17.
 */

@EBean(scope = Singleton)
public class DadosUsuarioBO extends BaseBO {

    public void cartoes(final SilceClient.Listener<ListCartaoDTOResponse> listener,
                        final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("meioPagamento", "1");

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

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

    public void sair(final RequestListener<NetworkResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        RequestListenerHelper<NetworkResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().post(SilceClient.SAIR_PATH, queryParams,
                "", NetworkResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), NetworkResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()),
                headers);
    }

    public void gravarUsuario(ApostadorDTO apostadorDTO, final RequestListener<UsuarioLogadoResponse> listener) {
        RequestListenerHelper<UsuarioLogadoResponse> helper = new RequestListenerHelper<>(listener);

        silceClient.getConnection().post(SilceClient.GRAVAR_USUARIO_PATH, new LinkedHashMap<String, String>(), apostadorDTO, UsuarioLogadoResponse.class,
                silceClient.volleyListener(helper.getSilceListener(), UsuarioLogadoResponse.class),
                silceClient.volleyErrorListener(helper.getErrorListener()), new LinkedHashMap<String, String>());

    }

    public static void updateFecharIntroducao(Activity activity, Boolean status){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(activity.getString(R.string.key_introducao), status);
        editor.commit();

    }

    public static Boolean checarFecharIntroducao(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean(activity.getString(R.string.key_introducao), false);
        return status;
    }


}
