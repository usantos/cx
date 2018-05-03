package caixa.gov.br.loterias.model.bo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.PopupMenu;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
import caixa.gov.br.loterias.model.bo.silce.dto.RedirectEnum;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.CadastrarActivity;
import caixa.gov.br.loterias.view.CadastrarActivity_;
import caixa.gov.br.loterias.view.CadastroActivity;
import caixa.gov.br.loterias.view.DadosPessoaisActivity;
import caixa.gov.br.loterias.view.TermosUsoActivity;
import okhttp3.Response;

import static caixa.gov.br.loterias.model.bo.silce.dto.RedirectEnum.*;

/**
 * Created by cedesbr450 on 11/04/18.
 */

public class RedirectNetwork {
    public static void checkRedirect(VolleyError error, final Activity activity){
        if (error.networkResponse == null){
            ViewUtils.alert( activity, activity.getResources().getString( R.string.seminternet ) );
            return;
        }
        NetworkResponse response = error.networkResponse;

        final byte[] textBytes = response.data;
        String msgErro;
        String msgErroResponse = "";
        String redirect = "";
        String codigo = "";
        try {
            String json = new String(textBytes, "UTF-8");
            JSONObject jsonObj = new JSONObject(json);
            msgErroResponse = jsonObj.getString("mensagem");
            codigo = jsonObj.getString( "codigo" );
            if (jsonObj.getString( "redirect" ) != null){
                redirect = jsonObj.getString( "redirect");
            }

        } catch (UnsupportedEncodingException | JSONException e) {

        }
        if (redirect != ""){
            RedirectEnum redirectEnum = RedirectEnum.fromString( redirect );
            switch (redirectEnum){
                case ACEITAR_TERMO_DE_USO:
                    activity.startActivity(new Intent(activity, TermosUsoActivity.class));
                    return;
                case LOGIN:
                    connectKeycloak(activity);
                    return;
                case CADASTRAR_APOSTADOR:
                    activity.startActivity(new Intent(activity, CadastrarActivity_.class));
                    return;
                default:
                    break;
            }
        }
        if (response.statusCode == 401){
            connectKeycloak(activity);
        }else {
            if (codigo.equals( "018003" )){
                ViewUtils.alertTitleButtonPositiveListener(activity, R.string.label_atencao,
                        msgErroResponse,
                        R.string.label_cotinuar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.stackoverflow.com"));
                                Uri webpage = Uri.parse("https://www.mercadopago.com.br/registration-mp?mode=mp");
                                Intent intentAc = new Intent(Intent.ACTION_VIEW, webpage);
                                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                    activity.startActivity(intentAc);
                                }
                            }
                        });
            }else{
                ViewUtils.alert( activity, MensagensNetwork.setMensagem( error, activity ) );
            }
        }
    }
    public static void checkRedirectSucesso(RedirectEnum redirect, Activity activity){
        switch (redirect){
            case ACEITAR_TERMO_DE_USO:
                activity.startActivity(new Intent(activity, TermosUsoActivity.class));
                break;
            case LOGIN:
                connectKeycloak(activity);
                break;
            case CADASTRAR_APOSTADOR:
                activity.startActivity(new Intent(activity, CadastrarActivity_.class));
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
