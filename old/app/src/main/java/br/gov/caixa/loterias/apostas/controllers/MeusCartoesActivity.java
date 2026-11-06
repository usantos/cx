package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.model.model.MeioPagamentoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;
import br.gov.caixa.loterias.apostas.view.listener.MeusCartoesListener;

public class MeusCartoesActivity extends LoteriasBaseAppActivity implements MeusCartoesListener {

    private Toolbar toolbar;


    private CartoesModel model;
    private TextView tvSemCartao;
    private NestedScrollView scrollCartoes;
    private List<RetornoCartao> payloadMp;
    private MeioPagamentoModel modelMeiosPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_cartoes);
        pegaExtras();
        setaViews();
        modelMeiosPagamento = new MeioPagamentoModel(MeusCartoesActivity.this);
        model = new CartoesModel(MeusCartoesActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buscaMeiosPagamentos();
    }

    private void buscaMeiosPagamentos() {
        AlertDialogUtils.show(this);
        modelMeiosPagamento.buscaMeiosPagamentos(onMeiosPagamentosListener());
    }

    private OnSilceListener<List<MeioPagamentoDTO>> onMeiosPagamentosListener() {
        return new OnSilceListener<List<MeioPagamentoDTO>>() {
            @Override
            public void success(List<MeioPagamentoDTO> payload) {
                if (payload != null && !payload.isEmpty()){
                    for (MeioPagamentoDTO meioPagamento: payload){

                        if (MeioPagamentoUtils.isMercadoPago(meioPagamento.getId())){
                            boolean temRecargaPay = MeioPagamentoUtils.isRecargaPay(meioPagamento.getId());

                            buscaCartoesMercadoPago(temRecargaPay);

                        }
                    }
                }

            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    private void buscaCartoesMercadoPago(boolean temRecargaPay){
        AlertDialogUtils.show(MeusCartoesActivity.this);
        model.buscaCartoesPorMeioPagamento(MeioPagamentoUtils.MP_MERCADO_PAGO, onBuscaCartaoMercadoPago(temRecargaPay));
    }

    @NonNull
    private OnSilceListener<List<RetornoCartao>> onBuscaCartaoMercadoPago(boolean temRecargaPay) {
        return new OnSilceListener<List<RetornoCartao>>() {
            @Override
            public void success(List<RetornoCartao> payload) {
                if (!temRecargaPay){
                    AlertDialogUtils.dismiss();
                }

                payloadMp = payload;
                if (payload != null && !payload.isEmpty()){
                    FragmentUtils.meusCartoesFragment(getSupportFragmentManager(),
                                                      R.id.frameMercadoPagoFragment,
                                                      payload, getConfigMercadoPago());
                }

                if(temRecargaPay){
                    buscaCartoesRecargaPay();
                } else {
                    configuraTela(payloadMp, null);
                }

            }

            @Override
            public void error(VolleyError error) {
                if(temRecargaPay){
                    buscaCartoesRecargaPay();
                } else {
                    configuraTela(null, null);
                }
            }
        };
    }

    private void buscaCartoesRecargaPay(){
        if (!AlertDialogUtils.isShow()){
            AlertDialogUtils.show(MeusCartoesActivity.this);
        }
        model.buscaCartoesPorMeioPagamento(MeioPagamentoUtils.MP_RECARGA_PAY, onBuscaCartaoRecargaPay());
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
        return new MeusCartoesConfig(R.color.azul_meu_cartao, R.drawable.ic_mercado_pago, R.string.confira_cartoes_salvos_mercado_pago, MeioPagamentoUtils.MERCADO_PAGO_VALUE);
    }

    private MeusCartoesConfig getConfigRecargaPay() {
        return new MeusCartoesConfig(R.color.laranja_recarga_pay, R.drawable.recargapay, R.string.confira_cartoes_salvos_recargapa_pay, MeioPagamentoUtils.RECARGAPAY_VALUE);
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

        setTitle(ViewUtils.textCaixaSTDBold(this, getString(R.string.meus_cartoes)));
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