package br.gov.caixa.loterias.apostar.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.utils.ValidacaoUtils;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.view.custom.LoadingViewLoterias;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailPremioMercadoPagoEditText = (EditText) findViewById( R.id.emailPremioMercadoPagoEditText );
        textoTituloPremioConfirmado = (TextView) findViewById( R.id.textoTituloPremioConfirmado );
        textoPremioConfirmado = (TextView) findViewById( R.id.textoPremioConfirmado );
        valorPremioTextView = (TextView) findViewById( R.id.valorPremioTextView );
        resgatarPremioButton = (Button) findViewById( R.id.resgatarPremioButton );
        voltarPremioButton = (Button) findViewById( R.id.voltarPremioButton );

        contentTopoConfirmarLinearLayout = (LinearLayout) findViewById( R.id.contentTopoConfirmarLinearLayout );
        contentTopoConfirmadoLinearLayout = (LinearLayout) findViewById( R.id.contentTopoConfirmadoLinearLayout );
        contentConfirmarLinearLayout = (LinearLayout) findViewById( R.id.contentConfirmarLinearLayout );
        contentConfirmadoLinearLayout = (LinearLayout) findViewById( R.id.contentConfirmadoLinearLayout );


        textoTituloPremioConfirmado.setText( "Em breve seu prêmio estará disponível\n em sua conta no mercado pago" );
        textoPremioConfirmado.setText( "Um email foi enviado para\n"+ emailPremioMercadoPagoEditText.getText().toString() + "\n\n\ncom todas as informações e passo-a-passo para o recebimento de seu prêmio" );


        Bundle b = getIntent().getExtras();
        String valorPremio = ""; // or other values
        if(b != null) {
            valorPremio = b.getString( "valorPremio" );
            valorPremioTextView.setText( valorPremio );
            idAposta = b.getLong( "idAposta" );
        }


        emailPremioMercadoPagoEditText.setText( SessaoUsuario.getInstance().getEmail() );

        resgatarPremioButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidacaoUtils.validarEmail( emailPremioMercadoPagoEditText.getText().toString() )) {
                    ViewUtils.alertTitleButton( JogosConfirmadosPremioMercadoPagoActivity.this,
                            R.string.label_atencao,
                            getResources().getString( R.string.MA021 ),
                            getResources().getString( R.string.ok ) );
                    return;
                }
                callWebserviceResgatarPremio();
            }
        } );

        voltarPremioButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        } );
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
        informacaoPagamentoDTO.setMeioPagamento( new Long( "1" ) );
        Map<String, String> mapPagamento = new HashMap<String, String>();
        mapPagamento.put("EMAIL", emailPremioMercadoPagoEditText.getText().toString());
        informacaoPagamentoDTO.setParametrosPagamento( mapPagamento );

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);

        ApostaBO apostaBO = ApostaBO_.getInstance_(JogosConfirmadosPremioMercadoPagoActivity.this);
        apostaBO.postResgatarAposta(idAposta, informacaoPagamentoDTO, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                loadViewProgress.dismiss();
                contentTopoConfirmarLinearLayout.setVisibility( View.GONE );
                contentTopoConfirmadoLinearLayout.setVisibility( View.VISIBLE );
                contentConfirmarLinearLayout.setVisibility( View.GONE );
                contentConfirmadoLinearLayout.setVisibility( View.VISIBLE );
                resgatarPremioButton.setVisibility( View.GONE );
                voltarPremioButton.setVisibility( View.VISIBLE );
                textoPremioConfirmado.setText( "Um email foi enviado para\n"+ emailPremioMercadoPagoEditText.getText().toString() + "\n\n\ncom todas as informações e passo-a-passo para o recebimento de seu prêmio" );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, JogosConfirmadosPremioMercadoPagoActivity.this );
            }
        });
    }

}
