package br.gov.caixa.loterias.apostas.model.bo.listener;

import com.android.volley.VolleyError;

public interface OnSilceListener<T> {

	void success(T payload);

	void error(VolleyError error);

}
