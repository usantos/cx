package br.gov.caixa.loterias.apostas.model.repository;

import android.app.Activity;

import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SilceResponse;

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
