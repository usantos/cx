package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.utils.ViewUtils;

public class JogosConfirmadosPremioMercadoPagoActivity extends AppCompatActivity {

    EditText emailPremioMercadoPagoEditText;
    TextView valorPremioTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogos_confirmados_premio_mercado_pago);
        setTitle( ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_resultado_ler_bilhetes)));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailPremioMercadoPagoEditText = (EditText) findViewById( R.id.emailPremioMercadoPagoEditText );
        valorPremioTextView = (TextView) findViewById( R.id.valorPremioTextView );

        Bundle b = getIntent().getExtras();
        String valorPremio = ""; // or other values
        if(b != null) {
            valorPremio = b.getString( "valorPremio" );
            valorPremioTextView.setText( valorPremio );
        }


        emailPremioMercadoPagoEditText.setText( SessaoUsuario.getInstance().getEmail() );

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            abrirTermosUso();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void abrirTermosUso(){
        Intent activity = new Intent(JogosConfirmadosPremioMercadoPagoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }

}
