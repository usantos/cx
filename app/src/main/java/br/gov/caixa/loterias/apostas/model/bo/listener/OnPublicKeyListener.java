package br.gov.caixa.loterias.apostas.model.bo.listener;

import com.android.volley.VolleyError;

public interface OnPublicKeyListener {

	void success();

	void failed(String url, VolleyError volleyError);

}
