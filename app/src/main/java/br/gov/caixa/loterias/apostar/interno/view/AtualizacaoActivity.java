package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.BuildConfig;
import br.gov.caixa.loterias.apostar.interno.R;

public class AtualizacaoActivity extends AppCompatActivity {

    public Button buttonAtualizar;

    private TextView textAtualizacaoVersao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atualizacao);

        textAtualizacaoVersao = findViewById(R.id.txtVersaoAtualizacao );

        textAtualizacaoVersao.setText( TextUtils.concat( "v" ,BuildConfig.VERSION_NAME) );

        buttonAtualizar = findViewById(R.id.atualizarButton);
        buttonAtualizar.setOnClickListener(view -> {
            String url = BuildConfig.FLAVOR.equals("prd") ? "http://www.caixa.gov.br/atendimento/aplicativos/Documents/aplicativo-loterias-online.apk" : "https://loja.mobilidade.caixa.gov.br/applicationcenter/installers.html";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
    }

    @Override
    public void onBackPressed() { }
}
