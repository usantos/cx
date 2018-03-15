package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroSimulacao;
import caixa.gov.br.loterias.model.dao.DBLoteriasCrud;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.CarrinhoApostasListViewAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class CarrinhoActivity extends SettingToolbarActivity {

    public CarrinhoDTO carrinho;
    private ExpandableHeightRecyclerView recyclerView;
    private List<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private TextView valorTotalCarrinho;
    TextView infoValorMinimoTextView;
    private RecyclerView.LayoutManager layoutManager;

    List<IdentificaoDeUmaApostaDas8Modalidades> listIdentificaoDeUmaApostaDas8Modalidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        valorTotalCarrinho = findViewById(R.id.valorTotalCarrinho);
        ViewUtils.setMoedaFormatHtml(BigDecimal.ZERO, this.valorTotalCarrinho);

        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_carrinho)));

        recyclerView = findViewById(R.id.apostasListaRecycleView);
        layoutManager = new LinearLayoutManager(this);

        callWebservice();
    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        infoValorMinimoTextView = (TextView) findViewById(R.id.infoValorMinimo);
        ViewUtils.setMoedaFormatHtml(valorCarrinho, infoValorMinimoTextView, getResources().getString(R.string.label_lembrar_valor_minimo).replace("_", "<br>") + " ");
    }

    private void callWebservice() {

        final Dialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = new ApostaBO(CarrinhoActivity.this);

        apostaBO.buscaCarrinho(new SilceClient.Listener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                loadViewProgress.dismiss();
                carrinho = response.getPayload();
                atualizaLayout(Boolean.TRUE);
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                if (error.networkResponse == null || error.networkResponse.statusCode == 401) {
                    lerCarrinhoLocal(Boolean.TRUE);
                }else{
                    ViewUtils.alert(CarrinhoActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
                }
            }
        });
    }

    public void lerCarrinhoLocal(Boolean animacao) {
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        carrinho = new CarrinhoDTO();
        carrinho.setApostas(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());
        atualizaLayout(animacao);
    }

    public void atualizaLayout(boolean animacao) {
        montarLayout();
        if (carrinho.getApostas() != null) {
            configuraRecyclerViewCarrinhoAposta(animacao);
            setValorTotalCarrinho();
        }
        configuraListeners();
    }

    private void configuraListeners() {
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));


        findViewById(R.id.botaoFazerNovaAposta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(2);
                finish();
            }
        });


        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(CarrinhoActivity.this, FormaPagamentoActivity.class);

                startActivity(activity);
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta(boolean animacao) {
        listIdentificaoDeUmaApostaDas8Modalidades = new ArrayList<IdentificaoDeUmaApostaDas8Modalidades>();
        for (IdentificaoDeUmaApostaDas8Modalidades identificaoDeUmaApostaDas8Modalidades : carrinho.getApostas()) {
            if (identificaoDeUmaApostaDas8Modalidades.getModalidade() != null){
                listIdentificaoDeUmaApostaDas8Modalidades.add(identificaoDeUmaApostaDas8Modalidades);
            }
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) listIdentificaoDeUmaApostaDas8Modalidades, this, animacao));
        recyclerView.setExpanded(Boolean.TRUE);

    }

    private void setValorTotalCarrinho() {

        BigDecimal valorTotalCarrinho = BigDecimal.ZERO;
        for (IdentificaoDeUmaApostaDas8Modalidades aposta : carrinho.getApostas()) {
            valorTotalCarrinho = valorTotalCarrinho.add(aposta.getValor());
        }

        ViewUtils.setMoedaFormatHtml(valorTotalCarrinho, this.valorTotalCarrinho);
    }
}
