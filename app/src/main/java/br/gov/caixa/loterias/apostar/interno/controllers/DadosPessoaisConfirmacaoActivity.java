package br.gov.caixa.loterias.apostar.interno.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.Utils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class DadosPessoaisConfirmacaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dados_pessoais_confirmacao );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        setTitle( ViewUtils.textFuturaAndFuturaBoldTitle( this, getTitle().toString() ) );

        TextView tituloConfirmacaoCadastroTextView = findViewById( R.id.tituloConfirmacaoCadastroTextView );
        TextView subtituloConfirmaCadastroTextView = findViewById( R.id.subtituloConfirmaCadastroTextView );
        tituloConfirmacaoCadastroTextView.setText( ViewUtils.textFuturaAndFuturaBold( this, getResources().getString(R.string.cadastro_atualizado) ) );
        subtituloConfirmaCadastroTextView.setText( getResources().getString(R.string.aguarde_redirecionamento_tela_inicial) );

        Utils.delay(3, () -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dados_pessoais, menu);

        return true;
    }

}
