package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repasses_sociais);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_repasses_sociais)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        DadosCorporativosBO.getInstance().repasses(new RequestListener<RepassometroDTOResponse>() {
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

        String htmlTextViewRepasseQuadro1 = getResources().getString(R.string.html_repasse_1);
        String htmlTextViewRepasseQuadro2 = getResources().getString(R.string.html_repasse_2);
        String htmlTextButtonAposte = getResources().getString(R.string.aposte_e_ajude);
        TextView textViewRepasseQuadro1 = findViewById(R.id.textViewRepasseQuadro1);
        TextView textViewRepasseQuadro2 = findViewById(R.id.textViewRepasseQuadro2);
        TextView valorRepasses = findViewById(R.id.valorRepassesSociais);

        TextView textViewEducacaoValorArrecadado = findViewById(R.id.textViewEducacaoValorArrecadado);
        String[] valoresEducacao = formatValorArrecados(repasseOBj.getPercentualEducacao());
        textViewEducacaoValorArrecadado.setText(valoresEducacao[0]);
        TextView textViewEducacaoUnidadeValor = findViewById(R.id.textViewEducacaoUnidadeValor);
        textViewEducacaoUnidadeValor.setText(valoresEducacao[1]);

        String[] valoresCultura = formatValorArrecados(repasseOBj.getPercentualCultura());
        TextView textViewCulturaValorArrecadado = findViewById(R.id.textViewCulturaValorArrecadado);
        textViewCulturaValorArrecadado.setText(valoresCultura[0]);
        TextView textViewCulturaUnidadeValor = findViewById(R.id.textViewCulturaUnidadeValor);
        textViewCulturaUnidadeValor.setText(valoresCultura[1]);

        String[] valoresEsporte = formatValorArrecados(repasseOBj.getPercentualEsporte());
        TextView textViewEsporteValorArrecadado = findViewById(R.id.textViewEsporteValorArrecadado);
        textViewEsporteValorArrecadado.setText(valoresEsporte[0]);
        TextView textViewEsporteUnidadeValor = findViewById(R.id.textViewEsporteUnidadeValor);
        textViewEsporteUnidadeValor.setText(valoresEsporte[1]);

        String[] valoresSeguranca = formatValorArrecados(repasseOBj.getPercentualSeguranca());
        TextView textViewSegurancaValorArrecadado = findViewById(R.id.textViewSegurancaValorArrecadado);
        textViewSegurancaValorArrecadado.setText(valoresSeguranca[0]);
        TextView textViewSegurancaUnidadeValor = findViewById(R.id.textViewSegurancaUnidadeValor);
        textViewSegurancaUnidadeValor.setText(valoresSeguranca[1]);

        String[] valoresSeguridade = formatValorArrecados(repasseOBj.getPercentualSeguridade());
        TextView textViewSeguridadeValorArrecadado = findViewById(R.id.textViewSeguridadeValorArrecadado);
        textViewSeguridadeValorArrecadado.setText(valoresSeguridade[0]);
        TextView textViewSeguridadeUnidadeValor = findViewById(R.id.textViewSeguridadeUnidadeValor);
        textViewSeguridadeUnidadeValor.setText(valoresSeguridade[1]);

        String[] valoresOutros = formatValorArrecados(repasseOBj.getPercentualOutros());
        TextView textViewOutrosValorArrecadado = findViewById(R.id.textViewOutrosValorArrecadado);
        textViewOutrosValorArrecadado.setText(valoresOutros[0]);
        TextView textViewOutrosUnidadeValor = findViewById(R.id.textViewOutrosUnidadeValor);
        textViewOutrosUnidadeValor.setText(valoresOutros[1]);

        AnimacaoCalculoRepassesSociais.start(valorRepasses, 0, repasseOBj);

        Button buttonAposte = findViewById(R.id.ButtonAposteSociais);
        textViewRepasseQuadro1.setText(ViewUtils.fromHtml(htmlTextViewRepasseQuadro1));
        textViewRepasseQuadro2.setText(ViewUtils.fromHtml(htmlTextViewRepasseQuadro2));
        buttonAposte.setText(ViewUtils.textFuturaAndFuturaBold(this, htmlTextButtonAposte));


        buttonAposte.setOnClickListener(v -> finish());
    }

    private String[] formatValorArrecados(BigDecimal valor) {
        BigDecimal arrecadacaoAcumulada = BigDecimal.valueOf(0.0);
        if (repasseOBj.getArrecadacaoAcumulada() != null) {
            arrecadacaoAcumulada = repasseOBj.getArrecadacaoAcumulada();
        }
        valor = (arrecadacaoAcumulada.divide(BigDecimal.valueOf(100))).multiply(valor);
        String[] valores = new String[2];

        BigInteger valorArredondado = valor.toBigInteger();
        String strValor = valorArredondado.toString();
        String[] parts = strValor.split(getResources().getString(R.string.string_vazia));
        int countParts = parts.length;
        if (countParts == 5 || countParts == 6 || countParts == 7){
            valores[0] = valorArredondado.divide(BigInteger.valueOf(1000)).toString() + getResources().getString(R.string.virgula) + parts[1+valorArredondado.divide(BigInteger.valueOf(1000)).toString().length()];
            valores[1] = getResources().getString(R.string.mil_reais);
        }else if (countParts == 8 || countParts == 9 || countParts == 10) {
            valores[0] = valorArredondado.divide(BigInteger.valueOf(1000000)).toString() +  getResources().getString(R.string.virgula) + parts[1+valorArredondado.divide(BigInteger.valueOf(1000000)).toString().length()];
            valores[1] = getResources().getString(R.string.milhoes_de_reais);
        }else if (countParts == 11 || countParts == 12 || countParts == 13) {
            valores[0] = valorArredondado.divide(BigInteger.valueOf(1000000000)).toString() + getResources().getString(R.string.virgula) + parts[1+valorArredondado.divide(BigInteger.valueOf(1000000000)).toString().length()];
            valores[1] = getResources().getString(R.string.bilhoes_de_reais);
        }else{
            valores[0] = strValor;
            valores[1] = getResources().getString(R.string.reais);
        }
        return valores;
    }

}
