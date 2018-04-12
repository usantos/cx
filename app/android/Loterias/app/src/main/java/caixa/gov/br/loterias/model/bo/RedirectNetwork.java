package caixa.gov.br.loterias.model.bo;

import android.app.Activity;
import android.content.Intent;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.DadosPessoaisActivity;
import caixa.gov.br.loterias.view.TermosUsoActivity;
import okhttp3.Response;

/**
 * Created by cedesbr450 on 11/04/18.
 */

public class RedirectNetwork {
    public static void checkRedirect(VolleyError error, Activity activity){
        NetworkResponse response = error.networkResponse;

        final byte[] textBytes = response.data;
        String msgErro;
        String msgErroResponse;
        String redirect = "";
        try {
            String json = new String(textBytes, "UTF-8");
            JSONObject jsonObj = new JSONObject(json);
            msgErroResponse = jsonObj.getString("mensagem");
            if (jsonObj.getString( "redirect" ) != null){
                redirect = jsonObj.getString( "redirect");
            }

        } catch (UnsupportedEncodingException | JSONException e) {
            msgErroResponse = activity.getString( R.string.sistema_indisponivel);
        }
        switch (redirect){
            case "aceitar-termo-de-uso":
                activity.startActivity(new Intent(activity, TermosUsoActivity.class));
                break;
            case "login":
                connectKeycloak(activity);
                break;
            default:
                ViewUtils.alert( activity, MensagensNetwork.setMensagem( error, activity ) );
                break;
        }
    }
    public static void checkRedirectSucesso(String redirect, Activity activity){
        switch (redirect){
            case "aceitar-termo-de-uso":
                activity.startActivity(new Intent(activity, TermosUsoActivity.class));
                break;
            case "login":
                connectKeycloak(activity);
                break;
            default:
                break;
        }
    }

    private static void connectKeycloak(final Activity activity) {
        if(AppUtils.isNetworkAvailable(activity)){
            KeycloakBO_.getInstance_(activity).login(activity, new KeycloakBO.OnLogin() {
                        @Override
                        public void onSuccess(String token) {
                            if(activity != null){

                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if(activity != null){
                                ViewUtils.alert(activity, activity.getString(R.string.sistema_indisponivel));
                            }
                        }
                    }
            );
        }else{
            ViewUtils.alert(activity, activity.getString(R.string.seminternet));
        }
    }


}
