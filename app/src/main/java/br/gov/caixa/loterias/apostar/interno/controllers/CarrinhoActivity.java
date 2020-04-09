package br.gov.caixa.loterias.apostar.interno.controllers;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
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
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostar.interno.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.CarrinhoApostasListViewAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;

import static br.gov.caixa.loterias.apostar.interno.utils.Utils.isErroNegocial;


public class CarrinhoActivity extends AppCompatActivity {

    public static String CARRINHO = "CARRINHO";
    public static String LER_CARRINHO_LOCAL = "LER_CARRINHO_LOCAL";
    public static int FIM_SCROLL = 0;
    public static int PAGE_SIZE = 10;
    public int offSet = 0;

    private Toolbar toolbar;
    private Dialog dialogFavoritar;
    private ExpandableHeightRecyclerView rvListaApostas;
    private ConstraintLayout clCarrinhosFavoritos;
    private TextView tvValorTotal, infoValorMinimo;
    protected View vLoading;
    private EditText etCarrinho;
    private Button btnAvancaPagamento, btnNovaAposta;
    private ImageButton btnLimpar, btnFavoritar;
    private CarrinhoDTO carrinho;
    private boolean lerCarrinhoLocal = false;
    private boolean ocorrendoValidacao = false;
    private boolean bloqScroll = false;
    private List<IdentificaoDeUmaApostaDas8Modalidades> listApostas = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private ScrollView svCarrinho;

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
        if(DadosUsuarioBO.checarUsuarioLogado(this)){
            lerCarrinhoLocal = false;
        }
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void lerCarrinho() {
        if(getIntent().getComponent().getClassName().equals(CarrinhoActivity.class.getName())){
            lerCarrinhoLocal = false;
            carrinho = null;
        }
        if(lerCarrinhoLocal){
            FirebaseManager.registraEvento(CarrinhoActivity.this ,getResources().getString(R.string.evento_carrinho_offline));
            lerCarrinhoLocal(true);
        }else{
            atualizaLayout(offSet);
        }
        if(!DadosUsuarioBO.checarUsuarioLogado(this)){
            btnFavoritar.setVisibility(View.GONE);
        }else {
            if(carrinho!= null && carrinho.getApostas()!= null && carrinho.getApostas().size() > 0){
                btnFavoritar.setVisibility(View.VISIBLE);

            }
        }
    }

    private void setaDialogFavoritar() {
        dialogFavoritar = new Dialog(this);
        dialogFavoritar.setContentView(R.layout.custom_dialog_edit_text);

        TextView tituloDialog = dialogFavoritar.findViewById(R.id.tv_titulo);
        tituloDialog.setText("Carrinho Favorito");

        etCarrinho = dialogFavoritar.findViewById(R.id.et_carrinho);

        Button btnOk = dialogFavoritar.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogFavoritar.findViewById(R.id.btn_cancelar);

        btnOk.setOnClickListener(v -> {
            String text  = etCarrinho.getText().toString();
            if(text != null && !text.isEmpty()){
                validarCarrinhoFavorito(text);
            }
        });
        dialogFavoritar.setOnDismissListener(dialogInterface -> {
            etCarrinho.setText("");
        });
        btnCancelar.setOnClickListener(v -> dialogFavoritar.dismiss());
    }

    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            carrinho = Parcels.unwrap(bundle.getParcelable(CARRINHO));
            lerCarrinhoLocal = bundle.getBoolean(LER_CARRINHO_LOCAL);
        }
    }

    private void setaViews(){
        rvListaApostas = findViewById(R.id.erv_apostas_listas);
        tvValorTotal = findViewById(R.id.tv_valor_total_carrinho);
        infoValorMinimo = findViewById(R.id.tv_info_valor_minimo);
        btnAvancaPagamento = findViewById(R.id.btn_avancar_pagamento);
        btnNovaAposta = findViewById(R.id.btn_nova_aposta);
        toolbar = findViewById(R.id.toolbar);
        btnLimpar = findViewById(R.id.ib_excluir);
        btnFavoritar = findViewById(R.id.ib_favoritar);
        clCarrinhosFavoritos = findViewById(R.id.cl_carrinhos_favoritos);
        vLoading = findViewById(R.id.pb_loading);
        svCarrinho = findViewById(R.id.sv_carrinho);


        ViewUtils.setMoedaFormatHtml(BigDecimal.ZERO, tvValorTotal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_carrinho)));
        setaDialogFavoritar();


    }
    private void setaMetodos(){
        btnAvancaPagamento.setOnClickListener(v -> validarCarrinho());
        btnNovaAposta.setOnClickListener(v -> clickBotaoFazerNovaAposta());
        btnLimpar.setOnClickListener(v -> clickLimparCarrinhoLinearLayout());
        btnFavoritar.setOnClickListener(v -> dialogFavoritar.show());
        clCarrinhosFavoritos.setOnClickListener(v -> mandaParaCarrinhosFavoritos());

        layoutManager = new LinearLayoutManager(this);
        rvListaApostas.setLayoutManager(layoutManager);
        rvListaApostas.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) listApostas, this, true));
        rvListaApostas.setExpanded(Boolean.TRUE);
        rvListaApostas.setItemViewCacheSize(PAGE_SIZE);

        svCarrinho.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = svCarrinho.getScrollY();
            View view =  svCarrinho.getChildAt(svCarrinho.getChildCount() - 1);
            int diff = (view.getBottom() - (svCarrinho.getHeight() + scrollY));
            if (diff == FIM_SCROLL && !bloqScroll) {
                vLoading.setVisibility(View.VISIBLE);
                bloqScroll = true;
                new Handler().postDelayed(() -> {
                    atualizaLayout(offSet);
                }, 1);
            }
        });
    }

    public void deletaAposta(int pos, IdentificaoDeUmaApostaDas8Modalidades aposta){
        if (listApostas != null) {
            synchronized(rvListaApostas) {
                listApostas.remove(aposta);
                rvListaApostas.getAdapter().notifyItemRemoved(pos);
                offSet--;
                setValorTotalCarrinho();
                configuraBotoesSuperiores();
            }
        }
    }

    public void atualizaLayout(int offSet) {
        try {
            if (carrinho != null) {
                if (carrinho.getApostas() != null) {
                    configuraRecyclerViewCarrinhoAposta(offSet);
                    setValorTotalCarrinho();
                }
            } else {
                callWebservice();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        montarLayout();
    }

    public void  lerCarrinhoLocal(Boolean animacao) {
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        carrinho = new CarrinhoDTO();
        carrinho.setApostas(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());
        atualizaLayout(offSet);
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
                startActivity(new Intent(CarrinhoActivity.this, CartoesActivity.class));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                ocorrendoValidacao = false;
                RedirectNetwork.checkRedirect(error, CarrinhoActivity.this);
            }
        });
    }

    private void validarCarrinhoFavorito(String nome) {
        DialogUtils.show(CarrinhoActivity.this);
        ApostaBO_.getInstance_(CarrinhoActivity.this).validarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), CarrinhoActivity.this);
                }
                favoritarCarrinho(nome);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                if(isErroNegocial(error)){
                    ViewUtils.alertTitleButtonYesOrNotListener(
                            CarrinhoActivity.this,
                            MensagensNetwork.getErrorMessage(error),
                            (dialogInterface, i) ->
                                    favoritarCarrinho(nome)
                                                              );
                } else {
                    RedirectNetwork.checkRedirect(error, CarrinhoActivity.this);
                }
            }
        });
    }

    private void favoritarCarrinho(String nome) {
        DialogUtils.show(CarrinhoActivity.this);
        ApostaBO_.getInstance_(CarrinhoActivity.this).salvarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), CarrinhoActivity.this);
                }
                ViewUtils.alertTitleButton(CarrinhoActivity.this,
                        R.string.label_atencao, getString(R.string.carrinho_fav_lbl_msg),
                        getString(R.string.ok), dialogInterface -> {
                                                   dialogFavoritar.dismiss();
                                                  }
                                           );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, CarrinhoActivity.this);
            }
        });
    }

    private void mandaParaCarrinhosFavoritos(){
        startActivity(new Intent(CarrinhoActivity.this,CarrinhosFavoritosActivity.class));
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
                    FirebaseManager.registraEvento(CarrinhoActivity.this ,getResources().getString(R.string.evento_limpou_carrinho_online));
                    DialogUtils.dismiss();
                    if (result.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( result.getRedirect(), CarrinhoActivity.this);
                    }
                    limparCarrinhoAtualizarTela();
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
        FirebaseManager.registraEvento(CarrinhoActivity.this ,getResources().getString(R.string.evento_limpou_carrinho_offline));
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        crud.deleteAll();
        limparCarrinhoAtualizarTela();
    }

    private void limparCarrinhoAtualizarTela() {
        offSet = 0;

        carrinho.getApostas().removeAll(carrinho.getApostas());
        carrinho.setValorTotal(BigDecimal.ZERO);
        CarrinhoSingleton.getInstance().setCarrinho(carrinho);
        synchronized(rvListaApostas) {
            listApostas.removeAll(listApostas);
            rvListaApostas.getAdapter().notifyDataSetChanged();
        }
        atualizaLayout(offSet);
        configuraBotoesSuperiores();

    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        ViewUtils.setMoedaFormatHtml(valorCarrinho, infoValorMinimo, getResources().getString(R.string.label_lembrar_valor_minimo).replace(getResources().getString(R.string.underline), getResources().getString(R.string.quebra_html)) + getResources().getString(R.string.espaco_em_branco));
        btnAvancaPagamento.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));
    }

    private void callWebservice() {

        final ApostaBO apostaBO = ApostaBO_.getInstance_(CarrinhoActivity.this);
        DialogUtils.show(CarrinhoActivity.this);
        vLoading.setVisibility(View.VISIBLE);
        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                vLoading.setVisibility(View.GONE);
                DialogUtils.dismiss();
                FirebaseManager.registraEvento(CarrinhoActivity.this ,getResources().getString(R.string.evento_entrou_carrinho_online));
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CarrinhoActivity.this);
                }
                setCarrinho(response.getPayload());
                atualizaLayout(offSet);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                vLoading.setVisibility(View.GONE);
                DialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    lerCarrinhoLocal(Boolean.TRUE);
                } else {
                    RedirectNetwork.checkRedirect( error, CarrinhoActivity.this );
                }
            }
        });
    }

    public void configuraRecyclerViewCarrinhoAposta(int offSet) {
        int cont ;
        int maximoLista = 0;

        List<IdentificaoDeUmaApostaDas8Modalidades> apostas = new ArrayList<>();
        if(carrinho  != null && carrinho.getApostas() != null && carrinho.getApostas().size() > 0) {
            if(offSet > carrinho.getApostas().size()){
                vLoading.setVisibility(View.GONE);
                return;
            }
            maximoLista = pegaMaximoLista();
            for (cont = offSet; cont  < maximoLista ; cont++) {
                IdentificaoDeUmaApostaDas8Modalidades aposta = carrinho.getApostas().get(cont) == null ? null : carrinho.getApostas().get(cont);
                if (aposta.getModalidade() != null) {
                    apostas.add(aposta);
                }
            }
        }

        if (apostas != null) {
            listApostas.addAll(apostas);
        }

        configuraBotoesSuperiores();

        if (listApostas != null) {
            synchronized(rvListaApostas) {
                rvListaApostas.getAdapter().notifyItemRangeInserted(offSet, maximoLista);
                vLoading.setVisibility(View.GONE);
            }

            this.offSet += PAGE_SIZE;
        }
        bloqScroll = false;

    }

    private void configuraBotoesSuperiores(){
        if (carrinho.getApostas().size() > 0) {
            btnLimpar.setVisibility(View.VISIBLE);
            if(DadosUsuarioBO.checarUsuarioLogado(this)){
                btnFavoritar.setVisibility(View.VISIBLE);
            }
        } else {
            btnLimpar.setVisibility(View.GONE);
            btnFavoritar.setVisibility(View.GONE);
        }
    }

    private int pegaMaximoLista(){
        return offSet + PAGE_SIZE < carrinho.getApostas().size() ? offSet + PAGE_SIZE : carrinho.getApostas().size();
    }

    private void setValorTotalCarrinho() {
        BigDecimal valorTotalCarrinho = BigDecimal.ZERO;
        for (IdentificaoDeUmaApostaDas8Modalidades aposta : carrinho.getApostas()) {
            valorTotalCarrinho = valorTotalCarrinho.add(aposta.getValor());
        }
        ViewUtils.setMoedaFormatHtml(valorTotalCarrinho, this.tvValorTotal);
        carrinho.setValorTotal(valorTotalCarrinho);
    }
}
