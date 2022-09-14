package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;

public class MeusCartoesActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private CartoesModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_cartoes);
        pegaExtras();
        setaViews();
        model = new CartoesModel(MeusCartoesActivity.this);

        model.buscaCartoesMercadoPago(onBuscaCartaoMercadoPago());


//        FragmentUtils.meusCartoesFragment(getSupportFragmentManager(),
//                                          R.id.frameRecargaPayFragment,
//                                          null, getConfigRecargaPay());
    }

    private List<RetornoCartao> getListCartoes() {
        List<RetornoCartao> lista = new ArrayList<>();

        RetornoCartao cartaofav = new RetornoCartao();
        cartaofav.setFav(false);
        cartaofav.setUltimosDigitos("1234");
        cartaofav.setIdMetodoPagamento("master");
        cartaofav.setMesValidade(2L);
        cartaofav.setAnoValidade(2025L);
        cartaofav.setIdCartao(1L);

        RetornoCartao cartao = new RetornoCartao();
        cartao.setFav(false);
        cartao.setUltimosDigitos("3456");
        cartao.setIdMetodoPagamento("hipercard");
        cartao.setIdCartao(2L);
        cartao.setMesValidade(2L);
        cartao.setAnoValidade(2025L);

        RetornoCartao cartao3 = new RetornoCartao();
        cartao3.setFav(false);
        cartao3.setUltimosDigitos("5678");
        cartao3.setIdMetodoPagamento("visa");
        cartao3.setIdCartao(3L);
        cartao3.setMesValidade(2L);
        cartao3.setAnoValidade(2025L);

        RetornoCartao cartao4 = new RetornoCartao();
        cartao4.setFav(false);
        cartao4.setUltimosDigitos("7890");
        cartao4.setIdMetodoPagamento("amex");
        cartao4.setIdCartao(4L);
        cartao4.setMesValidade(2L);
        cartao4.setAnoValidade(2025L);

        lista.add(cartaofav);
        lista.add(cartao);
        lista.add(cartao3);
        lista.add(cartao4);

        return lista;
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
        return new MeusCartoesConfig(R.color.azul_meu_cartao, R.drawable.ic_mercado_pago, R.string.confira_cartoes_salvos_mercado_pago, MeioPagamentoUtils.MERCADO_PAGO);
    }

    private MeusCartoesConfig getConfigRecargaPay() {
        return new MeusCartoesConfig(R.color.laranja_recarga_pay, R.drawable.recargapay, R.string.confira_cartoes_salvos_recargapa_pay, MeioPagamentoUtils.RECARGAPAY);
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