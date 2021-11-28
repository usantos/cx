package br.gov.caixa.loterias.apostas.view.listener;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;

public interface OnCompraAsync {
    void successCompra();
    void redirectCompra(CompraAsyncResponse compraAsyncResponse);
    void errorCompra(VolleyError volleyError);
}
