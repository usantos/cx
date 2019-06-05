package br.gov.caixa.loterias.apostar.interno.model.bo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.AppIndisponivelActivity;
import br.gov.caixa.loterias.apostar.interno.view.CadastrarActivity_;
import br.gov.caixa.loterias.apostar.interno.view.CarrinhoActivity;
import br.gov.caixa.loterias.apostar.interno.view.TermosUsoActivity;


/**
 * Created by cedesbr450 on 11/04/18.
 */

public class  RedirectNetwork {

    public static void checkRedirect(VolleyError error, final Context context, Activity activity){

        if (error.networkResponse == null){
            ViewUtils.alert( context, context.getResources().getString( R.string.seminternet ) );
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
                    activity.startActivityForResult(new Intent(context, TermosUsoActivity.class), 1);
                    return;
                case LOGIN:
                    connectKeycloak(activity);
                    return;
                case CADASTRAR_APOSTADOR:
                    context.startActivity(new Intent(context, CadastrarActivity_.class));
                    return;
                case INDISPONIVEL:
                    context.startActivity(new Intent(context, AppIndisponivelActivity.class));
                    return;
                default:
                    break;
            }
        }

        if (response.statusCode == 401){
            connectKeycloak(activity);
        }else {
            if (errorResponse.getCodigo() != null &&  errorResponse.getCodigo().equals( "018003" )){
                ViewUtils.alertTitleButtonYesOrNotListener(context, errorResponse.getMensagem(), (dialog, which) -> {
                    Uri webpage = Uri.parse("https://www.mercadopago.com.br/registration-mp?mode=mp");
                    Intent intentAc = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intentAc.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intentAc);
                    }
                });
            }else{
                ViewUtils.alertTitleButton(context, R.string.label_atencao, MensagensNetwork.setMensagem( error, context ), context.getResources().getString( R.string.ok ) );
            }
        }
    }

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
        if(error.networkResponse.data != null) {
            try {
                body = new String(error.networkResponse.data,"UTF-8");
                errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if (errorResponse != null) {
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
                    case INDISPONIVEL:
                        if (activity.getClass() != AppIndisponivelActivity.class) {
                            activity.startActivityForResult(new Intent(activity, AppIndisponivelActivity.class), 1);
                        } else {
                            ViewUtils.alertTitleButton(activity, R.string.label_atencao, activity.getResources().getString(R.string.erro_500), "Ok", dialogInterface -> activity.onBackPressed());
                        }
                        return;
                    case CARRINHO:
                        activity.startActivityForResult(new Intent(activity, CarrinhoActivity.class), 1);
                        return;
                    default:
                        break;
                }
            }
        }

        if (response.statusCode == 401){
            connectKeycloak(activity);
        }else {
            if (errorResponse.getCodigo() != null &&  errorResponse.getCodigo().equals( "018003" )){
                ViewUtils.alertTitleButtonYesOrNotListener(activity, errorResponse.getMensagem(), (dialog, which) -> {
                    Uri webpage = Uri.parse("https://www.mercadopago.com.br/registration-mp?mode=mp");
                    Intent intentAc = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intentAc.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivity(intentAc);
                    }
                });
            }else{
                ViewUtils.alertTitleButton(activity, R.string.label_atencao, MensagensNetwork.setMensagem(error, activity), "Ok", dialogInterface -> activity.onBackPressed());
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
            case INDISPONIVEL:
                activity.startActivityForResult(new Intent(activity, AppIndisponivelActivity.class), 1);
                return;
            default:
                break;
        }
    }

    //TODO: LOGIN - Trocando
    private static void connectKeycloak(final Activity activity) {
        if(AppUtils.isNetworkAvailable(activity)){
            AcessoBO_.getInstance_(activity).iniciarLoginSSO(activity);
        }else{
            ViewUtils.alert(activity, activity.getString(R.string.seminternet));
        }
    }


}
