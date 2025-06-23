package br.gov.caixa.loterias.apostas.controllers;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.gov.caixa.loterias.apostas.view.activity.BolaoActivity;
import com.android.volley.VolleyError;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.FiltroMarketplaceModel;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.SessaoUsuarioUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaModalidadesAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotericaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.QueroNaoQueroAdapter;
import br.gov.caixa.loterias.apostas.view.custom.MoedaMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.holder.QueroNaoQueroViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class FiltraMarketplaceActivity extends LoteriasAppMarketPlaceActivity {
    public static final String FILTRO_MARKETPLACE_RETORNO = "FILTRO_MARKETPLACE_RETORNO";
    public static final String ARG_TITULO_QUERO_NAO_QUERO = "titulo";
    public static final String ARG_QUERO = "quero";
    public static final String ARG_NAO_QUERO = "naoQuero";
    public static final String ARG_DEZENAS_SELECIONADAS = "dezenasSelecionadas";
    public static  final String ARG_DEZENAS_QUERO = "dezenasQuero";
    public static  final String ARG_DEZENAS_NAO_QUERO = "dezenasNaoQuero";
    public static final int ARG_QUANTIDADE_COLUNA_LISTAS = 6;
    private List<Integer> dezenasSelecionadasQuero = new ArrayList<>();
    private List<Integer> dezenasSelecionadasNaoQuero = new ArrayList<>();
    private RecyclerView listView;
    private ListaModalidadesAdapter listaModalidadesAdapter;
    private EditText editValMin, editValMax, editQtdCotasMin, editQtdCotasMax, editQtdDezenas;
    private EditText editLoterica;
    private TextView validaDezenas, validaQtdCotaMin, validaQtdCotaMax;
    private RecyclerView recyclerViewLoterica, recyclerViewQuero, recyclerViewNaoQuero;
    private LotericaAdapter lotericaAdapter;
    private QueroNaoQueroAdapter queroAdapter, naoQueroAdapter;
    private LinearLayout accordionQuero, accordionNaoQuero;
    private Button btnLimpaFiltros, btnAplicar, btnExcluirQuero, btnExcluirNaoQuero;
    private AppCompatImageView btnPlusQuero, btnPlusNaoQuero;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getExtras();
        setContentView( R.layout.activity_filtra_marketplace );
        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getResources().getString(R.string.filtrar)));
        launcher = IntentUtil.registerLauncherActivityForResult(this, onQueroNaoQueroForResult());
        model =  new FiltroMarketplaceModel(this);
        setaViews();
        limpaFiltros();
        setaMetodos();
        startFragmentSomadorCarrinho();

    }

    private void getExtras(){
          Intent intent = getIntent();
            if (intent != null) {
                Bundle args = intent.getExtras();
                if (args != null) {
                    filtro = (FiltroAplicadoMarketplace) args.getSerializable(BolaoActivity.ARG_FILTRO_MARKETPLACE);
                    selectModalidade = filtro.getModalidade();
                    selectTipoConcurso = filtro.getTipoConcurso();
                }
            }
    }

    private void startFragmentSomadorCarrinho() {
        if(somadorCarrinhoFragment == null){
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, true);
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
        editQtdDezenas = findViewById(R.id.quant_dezenas);

        validaDezenas = findViewById(R.id.txt_valida_dezenas);
        validaQtdCotaMin = findViewById(R.id.txt_valida_cota_min);
        validaQtdCotaMax = findViewById(R.id.txt_valida_cota_max);

        editLoterica = findViewById(R.id.edit_loterica);
        recyclerViewLoterica = findViewById(R.id.recyclerViewLoterica);

        btnPlusQuero = findViewById(R.id.img_accordion_quero);
        btnPlusNaoQuero = findViewById(R.id.img_accordion_nao_quero);

        accordionQuero = findViewById(R.id.accordionContentQuero);
        accordionQuero.setVisibility(View.VISIBLE);

        accordionNaoQuero = findViewById(R.id.accordionContentNaoQuero);
        accordionNaoQuero.setVisibility(View.VISIBLE);
        recyclerViewQuero = findViewById(R.id.rcv_quero);
        recyclerViewNaoQuero = findViewById(R.id.rcv_nao_quero);

        btnExcluirQuero = findViewById(R.id.btn_excluir_num_quero);
        btnExcluirNaoQuero = findViewById(R.id.btn_excluir_num_nao_quero);
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
        editQtdDezenas.addTextChangedListener(textWatcher);

        btnPlusQuero.setOnClickListener(v -> abreNumerosQuero());
        btnPlusNaoQuero.setOnClickListener(v -> abreNumerosNaoQuero());

        btnExcluirQuero.setOnClickListener(v -> excluirNumerosQuero());
        btnExcluirNaoQuero.setOnClickListener(v -> excluirNumerosNaoQuero());

        recuperaEstadoFiltros();

        int posicaoModalidadeSelecionada = posicaoModalidadeSelecionada();

        listaModalidadesAdapter = new ListaModalidadesAdapter(this, listModalidadeDisponivel, onItemClickListenerModalidade(),posicaoModalidadeSelecionada);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        listView.setAdapter(listaModalidadesAdapter);
        if(posicaoModalidadeSelecionada != -1){
            listView.scrollToPosition(posicaoModalidadeSelecionada());
        }

        queroAdapter = new QueroNaoQueroAdapter(dezenasSelecionadasQuero, onItemClickQueroListener());
        recyclerViewQuero.setAdapter(queroAdapter);
        recyclerViewQuero.setLayoutManager(new GridLayoutManager(FiltraMarketplaceActivity.this, ARG_QUANTIDADE_COLUNA_LISTAS));
        recyclerViewQuero.setNestedScrollingEnabled(false);

        naoQueroAdapter = new QueroNaoQueroAdapter(dezenasSelecionadasNaoQuero, onItemClickNaoQueroListener());
        recyclerViewNaoQuero.setAdapter(naoQueroAdapter);
        recyclerViewNaoQuero.setLayoutManager(new GridLayoutManager(FiltraMarketplaceActivity.this, ARG_QUANTIDADE_COLUNA_LISTAS));
        recyclerViewNaoQuero.setNestedScrollingEnabled(false);

        lotericaAdapter = new LotericaAdapter(listLotericaDTO, onItemClickListener());
        recyclerViewLoterica.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLoterica.setAdapter(lotericaAdapter);

    }

    private void recuperaEstadoFiltros(){

        if(filtro != null){
            if(filtro.getValorMinimoAposta() != null && !filtro.getValorMinimoAposta().isEmpty()){
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
            if(filtro.getQtdDezenas() != null && filtro.getQtdDezenas() != 0){
                editQtdDezenas.setText(filtro.getQtdDezenas().toString());
            }
            if(filtro.getLotericaDTO() != null){
                editLoterica.setText(String.format("%s - %s", mioloCodigoLoterica(filtro.getLotericaDTO().getCodigo()), filtro.getLotericaDTO().getNomeFantasia().trim()));
                selectLotericaDTO = filtro.getLotericaDTO();
            }
            if (filtro.getNumerosQuero() != null && !filtro.getNumerosQuero().isEmpty()) {
                dezenasSelecionadasQuero = new ArrayList<>(converteStringParaList(filtro.getNumerosQuero()));
                mostraOcultaNumerosQueroNaoQuero();
            }
            if(filtro.getNumerosNaoQuero() != null && !filtro.getNumerosNaoQuero().isEmpty()){
                dezenasSelecionadasNaoQuero = new ArrayList<>(converteStringParaList(filtro.getNumerosNaoQuero()));
                mostraOcultaNumerosQueroNaoQuero();
            }
        }
    }

    private List<Integer> converteStringParaList(String numerosString){
        List<Integer> lista = new ArrayList<>();
        String[] numeros= numerosString.split(";");
        for(String numero : numeros){
            lista.add(Integer.parseInt(numero));
        }
        return lista;
    }


    private int posicaoModalidadeSelecionada() {
        for (int i = 0; i < listModalidadeDisponivel.size(); i++) {
            ModalidadeDisponivelCota modalidadeDisponivelCota = listModalidadeDisponivel.get(i);
            if (Objects.equals(modalidadeDisponivelCota.getModalidadeDetalhada().getValor(), ModalidadeEnum.fromStringToIdModalidade(filtro.getModalidade()))
                    && modalidadeDisponivelCota.getTipoConcurso().toString().equals(filtro.getTipoConcurso().toString())) {
                return i;
            }
        }
        return -1; // Adiciona um valor de retorno padrão
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
                        mostraOcultaNumerosQueroNaoQuero();
                        Log.d("Dezenas Quero", String.valueOf(dezenasSelecionadasQuero));
                    }else if(temp !=null  && temp.equals(ARG_NAO_QUERO)){
                        dezenasSelecionadasNaoQuero = args.getIntegerArrayList(ARG_DEZENAS_SELECIONADAS);
                        naoQueroAdapter.atualizaSelecionados(dezenasSelecionadasNaoQuero);
                        mostraOcultaNumerosQueroNaoQuero();
                        Log.d("Dezenas Não Quero", String.valueOf(dezenasSelecionadasNaoQuero));

                    }else{
                        Log.d("DezenasQueroNaoQuero", getString(R.string.Quero_Nao_Quero_Impossivel_de_Identificar));
                    }
                }
            }
        };
    }
    private void excluirNumerosQuero(){
        dezenasSelecionadasQuero = new ArrayList<>();
        queroAdapter.atualizaSelecionados(dezenasSelecionadasQuero);
        mostraOcultaNumerosQueroNaoQuero();
    }

    private void excluirNumerosNaoQuero(){
        dezenasSelecionadasNaoQuero = new ArrayList<>();
        naoQueroAdapter.atualizaSelecionados(dezenasSelecionadasNaoQuero);
        mostraOcultaNumerosQueroNaoQuero();
    }
    private void abreNumerosQuero(){
        putExtrasQueroNaoQuero(ARG_QUERO);
    }

    private void abreNumerosNaoQuero(){
        putExtrasQueroNaoQuero(ARG_NAO_QUERO);
    }

    private void putExtrasQueroNaoQuero(String argQueroNaoQuero){

        //Casting necessário para passar o dado via putIntegerArrayList, a seguir.
        ArrayList<Integer> dezenasQuero = new ArrayList<>(dezenasSelecionadasQuero);
        ArrayList<Integer> dezenasNaoQuero = new ArrayList<>(dezenasSelecionadasNaoQuero);

        Bundle args = new Bundle();
        args.putString(ARG_TITULO_QUERO_NAO_QUERO, argQueroNaoQuero);
        args.putIntegerArrayList(ARG_DEZENAS_QUERO, dezenasQuero);
        args.putIntegerArrayList(ARG_DEZENAS_NAO_QUERO, dezenasNaoQuero);
        Intent intent = IntentUtil.getIntentOrigemDestino(FiltraMarketplaceActivity.this, QueroNaoQueroActivity.class);
        intent.putExtras(args);
        IntentUtil.startActivityForResult(launcher, intent);
    }

    private OnItemClickListener onItemClickListenerModalidade() {
        return (holder, position) -> {
            selectModalidade = listModalidadeDisponivel.get(position).getModalidade();
            selectTipoConcurso = listModalidadeDisponivel.get(position).getTipoConcurso();
        };
    }

    private void mostraOcultaNumerosQueroNaoQuero(){
        if(dezenasSelecionadasQuero !=null && dezenasSelecionadasQuero.size() > 0){
            accordionQuero.setVisibility(View.VISIBLE);
        }else{
            accordionQuero.setVisibility(View.GONE);
        }

        if(dezenasSelecionadasNaoQuero !=null && dezenasSelecionadasNaoQuero.size() >0){
            accordionNaoQuero.setVisibility(View.VISIBLE);
        }else{
            accordionNaoQuero.setVisibility(View.GONE);
        }
    }

    private OnItemClickListener onItemClickListener() {
        return (holder, position) -> {
            LotericaDTO lotericaDTO = listLotericaDTO.get(position);
            editLoterica.setText(String.format("%s - %s", mioloCodigoLoterica(lotericaDTO.getCodigo()), lotericaDTO.getNomeFantasia().trim()));
            recyclerViewLoterica.setVisibility(View.GONE);
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
                mostraOcultaNumerosQueroNaoQuero();

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
                mostraOcultaNumerosQueroNaoQuero();
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
        validaFiltros();
    }

    private void validaFiltros() {
        validaFiltro(editQtdDezenas, validaDezenas, 5, 21, R.string.qtd_dezenas_menor_5, R.string.qtd_dezenas_maior_20);
        validaFiltro(editQtdCotasMin, validaQtdCotaMin, 2, 100, R.string.qtd_min_menor_2, R.string.qtd_min_maior_max);
        validaFiltro(editQtdCotasMax, validaQtdCotaMax, 2, 100, R.string.qtd_max_menor_2, R.string.qtd_max_maior_100);
        habilitaDesabilitaBtnAplicar();
    }


    private void validaFiltro(EditText editText, TextView validaTextView, int minValue, int maxValue, int msgMinErro, int msgMaxErro) {
        boolean validacaoQtdMin = false;
        if(editText == findViewById(R.id.quant_min_cotas)){
            validacaoQtdMin = true;
        }

        if (editText.getText() != null && !editText.getText().toString().equals("")) {
            int value;
            if (editText.getText() == null || editText.getText().toString().equals("")) {
                validaTextView.setVisibility(View.GONE);
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
                validaTextView.setVisibility(View.GONE);
            }
        }else{
            validaTextView.setVisibility(View.GONE);
        }
    }

    private void habilitaDesabilitaBtnAplicar(){

        if(validaDezenas.getVisibility() == View.GONE &&
                validaQtdCotaMin.getVisibility() == View.GONE &&
                validaQtdCotaMax.getVisibility() == View.GONE){
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
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private FiltroAplicadoMarketplace getExtraFiltro() {
        String qtdCotasMin, qtdCotasMax, qtdDezenas, numerosQuero = "", numerosNaoQuero = "";

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
        }else{
            qtdCotasMin = editQtdCotasMin.getText().toString();
        }
        if(editQtdCotasMax.getText() == null || editQtdCotasMax.getText().toString().isEmpty()){
            qtdCotasMax = "0";
        }else{
            qtdCotasMax = editQtdCotasMax.getText().toString();
        }
        if(editQtdDezenas.getText() == null || editQtdDezenas.getText().toString().isEmpty()){
            qtdDezenas = "0";
        }else{
            qtdDezenas = editQtdDezenas.getText().toString();
        }
        if(editLoterica.getText().toString().isEmpty() || editLoterica.getText().toString().length() < 6){
            selectLotericaDTO = null;
        }
        if(dezenasSelecionadasQuero != null && dezenasSelecionadasQuero.size() > 0){
            String numeros = dezenasSelecionadasQuero.toString();
            numerosQuero = numeros.replace("[", "").replace("]", "").replace(", ", ";");
            Log.d("numerosQueroString", numerosQuero);
        }

        if(dezenasSelecionadasNaoQuero != null && dezenasSelecionadasNaoQuero.size() > 0){
            String numeros = dezenasSelecionadasNaoQuero.toString();
            numerosNaoQuero = numeros.replace("[", "").replace("]", "").replace(", ", ";");
            Log.d("numerosQueroString", numerosNaoQuero);
        }

        return model.getFiltroAplicado( selectLotericaDTO,editValMin.getText().toString(),
                editValMax.getText().toString(),
                Integer.valueOf(qtdCotasMin),
                Integer.valueOf(qtdCotasMax),
                Integer.valueOf(qtdDezenas),
                numerosQuero, numerosNaoQuero,
                selectModalidade, selectTipoConcurso);
    }


    private void limpaFiltros() {
        editQtdCotasMin.setText("");
        editQtdCotasMax.setText("");
        editQtdDezenas.setText("");
        editValMin.setText("");
        editValMax.setText("");
        editLoterica.setText("");
        selectLotericaDTO = null;
        if(queroAdapter != null){
            excluirNumerosQuero();
        }
        if(naoQueroAdapter != null){
            excluirNumerosNaoQuero();
        }
        mostraOcultaNumerosQueroNaoQuero();
        if(listaModalidadesAdapter != null) {
            listaModalidadesAdapter.montaListComTodaModalidades(false);
        }
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
            recyclerViewLoterica.setVisibility(View.GONE);
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

}
