package br.gov.caixa.loterias.apostas.model.bo.listener;

import com.android.volley.VolleyError;

public interface OnRefreshTokenListener {

    void successRefresh();
    void errorRefresh(VolleyError volleyError);

}
