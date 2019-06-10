package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RepassometroDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RepassometroDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.animation.AnimacaoCalculoRepassesSociais;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class RepassesSociaisActivity extends SettingToolbarActivity {
    private RepassometroDTO repasseOBj;
//    RepassometroDTO repassesSociais;

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

        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(RepassesSociaisActivity.this);
        dadosCorporativosBO.repasses(new RequestListener<RepassometroDTOResponse>() {
            @Override
            public void onResponse(RepassometroDTOResponse response) {
                loadViewProgress.dismiss();
                FirebaseManager.registraEvento(RepassesSociaisActivity.this ,"ENTROU_REPASSOMETRO");
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
        String texto = "Em " + Calendar.getInstance().get(Calendar.YEAR)+ ", as Loterias já destinaram";
        ttvTituloAno.setText(texto);

        String htmlTextViewRepasseQuadro1 = "<html>Para cada <br><strong><font color=\"#ff7f00\">R$1,00</font></strong><br>apostados nas<br>loterias federais</html>";
        String htmlTextViewRepasseQuadro2 = "<html><strong><font color=\"#345ca5\">R$0,48*</font></strong><br>são destinados<br>a programas sociais</html>";
        String htmlTextButtonAposte = "_Aposte_\ne ajude ainda mais";
        TextView TextViewRepasseQuadro1 = findViewById(R.id.textViewRepasseQuadro1);
        TextView TextViewRepasseQuadro2 = findViewById(R.id.textViewRepasseQuadro2);
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

        AnimacaoCalculoRepassesSociais.start(valorRepasses, 0, 0, repasseOBj);

        Button ButtonAposte = findViewById(R.id.ButtonAposteSociais);
        TextViewRepasseQuadro1.setText(ViewUtils.fromHtml(htmlTextViewRepasseQuadro1));
        TextViewRepasseQuadro2.setText(ViewUtils.fromHtml(htmlTextViewRepasseQuadro2));
        ButtonAposte.setText(ViewUtils.textFuturaAndFuturaBold(this, htmlTextButtonAposte));


        ButtonAposte.setOnClickListener(v -> finish());
    }

    private String[] formatValorArrecados(BigDecimal valor) {
        BigDecimal arrecadacaoAcumulada = new BigDecimal(0.0);
        if (repasseOBj.getArrecadacaoAcumulada() != null) {
            arrecadacaoAcumulada = repasseOBj.getArrecadacaoAcumulada();
        }
        valor = (arrecadacaoAcumulada.divide(BigDecimal.valueOf(100))).multiply(valor);
        String[] valores = new String[2];

        BigInteger valorArredondado = valor.toBigInteger();
        String strValor = valorArredondado.toString();
        String[] parts = strValor.split("");
        int countParts = parts.length;
        if (countParts == 5 || countParts == 6 || countParts == 7){
            valores[0] = valorArredondado.divide(BigInteger.valueOf(1000)).toString() + "," + parts[1+valorArredondado.divide(BigInteger.valueOf(1000)).toString().length()];
            valores[1] = "mil reais";
        }else if (countParts == 8 || countParts == 9 || countParts == 10) {
            valores[0] = valorArredondado.divide(BigInteger.valueOf(1000000)).toString() + "," + parts[1+valorArredondado.divide(BigInteger.valueOf(1000000)).toString().length()];
            valores[1] = "milhões de reais";
        }else if (countParts == 11 || countParts == 12 || countParts == 13) {
            valores[0] = valorArredondado.divide(BigInteger.valueOf(1000000000)).toString() + "," + parts[1+valorArredondado.divide(BigInteger.valueOf(1000000000)).toString().length()];
            valores[1] = "bilhões de reais";
        }else{
            valores[0] = strValor;
            valores[1] = "reais";
        }
        return valores;
    }

}
