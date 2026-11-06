package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLHandshakeException;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.ErrorRecargaPayResponse;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppUtils;

/**
 * Created by cedesbr450 on 08/12/17.
 */

public class MensagensNetwork {
    private static final int UNAUTHORIZED = 401;
    private static final int TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;

    private MensagensNetwork(){}
    private static Throwable getRootCause(Throwable t) {
        Throwable cause = t;
        while (cause != null && cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }

    private static boolean isSslError(Throwable t) {
        if (t == null) return false;
        return (t instanceof SSLHandshakeException) ||
                (t instanceof CertificateException) ||
                (t instanceof CertPathValidatorException);
    }

    private static boolean isConnectivityError(Throwable t) {
        if (t == null) return false;
        return (t instanceof UnknownHostException) ||
                (t instanceof ConnectException) ||
                (t instanceof SocketTimeoutException);
    }

    public static String setMensagemRecargaPay(VolleyError error, Context context){
        NetworkResponse response = error.networkResponse;
        boolean isNetworkAvailable = true;
        isNetworkAvailable = AppUtils.isNetworkAvailable(context.getApplicationContext());

        if (!isNetworkAvailable){
            return context.getString(R.string.seminternet);
        }
        else {
            if (response == null){
                final Throwable root = getRootCause(error);
                Log.e("caixa", "Sem HTTP response. Classe=" + error.getClass().getSimpleName()
                        + " Root=" + (root != null ? root.getClass().getSimpleName() : "null"), error);

                if (error instanceof TimeoutError || (root instanceof SocketTimeoutException)) {
                    return context.getString(R.string.erro_408);
                }


                if (error instanceof AuthFailureError) {
                    return context.getString(R.string.erro_401);
                }

                if (isSslError(root) || isConnectivityError(root) || error instanceof NoConnectionError) {
                    return context.getString(R.string.connectionServerErro);
                }

                return context.getString(R.string.sistema_indisponivel);
            }

            Gson gsonResponse = new Gson();

            ErrorRecargaPayResponse errorResponse = new ErrorRecargaPayResponse();

            String body;
            //get response body and parse with appropriate encoding
            if(error.networkResponse.data!=null) {
                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    errorResponse = gsonResponse.fromJson(body, ErrorRecargaPayResponse.class);
                } catch (Exception e){
                    errorResponse = new ErrorRecargaPayResponse();
                    errorResponse.setMessage( context.getString( R.string.sistema_indisponivel ) + " - " + response.statusCode);
                }
            }

            return errorResponse.getMessage();
        }
    }
    public static String setMensagem(VolleyError error, final Context context) {
        NetworkResponse response = error.networkResponse;
        boolean isNetworkAvailable = AppUtils.isNetworkAvailable(context.getApplicationContext());

        if (!isNetworkAvailable){
            return context.getString(R.string.seminternet);
        }

        if (response == null){
            final Throwable root = getRootCause(error);
            Log.e("caixa", "Sem HTTP response. Classe=" + error.getClass().getSimpleName()
                    + " Root=" + (root != null ? root.getClass().getSimpleName() : "null"), error);

            if (error instanceof TimeoutError || (root instanceof SocketTimeoutException)) {
                    return context.getString(R.string.erro_408);
                }

                if (error instanceof AuthFailureError) {
                    return context.getString(R.string.erro_401);
                }

                if (isSslError(root) || isConnectivityError(root) || error instanceof NoConnectionError) {
                    return context.getString(R.string.connectionServerErro);
                }

                return context.getString(R.string.sistema_indisponivel);
            }

            Gson gsonResponse = new Gson();
            ErrorResponse errorResponse = new ErrorResponse();

            String body;
            //get response body and parse with appropriate encoding
            if(error.networkResponse.data!=null) {
                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    errorResponse = gsonResponse.fromJson(body, ErrorResponse.class);
                } catch (Exception e){
                    errorResponse = new ErrorResponse();
                    errorResponse.setMensagem( context.getString( R.string.sistema_indisponivel ) + " - " + response.statusCode);
                }
            }

        if (errorResponse == null || errorResponse.getMensagem() == null || errorResponse.getMensagem().isEmpty()) {
            errorResponse = new ErrorResponse();
        }

        // Tratamento por código de status HTTP
        switch (response.statusCode) {
            case UNAUTHORIZED:
                errorResponse.setMensagem(context.getString(R.string.erro_401));
                break;
            case TIMEOUT:
                errorResponse.setMensagem(context.getString(R.string.erro_408));
                break;
            case INTERNAL_SERVER_ERROR:
                errorResponse.setMensagem(context.getString(R.string.erro_500));
                break;
            default:
                if (errorResponse.getMensagem() == null || errorResponse.getMensagem().isEmpty()) {
                    errorResponse.setMensagem(context.getString(R.string.nao_concluiu_operacao));
                }
                break;
        }

        return errorResponse.getMensagem();
    }

    public static String getErrorMessage(VolleyError error){
        try{
            String errorBody = Bin.fromBytes(error.networkResponse.data).toUtf8();
            return new Gson().fromJson(errorBody, ErrorResponse.class).getMensagem();
        } catch (Exception e){
            return  Aplicacao.application.getApplicationContext().getString(R.string.nao_concluiu_operacao);
        }

    }

    public static ErrorResponse getError(VolleyError error){
        try{
            String errorBody = Bin.fromBytes(error.networkResponse.data).toUtf8();
            return new Gson().fromJson(errorBody, ErrorResponse.class);
        } catch (Exception e){
            return null;
        }

    }

    public static boolean isUnauthorizedError(VolleyError error){
        NetworkResponse response = error.networkResponse;
        if (response != null){
            return response.statusCode == UNAUTHORIZED;
        }
        return false;
    }

    public static boolean isCertificadoInvalido(VolleyError error) {
        if (error != null && error.getLocalizedMessage() != null && error.getLocalizedMessage().contains("SSLHandshakeException")) {
            return true;
        }
        return false;
    }
}
