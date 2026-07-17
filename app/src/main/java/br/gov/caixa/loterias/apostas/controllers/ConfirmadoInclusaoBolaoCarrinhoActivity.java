package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SubBarraModalidadeFragment;

public class ConfirmadoInclusaoBolaoCarrinhoActivity extends LoteriasAppMarketPlaceActivity {
    private SubBarraModalidadeFragment subBarFragment;
    private RodapeSimulacaoApostaFragment rodapeFragment;
    private ModalidadeEnum modalidade;
    private BigDecimal valorAposta;
    private String concurso;
    private String dataSorteio;
    private String dataHoraExpiracao;
    //private AppCompatImageView tagBolao;

    private LinearLayout btnVoltarInicio;
    private TextView tvVoltarInicio, tvContador, tvDescricaoInclusao, tvConclusaoPagamento;
    private ConstraintLayout background, timer, arrendondamento;
    private EstiloModalidadeMKP estilo;
    private Boolean isEspecial;
    private ImageView imageSetaApontandoCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmado_inclusao_bolao_carrinho);

        getExtras();
        estilo = new EstiloModalidadeMKP(modalidade);
        setViews();
        setMetodos();
        startFragments();
        aplicaEstilo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(modalidade == null || valorAposta == null || concurso == null){
            return;
        }

        AnalyticsHelper.getInstance().logViewScreenApostaValor(
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.BOLAO,
                AnalyticsHelper.Tela.BOLAO_CART,
                ModalidadeEnum.fromString(modalidade),
                ViewUtils.getMoedaFormat(valorAposta),
                concurso
        );

        AnalyticsHelper.getInstance().logOperationSuccess(
                AnalyticsHelper.StatusParams.SUCESSSO,
                AnalyticsHelper.Tela.BOLAO_CART,
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.BOLAO,
                ModalidadeEnum.fromString(modalidade),
                concurso,
                ViewUtils.getMoedaFormat(valorAposta),
                true,
                AnalyticsHelper.AnalyticsFields.BOLAO
        );

    }

    private void aplicaEstilo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, estilo.getCorEscura()));
        }

        background.setBackgroundColor(ContextCompat.getColor(this, estilo.getCorClara()));
        if (modalidade.name().toUpperCase().equals("DIA_DE_SORTE") ||
            modalidade.name().toUpperCase().equals("TIMEMANIA")) {
            timer.setBackground(VectorUtils.getShape(R.drawable.button_white_with_rouded_black_border, estilo.getCorFonteFundoClaro()));
            arrendondamento.setBackground(VectorUtils.getShape(R.drawable.button_white_with_rouded_black_border, estilo.getCorFonteFundoClaro()));
            imageSetaApontandoCarrinho.setImageDrawable(VectorUtils.getShape(R.drawable.rectangle_5_copy, estilo.getCorFonteFundoClaro()));
        } else {
            timer.setBackground(VectorUtils.getShape(R.drawable.button_white_with_rouded_black_border, estilo.getCorEscura()));
            arrendondamento.setBackground(VectorUtils.getShape(R.drawable.button_white_with_rouded_black_border, estilo.getCorEscura()));
        }
        aplicaEstiloFonte();
    }

    private void aplicaEstiloFonte() {
        //tagBolao.setImageResource(estilo.getImagemTrianguloOutline());
        tvDescricaoInclusao.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
        tvConclusaoPagamento.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));
    }

    private void startFragments() {
        subBarFragment = FragmentUtils.startFragmentSubBarraModalidade(getSupportFragmentManager(),
                R.id.id_fragment_sub_barra, modalidade, concurso, dataSorteio, isEspecial);
        rodapeFragment = FragmentUtils.startRodapeSimulacaoAPosta(getSupportFragmentManager(),
                R.id.id_fragment_rodape, valorAposta);
    }

    private void getExtras(){
        modalidade = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
        valorAposta = (BigDecimal) getIntent().getSerializableExtra(getResources().getString(R.string.valorAposta));
        concurso = getIntent().getStringExtra(getResources().getString(R.string.extra_numero_concurso_atual));
        dataSorteio = getIntent().getStringExtra(getResources().getString(R.string.extra_data_sorteio_atual));
        dataHoraExpiracao = getIntent().getStringExtra(getResources().getString(R.string.extra_data_hora_expiracao_reserva));
        isEspecial = getIntent().getBooleanExtra(getResources().getString(R.string.extra_especial_reserva), false);
    }

    private void setViews(){
        //tagBolao = findViewById(R.id.tag_bolao_carrinho);
        btnVoltarInicio = findViewById(R.id.botaoLinearLayoutVoltarPrincipal);
        tvVoltarInicio = findViewById(R.id.txtVoltarAoInicio);
        tvContador = findViewById(R.id.txt_contador);
        background = findViewById(R.id.layout_corpo);
        timer = findViewById(R.id.timer);
        arrendondamento = findViewById(R.id.layout_arrendondamento);
        tvDescricaoInclusao = findViewById(R.id.textTituloTela);
        tvConclusaoPagamento = findViewById(R.id.textObservacaoConfirmacao);
        imageSetaApontandoCarrinho = findViewById(R.id.imageSetaApontandoCarrinho);

        tvVoltarInicio.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.btn_title_voltarAoInicio)));
    }

    private void setMetodos() {
        btnVoltarInicio.setOnClickListener(v -> {
            //PrincipalActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
            Intent intent = IntentUtil.getIntentOrigemDestino(this, PrincipalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("ORIGEM_TELA", "CARRINHO_BOLAO");
            startActivity(intent);
        });

        startTimer(dataHoraExpiracao);
    }

    private void startTimer(String dataHoraExpiracaoReserva) {
        long tempo_inicial_milesec = DateUtils.diffMillisSecondsTimerZone(dataHoraExpiracaoReserva);

        ContagemRegressiva contDown = new ContagemRegressiva(tvContador, tempo_inicial_milesec, 1000);
        contDown.start();
        //TimerSingleton.getInstance().setContagemRegressiva(contDown);
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_OK);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
