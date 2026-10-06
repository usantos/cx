package br.gov.caixa.loterias.apostas.controllers;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Calendar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepassometroDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RepassometroDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoCalculoRepassesSociais;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class RepassesSociaisActivity extends SettingToolbarActivity {
    private RepassometroDTO repasseOBj;
    ImageButton customButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repasses_sociais);
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setText(getString(R.string.title_repassesSociais));
        textTitulo.setHint(getString(R.string.titulo));
        customButton = findViewById(R.id.customButton);
        customButton.setFocusable(true);
        customButton.setFocusableInTouchMode(true);
        customButton.requestFocus();
        customButton.setContentDescription(getString(R.string.voltar));
        customButton.setOnClickListener(v -> onBackPressed());
        ImageButton questionButton = findViewById(R.id.buttonQuestions);
        questionButton.setOnClickListener(v ->
                    startActivity(new Intent(this, TermosUsoActivity.class))
        );
        TextView textEntenda = findViewById(R.id.entendaText);
        textEntenda.setContentDescription(getString(R.string.entenda_como) + " "+ getString(R.string.esse_dinheiro_distribu_do));
        textEntenda.setHint(getString(R.string.titulo));
        callWebservice();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    private void callWebservice () {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);

        DadosCorporativosSilceBO.getInstance().repasses(new RequestListener<RepassometroDTOResponse>() {
            @Override
            public void onResponse(RepassometroDTOResponse response) {
                loadViewProgress.dismiss();
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_repassometro));
                repasseOBj = response.getPayload();
                setLayout();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), RepassesSociaisActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, RepassesSociaisActivity.this );
            }
        });
    }

    private void setLayout(){

        TextView ttvTituloAno = findViewById(R.id.ttvTituloAno);
        String texto = getResources().getString(R.string.em_espaco) + Calendar.getInstance().get(Calendar.YEAR)+ getResources().getString(R.string.ja_destinaram);
        ttvTituloAno.setText(texto);
        ttvTituloAno.setContentDescription(texto);

        String htmlTextButtonAposte = getResources().getString(R.string.aposte_e_ajude);
        TextView textViewRepasseQuadro1 = findViewById(R.id.textViewRepasseQuadro1);
        TextView textViewRepasseQuadro2 = findViewById(R.id.textViewRepasseQuadro2);
        TextView valorRepasses = findViewById(R.id.valorRepassesSociais);

        TextView textViewEducacaoValorArrecadado = findViewById(R.id.textViewEducacaoValorArrecadado);
        String[] valoresEducacao = formatValorArrecadados(repasseOBj.getPercentualEducacao());
        String valEducacao = "R$ " + valoresEducacao[0];
        textViewEducacaoValorArrecadado.setText(valEducacao);
        TextView textEducacao = findViewById(R.id.textEducacao);
        textViewEducacaoValorArrecadado.setContentDescription(valoresEducacao[0] + " " + valoresEducacao[1] + " para " + textEducacao.getText());
        TextView textViewEducacaoUnidadeValor = findViewById(R.id.textViewEducacaoUnidadeValor);
        textViewEducacaoUnidadeValor.setText(valoresEducacao[1]);

        String[] valoresCultura = formatValorArrecadados(repasseOBj.getPercentualCultura());
        TextView textViewCulturaValorArrecadado = findViewById(R.id.textViewCulturaValorArrecadado);
        String valCultura = "R$ " + valoresCultura[0];
        textViewCulturaValorArrecadado.setText(valCultura);
        TextView textViewCultura = findViewById(R.id.textCultura);
        textViewCulturaValorArrecadado.setContentDescription(valoresCultura[0] + " " + valoresCultura[1] + " para " + textViewCultura.getText());
        TextView textViewCulturaUnidadeValor = findViewById(R.id.textViewCulturaUnidadeValor);
        textViewCulturaUnidadeValor.setText(valoresCultura[1]);

        String[] valoresEsporte = formatValorArrecadados(repasseOBj.getPercentualEsporte());
        TextView textViewEsporteValorArrecadado = findViewById(R.id.textViewEsporteValorArrecadado);
        String valEsporte = "R$ " + valoresEsporte[0];
        textViewEsporteValorArrecadado.setText(valEsporte);
        TextView textEsporte = findViewById(R.id.textEsporte);
        textViewEsporteValorArrecadado.setContentDescription(valoresEsporte[0] + " " + valoresEsporte[1] + " para " + textEsporte.getText());
        TextView textViewEsporteUnidadeValor = findViewById(R.id.textViewEsporteUnidadeValor);
        textViewEsporteUnidadeValor.setText(valoresEsporte[1]);

        String[] valoresSeguranca = formatValorArrecadados(repasseOBj.getPercentualSeguranca());
        TextView textViewSegurancaValorArrecadado = findViewById(R.id.textViewSegurancaValorArrecadado);
        String valSeguranca = "R$ " + valoresSeguranca[0];
        textViewSegurancaValorArrecadado.setText(valSeguranca);
        TextView textSeguranca = findViewById(R.id.textSeguranca);
        textViewSegurancaValorArrecadado.setContentDescription(valoresSeguranca[0] + " " + valoresSeguranca[1] + " para " + textSeguranca.getText());
        TextView textViewSegurancaUnidadeValor = findViewById(R.id.textViewSegurancaUnidadeValor);
        textViewSegurancaUnidadeValor.setText(valoresSeguranca[1]);

        String[] valoresSeguridade = formatValorArrecadados(repasseOBj.getPercentualSeguridade());
        TextView textViewSeguridadeValorArrecadado = findViewById(R.id.textViewSeguridadeValorArrecadado);
        String valSeguridade = "R$ " + valoresSeguridade[0];
        textViewSeguridadeValorArrecadado.setText(valSeguridade);
        TextView textSeguridade = findViewById(R.id.textSeguridade);
        textViewSeguridadeValorArrecadado.setContentDescription(valoresSeguridade[0] + " " + valoresSeguridade[1] + " para " + textSeguridade.getText());
        TextView textViewSeguridadeUnidadeValor = findViewById(R.id.textViewSeguridadeUnidadeValor);
        textViewSeguridadeUnidadeValor.setText(valoresSeguridade[1]);

        String[] valoresOutros = formatValorArrecadados(repasseOBj.getPercentualOutros());
        TextView textViewOutrosValorArrecadado = findViewById(R.id.textViewOutrosValorArrecadado);
        String valOutros = "R$ " + valoresOutros[0];
        textViewOutrosValorArrecadado.setText(valOutros);
        TextView textOutros = findViewById(R.id.textOutros);
        textViewOutrosValorArrecadado.setContentDescription(valoresOutros[0] + " " + valoresOutros[1] + " para " + textOutros.getText());
        TextView textViewOutrosUnidadeValor = findViewById(R.id.textViewOutrosUnidadeValor);
        textViewOutrosUnidadeValor.setText(valoresOutros[1]);

        AnimacaoCalculoRepassesSociais.start(valorRepasses, 0, repasseOBj);

        Button buttonAposte = findViewById(R.id.ButtonAposteSociais);
        buttonAposte.setContentDescription(getString(R.string.aposte_ne_ajude_ainda_mais));

        LinearLayout infoRepasses = findViewById(R.id.infoRepasses);
        infoRepasses.setContentDescription(getString(R.string.r_0_48_ns_o_destinados_na_programas_sociais));

        textViewRepasseQuadro1.setContentDescription(getString(R.string.para_cada_nr_1_00_napostados_nas_nloterias_federais));
        textViewRepasseQuadro2.setContentDescription(getString(R.string.r_0_48_ns_o_destinados_na_programas_sociais));
        buttonAposte.setText(ViewUtils.textCaixaSTDBold(this, ViewUtils.fromHtml(htmlTextButtonAposte).toString()));
        buttonAposte.setOnClickListener(v -> finish());
    }
    private String[] formatValorArrecadados(BigDecimal valor) {
        BigDecimal arrecadacaoAcumulada = repasseOBj.getArrecadacaoAcumulada() != null
                ? repasseOBj.getArrecadacaoAcumulada()
                : BigDecimal.ZERO;

        valor = arrecadacaoAcumulada.divide(BigDecimal.valueOf(100)).multiply(valor);

        String[] valores = new String[2];

        if (valor.compareTo(BigDecimal.valueOf(1_000_000_000)) >= 0) {
            BigDecimal bilhoes = valor.divide(BigDecimal.valueOf(1_000_000_000), 1, RoundingMode.DOWN);
            valores[0] = bilhoes.toPlainString().replace('.', ',');
            valores[1] = bilhoes.compareTo(BigDecimal.valueOf(2)) < 0 ? getString(R.string.bilhao_de_reais) : getString(R.string.bilhoes_de_reais);

        } else if (valor.compareTo(BigDecimal.valueOf(1_000_000)) >= 0) {
            BigDecimal milhoes = valor.divide(BigDecimal.valueOf(1_000_000), 1, RoundingMode.DOWN);
            valores[0] = milhoes.toPlainString().replace('.', ',');
            valores[1] = milhoes.compareTo(BigDecimal.valueOf(2)) < 0 ? getString(R.string.milhao_de_reais) : getString(R.string.milhoes_de_reais);

        } else if (valor.compareTo(BigDecimal.valueOf(1_000)) >= 0) {
            BigDecimal milhares = valor.divide(BigDecimal.valueOf(1_000), 1, RoundingMode.DOWN);
            valores[0] = milhares.toPlainString().replace('.', ',');
            valores[1] = getString(R.string.mil_reais);

        } else {
            valores[0] = valor.toPlainString().replace('.', ',');
            valores[1] = getString(R.string.reais);
        }

        return valores;
    }
        @Override
    protected void configuraActivityBack() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient_azul, null));
    }

}
