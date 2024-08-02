package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
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
    private TextView titulo;
    private TextView infoApostas;
    private Button btnVoltarInicio;
    private Button btnAcompanheCompras;

    protected DetalhesPremioDTO detalhesPremioDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pagamento);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_confirmacao_pagamento)));

        configViews();

        getExtras();
        configLayout();
        configuraBotoes();
        RateUtils.avaliarAplicativo(this,RateUtils.getQtdCompras());
    }

    private void configViews() {
        titulo = findViewById(R.id.textTitulo);
        infoApostas = findViewById(R.id.textInfoApostas);
        btnVoltarInicio = findViewById(R.id.botaoVoltarInicio);
        btnAcompanheCompras = findViewById(R.id.botaoAcompanheSuasCompras);
    }

    private void configLayout() {
        if (origem != null && origem.equals(MeioPagamentoUtils.PIX)) {
            if (fluxo != null && fluxo.equals(PixUtils.FLUXO_RESGATE)){
                titulo.setText(R.string.resgate_pix_concluido);
                infoApostas.setText(R.string.premio_pix_ja_creditado);
                btnVoltarInicio.setBackgroundColor(getResources().getColor(R.color.branco));
                btnVoltarInicio.setTextColor(getResources().getColor(R.color.verde_acompanha_compras));
                btnAcompanheCompras.setBackgroundColor(getResources().getColor(R.color.verde_acompanha_compras));
            } else {
                titulo.setText(R.string.confirmacaopagamentoParabensPix);
                infoApostas.setText(R.string.confirmacaoPagamentoInfoApostasPIX);
                btnVoltarInicio.setBackgroundColor(getResources().getColor(R.color.branco));
                btnVoltarInicio.setTextColor(getResources().getColor(R.color.verde_acompanha_compras));
                btnAcompanheCompras.setBackgroundColor(getResources().getColor(R.color.verde_acompanha_compras));
            }
         }
    }

    private void getExtras() {
        if (getIntent() != null && getIntent().getExtras() != null)  {
            origem = getIntent().getExtras().getLong(ARG_ORIGEM);
            fluxo = getIntent().getExtras().getLong(ARG_FLUXO);
            idAposta = getIntent().getExtras().getLong( getResources().getString(R.string.id_aposta));
        }
    }

    @Override
    public void onBackPressed() { }

    private void configuraBotoes(){

        btnVoltarInicio.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.confirmacaoPagamentoBotaoVoltarInicio)));
        if (isPixResgate()){
            btnAcompanheCompras.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.visualizar_comprovante)));
        } else {
            btnAcompanheCompras.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.label_acompanhe_suas_compras_pix)));
        }

        btnVoltarInicio.setOnClickListener(view -> PrincipalActivity_.intent(ConfirmacaoPagamentoActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start());

        if (isPixResgate()){
            btnAcompanheCompras.setOnClickListener(view -> {
                if (detalhesPremioDTO != null){
                    PdfUtils.createComprovantePremioPdf(detalhesPremioDTO, ConfirmacaoPagamentoActivity.this);
                }  else {
                    atualizaComprovante();
                }
            });
        } else {
            btnAcompanheCompras.setOnClickListener(view ->
                                                           startActivity(new Intent(ConfirmacaoPagamentoActivity.this, ListaComprasActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_SINGLE_TOP)));

        }

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
        ApostaBO.getInstance().getApostaComprovantePremio(idAposta, new RequestListener<DetalhesPremioDTOResponse>() {
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
