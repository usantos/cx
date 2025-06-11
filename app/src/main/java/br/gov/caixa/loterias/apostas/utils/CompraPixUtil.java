package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.CopiaColaPixActivity;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;
import br.gov.caixa.loterias.apostas.view.listener.OnVerificaPagamentoPix;

public class CompraPixUtil {
    private static final int SECONDS = 5;

    public static void checkStatusCompraPeriodicamente(final Context context, GerarPixDTO pix,
                                                    Long idCompraCarrinho, final OnCompraAsync listener) {
        checkagemPix(context, pix, idCompraCarrinho, listener, onChecaPeriodicamente());
    }

    private static OnVerificaPagamentoPix onChecaPeriodicamente() {
        return (VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context) -> {
            trataVerificaProcessamentoPeriodicamente(error, pix, listener, context);
        };
    }

    private static void checkagemPix(Context context, GerarPixDTO pix, Long idCompraCarrinho, OnCompraAsync listener, OnVerificaPagamentoPix checagem) {
        if (isActivityRunning() && pix.estaNoPrazo()) {
            Utils.delay(SECONDS, () -> {
                ServicoFactoryUtil.getApostaService().verificaCompraProcessamento(idCompraCarrinho, new RequestListener<CompraAsyncResponse>() {
                    @Override
                    public void onResponse(CompraAsyncResponse result) {
                        listener.successCompra();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        checagem.check(error, pix, listener, context);
                    }
                });
            });
        }
    }

    private static boolean isActivityRunning() {
        return CopiaColaPixActivity.isRunning || DetalhesComprasActivity.isRunning;
    }

    private static void trataVerificaProcessamentoPeriodicamente(VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context) {
        CompraAsyncResponse response = CompraUtil.trataErroRequisicao(error, listener);
        if (response != null) {
            if (response.getPayload() != null && response.getPayload().getIdSituacaoCompra() == 8){
                listener.successCompra();
            } else {
                checkStatusCompraPeriodicamente(context, pix, pix.getIdCompra(), listener);
            }
        }
    }

    private static void trataVerificaProcessamento(VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context) {
        CompraAsyncResponse response = CompraUtil.trataErroRequisicao(error, listener);
        if (response != null) {
            if (response.getPayload() != null && response.getPayload().getIdSituacaoCompra() == 8){
                listener.successCompra();
            } else {
                listener.errorCompra(error);
            }
        }
    }

    public static void checkStatusCompra(final Context context, GerarPixDTO pix,
                                         Long idCompraCarrinho, final OnCompraAsync listener) {
        checkagemPix(context,pix, idCompraCarrinho, listener, onCheckPix());
    }

    private static OnVerificaPagamentoPix onCheckPix() {
        return (error, pix, listener, context) -> trataVerificaProcessamento(error, pix, listener, context);
    }
}
