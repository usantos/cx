package br.gov.caixa.loterias.apostas.controllers;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.scottyab.rootbeer.RootBeer;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.MercadoPagoUtil;
import br.gov.caixa.loterias.apostas.view.fragment.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RootBeer rootBeer = new RootBeer(this);
        if (rootBeer.isRooted()) {
            montarMensagem("Desative modo root para usar o aplicativo");
        } else {
            setContentView(R.layout.activity_login);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new LoginFragment());
            fragmentTransaction.commit();
        }

    }

    private static String montarMensagem(String codError) {
        String codErrorString = codError == null ? "" : codError;
        String mensagemCompleta = MercadoPagoUtil.getMessageFromCode(codErrorString) + "\n\n";
        if (codError != null && codError != "MP_PAYMENT_METHOD_NOT_FOUND") {
            mensagemCompleta += codErrorString + "\n";
        }
        mensagemCompleta += DateUtils.getDateTime();
        return mensagemCompleta;
    }

}
