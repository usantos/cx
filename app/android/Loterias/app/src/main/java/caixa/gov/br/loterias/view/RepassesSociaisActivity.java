package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.text.DecimalFormat;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.RepassometroDTO;
import caixa.gov.br.loterias.bo.silce.dto.RepassometroDTOResponse;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;

public class RepassesSociaisActivity extends AppCompatActivity {
    private RepassometroDTO repasseOBj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repasses_sociais);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callWebservice();
    }

    private void callWebservice () {
        final View loadViewProgress = LoadingViewLoterias.init(this);

        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(RepassesSociaisActivity.this);
        dadosCorporativosBO.repasses(new SilceClient.Listener<RepassometroDTOResponse>() {
            @Override
            public void onResponse(RepassometroDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                repasseOBj = response.getPayload();
                setLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(RepassesSociaisActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void setLayout(){

        String htmlTextViewRepasseQuadro1 = "<html>Para cada <br><strong><font color=\"#ff7f00\">R$1,00</font></strong><br>apostados nas<br>loterias federais</html>";
        String htmlTextViewRepasseQuadro2 = "<html><strong><font color=\"#345ca5\">R$0,48*</font></strong><br>são destinados<br>a programas sociais</html>";
        String htmlTextButtonAposte = "<html><strong>Aposte</strong><br>e ajude ainda mais</html>";
        TextView TextViewRepasseQuadro1 = (TextView) findViewById(R.id.textViewRepasseQuadro1);
        TextView TextViewRepasseQuadro2 = (TextView) findViewById(R.id.textViewRepasseQuadro2);
        TextView valorRepasses = (TextView) findViewById(R.id.valorRepassesSociais);

        DecimalFormat formatter = ViewUtils.getMoedaFormatter();
        String format = formatter.format(repasseOBj.getArrecadacaoAcumulada());

        valorRepasses.setText("R$" +(format));
        Button ButtonAposte = (Button) findViewById(R.id.ButtonAposteSociais);
        TextViewRepasseQuadro1.setText(ViewUtils.fromHtml(htmlTextViewRepasseQuadro1));
        TextViewRepasseQuadro2.setText(ViewUtils.fromHtml(htmlTextViewRepasseQuadro2));
        ButtonAposte.setText(ViewUtils.fromHtml(htmlTextButtonAposte));


        ButtonAposte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

}
