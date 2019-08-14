package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostar.interno.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.CarrinhoApostasListViewAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;


public class CarrinhoActivity extends SettingToolbarActivity {

    public static String CARRINHO = "CARRINHO";
    public static String LER_CARRINHO_LOCAL = "LER_CARRINHO_LOCAL";

    private ExpandableHeightRecyclerView apostasListaRecycleView;
    private TextView valorTotalCarrinho, infoValorMinimo;
    private Button botaoAvancarPagamento, botaoFazerNovaAposta;
    private LinearLayout limparCarrinhoLinearLayout;
    private CarrinhoDTO carrinho;
    private boolean lerCarrinhoLocal = false;
    private boolean ocorrendoValidacao = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);
        pegaExtras();
        setaViews();
        setaMetodos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ocorrendoValidacao){
            validarCarrinho();
        }
        lerCarrinho();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }


    private void lerCarrinho() {
        if(getIntent().getComponent().getClassName().equals(CarrinhoActivity.class.getName())){
            lerCarrinhoLocal = false;
            carrinho = null;
        }
        if(lerCarrinhoLocal){
            FirebaseManager.registraEvento(CarrinhoActivity.this ,"ENTROU_CARRINHO_DE_APOSTAS_OFFLINE");
            lerCarrinhoLocal(true);
        }else{
            atualizaLayout(true);
        }
    }


    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            carrinho = Parcels.unwrap(bundle.getParcelable(CARRINHO));
            lerCarrinhoLocal = bundle.getBoolean(LER_CARRINHO_LOCAL);
        }
    }

    private void setaViews(){
        apostasListaRecycleView = findViewById(R.id.erv_apostas_listas);
        valorTotalCarrinho = findViewById(R.id.tv_valor_total_carrinho);
        infoValorMinimo = findViewById(R.id.tv_info_valor_minimo);
        botaoAvancarPagamento = findViewById(R.id.btn_avancar_pagamento);
        botaoFazerNovaAposta  = findViewById(R.id.btn_nova_aposta);
        limparCarrinhoLinearLayout = findViewById(R.id.ll_limpar_carrinho);

        ViewUtils.setMoedaFormatHtml(BigDecimal.ZERO, valorTotalCarrinho);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_carrinho)));
    }
    private void setaMetodos(){
        botaoAvancarPagamento.setOnClickListener(v -> validarCarrinho());
        botaoFazerNovaAposta.setOnClickListener(v -> clickBotaoFazerNovaAposta());
        limparCarrinhoLinearLayout.setOnClickListener(v -> clickLimparCarrinhoLinearLayout());
    }


    public void atualizaLayout(boolean animacao) {
        try {
            if (carrinho != null) {
                if (carrinho.getApostas() != null) {
                    configuraRecyclerViewCarrinhoAposta(animacao);
                    setValorTotalCarrinho();
                }
            }  else {
                callWebservice();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        montarLayout();
    }



    public void lerCarrinhoLocal(Boolean animacao) {
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        carrinho = new CarrinhoDTO();
        carrinho.setApostas(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());
        atualizaLayout(animacao);
    }



    public void setCarrinho(CarrinhoDTO carrinho) {
        this.carrinho = carrinho;
        CarrinhoSingleton.getInstance().setCarrinho(this.carrinho);
    }


    private void validarCarrinho() {
        ocorrendoValidacao = true;
        DialogUtils.show(CarrinhoActivity.this);
        ApostaBO_.getInstance_(CarrinhoActivity.this).validarCarrinho(new RequestListener<ResourceResponse>() {
            @Override
            public void onResponse(ResourceResponse result) {
                ocorrendoValidacao = false;
                DialogUtils.dismiss();
                CarrinhoSingleton.getInstance().setCarrinho(carrinho);
                startActivity(new Intent(CarrinhoActivity.this, FormaPagamentoActivity.class));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, CarrinhoActivity.this);
            }
        });
    }

    protected void clickBotaoFazerNovaAposta() {
        setResult(2);
        finish();
    }

    protected void clickLimparCarrinhoLinearLayout() {
        ViewUtils.alertTitleButtonYesOrNotListener(this, getString(R.string.MA011), (dialogInterface, i) -> {
            DialogUtils.show(CarrinhoActivity.this);
            ApostaBO apostaBO = ApostaBO_.getInstance_(CarrinhoActivity.this);
            apostaBO.limparCarrinho(new RequestListener<CarrinhoDTOResponse>() {
                @Override
                public void onResponse(CarrinhoDTOResponse result) {
                    FirebaseManager.registraEvento(CarrinhoActivity.this ,"LIMPOU_CARRINHO_DE_APOSTAS_ONLINE");
                    DialogUtils.dismiss();
                    limparCarrinhoAtualizarTela();
                    if (result.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( result.getRedirect(), CarrinhoActivity.this);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogUtils.dismiss();
                    if (MensagensNetwork.isUnauthorizedError(error)) {
                        deleteCarrinhoLocal();
                    } else {
                        RedirectNetwork.checkRedirect( error, CarrinhoActivity.this );
                    }
                }
            });
        });

    }

    private void deleteCarrinhoLocal() {
        FirebaseManager.registraEvento(CarrinhoActivity.this ,"LIMPOU_CARRINHO_DE_APOSTAS_LOCAL");
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        crud.deleteAll();
        limparCarrinhoAtualizarTela();
    }

    private void limparCarrinhoAtualizarTela() {
        carrinho.getApostas().removeAll(carrinho.getApostas());
        carrinho.setValorTotal(BigDecimal.ZERO);
        CarrinhoSingleton.getInstance().setCarrinho(carrinho);
        atualizaLayout(Boolean.FALSE);
    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        ViewUtils.setMoedaFormatHtml(valorCarrinho, infoValorMinimo, getResources().getString(R.string.label_lembrar_valor_minimo).replace("_", "<br>") + " ");
        botaoAvancarPagamento.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));
    }

    private void callWebservice() {

        final ApostaBO apostaBO = ApostaBO_.getInstance_(CarrinhoActivity.this);
        DialogUtils.show(CarrinhoActivity.this);

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                FirebaseManager.registraEvento(CarrinhoActivity.this ,"ENTROU_CARRINHO_DE_APOSTAS_ONLINE");
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CarrinhoActivity.this);
                }
                DialogUtils.dismiss();
                setCarrinho(response.getPayload());
                atualizaLayout(Boolean.TRUE);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    lerCarrinhoLocal(Boolean.TRUE);
                } else {
                    RedirectNetwork.checkRedirect( error, CarrinhoActivity.this );
                }
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta(boolean animacao) {
        List<IdentificaoDeUmaApostaDas8Modalidades> listIdentificaoDeUmaApostaDas8Modalidades = new ArrayList<>();
        for (IdentificaoDeUmaApostaDas8Modalidades identificaoDeUmaApostaDas8Modalidades : carrinho.getApostas()) {
            if (identificaoDeUmaApostaDas8Modalidades.getModalidade() != null) {
                listIdentificaoDeUmaApostaDas8Modalidades.add(identificaoDeUmaApostaDas8Modalidades);
            }
        }
        if (carrinho.getApostas().size() > 0) {
            limparCarrinhoLinearLayout.setVisibility(View.VISIBLE);
        } else {
            limparCarrinhoLinearLayout.setVisibility(View.GONE);
        }
        apostasListaRecycleView.setLayoutManager(new LinearLayoutManager(this));
        apostasListaRecycleView.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) listIdentificaoDeUmaApostaDas8Modalidades, this, animacao));
        apostasListaRecycleView.setExpanded(Boolean.TRUE);

    }

    private void setValorTotalCarrinho() {
        BigDecimal valorTotalCarrinho = BigDecimal.ZERO;
        for (IdentificaoDeUmaApostaDas8Modalidades aposta : carrinho.getApostas()) {
            valorTotalCarrinho = valorTotalCarrinho.add(aposta.getValor());
        }
        ViewUtils.setMoedaFormatHtml(valorTotalCarrinho, this.valorTotalCarrinho);
        carrinho.setValorTotal(valorTotalCarrinho);
    }
}
