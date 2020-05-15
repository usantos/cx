package br.gov.caixa.loterias.apostar.interno.controllers;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.SituacoesApostaEnum;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ExpandableListDataFiltro;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.Tupla;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FiltroApostasAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ListaComprasDetalhesAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class DetalhesComprasActivity extends AppCompatActivity {

    private CompraDTO compraDTO;
    private ArrayList<ApostaDTO> listaAposta = new ArrayList<>();
    private ArrayList<ApostaDTO> listaFiltrada = new ArrayList<>();;
    private ArrayList<String> tiposApostas = new ArrayList<>();
    private Dialog dialogFavoritar, dialogFiltrar;
    private ExpandableListView listViewFiltro;
    private ConstraintLayout listViewCompras;
    private ListaComprasDetalhesAdapter listaComprasDetalhesAdapter;
    private FiltroApostasAdapter filtroAdapter;
    private EditText etCarrinho;
    private ImageButton btnFiltro, btnDuvidas;
    private RecyclerView recyclerDetalhesApostas;
    private Boolean mantemSurpresinhas = true;
    private TextView tvBadge;
    int ordemCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_compras);
        setaViews();
        pegaExtras();
        setaMetodos();
    }

    private void setaViews(){
        btnFiltro = findViewById(R.id.ib_filtrar);
        btnDuvidas = findViewById(R.id.ib_duvidas);
        recyclerDetalhesApostas = findViewById(R.id.gridviewDetalhesCompras);
        tvBadge = findViewById(R.id.tv_badge);
        listViewCompras = findViewById(R.id.list_group_compras);

        btnFiltro.setVisibility(View.VISIBLE);
        btnDuvidas.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.title_activity_detalhes_compras))));
    }


    private void setaMetodos(){
        btnFiltro.setOnClickListener(view -> {
            dialogFiltrar.show();
        });
        btnDuvidas.setOnClickListener(view -> {
            abrirTermosUso();
        });

        criarCabecalho();
        buscaDetalhesCompras();
        setaDialogFavoritar();
        buscaModalidades();
    }

    private void pegaExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String compraDTOJson = bundle.getString("CompraDTO");
            ordemCompra = bundle.getInt("ordem");
            compraDTO = new Gson().fromJson(compraDTOJson, CompraDTO.class);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            buscaDetalhesCompras();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void criarCabecalho() {
        FrameLayout setaIndicador = findViewById(R.id.setaComprasGroup);
        setaIndicador.setVisibility(View.INVISIBLE);

        TextView dataCompra = findViewById(R.id.dataCompraTextView);
        if(compraDTO.getDataFinalizacaoCompra() != null){
            dataCompra.setText("Data: " + compraDTO.getDataFinalizacaoCompra());
        }
        TextView valorCompra = findViewById(R.id.valorCompra);
        valorCompra.setText(ViewUtils.textFuturaAndFuturaBold(this, this.getString(R.string.label_compra_lista_valor,
                ViewUtils.getMoedaFormat(compraDTO.getValorTotal()))));

        TextView situacaoCompraTextView = findViewById(R.id.situacaoCompraTextView);
        situacaoCompraTextView.setText(ViewUtils.textFuturaAndFuturaBold(this, this.getString(R.string.label_compra_lista_situacao,
                compraDTO.getSituacao().getDescricao())));
        ImageButton ibFavoritar = listViewCompras.findViewById(R.id.ib_favoritar);
        ibFavoritar.setOnClickListener(v -> {
                    configuraVisibilidadeMantSurpresinha(listaAposta);
                });
    }

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void buscaDetalhesCompras() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(DetalhesComprasActivity.this);

        apostaBO.getDetalhesCompras(compraDTO.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
            @Override
            public void onResponse(AgrupadorApostaCompraDTOResponse response) {
                loadViewProgress.dismiss();
                FirebaseManager.registraEvento(DetalhesComprasActivity.this ,"ENTROU_COMPRAS_DETALHE");

                listaAposta = (ArrayList<ApostaDTO>) response.getPayload().getApostas();

                tiposApostas = new ArrayList<>();
                for(ApostaDTO aposta : listaAposta){
                    tiposApostas.add(aposta.getModalidade().toString());
                }

                configuraAdapter(listaAposta);
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalhesComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                configuraAdapter(listaAposta);
                RedirectNetwork.checkRedirect(error, DetalhesComprasActivity.this);
            }
        });
    }

    private void configuraVisibilidadeMantSurpresinha(List<ApostaDTO> apostas){
        dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.GONE);
        dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.GONE);
        for (ApostaDTO aposta: apostas) {
            if  (   aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA ||
                    aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA_NUMERICA) {
                dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.VISIBLE);
                dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.VISIBLE);
                break;
            }
        }
        dialogFavoritar.show();
    }

    private void setaDialogFavoritar() {
        dialogFavoritar = new Dialog(this);
        dialogFavoritar.setContentView(R.layout.custom_dialog_edit_text);

        TextView tituloDialog = dialogFavoritar.findViewById(R.id.tv_titulo);
        tituloDialog.setText("Carrinho Favorito");

        EditText etCarrinho = dialogFavoritar.findViewById(R.id.et_carrinho);

        Button btnOk = dialogFavoritar.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogFavoritar.findViewById(R.id.btn_cancelar);

        CheckBox ckManterSurpresinha = dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas);
        ckManterSurpresinha.setOnCheckedChangeListener((compoundButton, b) -> mantemSurpresinhas = b);

        btnOk.setOnClickListener(v -> {
            String text  = etCarrinho.getText().toString();
            if(text != null && !text.isEmpty() && compraDTO != null && compraDTO.getId() != null){
                validarCarrinhoFavorito(text,compraDTO.getId());
            }
        });
        btnCancelar.setOnClickListener(v -> dialogFavoritar.dismiss());
        dialogFavoritar.setOnDismissListener(dialogInterface -> {
            etCarrinho.setText("");
            dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.GONE);
            dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.GONE);
        });
    }

    private void setaDialogFiltrar(List<ModalidadeDTO> modalidades) {
        dialogFiltrar = new Dialog(this);
        dialogFiltrar.setContentView(R.layout.dialog_filtro_compras);
        listViewFiltro = dialogFiltrar.findViewById(R.id.elv_filtros);
        Button btnOk = dialogFiltrar.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogFiltrar.findViewById(R.id.btn_cancelar);
        ImageButton btnLimpar = dialogFiltrar.findViewById(R.id.ib_filtro_limpar);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogFiltrar.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        btnOk.setOnClickListener(v -> filtrarDados());
        btnCancelar.setOnClickListener(v -> dialogFiltrar.dismiss());
        dialogFiltrar.getWindow().setAttributes(lp);

        final List<String>                  expandableListTitle;
        final HashMap<String, List<String>> expandableListDetail;

        listViewFiltro.setGroupIndicator(null);
        expandableListDetail = ExpandableListDataFiltro.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        filtroAdapter = new FiltroApostasAdapter(this,
                                                 expandableListTitle,
                                                 expandableListDetail,
                                                 modalidades,
                                                 DetalhesComprasActivity.this,
                                                 false,
                                                 SituacoesApostaEnum.getSituacoes());
        listViewFiltro.setAdapter(filtroAdapter);
        btnLimpar.setOnClickListener(v -> {
            filtroAdapter.limpaFiltros();
            dialogFiltrar.dismiss();
            tvBadge.setText(String.valueOf(0));
            tvBadge.setVisibility(View.GONE);
            configuraAdapter(listaAposta);
        });
    }

    private void configuraAdapter(List<ApostaDTO>  listaAposta) {
        if(listaAposta != null){
            recyclerDetalhesApostas.setLayoutManager(new LinearLayoutManager(this));
            listaComprasDetalhesAdapter = new ListaComprasDetalhesAdapter(this, this, listaAposta);
            recyclerDetalhesApostas.setAdapter(listaComprasDetalhesAdapter);
        }
    }

    private void filtrarDados() {
        if (listaAposta != null) {
            listaFiltrada = (ArrayList<ApostaDTO>) listaAposta.clone();
            Tupla<List<ApostaDTO>,Boolean> tuplaConcurso;
            Tupla<List<ApostaDTO>,Boolean> tuplaModalidade;
            Tupla<List<ApostaDTO>,Boolean> tuplaSituacao;
            int contFiltros = 0;
            if(filtroAdapter != null){
                tuplaConcurso = filtroAdapter.getApostasFiltradasConcurso(listaAposta);
                tuplaSituacao = filtroAdapter.getApostasFiltradasSituacao(listaAposta);
                tuplaModalidade = filtroAdapter.getApostasFiltradasModalidade(listaAposta);

                if(tuplaConcurso.y){
                    contFiltros++;
                    listaFiltrada.retainAll(tuplaConcurso.x);
                }
                if(tuplaSituacao.y){
                    contFiltros++;
                    listaFiltrada.retainAll(tuplaSituacao.x);
                }
                if(tuplaModalidade.y){
                    contFiltros++;
                    listaFiltrada.retainAll(tuplaModalidade.x);
                }
            }

            tvBadge.setText(String.valueOf(contFiltros));
            if(contFiltros == 0){
                tvBadge.setVisibility(View.GONE);
                configuraAdapter(listaAposta);
            } else {
                tvBadge.setVisibility(View.VISIBLE);
                configuraAdapter(listaFiltrada);
            }

        }
        dialogFiltrar.dismiss();
    }

    private void validarCarrinhoFavorito(String nome, Long idFavorito){
        DialogUtils.show(DetalhesComprasActivity.this);
        ApostaBO_.getInstance_(DetalhesComprasActivity.this).validarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), DetalhesComprasActivity.this);
                }
                favoritarCarrinho(nome,idFavorito);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                ViewUtils.alertTitleButtonYesOrNotListener(
                        DetalhesComprasActivity.this,
                        MensagensNetwork.getErrorMessage(error),
                        (dialogInterface, i) ->  favoritarCarrinho(nome,idFavorito)
                );
            }
        });
    }

    private void favoritarCarrinho(String nome, Long idFavorito) {
        DialogUtils.show(DetalhesComprasActivity.this);
        ApostaBO_.getInstance_(DetalhesComprasActivity.this).salvarCarrinhoFavorito(nome,idFavorito.toString(), mantemSurpresinhas, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), DetalhesComprasActivity.this);
                }
                ViewUtils.alertTitleButton(DetalhesComprasActivity.this,
                        R.string.label_atencao, getString(R.string.carrinho_fav_lbl_msg),
                        getString(R.string.ok), dialogInterface -> {
                            dialogFavoritar.dismiss();
                            startActivity(new Intent(DetalhesComprasActivity.this, CarrinhosFavoritosActivity.class));
                        }
                );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, DetalhesComprasActivity.this);
            }
        });
    }

    private void buscaModalidades() {
        DialogUtils.show(DetalhesComprasActivity.this);
        ApostaBO_.getInstance_(DetalhesComprasActivity.this).getModalidades(new RequestListener<ModalidadeDTOResponse>() {
            @Override
            public void onResponse(ModalidadeDTOResponse response) {
                DialogUtils.dismiss();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalhesComprasActivity.this);
                }
                if(response.getPayload() != null){
                    setaDialogFiltrar(response.getPayload());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, DetalhesComprasActivity.this);
            }
        });
    }
}
