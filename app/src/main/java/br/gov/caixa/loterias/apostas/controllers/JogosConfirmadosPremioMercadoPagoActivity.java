package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostas.utils.ValidacaoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class JogosConfirmadosPremioMercadoPagoActivity extends AppCompatActivity {

    EditText emailPremioMercadoPagoEditText;
    TextView valorPremioTextView;
    Long idAposta;
    Button resgatarPremioButton, voltarPremioButton;
    TextView textoTituloPremioConfirmado, textoPremioConfirmado;
    LinearLayout contentTopoConfirmarLinearLayout, contentTopoConfirmadoLinearLayout, contentConfirmarLinearLayout, contentConfirmadoLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogos_confirmados_premio_mercado_pago);
        setTitle( ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailPremioMercadoPagoEditText = findViewById( R.id.emailPremioMercadoPagoEditText );
        textoTituloPremioConfirmado = findViewById( R.id.textoTituloPremioConfirmado );
        textoPremioConfirmado = findViewById( R.id.textoPremioConfirmado );
        valorPremioTextView = findViewById( R.id.valorPremioTextView );
        resgatarPremioButton = findViewById( R.id.resgatarPremioButton );
        voltarPremioButton = findViewById( R.id.voltarPremioButton );

        contentTopoConfirmarLinearLayout = findViewById( R.id.contentTopoConfirmarLinearLayout );
        contentTopoConfirmadoLinearLayout = findViewById( R.id.contentTopoConfirmadoLinearLayout );
        contentConfirmarLinearLayout = findViewById( R.id.contentConfirmarLinearLayout );
        contentConfirmadoLinearLayout = findViewById( R.id.contentConfirmadoLinearLayout );


        textoTituloPremioConfirmado.setText( getResources().getString(R.string.premio_disponivel_em_breve_mp) );
        textoPremioConfirmado.setText( getResources().getString(R.string.email_enviado_para)+ emailPremioMercadoPagoEditText.getText().toString() + getResources().getString(R.string.com_o_passo_a_passo) );


        Bundle b = getIntent().getExtras();
        String valorPremio = ""; // or other values
        if(b != null) {
            valorPremio = b.getString( getResources().getString(R.string.valor_premio) );
            valorPremioTextView.setText( valorPremio );
            idAposta = b.getLong( getResources().getString(R.string.id_aposta) );
        }

        emailPremioMercadoPagoEditText.setText(KeycloakBO_.getInstance_(this).getFromToken(getResources().getString(R.string.email_minusculo)));
        emailPremioMercadoPagoEditText.setSelection(emailPremioMercadoPagoEditText.getText().length());

        resgatarPremioButton.setOnClickListener(view -> {
            if (!ValidacaoUtils.validarEmail( emailPremioMercadoPagoEditText.getText().toString() )) {
                ViewUtils.alertTitleButton( JogosConfirmadosPremioMercadoPagoActivity.this,
                        R.string.label_atencao,
                        getResources().getString( R.string.MA021 ),
                        getResources().getString( R.string.ok ) );
                return;
            }
            callWebserviceResgatarPremio();
        });

        voltarPremioButton.setOnClickListener(view -> finish());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebserviceResgatarPremio();
        }
    }


    private void abrirTermosUso(){
        Intent activity = new Intent(JogosConfirmadosPremioMercadoPagoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void callWebserviceResgatarPremio() {
        InformacaoPagamentoDTO informacaoPagamentoDTO = new InformacaoPagamentoDTO();
        informacaoPagamentoDTO.setMeioPagamento( new Long( getResources().getString(R.string.um) ) );
        Map<String, String> mapPagamento = new HashMap<>();
        mapPagamento.put(getResources().getString(R.string.email_maiusculo), emailPremioMercadoPagoEditText.getText().toString());
        informacaoPagamentoDTO.setParametrosPagamento( mapPagamento );

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);

        ApostaBO apostaBO = ApostaBO_.getInstance_(JogosConfirmadosPremioMercadoPagoActivity.this);
        apostaBO.postResgatarAposta(idAposta, informacaoPagamentoDTO, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                loadViewProgress.dismiss();
                setResult(RESULT_OK);
                contentTopoConfirmarLinearLayout.setVisibility( View.GONE );
                contentTopoConfirmadoLinearLayout.setVisibility( View.VISIBLE );
                contentConfirmarLinearLayout.setVisibility( View.GONE );
                contentConfirmadoLinearLayout.setVisibility( View.VISIBLE );
                resgatarPremioButton.setVisibility( View.GONE );
                voltarPremioButton.setVisibility( View.VISIBLE );
                textoPremioConfirmado.setText( getResources().getString(R.string.email_enviado_para)+ emailPremioMercadoPagoEditText.getText().toString() + getResources().getString(R.string.com_o_passo_a_passo) );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, JogosConfirmadosPremioMercadoPagoActivity.this );
            }
        });
    }

}
