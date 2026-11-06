package br.gov.caixa.loterias.apostas.controllers;



import static android.view.View.GONE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;
import br.gov.caixa.loterias.apostas.utils.*;
import br.gov.caixa.loterias.apostas.view.activity.BolaoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import com.android.volley.VolleyError;
import java.math.BigDecimal;
import java.util.*;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.FiltroMarketplaceModel;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaModalidadesAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotericaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.QueroNaoQueroAdapter;
import br.gov.caixa.loterias.apostas.view.custom.MoedaMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.holder.QueroNaoQueroViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import org.jetbrains.annotations.NotNull;

public class FiltraMarketplaceActivity extends LoteriasAppMarketPlaceActivity {
    public static final String FILTRO_MARKETPLACE_RETORNO = "FILTRO_MARKETPLACE_RETORNO";
    public static final String FILTRO_LIMPO_RETORNO = "FILTRO_LIMPO_RETORNO";
    public static final String IS_FILTRANDO_RETORNO = "IS_FILTRANDO_RETORNO";
    public static final String ARG_TITULO_QUERO_NAO_QUERO = "titulo";
    public static final String ARG_QUERO = "quero";
    public static final String ARG_NAO_QUERO = "naoQuero";
    public static final String ARG_QUANTIDADE_DEZENAS = "quantidadeDezenas";
    public static final String ARG_OPCOES_QUANTIDADE_DEZENAS = "opcoesQuantidadeDezenas";
    public static final String ARG_QUANTIDADE_DEZENAS_SELECIONADA = "quantidadeDezenasSelecionada";
    public static final String ARG_DEZENAS_SELECIONADAS = "dezenasSelecionadas";
    public static final String ARG_DEZENAS_QUERO = "dezenasQuero";
    public static final String ARG_DEZENAS_NAO_QUERO = "dezenasNaoQuero";
    public static final String ARG_QTD_MAX_PROGNOSTICOS = "qtdMaxPrognosticos";
    public static final int ARG_QUANTIDADE_COLUNA_LISTAS = 6;
    private List<Integer> dezenasSelecionadasQuero = new ArrayList<>();
    private List<Integer> dezenasSelecionadasNaoQuero = new ArrayList<>();
    private List<Integer> opcoesQtdDezenas = new ArrayList<>();
    private RecyclerView listView;
    private ListaModalidadesAdapter listaModalidadesAdapter;
    private EditText editValMin, editValMax, editQtdCotasMin, editQtdCotasMax;
    private EditText editLoterica;
    private TextView textLoterica;
    private AutoCompleteTextView autocomplete;
    private TextView textLotericaDescritivo;

    private TextView validaQtdCotaMin, validaQtdCotaMax, txtQtdApostas;
    private RecyclerView recyclerViewLoterica, recyclerViewQuero, recyclerViewNaoQuero, recyclerQtdApostas, recyclerQtdDezenas;
    private LotericaAdapter lotericaAdapter;
    private QueroNaoQueroAdapter queroAdapter, naoQueroAdapter, qtdDezenasSelecionadaAdapter;
    private LinearLayout accordionQuero, accordionNaoQuero;
    private Button btnLimpaFiltros, btnAplicar, btnExcluirQuero, btnExcluirNaoQuero, btnExcluirQtdDezenas;
    private AppCompatImageView btnPlusQuero, btnPlusNaoQuero, btnPlusQtdDezenas;
    private ActivityResultLauncher<Intent> launcher;
    private List<LotericaDTO> listLotericaDTO = new ArrayList<>();
    private LotericaDTO selectLotericaDTO = null;
    private SomadorCarrinhoFragment somadorCarrinhoFragment;
    private TextWatcher textWatcher;
    private FiltroMarketplaceModel model;
    private FiltroAplicadoMarketplace filtro;
    private List<ModalidadeDisponivelCota> listModalidadeDisponivel;
    private ModalidadeEnum selectModalidade = null;
    private TipoConcursoEnum selectTipoConcurso = null;
    private Group groupQueroNaoQuero;
    private int prognosticoMaximo = 80; //valor padrão;
    private List<Dezena> listaQtdApostas = new ArrayList<>();
    private ListaDezenaRecyclerView qtdApostasAdapter = null;
    private Integer qtdApostasSelecionadas = null;
    private Integer qtdDezenasSelecionada = null;
    private boolean todasModalidadesAtiva = false;
    private boolean desativarFiltro = false;
    private FiltroAplicadoMarketplace filtroLimpo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getExtras();
        setContentView( R.layout.activity_filtra_marketplace );
        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getResources().getString(R.string.filtros)));
        launcher = IntentUtil.registerLauncherActivityForResult(this, onQueroNaoQueroForResult());
        model =  new FiltroMarketplaceModel(this);
        setaViews();
        limpaFiltros();
        setaMetodos();
        configuraQtdApostasRcv();
        startFragmentSomadorCarrinho();
        if(desativarFiltro) desativarFiltro();
    }

    private void desativarFiltro(){
        if(desativarFiltro){
            editLoterica.setVisibility(View.GONE);
            textLoterica.setVisibility(View.GONE);
            autocomplete.setVisibility(View.GONE);
            textLotericaDescritivo.setVisibility(View.GONE);
            recyclerViewLoterica.setVisibility(View.GONE);
        }
    }

    private void getExtras(){
          Intent intent = getIntent();
        if (intent != null) {
            Bundle args = intent.getExtras();
            if (args != null) {
                filtro = (FiltroAplicadoMarketplace) args.getSerializable(BolaoActivity.ARG_FILTRO_MARKETPLACE);
                if(intent.hasExtra(BolaoActivity.ARG_FILTRO_LIMPO)){
                    filtroLimpo = (FiltroAplicadoMarketplace) args.getSerializable(BolaoActivity.ARG_FILTRO_LIMPO);
                }
                desativarFiltro = args.getBoolean(BolaoActivity.ARG_DESATIVAR_FILTRO);
            }
        }
    }

    private void startFragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_somador_carrinho, true);
        }else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizaParametros();
    }

    private void setaViews() {

        listView = findViewById(R.id.lista_modalidades);

        editValMin = findViewById(R.id.edit_val_min);
        editValMax = findViewById(R.id.edit_val_max);

        editQtdCotasMin = findViewById(R.id.quant_min_cotas);
        editQtdCotasMax = findViewById(R.id.quant_max_cotas);

        txtQtdApostas = findViewById(R.id.text_quantidade_apostas);

        validaQtdCotaMin = findViewById(R.id.txt_valida_cota_min);
        validaQtdCotaMax = findViewById(R.id.txt_valida_cota_max);

        editLoterica = findViewById(R.id.edit_loterica);
        textLoterica = findViewById(R.id.text_loterica);
        autocomplete = findViewById(R.id.autoComplete_loterica);
        textLotericaDescritivo = findViewById(R.id.text_loterica_descritivo);
        recyclerViewLoterica = findViewById(R.id.recyclerViewLoterica);

        btnPlusQuero = findViewById(R.id.img_accordion_quero);
        btnPlusNaoQuero = findViewById(R.id.img_accordion_nao_quero);
        btnPlusQtdDezenas = findViewById(R.id.img_accordion_qtd_dezenas);

        accordionQuero = findViewById(R.id.accordionContentQuero);
        accordionQuero.setVisibility(View.VISIBLE);

        groupQueroNaoQuero = findViewById(R.id.groupQueroNaoQuero);

        accordionNaoQuero = findViewById(R.id.accordionContentNaoQuero);
        accordionNaoQuero.setVisibility(View.VISIBLE);
        recyclerViewQuero = findViewById(R.id.rcv_quero);
        recyclerViewNaoQuero = findViewById(R.id.rcv_nao_quero);
        recyclerQtdApostas = findViewById(R.id.rv_qtde_apostas);
        recyclerQtdDezenas = findViewById(R.id.rcv_qtd_dezenas);

        btnExcluirQuero = findViewById(R.id.btn_excluir_num_quero);
        btnExcluirNaoQuero = findViewById(R.id.btn_excluir_num_nao_quero);
        btnExcluirQtdDezenas = findViewById(R.id.btn_excluir_qtd_dezenas);
        btnLimpaFiltros = findViewById(R.id.btn_limpar_filtros);
        btnAplicar = findViewById(R.id.btn_aplicar);
    }

    private void setaMetodos() {
        btnLimpaFiltros.setOnClickListener(v -> limpaFiltros());

        btnAplicar.setOnClickListener(v -> aplicarFiltro());

        montaListaModalidadeComTodas();

        editValMin.addTextChangedListener(new MoedaMaskTextWatcher(editValMin));

        editValMax.addTextChangedListener(new MoedaMaskTextWatcher(editValMax));

        textWatcher = MaskEditUtil.textWatcherOnTextTChangedListener(charSequence -> {
            onTextChanged(charSequence);
        });

        editLoterica.addTextChangedListener(textWatcher);

        editQtdCotasMin.addTextChangedListener(textWatcher);
        editQtdCotasMax.addTextChangedListener(textWatcher);

        btnPlusQuero.setOnClickListener(v -> abreActivityQueroNaoQuero(ARG_QUERO));
        btnPlusNaoQuero.setOnClickListener(v -> abreActivityQueroNaoQuero(ARG_NAO_QUERO));
        btnPlusQtdDezenas.setOnClickListener(v -> abreQuantidadeDezenas());

        btnExcluirQuero.setOnClickListener(v -> excluirNumerosQuero());
        btnExcluirNaoQuero.setOnClickListener(v -> excluirNumerosNaoQuero());
        btnExcluirQtdDezenas.setOnClickListener(v -> atualizaQuantidadeDezenas(null));

        recuperaEstadoFiltros();

        listaModalidadesAdapter = new ListaModalidadesAdapter(this, listModalidadeDisponivel, onItemClickListenerModalidade(),posicaoModalidadeInicial(filtro), todasModalidadesAtiva);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        listView.setAdapter(listaModalidadesAdapter);
        if(todasModalidadesAtiva){
            listView.scrollToPosition(0);
        }else{
            listView.scrollToPosition(posicaoModalidadeInicial(filtro));
        }

        queroAdapter = new QueroNaoQueroAdapter(dezenasSelecionadasQuero, onItemClickQueroListener());
        recyclerViewQuero.setAdapter(queroAdapter);
        recyclerViewQuero.setLayoutManager(new GridLayoutManager(FiltraMarketplaceActivity.this, ARG_QUANTIDADE_COLUNA_LISTAS));
        recyclerViewQuero.setNestedScrollingEnabled(false);

        naoQueroAdapter = new QueroNaoQueroAdapter(dezenasSelecionadasNaoQuero, onItemClickNaoQueroListener());
        recyclerViewNaoQuero.setAdapter(naoQueroAdapter);
        recyclerViewNaoQuero.setLayoutManager(new GridLayoutManager(FiltraMarketplaceActivity.this, ARG_QUANTIDADE_COLUNA_LISTAS));
        recyclerViewNaoQuero.setNestedScrollingEnabled(false);

        List<Integer> qtdDezenasIniciais = qtdDezenasSelecionada == null ? new ArrayList<>() : Collections.singletonList(qtdDezenasSelecionada);
        qtdDezenasSelecionadaAdapter = new QueroNaoQueroAdapter(qtdDezenasIniciais, (holder, position) -> atualizaQuantidadeDezenas(null));
        recyclerQtdDezenas.setAdapter(qtdDezenasSelecionadaAdapter);
        recyclerQtdDezenas.setLayoutManager(new GridLayoutManager(this, ARG_QUANTIDADE_COLUNA_LISTAS));
        recyclerQtdDezenas.setNestedScrollingEnabled(false);
        atualizaQuantidadeDezenas(qtdDezenasSelecionada);

        lotericaAdapter = new LotericaAdapter(listLotericaDTO, onItemClickListener());
        recyclerViewLoterica.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLoterica.setAdapter(lotericaAdapter);
        recyclerViewLoterica.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NotNull RecyclerView recyclerView, @NotNull MotionEvent event) {
                // Mantem o gesto na lista de sugestoes, sem consumir o toque de selecao.
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        recyclerView.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

    }

    private void recuperaEstadoFiltros(){

        if (filtro == null){
            return;
        }

        if (filtro.getValorMinimoAposta() != null && !filtro.getValorMinimoAposta().isEmpty()) {
            editValMin.setText(filtro.getValorMinimoAposta());
        }
        if(filtro.getValorMaximoAposta() != null && !filtro.getValorMaximoAposta().isEmpty()){
            editValMax.setText(filtro.getValorMaximoAposta());
        }
        if(filtro.getQtdMinCotas() != null && filtro.getQtdMinCotas() != 0){
            editQtdCotasMin.setText(filtro.getQtdMinCotas().toString());
        }
        if(filtro.getQtdMaxCotas() != null && filtro.getQtdMaxCotas() != 0){
            editQtdCotasMax.setText(filtro.getQtdMaxCotas().toString());
        }

        if(filtro.getLotericaDTO() != null){
            editLoterica.setText(String.format("%s - %s", mioloCodigoLoterica(filtro.getLotericaDTO().getCodigo()), filtro.getLotericaDTO().getNomeFantasia().trim()));
            selectLotericaDTO = filtro.getLotericaDTO();
            todasModalidadesAtiva = true;
        }
        if (filtro.getNumerosQuero() != null && !filtro.getNumerosQuero().isEmpty()) {
            dezenasSelecionadasQuero = new ArrayList<>(converteStringParaList(filtro.getNumerosQuero()));
            expandeContraiAccordionQueroNaoQuero();
        }
        if(filtro.getNumerosNaoQuero() != null && !filtro.getNumerosNaoQuero().isEmpty()){
            dezenasSelecionadasNaoQuero = new ArrayList<>(converteStringParaList(filtro.getNumerosNaoQuero()));
            expandeContraiAccordionQueroNaoQuero();
        }
        if(filtro.getModalidade() != null){
            selectModalidade = filtro.getModalidade();
        }

        if(filtro.getTipoConcurso() != null){
            selectTipoConcurso = filtro.getTipoConcurso();
        }
        if (filtro.getQtdApostas() != null && filtro.getQtdApostas() > 0) qtdApostasSelecionadas = filtro.getQtdApostas();
        if (filtro.getQtdDezenas() != null && filtro.getQtdDezenas() > 0) qtdDezenasSelecionada = filtro.getQtdDezenas();
    }

    private void configuraQtdApostasRcv(){
        if (selectModalidade == null) {
            opcoesQtdDezenas = model.getListaTodasModalidades();
        } else {
            ParametroSimulacao parametro = ViewUtils.getParametroSimulacao(SessaoUsuario.getInstance(), selectModalidade);
            if (parametro != null && parametro.getParametroJogo() != null) opcoesQtdDezenas = getQtdDezenas(parametro.getParametroJogo().getValoresAposta());
        }
        listaQtdApostas = montaListDezena(getOpcoesQtdApostas());
        DezenaConfig dezenaConfig = new DezenaConfig(true, R.color.azulFiltro, Color.WHITE, true);
        qtdApostasAdapter = new ListaDezenaRecyclerView(listaQtdApostas, new ArrayList<>(), dezenaConfig, onItemClickListenerQtdApostas());
        recyclerQtdApostas.setAdapter(qtdApostasAdapter);
        RecyclerView.LayoutManager layout = new GridLayoutManager(FiltraMarketplaceActivity.this, ARG_QUANTIDADE_COLUNA_LISTAS);
        recyclerQtdApostas.setLayoutManager(layout);
        recyclerQtdApostas.setNestedScrollingEnabled(false);
        if(qtdApostasSelecionadas != null) qtdApostasAdapter.atualizaSelecionados(Collections.singletonList(qtdApostasSelecionadas));
    }

    @NotNull
    private OnItemClickListener<DezenaHolder> onItemClickListenerQtdApostas() {
        return (holder, position) -> {
            if(listaQtdApostas == null || position >= listaQtdApostas.size()) return;
            try {
                Dezena dezena = listaQtdApostas.get(position);
                Integer valorSelecionado = Integer.valueOf(dezena.getValue());
                List<Integer> selecionadas;
                if(Objects.equals(qtdApostasSelecionadas, valorSelecionado)){
                    selecionadas = new ArrayList<>();
                    qtdApostasSelecionadas = null;
                }else{
                    selecionadas = Collections.singletonList(valorSelecionado);
                    qtdApostasSelecionadas = valorSelecionado;
                }
                qtdApostasAdapter.atualizaSelecionados(selecionadas);
            } catch (Exception e){
                Log.d("", e.getLocalizedMessage());
            }
        };
    }



    private List<Integer> converteStringParaList(String numerosString){
        List<Integer> lista = new ArrayList<>();
        String[] numeros= numerosString.split(";");
        for(String numero : numeros){
            lista.add(Integer.parseInt(numero));
        }
        return lista;
    }


    private int posicaoModalidadeInicial(FiltroAplicadoMarketplace filtro) {

        if (filtro == null || listModalidadeDisponivel == null){
            return -1;
        }

        for (int i = 0; i < listModalidadeDisponivel.size(); i++) {
            ModalidadeDisponivelCota modalidadeDisponivelCota = listModalidadeDisponivel.get(i);
            if (filtro.getModalidade() != null && filtro.getTipoConcurso() != null) {
                if (Objects.equals(modalidadeDisponivelCota.getModalidadeDetalhada().getValor(), ModalidadeEnum.fromStringToIdModalidade(filtro.getModalidade()))
                    && modalidadeDisponivelCota.getTipoConcurso().toString().equals(filtro.getTipoConcurso().toString())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private OnActivityForResult onQueroNaoQueroForResult() {
        return result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null){
                Bundle args = result.getData().getExtras();
                if(args != null){
                    String temp = args.getString(ARG_TITULO_QUERO_NAO_QUERO);
                    if(temp !=null &&temp.equals(ARG_QUERO)){
                        dezenasSelecionadasQuero = args.getIntegerArrayList(ARG_DEZENAS_SELECIONADAS);
                        queroAdapter.atualizaSelecionados(dezenasSelecionadasQuero);
                        expandeContraiAccordionQueroNaoQuero();
                        Log.d("Dezenas Quero", String.valueOf(dezenasSelecionadasQuero));
                    }else if(temp !=null  && temp.equals(ARG_NAO_QUERO)){
                        dezenasSelecionadasNaoQuero = args.getIntegerArrayList(ARG_DEZENAS_SELECIONADAS);
                        naoQueroAdapter.atualizaSelecionados(dezenasSelecionadasNaoQuero);
                        expandeContraiAccordionQueroNaoQuero();
                        Log.d("Dezenas Não Quero", String.valueOf(dezenasSelecionadasNaoQuero));

                    } else if (ARG_QUANTIDADE_DEZENAS.equals(temp)) {
                        ArrayList<Integer> selecionadas = args.getIntegerArrayList(ARG_DEZENAS_SELECIONADAS);
                        atualizaQuantidadeDezenas(selecionadas == null || selecionadas.isEmpty() ? null : selecionadas.get(0));

                    }else{
                        Log.d("DezenasQueroNaoQuero", getString(R.string.Quero_Nao_Quero_Impossivel_de_Identificar));
                    }
                }
            }
        };
    }

    private void abreQuantidadeDezenas() {
        Bundle args = new Bundle();
        args.putString(ARG_TITULO_QUERO_NAO_QUERO, ARG_QUANTIDADE_DEZENAS);
        args.putIntegerArrayList(ARG_OPCOES_QUANTIDADE_DEZENAS, new ArrayList<>(opcoesQtdDezenas));
        if (qtdDezenasSelecionada != null) args.putInt(ARG_QUANTIDADE_DEZENAS_SELECIONADA, qtdDezenasSelecionada);
        Intent intent = IntentUtil.getIntentOrigemDestino(this, QueroNaoQueroActivity.class);
        intent.putExtras(args);
        IntentUtil.startActivityForResult(launcher, intent);
    }

    private void atualizaQuantidadeDezenas(Integer quantidade) {
        qtdDezenasSelecionada = quantidade;
        if (qtdDezenasSelecionadaAdapter != null) {
            qtdDezenasSelecionadaAdapter.atualizaSelecionados(quantidade == null ? new ArrayList<>() : Collections.singletonList(quantidade));
        }
        View accordion = findViewById(R.id.accordionContentQtdDezenas);
        if (accordion != null) accordion.setVisibility(quantidade == null ? GONE : View.VISIBLE);
    }
    private void excluirNumerosQuero(){
        if(queroAdapter != null) {
            dezenasSelecionadasQuero = new ArrayList<>();
            queroAdapter.atualizaSelecionados(dezenasSelecionadasQuero);
            expandeContraiAccordionQueroNaoQuero();
        }
    }

    private void excluirNumerosNaoQuero(){
        if(naoQueroAdapter != null){
            dezenasSelecionadasNaoQuero = new ArrayList<>();
            naoQueroAdapter.atualizaSelecionados(dezenasSelecionadasNaoQuero);
            expandeContraiAccordionQueroNaoQuero();
        }
    }

    private void abreActivityQueroNaoQuero(String argQueroNaoQuero){

        try {
            ArrayList<Integer> dezenasQuero = new ArrayList<>(dezenasSelecionadasQuero);
            ArrayList<Integer> dezenasNaoQuero = new ArrayList<>(dezenasSelecionadasNaoQuero);

            Bundle args = new Bundle();
            args.putString(ARG_TITULO_QUERO_NAO_QUERO, argQueroNaoQuero);
            args.putIntegerArrayList(ARG_DEZENAS_QUERO, dezenasQuero);
            args.putIntegerArrayList(ARG_DEZENAS_NAO_QUERO, dezenasNaoQuero);
            args.putInt(ARG_QTD_MAX_PROGNOSTICOS, prognosticoMaximo);
            Intent intent = IntentUtil.getIntentOrigemDestino(FiltraMarketplaceActivity.this, QueroNaoQueroActivity.class);
            intent.putExtras(args);
            IntentUtil.startActivityForResult(launcher, intent);
        }catch (Exception e){

        }
    }

    private OnItemClickListener onItemClickListenerModalidade() {
        return (holder, position) -> {
            if (position == -1) {
                selectModalidade = null;
                selectTipoConcurso = null;
                todasModalidadesAtiva = true;
                prognosticoMaximo = 80; //Cartela com todas as modalidades selecionada
            }
            else {
                if (listModalidadeDisponivel != null && position >= 0 && position < listModalidadeDisponivel.size()) {
                    selectModalidade = listModalidadeDisponivel.get(position).getModalidade();
                    selectTipoConcurso = listModalidadeDisponivel.get(position).getTipoConcurso();
                    if (selectModalidade != null) {
                        todasModalidadesAtiva = false;
                        atualizaVolantes(selectModalidade);
                    }
                } else {
                    Log.w("FiltraMarketplace", "Posição inválida ou lista vazia. position=" + position);
                    return;
                }
            }

            atualizaLabelQtdDezenas();
            ocultaMostraCamposQueroNaoQuero();
            limpaUltimosFiltros();
            configuraQtdApostasRcv();
        };
    }

    private void atualizaLabelQtdDezenas(){

        if(selectModalidade != null && selectModalidade.equals(ModalidadeEnum.LOTECA)){
            txtQtdApostas.setText(R.string.quantidade_de_palpites);
        }else{
            txtQtdApostas.setText(R.string.quantidade_de_apostas);
        }

    }

    private void limpaUltimosFiltros(){
        if(qtdApostasAdapter != null){
            qtdApostasSelecionadas = null;
            qtdApostasAdapter.atualizaSelecionados(new ArrayList<>());
        }
        atualizaQuantidadeDezenas(null);
        if(dezenasSelecionadasNaoQuero != null && !dezenasSelecionadasNaoQuero.isEmpty()){
            excluirNumerosNaoQuero();
        }
        if(dezenasSelecionadasQuero != null && !dezenasSelecionadasQuero.isEmpty()){
            excluirNumerosQuero();
        }
    }

    private void ocultaMostraCamposQueroNaoQuero(){
        if(!mostraQueroNaoQuero()){
            groupQueroNaoQuero.setVisibility(View.VISIBLE);
            expandeContraiAccordionQueroNaoQuero();
        }else{
            groupQueroNaoQuero.setVisibility(GONE);
            accordionNaoQuero.setVisibility(GONE);
            accordionQuero.setVisibility(GONE);
        }
    }


    private void expandeContraiAccordionQueroNaoQuero(){
        if(dezenasSelecionadasQuero !=null && !dezenasSelecionadasQuero.isEmpty()){
            accordionQuero.setVisibility(View.VISIBLE);
        }else{
            accordionQuero.setVisibility(GONE);
        }

        if(dezenasSelecionadasNaoQuero !=null && !dezenasSelecionadasNaoQuero.isEmpty()){
            accordionNaoQuero.setVisibility(View.VISIBLE);
        }else{
            accordionNaoQuero.setVisibility(GONE);
        }
    }

    private OnItemClickListener onItemClickListener() {
        return (holder, position) -> {
            LotericaDTO lotericaDTO = listLotericaDTO.get(position);
            editLoterica.setText(String.format("%s - %s", mioloCodigoLoterica(lotericaDTO.getCodigo()), lotericaDTO.getNomeFantasia().trim()));
            recyclerViewLoterica.setVisibility(GONE);
            selectLotericaDTO = lotericaDTO;
            listaModalidadesAdapter.montaListComTodaModalidades(true);
        };
    }

    private OnItemClickListener<QueroNaoQueroViewHolder> onItemClickQueroListener() {
        return (holder, position) -> {
            try {
                Integer dezena = dezenasSelecionadasQuero.get(position);
                dezenasSelecionadasQuero.remove(dezena);
                queroAdapter.atualizaSelecionados(dezenasSelecionadasQuero);
                expandeContraiAccordionQueroNaoQuero();

            } catch (Exception e){
                Log.d("", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        };
    }

    private OnItemClickListener<QueroNaoQueroViewHolder> onItemClickNaoQueroListener() {
        return (holder, position) -> {
            try {
                Integer dezena = dezenasSelecionadasNaoQuero.get(position);
                dezenasSelecionadasNaoQuero.remove(dezena);
                naoQueroAdapter.atualizaSelecionados(dezenasSelecionadasNaoQuero);
                expandeContraiAccordionQueroNaoQuero();
            } catch (Exception e){
                Log.d("", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        };
    }


    private String mioloCodigoLoterica(String codigoLoterico) {
        return codigoLoterico.substring(3,9);
    }

    private void onTextChanged(String charSequence){
        editLoterica.removeTextChangedListener(textWatcher);

        filtraLoterica(charSequence.toString());

        editLoterica.setSelection(editLoterica.length());
        editLoterica.addTextChangedListener(textWatcher);
        if(editLoterica.length() == 0){
            selectLotericaDTO = null;
            listaModalidadesAdapter.montaListComTodaModalidades(false);
        }
        validaFiltros();
    }

    private void validaFiltros() {
        validaFiltro(editQtdCotasMin, validaQtdCotaMin, 2, 100, R.string.qtd_min_menor_2, R.string.qtd_min_maior_max);
        validaFiltro(editQtdCotasMax, validaQtdCotaMax, 2, 100, R.string.qtd_max_menor_2, R.string.qtd_max_maior_100);
        habilitaDesabilitaBtnAplicar();
    }


    private void validaFiltro(EditText editText, TextView validaTextView, int minValue, int maxValue, int msgMinErro, int msgMaxErro) {
        boolean validacaoQtdMin = editText == findViewById(R.id.quant_min_cotas);

        if (editText.getText() != null && !editText.getText().toString().isEmpty()) {
            int value;
            if (editText.getText() == null || editText.getText().toString().isEmpty()) {
                validaTextView.setVisibility(GONE);
                value = -1;
            } else {
                value = Integer.parseInt(editText.getText().toString());            }

            //A validacado de qtdMin para incluir o caso em que a qtdMin for maior que a qtdMax
            if(validacaoQtdMin){
                if (editQtdCotasMax.getText() != null && !editQtdCotasMax.getText().toString().isEmpty()){
                    maxValue = Integer.parseInt(editQtdCotasMax.getText().toString());
                }
            }
            if (value > maxValue) {
                validaTextView.setText(getString(msgMaxErro));
                validaTextView.setVisibility(View.VISIBLE);
            } else if (value >= 0 && value < minValue) {
                validaTextView.setText(getString(msgMinErro));
                validaTextView.setVisibility(View.VISIBLE);
            } else {
                validaTextView.setVisibility(GONE);
            }
        }else{
            validaTextView.setVisibility(GONE);
        }
    }

    private void habilitaDesabilitaBtnAplicar(){

        if(validaQtdCotaMin.getVisibility() == GONE &&
           validaQtdCotaMax.getVisibility() == GONE){
            btnAplicar.setEnabled(true);
            btnAplicar.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_rounded_blue));
        }else{
            btnAplicar.setEnabled(false);
            btnAplicar.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_rounded_cinza));
        }

    }

    private void aplicarFiltro() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FILTRO_MARKETPLACE_RETORNO, getExtraFiltro());
        returnIntent.putExtra(FILTRO_LIMPO_RETORNO, filtroLimpo);
        returnIntent.putExtra(IS_FILTRANDO_RETORNO, true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void limparFiltro() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(IS_FILTRANDO_RETORNO, false);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    private FiltroAplicadoMarketplace getExtraFiltro() {
        String qtdCotasMin, qtdCotasMax, qtdApostas, numerosQuero = "",numerosNaoQuero = "";
        if (filtroLimpo==null) filtroLimpo = new FiltroAplicadoMarketplace();
        filtroLimpo.setNumerosQuero("");
        filtroLimpo.setNumerosNaoQuero("");
        filtroLimpo.setValorMinimoAposta("");
        filtroLimpo.setValorMaximoAposta("");

        if(editValMax.getText() != null && !editValMax.getText().toString().isEmpty()){
            BigDecimal valor = new BigDecimal(editValMax.getText().toString().replace(".","").replace(",","."));
            if(valor.compareTo(BigDecimal.ZERO) == 0){
                editValMax.setText("");
            }
        }

        if(editValMin.getText() != null && !editValMin.getText().toString().isEmpty()){
            BigDecimal valor = new BigDecimal(editValMin.getText().toString().replace(".","").replace(",","."));
            if(valor.compareTo(BigDecimal.ZERO) == 0){
                editValMin.setText("");
            }
        }

        if(editQtdCotasMin.getText() == null || editQtdCotasMin.getText().toString().isEmpty()){
            qtdCotasMin = "0";
            filtroLimpo.setQtdMinCotas(0);
        }else{
            qtdCotasMin = editQtdCotasMin.getText().toString();
        }
        if(editQtdCotasMax.getText() == null || editQtdCotasMax.getText().toString().isEmpty()){
            qtdCotasMax = "0";
            filtroLimpo.setQtdMaxCotas(0);
        }else{
            qtdCotasMax = editQtdCotasMax.getText().toString();
        }
        if(qtdApostasSelecionadas == null){
            qtdApostas = "0";
            filtroLimpo.setQtdApostas(0);
        }else{
            qtdApostas = qtdApostasSelecionadas.toString();
        }
        if(qtdDezenasSelecionada == null){
            filtroLimpo.setQtdDezenas(0);
        }
        if(editLoterica.getText().toString().isEmpty() || editLoterica.getText().toString().length() < 6){
            selectLotericaDTO = null;
        }
        if(dezenasSelecionadasQuero != null && !dezenasSelecionadasQuero.isEmpty()){
            String numeros = dezenasSelecionadasQuero.toString();
            numerosQuero = numeros.replace("[", "").replace("]", "").replace(", ", ";");
            Log.d("numerosQueroString", numerosQuero);
        }

        if(dezenasSelecionadasNaoQuero != null && !dezenasSelecionadasNaoQuero.isEmpty()){
            String numeros = dezenasSelecionadasNaoQuero.toString();
            numerosNaoQuero = numeros.replace("[", "").replace("]", "").replace(", ", ";");
            Log.d("numerosQueroString", numerosNaoQuero);
        }

        return model.getFiltroAplicado( selectLotericaDTO,editValMin.getText().toString(),
                editValMax.getText().toString(),
                Integer.valueOf(qtdCotasMin),
                Integer.valueOf(qtdCotasMax),
                Integer.valueOf(qtdApostas),
                qtdDezenasSelecionada == null ? 0 : qtdDezenasSelecionada,
                numerosQuero, numerosNaoQuero,
                selectModalidade, selectTipoConcurso, todasModalidadesAtiva);
    }


    private void limpaFiltros() {
        editQtdCotasMin.setText("");
        editQtdCotasMax.setText("");
        if(qtdApostasAdapter != null){
            qtdApostasAdapter.atualizaSelecionados(new ArrayList<>());
        }
        qtdApostasSelecionadas = null;
        atualizaQuantidadeDezenas(null);
        editValMin.setText("");
        editValMax.setText("");
        if(!desativarFiltro) {
            editLoterica.setText("");
            selectLotericaDTO = null;
        }
        if(queroAdapter != null){
            excluirNumerosQuero();
        }
        if(naoQueroAdapter != null){
            excluirNumerosNaoQuero();
        }
        expandeContraiAccordionQueroNaoQuero();

        if(filtroLimpo != null){
            selectModalidade = filtroLimpo.getModalidade();
            selectTipoConcurso = filtroLimpo.getTipoConcurso();
        }

        if(listaModalidadesAdapter != null && listView != null && filtroLimpo != null){
            listaModalidadesAdapter = new ListaModalidadesAdapter(this, listModalidadeDisponivel, onItemClickListenerModalidade(),posicaoModalidadeInicial(filtroLimpo), todasModalidadesAtiva);
            listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            listView.setAdapter(listaModalidadesAdapter);
            listaModalidadesAdapter.montaListComTodaModalidades(desativarFiltro);
            listView.scrollToPosition(posicaoModalidadeInicial(filtroLimpo));
        }
        limparFiltro();
    }

    private void filtraLoterica(String text) {

        if (text.length() >= 6) {

            if (text.length() == 6) {
                AlertDialogUtils.show(this);
                model.buscaLotericas(text, onBuscaLotericasListener());
            } else {
                if (listLotericaDTO != null && listLotericaDTO.size()>0) {
                    listLotericaDTO = model.getListaFiltrada(text, listLotericaDTO);
                    apresentaListaFiltrada(text, model.getListaFiltrada(text, listLotericaDTO));
                }
            }
        } else {
            recyclerViewLoterica.setVisibility(GONE);
        }
    }

    private OnSilceListener<List<LotericaDTO>> onBuscaLotericasListener() {
        return new OnSilceListener<List<LotericaDTO>>() {
            @Override
            public void success(List<LotericaDTO> payload) {
                AlertDialogUtils.dismiss();
                listLotericaDTO = payload;
                apresentaListaFiltrada(null, listLotericaDTO);
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        };
    }

    private void apresentaListaFiltrada(String text, List<LotericaDTO> filteredList) {
        if (text == null && filteredList.isEmpty()){
            TestVisao.toast("Sua busca não retornou resultados.");
            return;
        }
        recyclerViewLoterica.setVisibility(View.VISIBLE);
        lotericaAdapter.filterList(filteredList);
    }

    private void montaListaModalidadeComTodas() {
        listModalidadeDisponivel = new ArrayList<>();

        List<ModalidadeDisponivelCota> listModalidadeDisponivelSessao = SessaoUsuario.getInstance().getParametrosSimulacao().getModalidadesDisponiveisCotas();
        if (listModalidadeDisponivelSessao != null && !listModalidadeDisponivelSessao.isEmpty()) {
            for (ModalidadeDisponivelCota modalidadesDisponiveisCota : listModalidadeDisponivelSessao) {
                listModalidadeDisponivel.add(modalidadesDisponiveisCota);
            }
        }
    }


    private void atualizaParametros() {
        AlertDialogUtils.show(this);

        if(SessaoUsuarioUtil.precisaAtualizar()){
            new ParametrosSimulacaoModel(this)
                    .buscaParametroSiumulacao(new OnSilceListener<ParametrosSimulacao>() {
                        @Override
                        public void success(ParametrosSimulacao payload) {
                            AlertDialogUtils.dismiss();
                            AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_lista_modalidades));
                            SessaoUsuarioUtil.atualizaParametrosSingleton(payload);
                        }

                        @Override
                        public void error(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            RedirectNetwork.checkRedirect(error, FiltraMarketplaceActivity.this);
                        }
                    });
        }else {
            AlertDialogUtils.dismiss();
        }
    }

    private void atualizaVolantes(ModalidadeEnum modalidade) {

        if (modalidade == null) {
            return;
        }

        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao params = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidade);

        if (params != null && params.getParametroJogo() != null) {

            ParametroJogoDTO parametroJogo = params.getParametroJogo();
            List<ParametroValorApostaDTO> listValorAposta = parametroJogo.getValoresAposta();
            opcoesQtdDezenas = getQtdDezenasDisponiveis(modalidade, listValorAposta);

            Log.d("FiltraMarketplace", "Parametro Jogo carregado: " + parametroJogo);

            if (!ModalidadeEnum.LOTECA.equals(modalidade)) {
                if (parametroJogo.getPrognosticoMaximo() == null){
                    prognosticoMaximo = 80;
                }
                else {
                    prognosticoMaximo = parametroJogo.getPrognosticoMaximo();
                }
            }

        } else {

            if (!ModalidadeEnum.LOTECA.equals(modalidade)) {
                prognosticoMaximo = model.getPrognosticosMaximoQueroNaoQuero(modalidade);
            }

            opcoesQtdDezenas = model.getListaQtdDezenas(modalidade);

        }

    }

    private List<Integer> getOpcoesQtdApostas() {
        int minimo = SharedPreferencesUtils.getValorInt(ConfiguracoesEnum.QTDE_MINIMA_APOSTA_BOLAO_FILTRO.get(), ConfiguracoesDefaultEnum.QTDE_MINIMA_APOSTA_BOLAO_FILTRO.asInt());
        int maximo = SharedPreferencesUtils.getValorInt(ConfiguracoesEnum.QTDE_MAXIMA_APOSTA_BOLAO_FILTRO.get(), ConfiguracoesDefaultEnum.QTDE_MAXIMA_APOSTA_BOLAO_FILTRO.asInt());
        return model.getIntervalo(minimo, maximo);
    }

    private List<Integer> getQtdDezenasDisponiveis(ModalidadeEnum modalidade, List<ParametroValorApostaDTO> listValorAposta) {
        if (listValorAposta != null && !listValorAposta.isEmpty()){
            return ModalidadeEnum.LOTECA.equals(modalidade)
                    ? model.getListaQtdDezenas(modalidade)
                    : getQtdDezenas(listValorAposta);
        } else {
            return model.getListaQtdDezenas(modalidade);
        }

    }

    private List<Integer> getQtdDezenas(List<ParametroValorApostaDTO> listValorAposta){
        Set<Integer> lista = new TreeSet<>();
        if(listValorAposta != null && !listValorAposta.isEmpty()){
            for(ParametroValorApostaDTO valorAposta : listValorAposta){
                if(valorAposta.getNumeroPrognosticos() != null){
                    lista.add(valorAposta.getNumeroPrognosticos());
                }
            }
        }
        return new ArrayList<>(lista);
    }


    public List<Dezena> montaListDezena(List<Integer> listaQtdDezenas){
        List<Dezena> dezenas = new ArrayList<>();
        for (Integer qtdDezena : listaQtdDezenas) {
            dezenas.add(new Dezena("" + qtdDezena, Boolean.FALSE));
        }
        return dezenas;
    }

    private boolean mostraQueroNaoQuero(){
        if(selectModalidade != null){
            return selectModalidade.equals(ModalidadeEnum.LOTECA) ||
                   selectModalidade.equals(ModalidadeEnum.SUPER_7);
        }else{
            return false;
        }

    }
}
