package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
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
import br.gov.caixa.loterias.apostas.controllers.CadastrarActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostas.controllers.JogosConfirmadosPremioMercadoPagoActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;

import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.controllers.SplashScreenActivity;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoApostadorEnum;
import br.gov.caixa.loterias.apostas.model.model.LoginSSOModel;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MercadoPagoUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;



/**
 * Created by cedesbr450 on 11/04/18.
 */

public class  RedirectNetwork {

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
        boolean lancouEvento = false;
        if (error.networkResponse == null) {
            AppCenterManager.registraEventoErro(AppCenterManager.ERRO_GENERICO, error);

            if (!AppUtils.isNetworkAvailable(activity)) {
                if (!activity.getClass().toString().equalsIgnoreCase(SplashScreenActivity.class.toString())) {
                    ViewUtils.alert(activity, activity.getResources().getString(R.string.seminternet));
                }
                return;
            } else {
                ViewUtils.alertTitleButton(activity, R.string.label_atencao, MensagensNetwork.setMensagem(error, activity), activity.getResources().getString(R.string.ok));
                return;
            }
        }
        NetworkResponse response = error.networkResponse;

        Gson gsonResponse = new Gson();

        ErrorResponse errorResponse = new ErrorResponse();

        String body = null;
        //get response body and parse with appropriate encoding
        if(error.networkResponse.data != null) {
            try {
                body = new String(error.networkResponse.data,"UTF-8");
                errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                AppCenterManager.registraEventoErro(AppCenterManager.ERRO_UNSUPPORTED_ENCODING, error, body);
                lancouEvento = true;
            } catch (Exception e){
                e.printStackTrace();
                AppCenterManager.registraEventoErro(AppCenterManager.ERRO_GENERICO_SERIALIZACAO, error, body);
                lancouEvento = true;
            }
        }else {
            AppCenterManager.registraEvento(AppCenterManager.ERRO_SERVICO_SEM_RESPONSEDATA);
            lancouEvento = true;
        }

        if (errorResponse != null) {
            if (errorResponse.getRedirect() != null){
                trataRedict(activity, errorResponse);
            }else {
                if (!lancouEvento) {
                    if (errorResponse.getCodigo() != null) {
                        AppCenterManager.registraEventoErro(errorResponse.getCodigo(), error, body);
                    } else {
                        AppCenterManager.registraEventoErro(String.valueOf(response.statusCode), error, body);
                    }
                }
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
                            enviaRegistrarMp(activity, error);
                        } else {
                            apresentaAlerta(activity, montarMensgem(activity, MensagensNetwork.setMensagem(error, activity), errorResponse.getCodigo()));
                        }
                        break;
                }
            }
        }else {
            AppCenterManager.registraEvento(AppCenterManager.ERRO_SERVICO_SEM_RESPONSEDATA);
        }
    }

    public static void checkRedirectCompraAsync(VolleyError error, final Activity activity){
        boolean lancouEvento = false;
        if (error.networkResponse == null) {
            AppCenterManager.registraEventoErro(AppCenterManager.ERRO_GENERICO, error);

            if (!AppUtils.isNetworkAvailable(activity)) {
                if (!activity.getClass().toString().equalsIgnoreCase(SplashScreenActivity.class.toString())) {
                    ViewUtils.alert(activity, activity.getResources().getString(R.string.seminternet));
                }
                return;
            } else {
                String msg = MensagensNetwork.setMensagem(error, activity);
                if (msg.equalsIgnoreCase(activity.getString(R.string.sistema_indisponivel))){
                       showAlertToRedirectMinhasCompras(activity);
                } else {
                    ViewUtils.alertTitleButton(activity, R.string.label_atencao, msg, activity.getResources().getString(R.string.ok));
                }
                return;
            }
        }
        NetworkResponse response = error.networkResponse;

        Gson gsonResponse = new Gson();

        ErrorResponse errorResponse = new ErrorResponse();

        String body = null;
        //get response body and parse with appropriate encoding
        if(error.networkResponse.data != null) {
            try {
                body = new String(error.networkResponse.data,"UTF-8");
                errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                AppCenterManager.registraEventoErro(AppCenterManager.ERRO_UNSUPPORTED_ENCODING, error, body);
                lancouEvento = true;
            } catch (Exception e){
                e.printStackTrace();
                AppCenterManager.registraEventoErro(AppCenterManager.ERRO_GENERICO_SERIALIZACAO, error, body);
                lancouEvento = true;
            }
        }else {
            AppCenterManager.registraEvento(AppCenterManager.ERRO_SERVICO_SEM_RESPONSEDATA);
            lancouEvento = true;
        }

        if (errorResponse != null) {
            if (errorResponse.getRedirect() != null){
                trataRedict(activity, errorResponse);
            }else {
                if (!lancouEvento) {
                    if (errorResponse.getCodigo() != null) {
                        AppCenterManager.registraEventoErro(errorResponse.getCodigo(), error, body);
                    } else {
                        AppCenterManager.registraEventoErro(String.valueOf(response.statusCode), error, body);
                    }
                }
                switch (response.statusCode){
                    case 401:
                        connectKeycloak(activity);
                        return;
                    case 404:
                    case 400:
                        treatNegotialErros(errorResponse, error, activity);
                        return;
                    case 500:
                        trataErro500(activity,errorResponse);
                        return;
                    default:
                        if (errorResponse.getCodigo() != null &&  errorResponse.getCodigo().equals( "018003" )){
                            enviaRegistrarMp(activity, error);
                        } else {
                            showAlertToRedirectMinhasCompras(activity);
                        }
                        break;
                }
            }
        }else {
            AppCenterManager.registraEvento(AppCenterManager.ERRO_SERVICO_SEM_RESPONSEDATA);
            showAlertToRedirectMinhasCompras(activity);
        }
    }

    private static void showAlertToRedirectMinhasCompras(Activity activity){
        ViewUtils.alertTitleButtonYesListener(activity,
                                              activity.getResources().getString(R.string.verificamos_compra_demorando_ou_processando),
                                              (dialog, which) -> {
                                                  goToMinhasCompras(activity);
                                              });
    }

    private static void goToMinhasCompras(Activity activity){
        activity.startActivity(new Intent(activity, ListaComprasActivity.class));
        activity.finish();
    }

    private static void trataRedict(Activity activity, ErrorResponse errorResponse) {
        try{
            RedirectEnum redirectEnum = RedirectEnum.fromString(errorResponse.getRedirect());
            switch (redirectEnum) {
                case ACEITAR_TERMO_DE_USO:
                    verificaRedirectTermo(activity);
                    return;
                case LOGIN:
                    connectKeycloak(activity);
                    return;
                case CADASTRAR_APOSTADOR:
                    if (!activity.getClass().getName().equalsIgnoreCase(TermosUsoActivity.class.getName())) {
                        activity.startActivity(new Intent(activity, CadastrarActivity.class));
                    }
                    return;
                case INDISPONIVEL:
                    if (activity.getClass() != AppIndisponivelActivity.class) {
                        activity.startActivityForResult(new Intent(activity, AppIndisponivelActivity.class), 1);
                    } else {
                        ViewUtils.alertTitleButton(activity, R.string.label_atencao, activity.getResources().getString(R.string.nao_concluiu_operacao), "Ok", dialogInterface -> {});
                    }
                    return;
                case CARRINHO:
                    activity.startActivity(new Intent(activity, CarrinhoActivity.class));
                    return;
                case CARRINHOS_FAVORITOS:
                    activity.startActivity(new Intent(activity, CarrinhosFavoritosActivity.class));
                    return;
                case VERIFICA_COMPRA_PROCESSAMENTO:
                    return;
                default:
                    break;
            }
        } catch (Exception e){
            AppCenterManager.registraEventoErro(AppCenterManager.ERRO_RESPONSE, errorResponse);
            ViewUtils.alertTitleButton(activity, R.string.label_atencao, activity.getResources().getString(R.string.nao_concluiu_operacao), "Ok", dialogInterface -> {});
        }
    }

    public static void trataBloqueioTotal(VolleyError error, final Activity activity) {
        Gson gsonResponse = new Gson();
        ErrorResponse errorResponse = new ErrorResponse();
        String body = null;
        if (error != null && error.networkResponse != null && error.networkResponse.data != null) {
            try {
                body = new String(error.networkResponse.data, "UTF-8");
                errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);
                if (errorResponse != null) {
                    if (errorResponse.getRedirect() != null) {
                        if (errorResponse.getCodigo() != null && errorResponse.getCodigo().equals("002020")) {
                            ViewUtils.alertTitleButton(activity, R.string.label_atencao, errorResponse.getMensagem(),
                                "Ok", dialogInterface -> {
                                    AppCenterManager.registraEvento(activity.getResources().getString(R.string.evento_logout_sucesso));
                                    new LoginSSOModel(activity).sair(() -> {});
                                    DadosUsuarioBO.updateUsuarioLogado(activity, false);
                                    activity.finish();
                                    dialogInterface.dismiss();
                                    return;
                                }
                            );
                            return;
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        activity.finish();
    }

    public static void trataBloqueioParcial(final Activity activity) {
        if (SessaoUsuario.getInstance().getSituacaoApostador() != null &&
            SessaoUsuario.getInstance().getSituacaoApostador().getEnum() != null && //Servico Nuvem (Erro)
            SessaoUsuario.getInstance().getSituacaoApostador().getEnum().equals(SituacaoApostadorEnum.BLOQUEADO_PARCIAL)) {
            ViewUtils.alertTitleButton(activity, R.string.label_atencao, activity.getString(R.string.usuario_bloqueio_parcial),
                    "Ok", dialogInterface -> {
                        activity.finish();
                        dialogInterface.dismiss();
                        return;
                    }
            );
            return;
        }
        activity.finish();
    }

    public static void treatNegotialErros(ErrorResponse error, VolleyError volleyError, Activity activity) {
        if (error != null && error.getCodigo() != null) {
            switch (error.getCodigo()) {
                case "011010":
                case "011011":
                case "011012":
                    ViewUtils.alertTitleCustomPositiveListener(activity, "Ok", "Apostar mais",
                                                               MensagensNetwork.setMensagem(volleyError, activity), (dialog, which) -> {
                                //PrincipalActivity_.intent(activity).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                                Intent intent = IntentUtil.getIntentOrigemDestino(activity, PrincipalActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(intent);
                            });
                    break;
                case "018003":
                    enviaRegistrarMp(activity, volleyError);
                    break;
                default:
                    ViewUtils.alertTitleButton(activity, R.string.label_atencao, montarMensgem(activity, MensagensNetwork.setMensagem(volleyError, activity), error.getCodigo()), "Ok", dialog -> {});
                    break;

            }
        }else {
            ViewUtils.alertTitleButton(activity, R.string.label_atencao, montarMensgem(activity, MensagensNetwork.setMensagem(volleyError, activity), error.getCodigo()), "Ok", dialog -> {});
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
                if (!activity.getClass().getName().equalsIgnoreCase(TermosUsoActivity.class.getName())) {
                    activity.startActivity(new Intent(activity, CadastrarActivity.class));
                }
                break;
            case INDISPONIVEL:
                activity.startActivityForResult(new Intent(activity, AppIndisponivelActivity.class), 1);
                return;
            case VERIFICA_COMPRA_PROCESSAMENTO:
                return;
            default:
                break;
        }
    }

    //TODO: LOGIN - Trocando
    public static void connectKeycloak(final Activity activity) {
        if(AppUtils.isNetworkAvailable(activity)){
            AcessoBO.getInstance().iniciarLoginSSO(activity);
        }else{
            ViewUtils.alert(activity, activity.getString(R.string.seminternet));
        }
    }

    public static void trataErro500(Activity activity, ErrorResponse error){
        if (error != null) {
            if (error.getCodigo() != null && error.getCodigo().equalsIgnoreCase("2067")) {
                ViewUtils.alert(activity, MercadoPagoUtil.getMessageFromCode("2067"));
            } else if (error.getTipo() != null && error.getTipo().equalsIgnoreCase("1")) {
                apresentaAlerta(activity, montarMensgem(activity, error.getMensagem(), error.getCodigo()));
            } else {
                apresentaAlerta(activity, montarMensgem(activity, null, error.getCodigo()));
            }
        } else {
            apresentaAlerta(activity, montarMensgem(activity, null, null));
        }

    }

    private static String montarMensgem(Activity activity, String mensagem, String codError) {
        String mensagemCompleta = activity.getString(R.string.erro_req) + "\n\n";
        if (mensagem != null) {
            mensagemCompleta = mensagem + "\n\n";
        }
        if (codError != null) {
            mensagemCompleta += codError + "\n";
        }
        mensagemCompleta += DateUtils.getDateTime();
        return mensagemCompleta;
    }

    private static void apresentaAlerta(Activity activity, String mensagem) {
        ViewUtils.alertTitleButton(
                activity,
                R.string.label_atencao,
                mensagem,
                activity.getResources().getString( R.string.ok ) );
    }

    private static void enviaRegistrarMp(Activity activity, VolleyError volleyError){
        ViewUtils.alertTitleButtonYesOrNotListener(
                activity,
                MensagensNetwork.setMensagem(volleyError, activity),
                (dialogInterface, i) -> {
                    JogosConfirmadosPremioMercadoPagoActivity jcpmpa = ((JogosConfirmadosPremioMercadoPagoActivity)activity);
                    jcpmpa.redirectMp();
                });
    }

    private static void enviaEmail(Activity activity, ErrorResponse error) {
        /*ViewUtils.alertTitleCustomPositiveListener(
                activity,
                activity.getString(R.string.ops),
                activity.getString(R.string.label_nao),
                activity.getString(R.string.quero),
                activity.getResources().getString( R.string.erro_500),
                (dialog1, which1) -> enviaEmail(activity,error)).show();*/
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
