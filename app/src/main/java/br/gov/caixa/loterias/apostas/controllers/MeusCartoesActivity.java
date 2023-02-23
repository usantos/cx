package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;
import br.gov.caixa.loterias.apostas.view.listener.MeusCartoesListener;

public class MeusCartoesActivity extends AppCompatActivity implements MeusCartoesListener {

    private Toolbar toolbar;


    private CartoesModel model;
    private TextView tvSemCartao;
    private NestedScrollView scrollCartoes;
    private List<RetornoCartao> payloadMp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_cartoes);
        pegaExtras();
        setaViews();
        model = new CartoesModel(MeusCartoesActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buscaCartoesMercadoPago();
    }

    private void buscaCartoesMercadoPago(){
        AlertDialogUtils.show(MeusCartoesActivity.this);
        model.buscaCartoesPorMeioPagamento("1", onBuscaCartaoMercadoPago());
    }

    @NonNull
    private OnSilceListener<List<RetornoCartao>> onBuscaCartaoMercadoPago() {
        return new OnSilceListener<List<RetornoCartao>>() {
            @Override
            public void success(List<RetornoCartao> payload) {
                payloadMp = payload;
                if (payload != null && !payload.isEmpty()){
                    FragmentUtils.meusCartoesFragment(getSupportFragmentManager(),
                                                      R.id.frameMercadoPagoFragment,
                                                      payload, getConfigMercadoPago());
                }

                buscaCartoesRecargaPay();
            }

            @Override
            public void error(VolleyError error) {
                buscaCartoesRecargaPay();
            }
        };
    }

    private void buscaCartoesRecargaPay(){
        if (!AlertDialogUtils.isShow()){
            AlertDialogUtils.show(MeusCartoesActivity.this);
        }
        model.buscaCartoesPorMeioPagamento("5", onBuscaCartaoRecargaPay());
    }

    private OnSilceListener<List<RetornoCartao>> onBuscaCartaoRecargaPay() {
        return new OnSilceListener<List<RetornoCartao>>() {
            @Override
            public void success(List<RetornoCartao> payload) {
                AlertDialogUtils.dismiss();
                configuraTela(payloadMp, payload);

                if (payload != null && !payload.isEmpty()){
                    FragmentUtils.meusCartoesFragment(getSupportFragmentManager(),
                                                      R.id.frameRecargaPayFragment,
                                                      payload, getConfigRecargaPay());
                }
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                configuraTela(payloadMp, null);
            }
        };
    }

    private void configuraTela(List<RetornoCartao> payloadMP, List<RetornoCartao> payloadRP) {
        if ((payloadMP == null || payloadMP.isEmpty()) && (payloadRP == null || payloadRP.isEmpty())){
            tvSemCartao.setVisibility(View.VISIBLE);
            scrollCartoes.setVisibility(View.GONE);
        } else {
            tvSemCartao.setVisibility(View.GONE);
            scrollCartoes.setVisibility(View.VISIBLE);
        }
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

        tvSemCartao = findViewById(R.id.tv_sem_cartao);
        scrollCartoes = findViewById(R.id.nsv_conteudo);

        setTitle(getString(R.string.meus_cartoes));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void semCartao() {
        //configuraTela(null, null);
    }
}