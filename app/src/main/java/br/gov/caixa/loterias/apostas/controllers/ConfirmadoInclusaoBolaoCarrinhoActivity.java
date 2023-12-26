package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
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

    private LinearLayout btnVoltarInicio;
    private TextView tvVoltarInicio;
    private TextView tvContador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmado_inclusao_bolao_carrinho);

        configToolbar(R.id.toolbar);
        configBackgroundColorMenu(R.color.mega_verde_escuro_mkp);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_apostas bolão_"));

        getExtras();
        setViews();
        setMetodos();
        startFragments();
    }

    private void startFragments() {
        subBarFragment = FragmentUtils.startFragmentSubBarraModalidade(getSupportFragmentManager(),
                R.id.id_fragment_sub_barra, modalidade, concurso, dataSorteio);
        rodapeFragment = FragmentUtils.startRodapeSimulacaoAPosta(getSupportFragmentManager(),
                R.id.id_fragment_rodape, valorAposta);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getExtras(){
        modalidade = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
        valorAposta = (BigDecimal) getIntent().getSerializableExtra(getResources().getString(R.string.valorAposta));
        concurso = getIntent().getStringExtra(getResources().getString(R.string.extra_numero_concurso_atual));
        dataSorteio = getIntent().getStringExtra(getResources().getString(R.string.extra_data_sorteio_atual));
        dataHoraExpiracao = getIntent().getStringExtra(getResources().getString(R.string.extra_data_hora_expiracao_reserva));
    }

    private void setViews(){
        btnVoltarInicio = findViewById(R.id.botaoLinearLayoutVoltarPrincipal);
        tvVoltarInicio = findViewById(R.id.txtVoltarAoInicio);
        tvContador = findViewById(R.id.txt_contador);

        tvVoltarInicio.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.btn_title_voltarAoInicio)));
    }

    private void setMetodos() {
        btnVoltarInicio.setOnClickListener(v ->
                PrincipalActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start());

        startTimer(dataHoraExpiracao);
    }

    private void startTimer(String dataHoraExpiracaoReserva) {
        long tempo_inicial_milesec = DateUtils.diffMillisSecondsTimerZone(dataHoraExpiracaoReserva);

        ContagemRegressiva contDown = new ContagemRegressiva(tvContador, tempo_inicial_milesec, 1000);
        contDown.start();
        //TimerSingleton.getInstance().setContagemRegressiva(contDown);
    }

}
