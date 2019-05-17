package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import br.gov.caixa.loterias.apostar.interno.R;

public class AtualizacaoActivity extends AppCompatActivity {

    public Button buttonAtualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizacao);

        buttonAtualizar = findViewById(R.id.atualizarButton);
        buttonAtualizar.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://loja.mobilidade.caixa.gov.br/applicationcenter/installers.html"));
            startActivity(browserIntent);
        });
    }

    @Override
    public void onBackPressed() { }
}
