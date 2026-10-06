package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.os.Handler;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.ParametrosPagamento;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class CompraUtil {

    private static void verificaCompraProcessamento(final Context context, Long idCompraCarrinho, int posicao, final OnCompraAsync listener) {
        //ServicoFactoryUtil.getApostaService().verificaCompraProcessamento(idCompraCarrinho, new RequestListener<CompraAsyncResponse>() {
        ServicoFactoryUtil.getComprasService().verificaCompraProcessamento(idCompraCarrinho, new RequestListener<CompraAsyncResponse>() {
            @Override
            public void onResponse(CompraAsyncResponse result) {
                listener.successCompra();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                trataVerificaProcessamento(error, listener, context, posicao+1);
            }
        });
    }

    public static void registraApostaAsync(int meioPag, String tokenPagamento, String payment_method_id, String savedCard, String saveCard, final OnCompraAsync listener) {
        ParametrosPagamento parametroPagamento = new ParametrosPagamento( tokenPagamento, savedCard, payment_method_id, saveCard );
        MeioPagamento meioPagamento = new MeioPagamento(meioPag, parametroPagamento);

        Context context = Aplicacao.application.getApplicationContext();
        //ServicoFactoryUtil.getApostaService().postRegistrarApostaCarrinhoAsync(meioPagamento, new RequestListener<CompraAsyncResponse>() {
        ServicoFactoryUtil.getComprasService().postRegistrarApostaCarrinhoAsync(meioPagamento, new RequestListener<CompraAsyncResponse>() {
            @Override
            public void onResponse(CompraAsyncResponse response) {
                if (verificaRedirect(response)) {
                    listener.redirectCompra(response);
                } else {
                    chamaVerificaCompraProcessamento(context, response, listener, 0);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                trataVerificaProcessamento(error, listener, context, 0);
            }
        });
    }

    public static CompraAsyncResponse trataErroRequisicao(VolleyError error, OnCompraAsync listener) {
        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 303 && error.networkResponse.data != null) {
            CompraAsyncResponse compraAsyncResponse = converteCompraAsyncResponse(error);
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

    private static void chamaVerificaCompraProcessamento(final Context context, CompraAsyncResponse response, final OnCompraAsync listener, int posicao){
        Long periodicidade = response.getPayload().getPeriodicidade();
        Long repeticoes = response.getPayload().getRepeticoes();
        Long idCompra = response.getPayload().getId();
        if (posicao < repeticoes) {
            new Handler().postDelayed(() -> {
                verificaCompraProcessamento(context, idCompra, posicao, listener);
            }, periodicidade);
        } else {
            listener.successCompra();
        }
    }

    private static boolean verificaRedirect(CompraAsyncResponse response) {
        if (response.getRedirect() != null && response.getRedirect() != RedirectEnum.VERIFICA_COMPRA_PROCESSAMENTO) {
            return true;
        } else {
            return false;
        }
    }

    private static void trataVerificaProcessamento(VolleyError error, OnCompraAsync listener, Context context, int posicao) {
        CompraAsyncResponse compraAsyncResponse = trataErroRequisicao(error, listener);
        if (compraAsyncResponse != null) {
            chamaVerificaCompraProcessamento(context, compraAsyncResponse, listener, posicao);
        }
    }

    private static CompraAsyncResponse converteCompraAsyncResponse(VolleyError error) {
        String body;
        Gson gsonResponse = new Gson();
        CompraAsyncResponse compraAsyncResponse = null;
        try {
            body = new String(error.networkResponse.data,"UTF-8");
            compraAsyncResponse = gsonResponse.fromJson(body, CompraAsyncResponse.class);

        } catch (UnsupportedEncodingException e) {
        } catch (Exception e){
        }
        return compraAsyncResponse;
    }
}
