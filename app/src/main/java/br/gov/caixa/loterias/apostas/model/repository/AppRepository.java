package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SilceResponse;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public abstract class AppRepository {

    private Activity activity;

    public AppRepository(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void checkRedirect(SilceResponse response){
        if (response.getRedirect() != null){
            RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
        }
    }
}
