package caixa.gov.br.loterias.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.ViewUtils;

public class RepassesSociaisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repasses_sociais);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setLayout();

    }


    private void setLayout(){

        String htmlTextViewRepasseQuadro1 = "<html>Para cada <br><strong><font color=\"#ff7f00\">R$1,00</font></strong><br>apostados nas<br>loterias federais</html>";
        String htmlTextViewRepasseQuadro2 = "<html><strong><font color=\"#345ca5\">R$0,48*</font></strong><br>são destinados<br>a programas sociais</html>";
        String htmlTextButtonAposte = "<html><strong>Aposte</strong><br>e ajude ainda mais</html>";
        TextView TextViewRepasseQuadro1 = (TextView) findViewById(R.id.textViewRepasseQuadro1);
        TextView TextViewRepasseQuadro2 = (TextView) findViewById(R.id.textViewRepasseQuadro2);
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
