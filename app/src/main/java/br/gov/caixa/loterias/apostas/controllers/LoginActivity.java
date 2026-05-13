package br.gov.caixa.loterias.apostas.controllers;


import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.TopazUtil;
import br.gov.caixa.loterias.apostas.view.fragment.LoginFragment;

public class LoginActivity extends LoteriasBaseAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TopazUtil.check(this);

        AnalyticsHelper.getInstance().logViewScreen(
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.ENTRY,
                AnalyticsHelper.Tela.LOGIN
        );

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new LoginFragment());
        fragmentTransaction.commit();

    }

}
