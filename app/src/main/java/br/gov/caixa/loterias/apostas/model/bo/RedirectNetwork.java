package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.AppIndisponivelActivity;
import br.gov.caixa.loterias.apostas.controllers.CadastrarActivity;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostas.controllers.JogosConfirmadosPremioMercadoPagoActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;

import br.gov.caixa.loterias.apostas.controllers.LoginActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.controllers.SplashScreenActivity;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoApostadorEnum;
import br.gov.caixa.loterias.apostas.model.model.LoginSSOModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogExperimenteLogarSingleton;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MercadoPagoUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;


/**
 * Created by cedesbr450 on 11/04/18.
 */

public class  RedirectNetwork {

    private static void  verificaRedirectTermo(Activity activity){
        if (!activity.getClass().equals(TermosUsoActivity.class)) {
            DialogUtils.dialogEntendiListener(
                    activity,
                    activity.getResources().getString(R.string.termo_novo),
                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(DialogInterface dialog, int which) {
                            activity.startActivityForResult(
                                    new Intent(activity, TermosUsoActivity.class),
                                    1);
                        }
                    }
            );
        }
    }

    public static void checkRedirect(VolleyError error, final Activity activity){
        boolean lancouEvento = false;
        if (error.networkResponse == null) {
            AppCenterManager.registraEventoErro(AppCenterManager.ERRO_GENERICO, error);

            if (!AppUtils.isNetworkAvailable(activity)) {
                if (activity != null && !activity.getClass().toString().equalsIgnoreCase(SplashScreenActivity.class.toString())) {
                    DialogUtils.dialogEntendi(
                            activity,
                            activity.getResources().getString(R.string.seminternet)
                    );
                }
                return;
            } else {
                DialogUtils.dialogEntendi(
                        activity,
                        MensagensNetwork.setMensagem(error, activity)
                );
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
                AppCenterManager.registraEventoErro(AppCenterManager.ERRO_UNSUPPORTED_ENCODING, error, body);
                lancouEvento = true;
            } catch (Exception e){
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
                        handleUnauthorized(activity);
                        return;
                    case 400:
                        treatNegotialErros(errorResponse, error, activity);
                        return;
                    case 500:
                        trataErro500(activity, errorResponse);
                        return;
                    default:
                        if (errorResponse.getCodigo() != null && errorResponse.getCodigo().equals( "018003" )){
                            enviaRegistrarMp(activity, error);
                        } else {
                            apresentaAlerta(activity, montarMensgem(activity, MensagensNetwork.setMensagem(error, activity), errorResponse.getCodigo()));
                        }
                        break;
                }
            }
        }else {
            if (response.statusCode == 401){
                handleUnauthorized(activity);
            } else {
                AppCenterManager.registraEvento(AppCenterManager.ERRO_SERVICO_SEM_RESPONSEDATA);
            }
        }
    }

    private static void handleUnauthorized(Activity activity) {
        if (KeycloakBO.getInstance().getAccessToken() == null) {
            AlertDialogExperimenteLogarSingleton.show(activity, true, null);
        } else {
            AlertDialogExperimenteLogarSingleton.showSessaoExpirada(activity,
                    () -> connectKeycloak(activity, null));
        }
    }

    public static void checkRedirectCompraAsync(VolleyError error, final Activity activity){
        boolean lancouEvento = false;
        if (error == null) {
            showAlertToRedirectMinhasCompras(activity);
            return;
        }

        if (error.networkResponse == null) {
            AppCenterManager.registraEventoErro(AppCenterManager.ERRO_GENERICO, error);
            if (!AppUtils.isNetworkAvailable(activity)) {
                if (!activity.getClass().toString().equalsIgnoreCase(SplashScreenActivity.class.toString())) {
                    DialogUtils.dialogEntendi(
                            activity,
                            activity.getResources().getString(R.string.seminternet)
                    );
                }
                return;
            } else {
                String msg = MensagensNetwork.setMensagem(error, activity);
                if (msg.equalsIgnoreCase(activity.getString(R.string.sistema_indisponivel))) {
                    showAlertToRedirectMinhasCompras(activity);
                } else {
                    DialogUtils.dialogEntendi(
                            activity,
                            msg
                    );
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
                AppCenterManager.registraEventoErro(AppCenterManager.ERRO_UNSUPPORTED_ENCODING, error, body);
                lancouEvento = true;
            } catch (Exception e){
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
                        handleUnauthorized(activity);
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
            if (response.statusCode == 401){
                handleUnauthorized(activity);
            } else {
                AppCenterManager.registraEvento(AppCenterManager.ERRO_SERVICO_SEM_RESPONSEDATA);
                showAlertToRedirectMinhasCompras(activity);
            }
        }
    }

    private static void showAlertToRedirectMinhasCompras(Activity activity){
        DialogUtils.dialogEntendiListener(
                activity,
                activity.getResources().getString(R.string.verificamos_compra_demorando_ou_processando),
                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        goToMinhasCompras(activity);
                    }
                }
        );
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
                    connectKeycloak(activity, null);
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
                        DialogUtils.dialogEntendi(
                                activity,
                                activity.getResources().getString(R.string.nao_concluiu_operacao)
                        );
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
                case HOME:
                    trataBloqueioTotal(errorResponse, activity);
                    return;
                default:
                    break;
            }
        } catch (Exception e){
            AppCenterManager.registraEventoErro(AppCenterManager.ERRO_RESPONSE, errorResponse);
            DialogUtils.dialogEntendi(
                    activity,
                    activity.getResources().getString(R.string.nao_concluiu_operacao)
            );
        }
    }

    public static void trataBloqueioTotal(ErrorResponse error, final Activity activity) {

        if (error != null && error.getCodigo() != null && isBloqueioTotal(error.getCodigo())) {
            encerrarSessaoLocal(activity);

            String mensagem = error.getMensagem() != null
                    ? error.getMensagem()
                    : activity.getString(R.string.nao_concluiu_operacao_nova);

            DialogUtils.dialogEntendiListener(
                    activity,
                    mensagem,
                    (dialog, which) -> {
                       sairSeguro(activity);
                       activity.finish();
                    }
            );
        }
    }

    public static void trataBloqueioParcial(final Activity activity) {
        if (SessaoUsuario.getInstance().getSituacaoApostador() != null &&
            SessaoUsuario.getInstance().getSituacaoApostador().getEnum() != null && //Servico Nuvem (Erro)
            SessaoUsuario.getInstance().getSituacaoApostador().getEnum().equals(SituacaoApostadorEnum.BLOQUEADO_PARCIAL)) {
            DialogUtils.dialogEntendiListener(
                    activity,
                    activity.getString(R.string.usuario_bloqueio_parcial),
                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(DialogInterface dialog, int which) {
                            activity.finish();
                            dialog.dismiss();
                            return;
                        }
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
                    DialogUtils.dialogDoisBotoesPersonalizados(
                            activity,
                            "Atenção",
                            MensagensNetwork.setMensagem(volleyError, activity),
                            "Apostar mais",
                            "Entendi",
                            new OnDialogDoisBotoesListener() {
                                @Override
                                public void PositiveButton(DialogInterface dialog, int which) {
                                    //PrincipalActivity_.intent(activity).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                                    Intent intent = IntentUtil.getIntentOrigemDestino(activity, PrincipalActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    activity.startActivity(intent);
                                }

                                @Override
                                public void NegativeButton(DialogInterface dialog, int which) {
                                    //Sem ação, dialog.dismiss() já é chamado automaticamente após clicar no botão
                                }
                            }
                    );

                    break;
                case "018003":
                    enviaRegistrarMp(activity, volleyError);
                    break;
                case "002020":
                case "002022":
                    trataBloqueioTotal(error, activity);
                    break;
                default:
                    DialogUtils.dialogEntendi(
                            activity,
                            montarMensgem(activity, MensagensNetwork.setMensagem(volleyError, activity), error.getCodigo())
                    );
                    break;

            }
        }else {
            DialogUtils.dialogEntendi(
                    activity,
                    montarMensgem(activity, MensagensNetwork.setMensagem(volleyError, activity), error.getCodigo())
            );
        }
    }

    public static void checkRedirectSucesso(RedirectEnum redirect, Activity activity){
        switch (redirect){
            case ACEITAR_TERMO_DE_USO:
                verificaRedirectTermo(activity);
                break;
            case LOGIN:
                connectKeycloak(activity, null);
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
    public static void connectKeycloak(final Activity activity, Bundle bolao) {
        if(AppUtils.isNetworkAvailable(activity)){
            AcessoBO.getInstance().iniciarLoginSSO(activity, bolao);
        } else {
            try {
                if (activity != null) {
                    DialogUtils.dialogEntendi(
                            activity,
                            activity.getString(R.string.seminternet)
                    );
                } else if (Aplicacao.application.getBaseContext() != null){
                    DialogUtils.dialogEntendi(
                            Aplicacao.application.getBaseContext(),
                            Aplicacao.application.getBaseContext().getString(R.string.seminternet)
                    );
                }
            } catch (Exception e){}
        }
    }

    public static void trataErro500(Activity activity, ErrorResponse error){
        if (error != null) {
            if (error.getCodigo() != null && error.getCodigo().equalsIgnoreCase("2067")) {
                DialogUtils.dialogEntendi(
                        activity,
                        MercadoPagoUtil.getMessageFromCode("2067")
                );
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
        DialogUtils.dialogEntendi(
                activity,
                mensagem
        );
    }

    private static void enviaRegistrarMp(Activity activity, VolleyError volleyError){
        DialogUtils.dialogSim(
                activity,
                MensagensNetwork.setMensagem(volleyError, activity),
                new OnDialogBotaoListener() {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        JogosConfirmadosPremioMercadoPagoActivity jcpmpa = ((JogosConfirmadosPremioMercadoPagoActivity)activity);
                        jcpmpa.redirectMp();
                    }
                }
        );
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

    public static boolean isBloqueioTotal(String codigo){
        return codigo.equalsIgnoreCase("002020") || codigo.equalsIgnoreCase("002022");
    }

    public static void encerrarSessaoLocal(Activity activity) {
        String vazio = "";
        LoginSP.loginRealizado(false);
        KeycloakBO.getInstance().setAccessToken(vazio);
        KeycloakBO.getInstance().setRefreshToken(vazio);
        DadosUsuarioBO.limparRegistros();
        DadosBiometriaBO.limparRegistros();
        DadosUsuarioBO.updateUsuarioLogado(activity, false);
    }
    private static void sairSeguro(Activity activity) {
        try {
            new LoginSSOModel(activity).sair(LoginActivity.class, () -> { });
        } catch (Exception e) {
            AppCenterManager.registraEvento(AppCenterManager.ERRO_GENERICO);
        }
    }

    public static void trataFalhaVerificacaoBloqueio(VolleyError error, final Activity activity, String origem) {

        encerrarSessaoLocal(activity);

        ErrorResponse errorResponse = null;

        try{
            if(error != null && error.networkResponse != null && error.networkResponse.data != null){
                String body = new String(error.networkResponse.data,"UTF-8");
                Gson gsonResponse = new Gson();
                errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);
            }
        }catch ( Exception ignored){
            //cai na mensagem genérica
        }

        if(errorResponse != null && errorResponse.getCodigo() != null && isBloqueioTotal(errorResponse.getCodigo())){
            trataBloqueioTotal(errorResponse, activity);
            return;
        }

        FirebaseCrashlytics crash = FirebaseCrashlytics.getInstance();
        crash.setCustomKey(origem, "Erro na chamada de verificação de usuário bloqueado");

        DialogUtils.dialogEntendiListener(activity,
                activity.getString(R.string.nao_concluiu_operacao_nova),
                (dialog, which) -> {
                    if(error != null){
                        crash.recordException(error);
                    }
                    sairSeguro(activity);
                    activity.finish();
                });
    }
}