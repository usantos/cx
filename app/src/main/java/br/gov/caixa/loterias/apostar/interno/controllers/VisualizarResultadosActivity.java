package br.gov.caixa.loterias.apostar.interno.controllers;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.Modalidade;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PremiacaoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostar.interno.view.custom.PartidaViewDetalhesResultado;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ResultadoDuplaSenaFragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ResultadoGenericoFragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ResultadoLotecaFragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ResultadoLotogolFragment;
import br.gov.caixa.loterias.apostar.interno.view.fragment.ResultadoTimemaniaDiaDeSorteFragment;
import static br.gov.caixa.loterias.apostar.interno.controllers.SelecaoModalidadesResultados.MODALIDADE;

public class VisualizarResultadosActivity extends AppCompatActivity {

    private static final String CONCURSO_ALVO = "CONCURSO_ALVO";
    private static final String CONCURSO_ATUAL = "CONCURSO_ATUAL";
    private static final String COR_CLARA = "COR_CLARA";
    private static final String COR_ESCURA = "COR_ESCURA";
    private static final String TRAPEZIO = "TRAPEZIO";
    private static final String ANIMACAO = "ANIMACAO";
    private static final int ANIM_DIR = 1;
    private static final int ANIM_ESQ = 2;
    private static final int ANIM_NULL = 3;

    private String txtModalidade;
    private Integer concursoAtual;
    private Integer concursoAlvo;
    private Integer corClara;
    private Integer corEscura;
    private Integer resourceTrapezio;
    private int direcaoAnimacao;

    private Toolbar toolbar;

    private View divisoria;
    private View containerFragment;

    private TextView tituloModalidade;
    private TextView dataSorteio;
    //private TextView txtPremiados;
    private TextView tvNumeroSorteio;

    private RecyclerView rvNumerosResultado;
    private ScrollView scrollResultado;

    private LinearLayout layoutPremiados;
    private ConstraintLayout layoutHeader;
    private ConstraintLayout layoutTrevo;
    private ConstraintLayout layoutPrimeiraParte;
    private ConstraintLayout layoutListaPremiacao;

    private Button btnVoltar;
    private Button btnConcursoAnterior;
    private Button btnProximoConcurso;
    private Button btnVoltarInterno;

    private PartidaViewDetalhesResultado partidaViewDetalhesResultado;
    private Modalidade modalidade;

    private ImageButton ibPesquisar;
    private EditText etPesquisar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_resultados);
        pegaExtras();
        setaViews();
        setaMetodos();
    }


    private void setaViews() {
        toolbar = findViewById(R.id.toolbar);
        tituloModalidade = findViewById(R.id.tv_titulo_modalidade);
        layoutHeader = findViewById(R.id.cl_top_modalidades);
        layoutTrevo = findViewById(R.id.cl_trevo);
        layoutPrimeiraParte = findViewById(R.id.cl_cabecalho_modalidades);
        layoutListaPremiacao = findViewById(R.id.cl_lista_premiacao);
        divisoria = findViewById(R.id.v_divisoria);
        scrollResultado = findViewById(R.id.sv_resultado);
        dataSorteio = findViewById(R.id.tv_data_sorteio);
        layoutPremiados = findViewById(R.id.ll_premiados);
        btnVoltar = findViewById(R.id.btn_voltar);
        btnConcursoAnterior = findViewById(R.id.btn_concurso_anterior);
        btnProximoConcurso = findViewById(R.id.btn_proximo_concurso);
        //txtPremiados = findViewById(R.id.tv_premiados);
        tvNumeroSorteio = findViewById(R.id.tv_numero_sorteio);
        partidaViewDetalhesResultado = findViewById(R.id.pvdr_resultado_partida);
        btnVoltarInterno = findViewById(R.id.voltarButton);
        containerFragment = findViewById(R.id.v_container_fragment);
        ibPesquisar = findViewById(R.id.ib_pesquisar);
        etPesquisar = findViewById(R.id.et_pesquisar);

        //txtPremiados.setText(getString(R.string.label_premiados));
        tvNumeroSorteio.setText(getString(R.string.resultados_numeros_sorteados));
        btnVoltarInterno.setVisibility(View.GONE);
        ibPesquisar.setVisibility(View.VISIBLE);
        if(corEscura!= null && corClara !=null && resourceTrapezio!= null){
            setBackground(corClara,corEscura,resourceTrapezio);
        }
        setTitle("");

    }
    private void setaMetodos(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (txtModalidade != null) {
            if(concursoAtual == null){
                getResultadoModalidade(ApostaDTO.lowerCaseFromString(ModalidadeEnum.toString(txtModalidade)),"");
            }else {
                getResultadoModalidade(ApostaDTO.lowerCaseFromString(ModalidadeEnum.toString(txtModalidade)),concursoAlvo.toString());
            }
        }
        configBtnsPesq();
    }

    private void setButtons(){
        if(concursoAlvo == null){
            return;
        }
        String anterior = String.valueOf(concursoAlvo-1);
        String proximo = String.valueOf(concursoAlvo +1);
        btnProximoConcurso.setText(ViewUtils.textFuturaAndFuturaBold(this,getString(R.string.resultados_botao,proximo)));
        btnConcursoAnterior.setText(ViewUtils.textFuturaAndFuturaBold(this,getString(R.string.resultados_botao,anterior)));
        btnVoltar.setText(ViewUtils.textFuturaAndFuturaBold(this,getString(R.string.label_voltar_bold)));
        if(concursoAlvo >= concursoAtual){
            btnProximoConcurso.setEnabled(false);
            btnProximoConcurso.setAlpha(.5f);
        }else {
            btnProximoConcurso.setOnClickListener(view ->
            {
                btnProximoConcurso.setEnabled(false);
                btnConcursoAnterior.setEnabled(false);
                reestartActivity(proximo,ANIM_DIR);
            });
        }

        btnConcursoAnterior.setOnClickListener(view -> {
            btnProximoConcurso.setEnabled(false);
            btnConcursoAnterior.setEnabled(false);
            reestartActivity(anterior,ANIM_ESQ);}
            );
        btnVoltar.setOnClickListener(view -> finish());
        configBtnsPesq();
    }

    private void configBtnsPesq(){
        ibPesquisar.setOnClickListener(view -> {
            if (etPesquisar.getVisibility() == View.GONE){
                etPesquisar.setText("");
                etPesquisar.setVisibility(View.VISIBLE);
                etPesquisar.requestFocus();
                ibPesquisar.setImageDrawable(getDrawable(R.drawable.ic_x));
                setTitle("");

            }else {
                ibPesquisar.setImageDrawable(getDrawable(R.drawable.ic_lupa));
                if (concursoAlvo != null) {
                    setTitlePorConcurso(concursoAlvo);
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                etPesquisar.setVisibility(View.GONE);

            }
        });

        etPesquisar.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        configuraPesquisa();
                    }
                    return true;
                });
        etPesquisar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput((etPesquisar), InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void configuraPesquisa(){
        if(etPesquisar.getText().length() > 0 && concursoAtual != null){
            if(Integer.valueOf( etPesquisar.getText().toString()) <= concursoAtual){
                etPesquisar.setVisibility(View.GONE);
                reestartActivity(etPesquisar.getText().toString(),ANIM_NULL);
            }else {
                ViewUtils.alert(this,getString(R.string.resultados_validacao_concurso,String.valueOf(concursoAtual)));
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    private void configuraModalidade(ResultadoConcursoDTO response) {

        divisoria.setVisibility(View.VISIBLE);
        tvNumeroSorteio.setVisibility(View.VISIBLE);
        layoutListaPremiacao.setVisibility(View.VISIBLE);
        dataSorteio.setText(ViewUtils.textFuturaAndFuturaBold(this,getString(R.string.resultados_data_sorteio, ("_"+response.getConcurso().getDataSorteio()+"_"))));

        switch (ModalidadeEnum.toString(txtModalidade)) {
            case MEGA_SENA:
                setBackground(R.color.megasenaclaro, R.color.megasenaescuro, R.drawable.background_trapeze_megasena);
                configuraResultadoSuperior(response,ResultadoGenericoFragment.newInstance(response));
                break;
            case LOTOFACIL:
                setBackground(R.color.lotofacilclaro, R.color.lotofacilescuro, R.drawable.background_trapeze_lotofacil);
                configuraResultadoSuperior(response,ResultadoGenericoFragment.newInstance(response));
                break;
            case LOTOMANIA:
                setBackground(R.color.lotomaniaclaro, R.color.lotomaniaescuro, R.drawable.background_trapeze_lotomania);
                configuraResultadoSuperior(response,ResultadoGenericoFragment.newInstance(response));
                break;
            case QUINA:
                setBackground(R.color.quinaclaro, R.color.quinaescuro, R.drawable.background_trapeze_quina);
                configuraResultadoSuperior(response,ResultadoGenericoFragment.newInstance(response));
                break;
            case DUPLA_SENA:
                setBackground(R.color.duplasenaclaro, R.color.duplasenaescuro, R.drawable.background_trapeze_duplasena);
                configuraResultadoSuperior(response,ResultadoDuplaSenaFragment.newInstance(response));
                //txtPremiados.setVisibility(View.GONE);
                break;
            case TIMEMANIA:
                setBackground(R.color.timemaniaclaro, R.color.timemaniamediaescuro, R.drawable.background_trapeze_timemania);
                configuraResultadoSuperior(response, ResultadoTimemaniaDiaDeSorteFragment.newInstance(response));
                break;
            case DIA_DE_SORTE:
                setBackground(R.color.diasorteclaro, R.color.diasorteescuro, R.drawable.background_trapeze_dia_sorte);
                configuraResultadoSuperior(response, ResultadoTimemaniaDiaDeSorteFragment.newInstance(response));
                break;
            case LOTECA:
                setBackground(R.color.lotecaclaro, R.color.lotecaescuro, R.drawable.background_trapeze_loteca);
                configuraResultadoSuperior(response, ResultadoLotecaFragment.newInstance(response));
                tvNumeroSorteio.setVisibility(View.GONE);
                break;
            case LOTOGOL:
                setBackground(R.color.lotogolclaro, R.color.lotogolescuro, R.drawable.background_trapeze_lotogol);
                configuraResultadoSuperior(response, ResultadoLotogolFragment.newInstance(response));
                tvNumeroSorteio.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void configuraResultadoSuperior(ResultadoConcursoDTO response, Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.v_container_fragment,f);
        fragmentTransaction.commit();
    }

    private void setBackground(int corClara, int corEscura, int resourceTrapezio) {
        this.corClara = corClara;
        this.corEscura = corEscura;
        this.resourceTrapezio = resourceTrapezio;
        scrollResultado.setBackgroundResource(corEscura);
        layoutHeader.setBackgroundResource(corClara);
        divisoria.setBackgroundResource(corClara);
        layoutListaPremiacao.setBackgroundResource(corClara);
        layoutTrevo.setBackgroundResource(resourceTrapezio);
    }

    private void getResultadoModalidade(String txtmodalidade, String concurso) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(this);
        apostaBO.getResultadoModalidade(txtmodalidade,String.valueOf(concurso), new RequestListener<ResultadoConcursoDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursoDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), VisualizarResultadosActivity.this);
                }
                configuraRetornoRequisicao(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                setButtons();
                ViewUtils.alertTitleButton(VisualizarResultadosActivity.this, R.string.label_atencao, MensagensNetwork.setMensagem(error, VisualizarResultadosActivity.this), "Ok", dialogInterface ->{});
            }
        });
    }

    private void configuraRetornoRequisicao(ResultadoConcursoDTOResponse response){

        if(concursoAtual == null){
            concursoAtual = response.getPayload().getConcurso().getNumero();
        } else {
            configAnimacao(direcaoAnimacao);
        }
        if(concursoAlvo == null){
            concursoAlvo = concursoAtual;
        }
        configuraModalidade(response.getPayload());
        configuraGeral(response);
    }

    private void configuraGeral(ResultadoConcursoDTOResponse response){
        ConcursoDTO concurso = response.getPayload().getConcurso();
        modalidade = new Modalidade (concurso.getModalidade().name(),
                concurso.getValorApostaMinima(),
                concurso.getModalidade().toString(),
                concurso.getModalidadeDetalhada().toString(),
                concurso.getModalidade(),
                concurso,
                concurso.getAberto(),
                response.getPayload());


       /* if(modalidade.getDescricao().equals(ModalidadeEnum.DUPLA_SENA.name())){
            adicionarPremiados(response.getPayload().getPremiacoesPrimeiroSorteio(),response.getPayload().getPremiacoesSegundoSorteio());
        }else {
            adicionarPremiados(response.getPayload().getPremiacoesPrimeiroSorteio());
        }*/
        partidaViewDetalhesResultado.setLayout(modalidade);
        if(response.getPayload().getConcurso().getTipoConcurso().toString().equalsIgnoreCase("ESPECIAL")){
            tituloModalidade.setText(response.getPayload().getConcurso().getModalidadeDetalhada().getDescricaoEspecial().toLowerCase());
        }else {
            tituloModalidade.setText(txtModalidade);
        }
        setButtons();
        setTitlePorConcurso(concursoAlvo);
    }

    private void configAnimacao(int ladoAnim){
        if(ladoAnim != ANIM_NULL){
            AnimatorSet animIn, animOut;

            if(ladoAnim == ANIM_ESQ){
                animOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.esq_out_anim);
                animIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.esq_in_anim);
            }else {
                animOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.dir_out_anim);
                animIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.dir_in_anim);
            }
            animOut.setTarget(scrollResultado);
            animIn.setTarget(scrollResultado);
            animIn.start();
            animOut.start();
        }
    }

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados){
        for (PremiacaoConcursoDTO premiacaoConcurso :  listaPremiados){
            TextView title1 = new TextView(VisualizarResultadosActivity.this);
            title1.setText(premiacaoConcurso.getDescricao());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(VisualizarResultadosActivity.this, R.style.fontForTitle1Premiados);
            this.layoutPremiados.addView(title1);

            TextView title2 = new TextView(VisualizarResultadosActivity.this);
            if (premiacaoConcurso.getQuantidadeGanhadores()>1){
                title2.setText(getResources().getString(R.string.label_numero_ganhadores, premiacaoConcurso.getQuantidadeGanhadores()));
            }else{
                title2.setText(getResources().getString(R.string.label_numero_ganhador, premiacaoConcurso.getQuantidadeGanhadores()));
            }

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(VisualizarResultadosActivity.this, R.style.fontForTitle2Premiados);
            this.layoutPremiados.addView(title2);
        }
    }

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados, List<PremiacaoConcursoDTO> listaPremiadosSegundoConcurso){
        TextView premiadosTextview1 = new TextView(VisualizarResultadosActivity.this);
        premiadosTextview1.setText(ViewUtils.textFuturaAndFuturaBold(VisualizarResultadosActivity.this, getResources().getString(R.string.label_premiados_primeiro_sorteio_bold)));
        LinearLayout.LayoutParams layoutParamsPremiadosTextview1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsPremiadosTextview1.setMargins(0, 35, 0, 0);

        premiadosTextview1.setLayoutParams(layoutParamsPremiadosTextview1);
        premiadosTextview1.setTextAppearance(VisualizarResultadosActivity.this, R.style.fontForTitleTopoPremiados);
        this.layoutPremiados.addView(premiadosTextview1);

        adicionarPremiados(listaPremiados);

        TextView premiadosTextview2= new TextView(VisualizarResultadosActivity.this);
        premiadosTextview2.setText(ViewUtils.textFuturaAndFuturaBold(VisualizarResultadosActivity.this, getResources().getString(R.string.label_premiados_segundo_sorteio_bold)));
        LinearLayout.LayoutParams layoutParamspremiadosTextview2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamspremiadosTextview2.setMargins(0, 20, 0, 0);
        premiadosTextview2.setLayoutParams(layoutParamspremiadosTextview2);
        premiadosTextview2.setTextAppearance(VisualizarResultadosActivity.this, R.style.fontForTitleTopoPremiados);
        this.layoutPremiados.addView(premiadosTextview2);

        if (listaPremiadosSegundoConcurso != null) {
            adicionarPremiados(listaPremiadosSegundoConcurso);
        }
    }

    private void setTitlePorConcurso(int concurso) {
        setTitle(getString(R.string.resultados_titulo, String.valueOf(concurso)));
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            txtModalidade = bundle.getString(MODALIDADE);
            if(bundle.getString(CONCURSO_ALVO) != null){
                concursoAlvo = Integer.valueOf(bundle.getString(CONCURSO_ALVO));
                concursoAtual = bundle.getInt(CONCURSO_ATUAL);
                corClara = bundle.getInt(COR_CLARA);
                corEscura =  bundle.getInt(COR_ESCURA);
                resourceTrapezio =  bundle.getInt(TRAPEZIO);
                direcaoAnimacao = bundle.getInt(ANIMACAO);
            }

        }
    }

    private void reestartActivity(String concurso, int direcaoAnimacao){
        Intent it = new Intent(VisualizarResultadosActivity.this,VisualizarResultadosActivity.class);
        it.putExtra(CONCURSO_ALVO,concurso);
        it.putExtra(MODALIDADE, txtModalidade);
        it.putExtra(COR_CLARA,corClara);
        it.putExtra(COR_ESCURA,corEscura);
        it.putExtra(TRAPEZIO,resourceTrapezio);
        it.putExtra(CONCURSO_ATUAL,concursoAtual);
        it.putExtra(ANIMACAO,direcaoAnimacao);
        startActivity(it);
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

