package caixa.gov.br.loterias.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.Utils;
import caixa.gov.br.loterias.utils.ViewUtils;

public class CadastrarConfirmacao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cadastrar_confirmacao );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        setTitle( ViewUtils.textFuturaAndFuturaBold( this, getTitle().toString() ) );

        TextView tituloConfirmacaoCadastroTextView = (TextView) findViewById( R.id.tituloConfirmacaoCadastroTextView );
        TextView subtituloConfirmaCadastroTextView = (TextView) findViewById( R.id.subtituloConfirmaCadastroTextView );
        tituloConfirmacaoCadastroTextView.setText( ViewUtils.textFuturaAndFuturaBold( this, "_Excelente_!\nSeu cadastro foi realizado\ncom sucesso!" ) );
        subtituloConfirmaCadastroTextView.setText( "Aguarde apenas um instante enquanto\nte redirecionamos para suas apostas :)" );

        Utils.delay(3, new Utils.DelayCallback() {
            @Override
            public void afterDelay() {
                PrincipalActivity_.intent(CadastrarConfirmacao.this).start();
                CadastrarConfirmacao.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dados_pessoais, menu);

        return true;
    }
}
