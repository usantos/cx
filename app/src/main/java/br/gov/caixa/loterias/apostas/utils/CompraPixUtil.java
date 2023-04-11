package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostas.CopiaColaPixActivity;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class CompraPixUtil {

    public static void checkStatusCompra(final Context context, GerarPixDTO pix,
                                                    Long idCompraCarrinho, final OnCompraAsync listener) {
        if (isActivityRunning() && pix.estaNoPrazo()) {
            Utils.delay(5, () -> {
                ApostaBO_.getInstance_(context)
                         .verificaCompraProcessamento(idCompraCarrinho, new RequestListener<CompraAsyncResponse>() {
                    @Override
                    public void onResponse(CompraAsyncResponse result) {
                        listener.successCompra();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        trataVerificaProcessamento(error, pix, listener, context);
                    }
                });
            });
        }
    }

    private static boolean isActivityRunning() {
        return CopiaColaPixActivity.isRunning;
    }

    private static CompraAsyncResponse trataErroRequisicao(VolleyError error, OnCompraAsync listener) {
        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 303 && error.networkResponse.data != null) {
            CompraAsyncResponse compraAsyncResponse = buscaCompraAsyncResponse(error);
            if (compraAsyncResponse != null) {
                if (verificaRedirect(compraAsyncResponse)) {
                    listener.redirectCompra(compraAsyncResponse);
                } else {
                    if (compraAsyncResponse.getCodigo() != null || compraAsyncResponse.getPayload() == null) {
                        listener.errorCompra(error);
                    } else {
                        return compraAsyncResponse;
                    }
                }
            } else {
                listener.errorCompra(error);
            }
        } else {
            listener.errorCompra(error);
        }
        return null;
    }

    private static boolean verificaRedirect(CompraAsyncResponse response) {
        if (response.getRedirect() != null && response.getRedirect() != RedirectEnum.VERIFICA_COMPRA_PROCESSAMENTO) {
            return true;
        } else {
            return false;
        }
    }

    private static void trataVerificaProcessamento(VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context) {
        CompraAsyncResponse response = trataErroRequisicao(error, listener);
        if (response != null) {
            checkStatusCompra(context, pix, pix.getIdCompra(), listener);
        }
    }

    private static CompraAsyncResponse buscaCompraAsyncResponse(VolleyError error) {
        String body;
        Gson gsonResponse = new Gson();
        CompraAsyncResponse compraAsyncResponse = null;
        try {
            body = new String(error.networkResponse.data,"UTF-8");
            compraAsyncResponse = gsonResponse.fromJson(body, CompraAsyncResponse.class);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return compraAsyncResponse;
    }
}
