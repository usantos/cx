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
import com.android.volley.VolleyError;
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
    private Button btnLimpaFiltros;
    private Button btnAplicar;
    private AppCompatImageView btnPlusQuero, btnPlusNaoQuero;
    private ActivityResultLauncher<Intent> launcher;
    private List<LotericaDTO> listLotericaDTO = new ArrayList<>();
    private LotericaDTO selectLotericaDTO = null;
    private SomadorCarrinhoFragment somadorCarrinhoFragment;
    private TextWatcher textWatcher;
    private FiltroMarketplaceModel model;
    private List<ModalidadeDisponivelCota> listModalidadeDisponivel;
    private ModalidadeEnum selectModalidade = null;
    private TipoConcursoEnum selectTipoConcurso = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
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

        btnLimpaFiltros = findViewById(R.id.btn_limpar_filtros);
        btnAplicar = findViewById(R.id.btn_aplicar);
    }

    private void setaMetodos() {
        btnLimpaFiltros.setOnClickListener(v -> limpaFiltros());

        btnAplicar.setOnClickListener(v -> aplicarFiltro());

        montaListaModalidadeComTodas();

        listaModalidadesAdapter = new ListaModalidadesAdapter(listModalidadeDisponivel, onItemClickListenerModalidade(),this);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        listView.setAdapter(listaModalidadesAdapter);

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
                        mostraNumerosQueroNaoQuero();
                        Log.d("Dezenas Quero", String.valueOf(dezenasSelecionadasQuero));
                    }else if(temp !=null  && temp.equals(ARG_NAO_QUERO)){
                        dezenasSelecionadasNaoQuero = args.getIntegerArrayList(ARG_DEZENAS_SELECIONADAS);
                        naoQueroAdapter.atualizaSelecionados(dezenasSelecionadasNaoQuero);
                        mostraNumerosQueroNaoQuero();
                        Log.d("Dezenas Não Quero", String.valueOf(dezenasSelecionadasNaoQuero));

                    }else{
                        Log.d("DezenasQueroNaoQuero", getString(R.string.Quero_Nao_Quero_Impossivel_de_Identificar));
                    }
                }
            }
        };
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
            if (position == 0) {
                selectModalidade = null;
                selectTipoConcurso = null;
            } else {
                selectModalidade = listModalidadeDisponivel.get(position).getModalidade();
                selectTipoConcurso = listModalidadeDisponivel.get(position).getTipoConcurso();
            }
        };
    }

    private void mostraNumerosQueroNaoQuero(){
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
            editLoterica.setText(mioloCodigoLoterica(lotericaDTO.getCodigo()) + " - " +
                    lotericaDTO.getNomeFantasia().trim());
            recyclerViewLoterica.setVisibility(View.GONE);
            selectLotericaDTO = lotericaDTO;
        };
    }

    private OnItemClickListener<QueroNaoQueroViewHolder> onItemClickQueroListener() {
        return (holder, position) -> {
            try {
                Integer dezena = dezenasSelecionadasQuero.get(position);
                dezenasSelecionadasQuero.remove(dezena);
                queroAdapter.atualizaSelecionados(dezenasSelecionadasQuero);
                mostraNumerosQueroNaoQuero();

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
                mostraNumerosQueroNaoQuero();
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


    private void validaFiltros(){
        validaFiltroDezenas();
        validaFiltroQtdCotasMin();
        validaFiltroQtdCotasMax();
        habilitaDesabilitaBtnAplicar();
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

    private void validaFiltroDezenas(){

        if(editQtdDezenas.getText() != null || !editQtdDezenas.getText().toString().equals("")) {
            int numero;
            if (editQtdDezenas.getText() == null || editQtdDezenas.getText().toString().equals("")) {
                validaDezenas.setVisibility(View.GONE);
                numero = -1;
            } else {
                numero = Integer.parseInt(editQtdDezenas.getText().toString());
            }

            if (numero > 21) {
                validaDezenas.setText(getString(R.string.qtd_dezenas_maior_20));
                validaDezenas.setVisibility(View.VISIBLE);

            }else if(numero >=0 && numero < 5){
                validaDezenas.setText(getString(R.string.qtd_dezenas_menor_5));
                validaDezenas.setVisibility(View.VISIBLE);

            }else{
                validaDezenas.setVisibility(View.GONE);
            }
        }

    }

    private void validaFiltroQtdCotasMin(){

        if(editQtdCotasMin.getText() != null || !editQtdCotasMin.getText().toString().equals("")) {
            int qtdMin, qtdMax;
            if (editQtdCotasMin.getText() == null || editQtdCotasMin.getText().toString().equals("")) {
                validaQtdCotaMin.setVisibility(View.GONE);
                qtdMin = -1;
            } else {
                qtdMin = Integer.parseInt(editQtdCotasMin.getText().toString());
            }

            if (editQtdCotasMax.getText() == null || editQtdCotasMax.getText().toString().equals("")) {
                validaQtdCotaMin.setVisibility(View.GONE);
                qtdMax = -1;
            } else {
                qtdMax = Integer.parseInt(editQtdCotasMax.getText().toString());
            }

            if (qtdMin >=0 && qtdMin < 2) {
                validaQtdCotaMin.setText(getString(R.string.qtd_min_menor_2));
                validaQtdCotaMin.setVisibility(View.VISIBLE);

            }else if((qtdMin > qtdMax && qtdMax >=0) || qtdMin>100){
                validaQtdCotaMin.setText(getString(R.string.qtd_min_maior_max));
                validaQtdCotaMin.setVisibility(View.VISIBLE);

            }else{
                validaQtdCotaMin.setVisibility(View.GONE);
            }
        }

    }
    private void validaFiltroQtdCotasMax(){
        if(editQtdCotasMax.getText() != null || !editQtdCotasMax.getText().toString().equals("")) {
            int qtdMax;
            if (editQtdCotasMax.getText() == null || editQtdCotasMax.getText().toString().equals("")) {
                validaQtdCotaMax.setVisibility(View.GONE);
                qtdMax = -1;
            } else {
                qtdMax = Integer.parseInt(editQtdCotasMax.getText().toString());
            }
            if (qtdMax >100) {
                validaQtdCotaMax.setText(getString(R.string.qtd_max_maior_100));
                validaQtdCotaMax.setVisibility(View.VISIBLE);

            } else if(qtdMax >=0 && qtdMax <2){
                validaQtdCotaMax.setText(getString(R.string.qtd_max_menor_2));
                validaQtdCotaMax.setVisibility(View.VISIBLE);

            }else{
                validaQtdCotaMax.setVisibility(View.GONE);
            }
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

        if(editQtdCotasMin.getText() == null || editQtdCotasMin.getText().toString().equals("")){
            qtdCotasMin = "0";
        }else{
            qtdCotasMin = editQtdCotasMin.getText().toString();
        }
        if(editQtdCotasMax.getText() == null || editQtdCotasMax.getText().toString().equals("")){
            qtdCotasMax = "0";
        }else{
            qtdCotasMax = editQtdCotasMax.getText().toString();
        }
        if(editQtdDezenas.getText() == null || editQtdDezenas.getText().toString().equals("")){
            qtdDezenas = "0";
        }else{
            qtdDezenas = editQtdDezenas.getText().toString();
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
        if (listaModalidadesAdapter != null) {
            listaModalidadesAdapter.limpaItemClicked();
        }
        if(queroAdapter != null){
            dezenasSelecionadasQuero = new ArrayList<>();
            queroAdapter.atualizaSelecionados(dezenasSelecionadasQuero);
        }
        if(naoQueroAdapter != null){
            dezenasSelecionadasNaoQuero = new ArrayList<>();
            naoQueroAdapter.atualizaSelecionados(dezenasSelecionadasNaoQuero);
        }
        mostraNumerosQueroNaoQuero();
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

        ModalidadeDisponivelCota modalidadeDisponivelTodas = new ModalidadeDisponivelCota();
        ModalidadeEnum modalidadeTodas = ModalidadeEnum.BOLAO;
        modalidadeDisponivelTodas.setModalidade(modalidadeTodas);

        listModalidadeDisponivel.add(0, modalidadeDisponivelTodas);
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
