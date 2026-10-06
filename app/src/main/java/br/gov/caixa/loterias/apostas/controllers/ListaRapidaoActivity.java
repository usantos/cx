package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrinhoApostasListViewRapidaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

public class ListaRapidaoActivity extends LoteriasBaseAppActivity {

    private ExpandableHeightRecyclerView recyclerView;
    public List<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private TextView valorTotalCarrinhoRapidao;
    private ImageButton btnConfiguracao;

    //fragments
    private SomadorCarrinhoFragment somadorCarrinhoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rapidao);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configuraActivityCarrinho();

        recyclerView = findViewById(R.id.apostasListaRecycleViewRapidao);
        btnConfiguracao = findViewById(R.id.ib_configuracao_rpd);

        valorTotalCarrinhoRapidao = findViewById(R.id.tv_valor_total_carrinho);
        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_lista_apostas_rapidao));

        ViewUtils.organizaTabLayout(this, R.id.carrinhoRapidaoMainLayout, R.id.apostasListaRecycleViewRapidao, R.id.carrinhoRapidaoScrollView);

        btnConfiguracao.setVisibility(View.VISIBLE);
        btnConfiguracao.setOnClickListener(view -> startActivity(new Intent(ListaRapidaoActivity.this, ConfiguracaoRapidaoActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (apostas == null) {
            callWebservice(0);
        }
    }

    public void atualizaLayout() {
        if (apostas != null) {
            configuraRecyclerViewCarrinhoAposta();
            FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_somador_carrinho, "TELA CARRINHO RAPIDAO");
        }
        configuraListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice(0);
        }
    }

    private void configuraActivityCarrinho() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }

    private void configuraListeners() {
        ((Button) findViewById(R.id.botaoAvancarPagamentoRapidao)).setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));


        findViewById(R.id.botaoFazerNovaApostaRapidao).setOnClickListener(view -> callWebservice(0));


        findViewById(R.id.botaoAvancarPagamentoRapidao).setOnClickListener(view -> validarCarrinho());
    }
    private void validarCarrinho() {
        if(valorTotalCarrinhoRapidao.getText().toString().equals("R$ 0,00")) {
            DialogUtils.dialogEntendi(ListaRapidaoActivity.this, getResources().getString(R.string.verCarrinhoVazio));
            return;
        }

        AlertDialogUtils.show(ListaRapidaoActivity.this);
        ServicoFactoryUtil.getApostaService().validarCarrinho(new RequestListener<ResourceResponse>() {
            @Override
            public void onResponse(ResourceResponse result) {
                AlertDialogUtils.dismiss();
                //startActivity(new Intent(ListaRapidaoActivity.this, CartoesActivity.class));
                startActivity(new Intent(ListaRapidaoActivity.this, FormaPagamentoActivity.class));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, ListaRapidaoActivity.this);

            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta() {
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        CarrinhoApostasListViewRapidaoAdapter carrinhoApostasListViewRapidaoAdapter =
                new CarrinhoApostasListViewRapidaoAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) apostas, this);
        recyclerView.setAdapter(carrinhoApostasListViewRapidaoAdapter);
        carrinhoApostasListViewRapidaoAdapter.setOnCarrinhoApostasRapidaoListener(apostaDas8Modalidades -> callWebserviceRemoveItem(apostaDas8Modalidades));
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setExpanded(true);
        valorTotal();
    }

    private void callWebserviceRemoveItem(IdentificaoDeUmaApostaDas8Modalidades identificaoDeUmaApostaDas8Modalidades){
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        ServicoFactoryUtil.getApostaService().deleteApostaCarrinho(identificaoDeUmaApostaDas8Modalidades.getId().toString(), new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                loadViewProgress.dismiss();
                CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
                apostas = null;
                apostas = CarrinhoSingleton.getInstance().getCarrinho().getApostas();
                atualizaLayout();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaRapidaoActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaRapidaoActivity.this );
            }
        });

    }

    private void callWebservice(int quantidade) {

//        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
//        ApostaSilceBO.getInstance().gerarApostas(quantidade, new RequestListener<CarrinhoDTOResponse>() {
//            @Override
//            public void onResponse(CarrinhoDTOResponse response) {
//                loadViewProgress.dismiss();
//                AppCenterManager.registraEvento("GEROU_LISTA_APOSTAS_RAPIDAO");
//
//                CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
//                apostas = null;
//                apostas = CarrinhoSingleton.getInstance().getCarrinho().getApostas();
//                atualizaLayout();
//                if (response.getRedirect() != null){
//                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaRapidaoActivity.this);
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loadViewProgress.dismiss();
//                RedirectNetwork.checkRedirect( error, ListaRapidaoActivity.this );
//            }
//        });
    }

    private void valorTotal(){
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (IdentificaoDeUmaApostaDas8Modalidades aposta: apostas){
            valorTotal = valorTotal.add(aposta.getValor());
        }

        valorTotalCarrinhoRapidao.setText(ViewUtils.getMoedaFormat(valorTotal));
    }
}
