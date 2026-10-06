package br.gov.caixa.loterias.apostas.controllers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.PixUtils;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ConfirmacaoPagamentoActivity extends SettingToolbarActivity {

    public static final String ARG_ORIGEM = "ARG_ORIGEM";
    public static final String ARG_FLUXO = "ARG_FLUXO";

    private Long origem;
    private Long fluxo;
    private Long idAposta;
    private TextView titulo, info1, info2,info3,info4;
    private TextView infoApostas;
    private Button btnVoltarInicio, btnVoltarInicioConfirmacao;
    private ImageButton btnTermoConfirmacao;
    private Button btnAcompanheCompras, btnAcompanheComprasConfirmacao;
    private ConstraintLayout layoutResgatePix, layoutConfirmacaoPagamento, rootLayout;
    protected DetalhesPremioDTO detalhesPremioDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pagamento);
        setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.title_activity_confirmacao_pagamento)));

        configViews();

        getExtras();
        configLayout();
    }

    private void configViews() {

        rootLayout = findViewById(R.id.confirmacaoPagamentoMainLayout);

        //Layout resgate por PIX (não alterado, mantém comportamento anterior)
        titulo = findViewById(R.id.textTitulo);
        infoApostas = findViewById(R.id.textInfoApostas);
        btnVoltarInicio = findViewById(R.id.botaoVoltarInicio);
        btnAcompanheCompras = findViewById(R.id.botaoAcompanheSuasCompras);
        layoutResgatePix = findViewById(R.id.layout_ResgatePix);

        //Layout confirmacao pagamento
        layoutConfirmacaoPagamento = findViewById(R.id.layout_confirmacao_pagamento);
        btnTermoConfirmacao = findViewById(R.id.botao_termo_confirmacao);
        btnVoltarInicioConfirmacao = findViewById(R.id.btn_voltar_inicio_confirmacao);
        btnAcompanheComprasConfirmacao = findViewById(R.id.btn_acompanhe_compras_confirmacao);
        info1 = findViewById(R.id.textInfo1_confirmacao);
        info2 = findViewById(R.id.textInfo2_confirmacao);
        info3 = findViewById(R.id.textInfo3_confirmacao);
        info4 = findViewById(R.id.textInfo4_confirmacao);
    }

    private void configLayout() {
        if (isPixResgate()){
            layoutResgatePix.setVisibility(VISIBLE);
            layoutConfirmacaoPagamento.setVisibility(GONE);
            titulo.setText(R.string.resgate_pix_concluido);
            infoApostas.setText(R.string.premio_pix_ja_creditado);
            btnVoltarInicio.setBackgroundColor(getResources().getColor(R.color.branco));
            btnVoltarInicio.setTextColor(getResources().getColor(R.color.verde_acompanha_compras));
            btnAcompanheCompras.setBackgroundColor(getResources().getColor(R.color.verde_acompanha_compras));

            ViewUtils.configuraStatusBarGradientLayout(this, getWindow());
            rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.navigation_gradient));
            configuraBotoesResgatePix();

        } else {
            layoutResgatePix.setVisibility(GONE);
            layoutConfirmacaoPagamento.setVisibility(VISIBLE);

            info1.setText(ViewUtils.textCaixaSTDSemiBold(this, getString(R.string.confirmacaoPagamentoProcessamentoInfo1)));
            info2.setText(ViewUtils.textCaixaSTDSemiBold(this, getString(R.string.confirmacaoPagamentoProcessamentoInfo2)));
            info3.setText(ViewUtils.textCaixaSTDSemiBold(this, getString(R.string.confirmacaoPagamentoProcessamentoInfo3)));
            info4.setText(ViewUtils.textCaixaSTDSemiBold(this, getString(R.string.confirmacaoPagamentoProcessamentoInfo4)));
            configuraBotoesConfirmacaoPagamento();
        }
    }

    private void getExtras() {
        if (getIntent() != null && getIntent().getExtras() != null)  {
            origem = getIntent().getExtras().getLong(ARG_ORIGEM);
            fluxo = getIntent().getExtras().getLong(ARG_FLUXO);
            idAposta = getIntent().getExtras().getLong( getResources().getString(R.string.id_aposta));
        }
    }

    private void configuraBotoesConfirmacaoPagamento(){

        btnAcompanheComprasConfirmacao.setOnClickListener(view ->
                startActivity(new Intent(ConfirmacaoPagamentoActivity.this, ListaComprasActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_SINGLE_TOP)));

        btnVoltarInicioConfirmacao.setOnClickListener(view -> {
            Intent intent = IntentUtil.getIntentOrigemDestino(ConfirmacaoPagamentoActivity.this, PrincipalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        btnTermoConfirmacao.setOnClickListener(v -> {
            Intent activity = new Intent(this, TermosUsoActivity.class);
            startActivity(activity);
        });

    }
    private void configuraBotoesResgatePix(){

        btnVoltarInicio.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.confirmacaoPagamentoBotaoVoltarInicio)));
        btnAcompanheCompras.setText(ViewUtils.textCaixaSTDBold(this, getString(R.string.visualizar_comprovante)));

        btnVoltarInicio.setOnClickListener(view -> {
            //PrincipalActivity_.intent(ConfirmacaoPagamentoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
            Intent intent = IntentUtil.getIntentOrigemDestino(ConfirmacaoPagamentoActivity.this, PrincipalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        btnAcompanheCompras.setOnClickListener(view -> {
            if (detalhesPremioDTO != null){
                PdfUtils.createComprovantePremioPdf(detalhesPremioDTO, ConfirmacaoPagamentoActivity.this);
            }  else {
                atualizaComprovante();
            }
        });

        findViewById(R.id.botao_termo).setOnClickListener(v -> {
            Intent activity = new Intent(this, TermosUsoActivity.class);
            startActivity(activity);
        });
    }

    private boolean isPixResgate() {
        return origem != null && origem.equals(MeioPagamentoUtils.PIX) && fluxo.equals(PixUtils.FLUXO_RESGATE);
    }

    private void atualizaComprovante(){
        AlertDialogUtils.show(this);
        ApostaSilceBO.getInstance().getApostaComprovantePremio(idAposta, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                AlertDialogUtils.dismiss();
                detalhesPremioDTO = result.getPayload();
                PdfUtils.createComprovantePremioPdf(detalhesPremioDTO, ConfirmacaoPagamentoActivity.this);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, ConfirmacaoPagamentoActivity.this);
            }
        });
    }
}
