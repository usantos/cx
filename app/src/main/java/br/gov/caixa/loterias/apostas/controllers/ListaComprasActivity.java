package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;

import java.util.*;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeiosPagamentosResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ExpandableListDataFiltro;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.FiltroComprasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaComprasRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnFavoritarClickListener;

public class ListaComprasActivity extends LoteriasBaseAppActivity {

    private static final int PAGE_SIZE = 10;
    private List<MesAnoDTO> listaMesAnoDTO;
    private List<CompraDTO> listaComprasDto;
    private List<MeioPagamentoDTO> listaMeioPagamentoDTO = new ArrayList<>();
    private List<SituacoesCompraDTO> listaSituacoesCompraDTO = new ArrayList<>();
    private CompraDTO compraSelecionada;
    private Boolean isMaisCompras = true, bloquearRequestScroll = false, mantemSurpresinhas = true;
    int offsetItem = 0, mes, ano;
    Long  idMeioPagamento = null;
    Long  idSituacao = null;
    private int situacaoPosicao = -1;
    private int meioPagamentoPosicao = -1;
    private Dialog dialogFavoritar;
    private Dialog dialogFiltrar;
    private ExpandableListView listViewFiltro;
    private Toolbar toolbar;
    private FiltroComprasAdapter filtroAdapter;
    private ImageButton btnDuvidas;
    private ImageButton btnFiltro;
    private TextView tvBadge;
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

        AlertDialogUtils.show(Aplicacao.application.getApplicationContext());
        ApostaSilceBO.getInstance().getMesesCompras(new RequestListener<MesAnoDTOResponse>() {
            @Override
            public void onResponse(MesAnoDTOResponse response) {
                AlertDialogUtils.dismiss();
                listaMesAnoDTO = response.getPayload();
                addSpinner();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );

            }
        });
    }

    private void callWebServiceCompras(){
        offsetItem = 0;
        isMaisCompras = true;
        bloquearRequestScroll = false;
        AlertDialogUtils.show(Aplicacao.application.getApplicationContext());
        try{
            //ApostaSilceBO.getInstance().getComprasFiltradaMeioPagamentoSituacao(mes, ano, offsetItem, PAGE_SIZE, idMeioPagamento, idSituacao ,new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
            ServicoFactoryUtil.getComprasLegadoService().getComprasFiltradaMeioPagamentoSituacao(mes, ano, offsetItem, PAGE_SIZE, idMeioPagamento, idSituacao ,new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
                @Override
                public void onResponse(ResultadoPesquisaPaginadaDTOCompraDTOResponse response) {
                    AlertDialogUtils.dismiss();
                    if(response.getPayload() != null){
                        listaComprasDto = response.getPayload().getLista();
                        offsetItem = PAGE_SIZE;
                        initRecyclerView(response.getDataHoraServidor());
                    }else{
                        Log.e("ListaComprasActivity", "O payload da resposta do servico que busca as compras é nulo");
                    }

                    if (response.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    Log.e("listaComprasActivity", "Erro ao recuperar as compras", error);
                    RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );
                }
            });
        }catch (Exception e){
            AlertDialogUtils.dismiss();
            Log.e("ListaComprasActivity", "Erro ao tentar acessar o serviço de compras", e);
        }

    }

    private void callWebServiceMeioPagamento(boolean chamaServicoSituacoesCompra){

        AlertDialogUtils.show(Aplicacao.application.getApplicationContext());
        try{
            DadosCorporativosSilceBO.getInstance().buscaMeiosPagamentos(new RequestListener<MeiosPagamentosResponse>() {
                @Override
                public void onResponse(MeiosPagamentosResponse response) {
                    AlertDialogUtils.dismiss();
                    if (response.getPayload() != null) {
                        listaMeioPagamentoDTO = response.getPayload();
                        if(chamaServicoSituacoesCompra) {
                            callWebServiceSituacoesCompra(idMeioPagamento, true);//se idMeioPagamento for nulo ele chama todas as situacoes de compra
                        }
                    } else {
                        Log.e("ListaComprasActivity", "O payload da resposta do servico que busca os meios de pagamento é nulo");
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    Log.e("ListaComprasActivity", "Erro ao recuperar os meios de pagamento", error);
                    RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );

                }
            });
        } catch (Exception e){
            AlertDialogUtils.dismiss();
            Log.e("ListaComprasActivity", "Erro ao tentar acessar o serviço de meios de pagamento", e);
        }
    }

    public void callWebServiceSituacoesCompra(Long idMeioPagamento, boolean abreModalFiltro){

        AlertDialogUtils.show(Aplicacao.application.getApplicationContext());
        try{
            ApostaSilceBO.getInstance().getSituacoesCompra(idMeioPagamento, new RequestListener<SituacoesCompraResponse>() {
                @Override
                public void onResponse(SituacoesCompraResponse response) {
                    AlertDialogUtils.dismiss();
                    if(response.getPayload() != null){
                        listaSituacoesCompraDTO = response.getPayload();
                        if(abreModalFiltro){
                            setaDialogFiltrar();//Somente após a recuperação dos meios de pagamento e situacoes de compra que abre a modal
                            dialogFiltrar.show();
                        }
                    }else{
                        Log.e("ListaComprasActivity", "O payload da resposta do servico que busca as situações de compra é nulo");
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    Log.e("ListaComprasActivity", "Erro ao recuperar as siturações de compra", error);
                    RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );

                }
            });
        } catch (Exception e){
            AlertDialogUtils.dismiss();
            Log.e("ListaComprasActivity", "Erro ao tentar acessar o serviço de situações de compra", e);
        }
    }


    private void setaViews(){
        toolbar = findViewById(R.id.toolbar);
        btnDuvidas = findViewById(R.id.ib_duvidas);
        btnFiltro = findViewById(R.id.ib_filtrar);
        tvBadge = findViewById(R.id.tv_badge);
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
        setFiltroAdapterDialogFiltrar();
    }

    private void setaMetodos(){
        btnFiltro.setOnClickListener(view -> {
            if(listaMeioPagamentoDTO.isEmpty())//só chama a o serviço para recuperar os meios de pagamento na primeira vez que clica no botao
            {
                callWebServiceMeioPagamento(true);
            }else{
                if(listaSituacoesCompraDTO.isEmpty()){
                    callWebServiceSituacoesCompra(idMeioPagamento, true);
                }else{
                    setaDialogFiltrar();
                    dialogFiltrar.show();
                }
            }

        });
        btnDuvidas.setOnClickListener(view -> {
            abrirTermosUso();
        });

    }


    private void setFiltroAdapterDialogFiltrar(){
        final List<String>                  expandableListTitle;
        final HashMap<String, List<String>> expandableListDetail;
        expandableListDetail = ExpandableListDataFiltro.getDataFiltroCompras();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());

        LinkedHashMap<Long, String> mapSituacoesCompra = new LinkedHashMap<>(montaMapSituacoesCompra(listaSituacoesCompraDTO));
        LinkedHashMap<Long, String> mapMeiosDePagamento = new LinkedHashMap<>(montaMapMeiosPagamento(listaMeioPagamentoDTO));

        if(idSituacao != null && idSituacao > 0){
            situacaoPosicao = new ArrayList<>(mapSituacoesCompra.keySet()).indexOf(idSituacao) + 1;
        }
        if(idMeioPagamento != null && idMeioPagamento > 0){
            meioPagamentoPosicao = new ArrayList<>(mapMeiosDePagamento.keySet()).indexOf(idMeioPagamento) + 1;
        }

        filtroAdapter = new FiltroComprasAdapter(this,
                expandableListTitle,
                expandableListDetail,
                ListaComprasActivity.this,
                mapSituacoesCompra,
                mapMeiosDePagamento,
                idMeioPagamento,
                idSituacao);
    }
    private void setaDialogFiltrar() {
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
        listViewFiltro.setGroupIndicator(null);

        setFiltroAdapterDialogFiltrar();

        listViewFiltro.setAdapter(filtroAdapter);

        if (idMeioPagamento != null && idMeioPagamento > 0) {
            listViewFiltro.expandGroup(FiltroComprasAdapter.MEIO_PAGAMENTO);
        }
        if (idSituacao != null && idSituacao > 0) {
            listViewFiltro.expandGroup(FiltroComprasAdapter.SITUACAO);

            // Dar foco na opção selecionada de idSituacao, caso haja meio de pagamento selecionado
            if (situacaoPosicao > 0 && meioPagamentoPosicao > 0) {
                listViewFiltro.setSelectedChild(FiltroComprasAdapter.SITUACAO, situacaoPosicao, true);
            }
        }

        btnLimpar.setOnClickListener(v -> {
              filtroAdapter.limpaFiltros();
              if(isMeioPagamentoOuSituacaoAlterado()){
                  callWebServiceCompras();
              }
              idMeioPagamento = filtroAdapter.getIdMeioPagamentoSelecionado();
              idSituacao = filtroAdapter.getIdSituacaoSelecionada();
              dialogFiltrar.dismiss();
              tvBadge.setText(String.valueOf(0));
              tvBadge.setVisibility(View.GONE);
        });
        btnOk.setOnClickListener(v -> {
            if (filtroAdapter.isAddBadge()) {
                tvBadge.setVisibility(View.VISIBLE);
            } else {
                tvBadge.setVisibility(View.GONE);
            }
            if (isMeioPagamentoOuSituacaoAlterado()){
                callWebServiceCompras();
            }
            dialogFiltrar.dismiss();

        });
        btnCancelar.setOnClickListener(v -> dialogFiltrar.dismiss());
    }

    private boolean isMeioPagamentoAlterado(){
        Long idMeioPagamentoTemp = idMeioPagamento;
        idMeioPagamento = filtroAdapter.getIdMeioPagamentoSelecionado();

        return !Objects.equals(idMeioPagamentoTemp, idMeioPagamento);
    }
     private boolean isMeioPagamentoOuSituacaoAlterado(){
         Long idSituacaoTemp = idSituacao;
         idSituacao = filtroAdapter.getIdSituacaoSelecionada();
         return isMeioPagamentoAlterado() || !Objects.equals(idSituacaoTemp, idSituacao);
     }


    private LinkedHashMap<Long, String> montaMapMeiosPagamento(List<MeioPagamentoDTO> listaMeiosPagamento){
        LinkedHashMap<Long, String> map = new LinkedHashMap<>();
        if(listaMeiosPagamento != null){
            map.put(0L, "Todos");
            for(MeioPagamentoDTO meioPagamento: listaMeiosPagamento){
                map.put(meioPagamento.getId(), StringUtils.capitalizer(
                        meioPagamento.getNomePagamentoPremio()));
            }
        }
        return map;
    }

    private LinkedHashMap<Long, String> montaMapSituacoesCompra(List<SituacoesCompraDTO> listaSituacoes){
        String descricao;
        LinkedHashMap<Long, String> map = new LinkedHashMap<>();
        if(listaSituacoes != null){
            map.put(0L, "Todas");
            for(SituacoesCompraDTO situacaoCompra: listaSituacoes){
                switch (situacaoCompra.getId().intValue()){
                    case 9:
                        descricao = situacaoCompra.getDescricao().replace(" - ", "-");
                        map.put(9L, descricao);
                        break;
                    case 12:
                        descricao = situacaoCompra.getDescricao().replace(" - ", "-");
                        map.put(12L, descricao);
                        break;
                    default:
                        map.put(situacaoCompra.getId(), situacaoCompra.getDescricao());
                }
            }
        }
        return map;
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

        if(dialogFavoritar.getWindow() != null){

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = (int) (screenWidth * 0.85); // Define a largura da dialog como 85% da largura da tela

            dialogFavoritar.getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogFavoritar.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        }
    }

    private void validarCarrinhoFavorito(String nome, Long idFavorito){
        AlertDialogUtils.show(ListaComprasActivity.this);
        ServicoFactoryUtil.getApostaService().validarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
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
               DialogUtils.dialogSim(
                        ListaComprasActivity.this,
                        MensagensNetwork.getErrorMessage(error),
                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                favoritarCarrinho(nome,idFavorito);
                            }
                        }
                );
            }
        });

    }

    private void favoritarCarrinho(String nome, Long idFavorito) {
        AlertDialogUtils.show(ListaComprasActivity.this);
        ServicoFactoryUtil.getApostaService().salvarCarrinhoFavorito(nome, idFavorito.toString(), mantemSurpresinhas, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                AlertDialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), ListaComprasActivity.this);
                }
                DialogUtils.dialogEntendiListener(
                        ListaComprasActivity.this,
                        getString(R.string.carrinho_fav_lbl_msg),
                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                dialogFavoritar.dismiss();
                                startActivity(new Intent(ListaComprasActivity.this, CarrinhosFavoritosActivity.class));
                            }
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

    private void abrirTermosUso(){
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void addSpinner(){
        Spinner selecaoMesSpinner;
        selecaoMesSpinner = findViewById(R.id.SelecaoMesSpinner);
        List<String> list = new ArrayList<>();
        int indicePadrao = 0;

        for (int i = 0; i < listaMesAnoDTO.size(); i++) {
            MesAnoDTO item = listaMesAnoDTO.get(i);
            list.add(item.getMes().getDescricao() + getResources().getString(R.string.de_espacado) + item.getAno());

            if (item.getMes().getValor().intValue() == mes && item.getAno().intValue() == ano) {
                indicePadrao = i;
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);

        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        selecaoMesSpinner.setAdapter(dataAdapter);
        selecaoMesSpinner.setSelection(indicePadrao);

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

    private void initRecyclerView(String dataHoraServidor) {
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCompras.setHasFixedSize(true);
        OnFavoritarClickListener listener = item -> {
            compraSelecionada = item;
            pegaApostasDaCompra();
        };
        listaComprasRecyclerViewAdapter = new ListaComprasRecyclerViewAdapter(this, listaComprasDto, dataHoraServidor,listener);
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

        AlertDialogUtils.show(Aplicacao.application.getApplicationContext());
        //ApostaSilceBO.getInstance().getDetalhesCompras(compraSelecionada.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
        ServicoFactoryUtil.getComprasLegadoService().getDetalhesCompras(compraSelecionada.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
            @Override
            public void onResponse(AgrupadorApostaCompraDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
                if(response.getPayload().getApostas() != null){
                    configuraVisibilidadeMantSurpresinha(response.getPayload().getApostas());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, ListaComprasActivity.this);
            }
        });
    }

    private void getComprasScroll(){
        if (!isMaisCompras) {
            return;
        }
        progressLoadingMore.setVisibility(View.VISIBLE);

        //ApostaSilceBO.getInstance().getComprasFiltradaMeioPagamentoSituacao(mes, ano, offsetItem, PAGE_SIZE,idMeioPagamento, idSituacao, new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
        ServicoFactoryUtil.getComprasLegadoService().getComprasFiltradaMeioPagamentoSituacao(mes, ano, offsetItem, PAGE_SIZE,idMeioPagamento, idSituacao, new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
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