package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.CopiaColaPixActivity;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;
import br.gov.caixa.loterias.apostas.view.listener.OnVerificaPagamentoPix;

public class CompraPixUtil {
    private static final int SECONDS = 5;
    private static int CHAMADAS_REALIZADAS = 0;

    public static void checkStatusCompraPeriodicamente(final Context context, GerarPixDTO pix,
                                                    Long idCompraCarrinho, final OnCompraAsync listener) {
        CHAMADAS_REALIZADAS++;
        checkagemPix(context, pix, idCompraCarrinho, listener, true, onChecaPeriodicamente());
    }

    private static OnVerificaPagamentoPix onChecaPeriodicamente() {
        return (VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context) ->
                trataVerificaProcessamentoPeriodicamente(error, pix, listener, context);
    }

    private static void checkagemPix(Context context, GerarPixDTO pix, Long idCompraCarrinho, OnCompraAsync listener, boolean buscaPeriodicamente, OnVerificaPagamentoPix checagem) {
        if (isActivityRunning() && pix.estaNoPrazo()) {
            //Utils.delay(SECONDS, () -> ServicoFactoryUtil.getApostaService().verificaCompraProcessamento(idCompraCarrinho, new RequestListener<CompraAsyncResponse>() {
            Utils.delay(SECONDS, () -> ServicoFactoryUtil.getComprasService().verificaCompraProcessamento(idCompraCarrinho, new RequestListener<CompraAsyncResponse>() {
                @Override
                public void onResponse(CompraAsyncResponse result) {
                    if (result.getPayload() != null && SituacaoCompraUtil.isSituacaoFINALIZADA(result.getPayload().getIdSituacaoCompra())){
                        zerarRepeticoes();
                        listener.successCompra();
                    } else {
                        if (buscaPeriodicamente){
                            if (CHAMADAS_REALIZADAS <= result.getPayload().getRepeticoes()){
                                checkStatusCompraPeriodicamente(context, pix, idCompraCarrinho, listener);
                            } else {
                                zerarRepeticoes();
                                listener.successCompra();
                            }
                        } else {
                            zerarRepeticoes();
                            listener.errorCompra(null);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    checagem.check(error, pix, listener, context);
                }
            }));
        }
    }

    private static void zerarRepeticoes() {
        CHAMADAS_REALIZADAS = 0;
    }

    private static boolean isActivityRunning() {
        return CopiaColaPixActivity.isRunning || DetalhesComprasActivity.isRunning;
    }

    private static void trataVerificaProcessamentoPeriodicamente(VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context) {
        CompraAsyncResponse response = CompraUtil.trataErroRequisicao(error, listener);
        if (response != null) {
            if (response.getPayload() != null && SituacaoCompraUtil.isSituacaoFINALIZADA(response.getPayload().getIdSituacaoCompra())){
                zerarRepeticoes();
                listener.successCompra();
            } else {
                checkStatusCompraPeriodicamente(context, pix, pix.getIdCompra(), listener);
            }
        } else {
            CHAMADAS_REALIZADAS = 0;
        }
    }

    private static void trataVerificaProcessamento(VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context) {
        CompraAsyncResponse response = CompraUtil.trataErroRequisicao(error, listener);
        if (response != null) {
            if (response.getPayload() != null && SituacaoCompraUtil.isSituacaoFINALIZADA(response.getPayload().getIdSituacaoCompra())){
                listener.successCompra();
            } else {
                listener.errorCompra(error);
            }
        }
    }

    public static void checkStatusCompra(final Context context, GerarPixDTO pix,
                                         Long idCompraCarrinho, final OnCompraAsync listener) {
        checkagemPix(context,pix, idCompraCarrinho, listener, false, onCheckPix());
    }

    private static OnVerificaPagamentoPix onCheckPix() {
        return (error, pix, listener, context) -> trataVerificaProcessamento(error, pix, listener, context);
    }
}
