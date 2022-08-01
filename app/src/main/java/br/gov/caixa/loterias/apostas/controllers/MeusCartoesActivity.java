package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;

public class MeusCartoesActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private CompraModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_cartoes);
        pegaExtras();
        setaViews();
        model = new CompraModel(MeusCartoesActivity.this);

        model.buscaCartoes(onBuscaCartaoMercadoPago());
    }

    @NonNull
    private OnSilceListener<List<RetornoCartao>> onBuscaCartaoMercadoPago() {
        AlertDialogUtils.show(MeusCartoesActivity.this);
        return new OnSilceListener<List<RetornoCartao>>() {
            @Override
            public void success(List<RetornoCartao> payload) {
                AlertDialogUtils.dismiss();
                FragmentUtils.meusCartoesFragment(getSupportFragmentManager(),
                                                  R.id.frameMercadoPagoFragment,
                                                  payload, getConfigMercadoPago());
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    private MeusCartoesConfig getConfigMercadoPago() {
        return new MeusCartoesConfig(R.color.azul_meu_cartao, R.drawable.mercadopago);
    }

    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

        }
    }

    private void setaViews(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.meus_cartoes));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}