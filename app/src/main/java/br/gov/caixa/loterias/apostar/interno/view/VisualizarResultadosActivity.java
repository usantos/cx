package br.gov.caixa.loterias.apostar.interno.view;


import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PremiacaoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.NumerosResultadosAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;
import static br.gov.caixa.loterias.apostar.interno.SelecaoModalidadesResultados.MODALIDADE;

public class VisualizarResultadosActivity extends AppCompatActivity {

    private static final String CONCURSO = "CONCURSO";
    private String modalidade;
    private Integer concursoAtual;
    private Integer concursoAlvo;
    private LinearLayout layoutPremiados;
    private RecyclerView rvNumerosResultado;
    private TextView tituloModalidade;
    private TextView dataSorteio;
    private TextView txtPremiados;
    private TextView tvNumeroSorteio;
    private Toolbar toolbar;
    private View divisoria;
    private ScrollView scrollResultado;
    private ConstraintLayout layoutHeader;
    private ConstraintLayout layoutTrevo;
    private ConstraintLayout layoutPrimeiraParte;
    private ConstraintLayout layoutListaPremiacao;
    private Button btnVoltar;
    private Button btnConcursoAnterior;
    private Button btnProximoConcurso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_resultados);
        getExtras();
        setViews();
        setMethods();
    }


    private void setViews() {
        toolbar = findViewById(R.id.toolbar);
        rvNumerosResultado = findViewById(R.id.rv_numeros_resultado);
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
        txtPremiados = findViewById(R.id.tv_premiados);
        tvNumeroSorteio = findViewById(R.id.tv_numero_sorteio);

        txtPremiados.setText(getString(R.string.label_premiados));
        tvNumeroSorteio.setText(getString(R.string.resultados_numeros_sorteados));

    }
    private void setMethods(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (modalidade != null) {
            concursoAtual = ViewUtils.getConcursoAtualPorModalidade(modalidade);
            if(concursoAlvo == null){
                concursoAlvo = concursoAtual - 1;
            }
            getResutadoModalidade(ApostaDTO.lowerCaseFromString(ModalidadeEnum.toString(modalidade)), concursoAlvo);
            setTitlePorConcurso(concursoAlvo);
            tituloModalidade.setText(modalidade);
        }
        setButtons();
    }

    private void setButtons(){
        String anterior = String.valueOf(concursoAlvo-1);
        String proximo = String.valueOf(concursoAlvo +1);
        btnProximoConcurso.setText(ViewUtils.textFuturaAndFuturaBold(this,getString(R.string.resultados_botao,proximo)));
        btnConcursoAnterior.setText(ViewUtils.textFuturaAndFuturaBold(this,getString(R.string.resultados_botao,anterior)));
        btnVoltar.setText(ViewUtils.textFuturaAndFuturaBold(this,getString(R.string.label_voltar_bold)));
        btnProximoConcurso.setOnClickListener(view -> reestartActivity(proximo));
        btnConcursoAnterior.setOnClickListener(view -> reestartActivity(anterior));
        btnVoltar.setOnClickListener(view -> finish());
    }

    private void reestartActivity(String concurso){
        Intent it = new Intent(VisualizarResultadosActivity.this,VisualizarResultadosActivity.class);
        it.putExtra(CONCURSO,concurso);
        it.putExtra(MODALIDADE,modalidade);
        startActivity(it);
        this.overridePendingTransition(0, 0);
        finish();
    }

    private void configuraTema(ResultadoConcursoDTO response) {
        switch (ModalidadeEnum.toString(modalidade)) {
            case MEGA_SENA:
                setBackground(R.color.megasenaclaro, R.color.megasenaescuro, R.drawable.background_trapeze_megasena);
                configuraMega(response);
                break;
            case DUPLA_SENA:
                setBackground(R.color.duplasenaclaro, R.color.duplasenaescuro, R.drawable.background_trapeze_duplasena);
                break;
            case TIMEMANIA:
                setBackground(R.color.timemaniaclaro, R.color.timemaniamediaescuro, R.drawable.background_trapeze_timemania);
                break;
            case LOTECA:
                setBackground(R.color.lotecaclaro, R.color.lotecaescuro, R.drawable.background_trapeze_loteca);
                break;
            case LOTOGOL:
                setBackground(R.color.lotogolclaro, R.color.lotogolescuro, R.drawable.background_trapeze_lotogol);
                break;
            case DIA_DE_SORTE:
                setBackground(R.color.diasorteclaro, R.color.diasorteescuro, R.drawable.background_trapeze_dia_sorte);
                break;
            case LOTOFACIL:
                setBackground(R.color.lotofacilclaro, R.color.lotofacilescuro, R.drawable.background_trapeze_lotofacil);
                break;
            case LOTOMANIA:
                setBackground(R.color.lotomaniaclaro, R.color.lotomaniaescuro, R.drawable.background_trapeze_lotomania);
                configuraMega(response);
                break;
            case QUINA:
                setBackground(R.color.quinaclaro, R.color.quinaescuro, R.drawable.background_trapeze_quina);
                break;
            default:
                break;
        }
    }

    private void configuraMega(ResultadoConcursoDTO response) {
        NumerosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;
        if (response.getNumerosSorteadosPrimeiroSorteio().size() > 6) {
            numberOfColumns = 4;
        }

        this.rvNumerosResultado.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(this, response.getNumerosSorteadosPrimeiroSorteio(), ModalidadeEnum.toString(modalidade));
        this.rvNumerosResultado.setAdapter(numerosResultadosAdapter);
        dataSorteio.setText(getString(R.string.resultados_data_sorteio,response.getConcurso().getDataSorteio()));

        adicionarPremiados(response.getPremiacoesPrimeiroSorteio());
    }

    private void setBackground(int corClara, int corEscura, int resourceTrapezio) {
        scrollResultado.setBackgroundResource(corEscura);
        layoutHeader.setBackgroundResource(corClara);
        divisoria.setBackgroundResource(corClara);
        layoutListaPremiacao.setBackgroundResource(corClara);
        layoutTrevo.setBackgroundResource(resourceTrapezio);
    }

    private void getResutadoModalidade(String modalidade, int concurso) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(this);
        apostaBO.getResultadoModalidadeConcurso(modalidade, concurso, new RequestListener<ResultadoConcursoDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursoDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), VisualizarResultadosActivity.this);
                }
                configuraTema(response.getPayload());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, VisualizarResultadosActivity.this);
                ViewUtils.alertTitleButton(VisualizarResultadosActivity.this, R.string.label_atencao, MensagensNetwork.setMensagem(error, VisualizarResultadosActivity.this), "Ok", dialogInterface ->btnConcursoAnterior.performClick());
            }
        });
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

    private void setTitlePorConcurso(int concurso) {
        setTitle(getString(R.string.resultados_titulo, String.valueOf(concurso)));
    }


    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            modalidade = bundle.getString(MODALIDADE);
            if(bundle.getString(CONCURSO) != null){
                concursoAlvo = Integer.valueOf(bundle.getString(CONCURSO));
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

