package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ExpandableListDataFiltro;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.FiltroApostasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaComprasRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.listener.OnFavoritarClickListener;

public class ListaComprasActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 10;

    private List<MesAnoDTO> listaMesAnoDTO;
    private List<CompraDTO> listaComprasDto;
    private List<ApostaDTO> listaAposta = new ArrayList<>();
    private CompraDTO compraSelecionada;
    private Boolean isMaisCompras = true, bloquearRequestScroll = false, mantemSurpresinhas = true;
    int offsetItem = 0, mes, ano;
    private Dialog dialogFavoritar;
    private Dialog dialogFiltrar;
    private ExpandableListView listViewFiltro;
    private Toolbar toolbar;
    private ImageButton btnDuvidas;
    private ImageButton btnFiltro;
    private ListaComprasRecyclerViewAdapter listaComprasRecyclerViewAdapter;
    private OnFavoritarClickListener listenerFavoritar;

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerViewCompras;
    private ProgressBar progressLoadingMore;
    private NestedScrollView scrollViewContentViewCompras;
    private EditText etCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
        setaViews();
        setaMetodos();
    }

    @Override
    protected void onResume() {
        super.onResume();

        callWebServiceMesCompras();
    }

    private void callWebServiceMesCompras(){

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        ApostaBO.getInstance().getMesesCompras(new RequestListener<MesAnoDTOResponse>() {
            @Override
            public void onResponse(MesAnoDTOResponse response) {
                loadViewProgress.dismiss();
                listaMesAnoDTO = response.getPayload();

                addSpinner();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );

            }
        });
    }

    private void callWebServiceCompras(){
        offsetItem = 0;
        isMaisCompras = true;
        bloquearRequestScroll = false;
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        ApostaBO.getInstance().getCompras(mes, ano, offsetItem, PAGE_SIZE ,new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOCompraDTOResponse response) {
                loadViewProgress.dismiss();
                listaComprasDto = response.getPayload().getLista();
                offsetItem = PAGE_SIZE;
                initRecyclerView();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );
            }
        });
    }

    private void setaViews(){
        toolbar = findViewById(R.id.toolbar);
        btnDuvidas = findViewById(R.id.ib_duvidas);
        btnFiltro = findViewById(R.id.ib_filtrar);
        btnFiltro.setVisibility(View.VISIBLE);
        btnDuvidas.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
        }

        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.title_activity_lista_compras))));

        scrollViewContentViewCompras = findViewById(R.id.scrollViewContentViewCompras);

        progressLoadingMore = findViewById(R.id.compras_loading_more);
        recyclerViewCompras = findViewById(R.id.recyclerCompras);

        AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_lista_compras));
        setaDialogFavoritar();
    }

    private void setaMetodos(){
        btnFiltro.setOnClickListener(view -> {
            //dialogFiltrar.show();
        });
        btnDuvidas.setOnClickListener(view -> {
            abrirTermosUso();
        });
    }

    private void setaDialogFiltrar(List<ModalidadeDTO> modalidades) {
        dialogFiltrar = new Dialog(this);
        dialogFiltrar.setContentView(R.layout.dialog_filtro_compras);
        listViewFiltro = dialogFiltrar.findViewById(R.id.elv_filtros);
        Button      btnOk       = dialogFiltrar.findViewById(R.id.btn_ok);
        Button      btnCancelar = dialogFiltrar.findViewById(R.id.btn_cancelar);
        ImageButton btnLimpar   = dialogFiltrar.findViewById(R.id.ib_filtro_limpar);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogFiltrar.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogFiltrar.getWindow().setAttributes(lp);

//        btnOk.setOnClickListener(v -> {
//            if (fragmentApostas != null) {
//                if (filtroAdapter.isAddBadge()) {
//                    tvBadge.setVisibility(View.VISIBLE);
//                } else {
//                    tvBadge.setVisibility(View.GONE);
//                }
//                fragmentApostas.filtraDados();
//                dialogFiltrar.dismiss();
//            }
//        });
//        btnCancelar.setOnClickListener(v -> dialogFiltrar.dismiss());
//        btnLimpar.setOnClickListener(v -> {
//            filtroAdapter.limpaFiltros();
//            tvBadge.setText(String.valueOf(0));
//            tvBadge.setVisibility(View.GONE);
//            fragmentApostas.filtraDados();
//            dialogFiltrar.dismiss();
//        });
//
//        final List<String>                  expandableListTitle;
//        final HashMap<String, List<String>> expandableListDetail;
//
//        listViewFiltro.setGroupIndicator(null);
//        expandableListDetail = ExpandableListDataFiltro.getDataSemConcurso();
//        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
//
//        LinkedHashMap<Integer,String> mapSituacoes;
//        if (ConfiguracaoAppSingleton.getInstance().isMicroServico()) {
//            mapSituacoes = SituacoesApostaFiltroEnum.getSituacoesMicroServico();
//        } else {
//            mapSituacoes = SituacoesApostaFiltroEnum.getSituacoes();
//        }
//        filtroAdapter = new FiltroApostasAdapter(this,
//                expandableListTitle,
//                expandableListDetail,
//                modalidades,
//                MinhasApostasActivity.this,
//                true,
//                mapSituacoes);
//
//        listViewFiltro.setAdapter(filtroAdapter);
//        btnLimpar.setOnClickListener(v -> {
//            filtroAdapter.limpaFiltros();
//            dialogFiltrar.dismiss();
//            tvBadge.setText(String.valueOf(0));
//            fragmentApostas.filtraDados();
//            tvBadge.setVisibility(View.GONE);
//        });
    }

    private void setaDialogFavoritar() {
        dialogFavoritar = new Dialog(this);
        dialogFavoritar.setContentView(R.layout.custom_dialog_edit_text);

        TextView tituloDialog = dialogFavoritar.findViewById(R.id.tv_titulo);
        tituloDialog.setText(getResources().getString(R.string.carrinho_favorito));

        etCarrinho = dialogFavoritar.findViewById(R.id.et_carrinho);

        Button btnOk = dialogFavoritar.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogFavoritar.findViewById(R.id.btn_cancelar);

        CheckBox ckManterSurpresinha = dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas);
        ckManterSurpresinha.setOnCheckedChangeListener((compoundButton, b) -> mantemSurpresinhas = b);

        btnOk.setOnClickListener(v -> {
            String text  = etCarrinho.getText().toString();
            if(text != null && !text.isEmpty() && compraSelecionada != null && compraSelecionada.getId() != null){
                validarCarrinhoFavorito(text,compraSelecionada.getId());
            }
        });
        btnCancelar.setOnClickListener(v -> dialogFavoritar.dismiss());
        dialogFavoritar.setOnDismissListener(dialogInterface -> {
            etCarrinho.setText("");
            dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.GONE);
            dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.GONE);
        });
    }

    private void validarCarrinhoFavorito(String nome, Long idFavorito){
        AlertDialogUtils.show(ListaComprasActivity.this);
        ApostaBO.getInstance().validarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                AlertDialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), ListaComprasActivity.this);
                }
                favoritarCarrinho(nome,idFavorito);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                ViewUtils.alertTitleButtonYesOrNotListener(
                        ListaComprasActivity.this,
                        MensagensNetwork.getErrorMessage(error),
                        (dialogInterface, i) ->  favoritarCarrinho(nome,idFavorito)
                );
            }
        });

    }

    private void favoritarCarrinho(String nome, Long idFavorito) {
        AlertDialogUtils.show(ListaComprasActivity.this);
        ApostaBO.getInstance().salvarCarrinhoFavorito(nome, idFavorito.toString(), mantemSurpresinhas, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                AlertDialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), ListaComprasActivity.this);
                }
                ViewUtils.alertTitleButton(ListaComprasActivity.this,
                        R.string.label_atencao, getString(R.string.carrinho_fav_lbl_msg),
                        getString(R.string.ok), dialogInterface -> {
                            dialogFavoritar.dismiss();
                            startActivity(new Intent(ListaComprasActivity.this, CarrinhosFavoritosActivity.class));
                        }
                );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, ListaComprasActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebServiceMesCompras();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.settings, menu);
//
//        return true;
//    }
//
//    // Mudando o texto dos MenuItem de acordo com os dados
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            abrirTermosUso();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void abrirTermosUso(){
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void addSpinner(){
        Spinner selecaoMesSpinner;
        selecaoMesSpinner = findViewById(R.id.SelecaoMesSpinner);
        List<String> list = new ArrayList<>();
        for(MesAnoDTO item : listaMesAnoDTO){
            list.add(item.getMes().getDescricao() + getResources().getString(R.string.de_espacado) + item.getAno());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);

        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        selecaoMesSpinner.setAdapter(dataAdapter);
        selecaoMesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Your code here
                mes = listaMesAnoDTO.get(i).getMes().getValor().intValue();
                ano = listaMesAnoDTO.get(i).getAno().intValue();
                callWebServiceCompras();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCompras.setHasFixedSize(true);
        OnFavoritarClickListener listener = item -> {
            compraSelecionada = item;
            pegaApostasDaCompra();
        };
        listaComprasRecyclerViewAdapter = new ListaComprasRecyclerViewAdapter(this, listaComprasDto,listener);
        recyclerViewCompras.setAdapter(listaComprasRecyclerViewAdapter);
        recyclerViewCompras.setLayoutManager(layoutManager);


        scrollViewContentViewCompras.setOnScrollChangeListener((NestedScrollView v,int scrollX,int scrollY,int oldScrollX,int oldScrollY) -> {
            View view =  v.getChildAt(v.getChildCount() - 1);
            int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));

            // if diff is zero, then the bottom has been reached
            if (diff == 0 && !bloquearRequestScroll) {
                // do stuff
                getComprasScroll();
                bloquearRequestScroll = true;
            }
        });

    }

    private void configuraVisibilidadeMantSurpresinha(List<ApostaDTO> apostas){
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

    private void pegaApostasDaCompra() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        ApostaBO.getInstance().getDetalhesCompras(compraSelecionada.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
            @Override
            public void onResponse(AgrupadorApostaCompraDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
                if(response.getPayload().getApostas() != null){
                    configuraVisibilidadeMantSurpresinha(response.getPayload().getApostas());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, ListaComprasActivity.this);
            }
        });
    }

    private void getComprasScroll(){
        if (!isMaisCompras) {
            return;
        }
        progressLoadingMore.setVisibility(View.VISIBLE);

        ApostaBO.getInstance().getCompras(mes, ano, offsetItem, PAGE_SIZE, new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOCompraDTOResponse response) {
                progressLoadingMore.setVisibility(View.GONE);
                if(response.getPayload().getLista().isEmpty()){
                    isMaisCompras = false;
                }else{
                    listaComprasDto.addAll(response.getPayload().getLista());
                    listaComprasRecyclerViewAdapter.notifyDataSetChanged();
                    offsetItem = offsetItem + PAGE_SIZE;
                }
                bloquearRequestScroll = false;
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressLoadingMore.setVisibility(View.GONE);
                bloquearRequestScroll = false;
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );
            }
        });
    }
}