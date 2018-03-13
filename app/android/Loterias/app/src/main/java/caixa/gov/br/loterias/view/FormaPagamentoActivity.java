package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.ListCartaoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.RetornoCartao;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.view.listener.FormaDePagamentoListener;
import caixa.gov.br.loterias.view.adapter.FormaPagamentoRecyclerViewAdapter;
import caixa.gov.br.loterias.utils.ViewUtils;

public class FormaPagamentoActivity extends SettingToolbarActivity implements FormaDePagamentoListener {
    private List<RetornoCartao> listaCartoes = null;
    private View itemToolbarSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pagamento);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_forma_pagamento)));
        
        configuraClickBotoes();
        callWebservice();

//        mockFormasPagamento();

        ViewUtils.organizaTabLayout(this, R.id.formaPagamentoMainLayout, R.id.formasPagamentoRecyclerView, R.id.formaPagamentoScrollView);
    }

    private void mockFormasPagamento(){
        RetornoCartao retornoCartao = new RetornoCartao();
        retornoCartao.setIdCartao(new Long(1));
        retornoCartao.setMesValidade(new Long(12));
        retornoCartao.setAnoValidade(new Long(2020));
        retornoCartao.setNomeMetodoPagamento("VISA");
        retornoCartao.setPrimeirosDigitos("DDDD");
        retornoCartao.setUltimosDigitos("AAAA");
        listaCartoes =  new ArrayList<>();
        listaCartoes.add(retornoCartao);
        listaCartoes.add(retornoCartao);
        listaCartoes.add(retornoCartao);
        listaCartoes.add(retornoCartao);
        listaCartoes.add(retornoCartao);
        listaCartoes.add(retornoCartao);
        listaCartoes.add(retornoCartao);
        if (listaCartoes != null && listaCartoes.size() > 0) {
            configuraRecyclerViewCarrinhoAposta();
        }
    }

    private void callWebservice() {
        final View loadViewProgress = LoadingViewLoterias.init(this);

        DadosUsuarioBO dadosUsuarioBO = new DadosUsuarioBO(FormaPagamentoActivity.this);
        dadosUsuarioBO.cartoes(new SilceClient.Listener<ListCartaoDTOResponse>() {
            @Override
            public void onResponse(ListCartaoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                listaCartoes = response.getPayload();
                if (listaCartoes != null && listaCartoes.size() > 0) {
                    configuraRecyclerViewCarrinhoAposta();
                }
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(FormaPagamentoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void configuraClickBotoes() {
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.apostarAutorizarCobranca)));
        ((Button) findViewById(R.id.botaoAdicionarFormaPagamento)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.adicionarFormaPagamento)));

        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(FormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
                startActivity(activity);
            }
        });

        final Button buttonAdicionarFormaPagamento = (findViewById(R.id.botaoAdicionarFormaPagamento));
        buttonAdicionarFormaPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(FormaPagamentoActivity.this, NovaFormaPagamentoActivity.class);
                startActivity(activity);
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta() {
        RecyclerView recyclerView = findViewById(R.id.formasPagamentoRecyclerView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        int sizeitem = ViewUtils.setDisplayMetric(75 * listaCartoes.size(), this);
        ConstraintLayout.LayoutParams layoutFormasPagamento = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, sizeitem);
        layoutFormasPagamento.topToBottom = R.id.textoInfoSelecioneFormaPagamento;
        layoutFormasPagamento.setMargins(0, ViewUtils.setDisplayMetric(40, this), 0, 0);
        recyclerView.setLayoutParams(layoutFormasPagamento);

        FormaPagamentoRecyclerViewAdapter formaPagamentoRecyclerViewAdapter = new FormaPagamentoRecyclerViewAdapter(listaCartoes, this);
        formaPagamentoRecyclerViewAdapter.formaDePagamentoListener = this;

        recyclerView.setAdapter(formaPagamentoRecyclerViewAdapter);
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void itemRemoved() {
        ViewUtils.organizaTabLayout(this, R.id.formaPagamentoMainLayout, R.id.formasPagamentoRecyclerView, R.id.formaPagamentoScrollView);
    }
}
