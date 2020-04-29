package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.AppIndisponivelActivity;
import br.gov.caixa.loterias.apostas.controllers.CadastrarActivity_;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity_;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;



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
                    verificaRedirectTermo(activity);
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

    private static void  verificaRedirectTermo(Activity activity){
        if (!activity.getClass().equals(TermosUsoActivity.class)) {
            ViewUtils.alertTitleButtonYesListener(activity,
                                                  activity.getResources().getString(R.string.termo_novo),
                                                  (dialogInterface, i) -> activity.startActivityForResult(
                                                          new Intent(activity, TermosUsoActivity.class),
                                                          1)
                                                 );
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
                        verificaRedirectTermo(activity);
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
                            ViewUtils.alertTitleButton(activity, R.string.label_atencao, activity.getResources().getString(R.string.nao_concluiu_operacao), "Ok", dialogInterface ->{});
                        }
                        return;
                    case CARRINHO:
                        activity.startActivity(new Intent(activity, CarrinhoActivity.class));
                        return;
                    case CARRINHOS_FAVORITOS:
                        activity.startActivity(new Intent(activity, CarrinhosFavoritosActivity.class));
                        return;
                    default:
                        break;
                }
            }else {
                switch (response.statusCode){
                    case 401:
                        connectKeycloak(activity);
                        return;
                    case 400:
                        treatNegotialErros(errorResponse, error, activity);
                        return;
                    case 500:
                        trataErro500(activity,errorResponse);
                        return;
                    default:
                        if (errorResponse.getCodigo() != null &&  errorResponse.getCodigo().equals( "018003" )){
                            ViewUtils.alertTitleButtonYesOrNotListener(activity, errorResponse.getMensagem(), (dialog, which) -> {
                                Uri webpage = Uri.parse("https://www.mercadopago.com.br/registration-mp?mode=mp");
                                Intent intentAc = new Intent(Intent.ACTION_VIEW, webpage);
                                if (intentAc.resolveActivity(activity.getPackageManager()) != null) {
                                    activity.startActivity(intentAc);
                                }
                            });
                        }else{
                            ViewUtils.alertTitleButton(activity, R.string.label_atencao, MensagensNetwork.setMensagem(error, activity), "Ok", dialogInterface -> {});
                        }
                        break;
                }
            }
        }
    }

    public static void treatNegotialErros(ErrorResponse error, VolleyError volleyError, Activity activity) {
         switch (error.getCodigo()) {
             case "011010": case "011011": case "011012":
                 ViewUtils.alertTitleCustomPositiveListener(activity,"Ok", "Apostar mais",
                         MensagensNetwork.setMensagem(volleyError, activity), (dialog, which) -> {
                             PrincipalActivity_.intent(activity).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                         });
                 break;

                 default:
                     ViewUtils.alertTitleButton(activity, R.string.label_atencao, MensagensNetwork.setMensagem(volleyError, activity), "Ok",dialog -> {});
                     break;

         }
    }

    public static void checkRedirectSucesso(RedirectEnum redirect, Activity activity){
        switch (redirect){
            case ACEITAR_TERMO_DE_USO:
                verificaRedirectTermo(activity);
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
    public static void connectKeycloak(final Activity activity) {
        if(AppUtils.isNetworkAvailable(activity)){
            AcessoBO_.getInstance_(activity).iniciarLoginSSO(activity);
        }else{
            ViewUtils.alert(activity, activity.getString(R.string.seminternet));
        }
    }

    public static void trataErro500(Activity activity, ErrorResponse error){
        ViewUtils.alertTitleCustomPositiveListener(
                activity,
                activity.getString(R.string.ops),
                activity.getString(R.string.label_nao),
                activity.getString(R.string.quero),
                activity.getResources().getString( R.string.erro_500),
                (dialog1, which1) -> enviaEmail(activity,error)).show();
    }

    private static void enviaEmail(Activity activity, ErrorResponse error) {

        final Intent shareIntent = new Intent(Intent.ACTION_SENDTO);
        shareIntent.setData(Uri.parse( "mailto:"));
        shareIntent.putExtra(Intent.EXTRA_EMAIL,new String[] {activity.getString(R.string.email_padrao)});
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,activity.getString(R.string.email_assunto));
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(formataEmail(error)));
        try {
            activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.enviando_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, activity.getResources().getString(R.string.erro_mail_provider), Toast.LENGTH_SHORT).show();
        }
    }

    private static String formataEmail(ErrorResponse error) {
        return new StringBuilder()
                .append("Versão do app: "+BuildConfig.VERSION_NAME+"<br/>")
                .append("Versão do SDK: "+Build.VERSION.SDK_INT+" ("+Build.VERSION.RELEASE+")<br/>")
                .append("Marca: "+Build.BRAND+"<br/>")
                .append("Modelo: "+Build.MODEL+"<br/>")
                .append("Plataforma: Android<br/>")
                .append("Código de erro: "+error.getCodigo()+"<br/>")
                .toString();
    }
}
