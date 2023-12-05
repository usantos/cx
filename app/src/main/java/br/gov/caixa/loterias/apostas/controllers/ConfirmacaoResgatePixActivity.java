package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class ConfirmacaoResgatePixActivity extends AppCompatActivity {

    protected DetalhesPremioDTO detalhesPremioDTO;
    private Long idAposta;
    private Button voltarConfirmacaoResgate;
    private ConstraintLayout comprovanteLayoutPix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_resgate_pix);

        pegaExtras();
        setaViews();
        setaMetodos();
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idAposta = bundle.getLong( getResources().getString(R.string.id_aposta) );
        }
    }

    private void setaViews() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        comprovanteLayoutPix = findViewById(R.id.comprovanteLayoutPix);
        voltarConfirmacaoResgate = findViewById(R.id.voltarConfirmacaoResgate);
    }

    private void setaMetodos() {

        //atualizaComprovante();

        //voltarConfirmacaoResgate.setOnClickListener(view -> finish());
        comprovanteLayoutPix.setOnClickListener(view -> {
            if (detalhesPremioDTO != null){
                PdfUtils.createComprovantePremioPdf(detalhesPremioDTO, ConfirmacaoResgatePixActivity.this);
            }  else {
                atualizaComprovante();
            }
        });
        voltarConfirmacaoResgate.setOnClickListener(view -> PrincipalActivity_.intent(ConfirmacaoResgatePixActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start());
    }

    private void atualizaComprovante(){
        AlertDialogUtils.show(this);
        ApostaBO apostaBO = ApostaBO_.getInstance_(this);
        apostaBO.getApostaComprovantePremio(idAposta, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                AlertDialogUtils.dismiss();
                detalhesPremioDTO = result.getPayload();
                PdfUtils.createComprovantePremioPdf(detalhesPremioDTO, ConfirmacaoResgatePixActivity.this);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, ConfirmacaoResgatePixActivity.this);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



}