package caixa.gov.br.loterias.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.bo.silce.dto.RepassometroDTO;
import caixa.gov.br.loterias.bo.silce.dto.RepassometroDTOResponse;
import caixa.gov.br.loterias.utils.AnimacaoCalculoRepassesSociais;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;

public class RepassesSociaisActivity extends SettingToolbarActivity {
    private RepassometroDTO repasseOBj;
//    RepassometroDTO repassesSociais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repasses_sociais);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_repasses_sociais)));

//        repassesSociais = new Gson().fromJson(getIntent().getStringExtra("repassesSociais"), RepassometroDTO.class);
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
        String htmlTextButtonAposte = "_Aposte_\ne ajude ainda mais";
        TextView TextViewRepasseQuadro1 = (TextView) findViewById(R.id.textViewRepasseQuadro1);
        TextView TextViewRepasseQuadro2 = (TextView) findViewById(R.id.textViewRepasseQuadro2);
        TextView valorRepasses = (TextView) findViewById(R.id.valorRepassesSociais);

//        DecimalFormat formatter = ViewUtils.getMoedaFormatter();
//        String format = formatter.format(repasseOBj.getArrecadacaoAcumulada());
//
//        valorRepasses.setText("R$" +(format));
        AnimacaoCalculoRepassesSociais.start(valorRepasses, 0, 0, repasseOBj);

        Button ButtonAposte = (Button) findViewById(R.id.ButtonAposteSociais);
        TextViewRepasseQuadro1.setText(ViewUtils.fromHtml(htmlTextViewRepasseQuadro1));
        TextViewRepasseQuadro2.setText(ViewUtils.fromHtml(htmlTextViewRepasseQuadro2));
        ButtonAposte.setText(ViewUtils.textFuturaAndFuturaBold(this, htmlTextButtonAposte));


        ButtonAposte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

}
