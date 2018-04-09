package caixa.gov.br.loterias.model.bo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.NetworkResponse;

import org.androidannotations.annotations.EBean;

import java.util.LinkedHashMap;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostadorDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostadorDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.AutenticacaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ListCartaoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.model.bo.silce.internals.GsonRequest;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * Created by cedesbr450 on 07/12/17.
 */

@EBean(scope = Singleton)
public class DadosUsuarioBO extends BaseBO {

    public void cartoes(final RequestListener<ListCartaoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("meioPagamento", "1");

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        GsonRequest request = silceConnection.buildGetRequest(ServerMethods.LIST_CARTOES_PATH, queryParams, ListCartaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void autenticar(final String email, final String senha,
                           final RequestListener<UsuarioLogadoResponse> listener) {

        AutenticacaoDTO autenticacaoDTO = new AutenticacaoDTO();
        autenticacaoDTO.setEmail(email);
        autenticacaoDTO.setSenha(senha);

        silceConnection.post(ServerMethods.LOGIN_PATH, new LinkedHashMap<String, String>(),
                autenticacaoDTO, UsuarioLogadoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), new LinkedHashMap<String, String>());
    }

    public void sair(final RequestListener<NetworkResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        GsonRequest request = silceConnection.buildPostRequest(ServerMethods.SAIR_PATH, queryParams,
                "", NetworkResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void gravarUsuario(ApostadorDTO apostadorDTO, final RequestListener<UsuarioLogadoResponse> listener) {
        silceConnection.post(ServerMethods.GRAVAR_USUARIO_PATH, new LinkedHashMap<String, String>(), apostadorDTO, UsuarioLogadoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), new LinkedHashMap<String, String>());
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


    public static void updateVerificarIdade(Activity activity, Boolean status){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(activity.getString(R.string.key_idade), status);
        editor.commit();

    }

    public static Boolean checarVerificarIdade(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean(activity.getString(R.string.key_idade), true);
        return status;
    }


    public void getDadosUsuario(final RequestListener<ApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        GsonRequest request = silceConnection.buildGetRequest(ServerMethods.USUARIOS_RECUPERAR_DADOS_PATH, queryParams, ApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

}
