package br.gov.caixa.loterias.apostas.view.listener;

import android.content.Context;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;

public interface OnVerificaPagamentoPix {
    void check(VolleyError error, GerarPixDTO pix, OnCompraAsync listener, Context context);

}
