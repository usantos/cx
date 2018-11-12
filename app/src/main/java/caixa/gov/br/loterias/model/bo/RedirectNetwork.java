package caixa.gov.br.loterias.model.bo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;


import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;


import java.io.UnsupportedEncodingException;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.ErrorResponse;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO;
import caixa.gov.br.loterias.model.bo.keycloak.KeycloakBO_;
import caixa.gov.br.loterias.model.bo.silce.dto.RedirectEnum;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.CadastrarActivity_;
import caixa.gov.br.loterias.view.TermosUsoActivity;


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

        Gson gsonResponse = new Gson();

        ErrorResponse errorResponse = new ErrorResponse();

        String body;
        //get response body and parse with appropriate encoding
        if(error.networkResponse.data!=null) {
            try {
                body = new String(error.networkResponse.data,"UTF-8");
                errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        if (errorResponse.getRedirect() != null){
            RedirectEnum redirectEnum = RedirectEnum.fromString( errorResponse.getRedirect() );
            switch (redirectEnum){
                case ACEITAR_TERMO_DE_USO:
                    activity.startActivityForResult(new Intent(activity, TermosUsoActivity.class), 1);
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
            if (errorResponse.getCodigo() != null &&  errorResponse.getCodigo().equals( "018003" )){
                ViewUtils.alertTitleButtonYesOrNotListener(activity, errorResponse.getMensagem(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri webpage = Uri.parse("https://www.mercadopago.com.br/registration-mp?mode=mp");
                                Intent intentAc = new Intent(Intent.ACTION_VIEW, webpage);
                                if (intentAc.resolveActivity(activity.getPackageManager()) != null) {
                                    activity.startActivity(intentAc);
                                }
                            }
                        });
            }else{
                ViewUtils.alertTitleButton(activity, R.string.label_atencao, MensagensNetwork.setMensagem( error, activity ), activity.getResources().getString( R.string.ok ) );
            }
        }
    }
    public static void checkRedirectSucesso(RedirectEnum redirect, Activity activity){
        switch (redirect){
            case ACEITAR_TERMO_DE_USO:
                activity.startActivityForResult(new Intent(activity, TermosUsoActivity.class), 1);
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

    //TODO: LOGIN - Trocando
    private static void connectKeycloak(final Activity activity) {
        if(AppUtils.isNetworkAvailable(activity)){
            AcessoBO_.getInstance_(activity).iniciarLoginSSO();
//            KeycloakBO_.getInstance_(activity).login(activity, new KeycloakBO.OnLogin() {
//                        @Override
//                        public void onSuccess(String token) {
//
//                        }
//
//                        @Override
//                        public void onFailure(Exception e) {
//                            if(activity != null){
//                                ViewUtils.alert(activity, activity.getString(R.string.sistema_indisponivel));
//                            }
//                        }
//                    }
//            );
        }else{
            ViewUtils.alert(activity, activity.getString(R.string.seminternet));
        }
    }


}
