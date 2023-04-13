package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.CopiaColaPixActivity;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class CompraPixUtil {
    private static final int SECONDS = 5;

    public static void checkStatusCompra(final Context context, GerarPixDTO pix,
                                                    Long idCompraCarrinho, final OnCompraAsync listener) {
        if (isActivityRunning() && pix.estaNoPrazo()) {
            Utils.delay(SECONDS, () -> {
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

    private static void trataVerificaProcessamento(VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context) {
        CompraAsyncResponse response = CompraUtil.trataErroRequisicao(error, listener);
        if (response != null) {
            if (response.getPayload() != null && response.getPayload().getIdSituacaoCompra() == 8){
                listener.successCompra();
            } else {
                checkStatusCompra(context, pix, pix.getIdCompra(), listener);
            }
        }
    }
}
