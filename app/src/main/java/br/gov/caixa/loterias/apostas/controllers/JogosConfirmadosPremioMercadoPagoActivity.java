package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.net.Uri;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StringResponse;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.ValidacaoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class JogosConfirmadosPremioMercadoPagoActivity extends AppCompatActivity {

    protected DetalhesPremioDTO detalhesPremioDTO;
    private EditText emailPremioMercadoPagoEditText;
    private TextView valorPremioTextView;
    private Long idAposta;
    private Button resgatarPremioButton, voltarPremioButton;
    private TextView textoTituloPremioConfirmado, textoPremioConfirmado;
    private LinearLayout contentTopoConfirmarLinearLayout, contentTopoConfirmadoLinearLayout, contentConfirmarLinearLayout, contentConfirmadoLinearLayout;
    private ConstraintLayout comprovanteLayout;
    private AlertDialog loadViewProgress;
    private String valorPremio = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogos_confirmados_premio_mercado_pago);

        pegaExtras();
        setaViews();
        setaMetodos();
    }

    private void pegaExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            valorPremio = bundle.getString(getResources().getString(R.string.valor_premio));
            idAposta = bundle.getLong( getResources().getString(R.string.id_aposta) );
        }
    }

    private void setaViews() {
        setTitle(ViewUtils.textCaixaSTDBold(this, getString(R.string.title_activity_resultado_ler_bilhetes)));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailPremioMercadoPagoEditText = findViewById(R.id.emailPremioMercadoPagoEditText);
        textoTituloPremioConfirmado = findViewById(R.id.textoTituloPremioConfirmado);
        textoPremioConfirmado = findViewById(R.id.textoPremioConfirmado);
        valorPremioTextView = findViewById(R.id.valorPremioTextView);
        resgatarPremioButton = findViewById(R.id.resgatarPremioButton);
        voltarPremioButton = findViewById(R.id.voltarPremioButton);
        comprovanteLayout = findViewById(R.id.comprovanteLayout);

        contentTopoConfirmarLinearLayout = findViewById(R.id.contentTopoConfirmarLinearLayout);
        contentTopoConfirmadoLinearLayout = findViewById(R.id.contentTopoConfirmadoLinearLayout);
        contentConfirmarLinearLayout = findViewById(R.id.contentConfirmarLinearLayout);
        contentConfirmadoLinearLayout = findViewById(R.id.contentConfirmadoLinearLayout);


        textoTituloPremioConfirmado.setText(getResources().getString(R.string.premio_disponivel_em_breve_mp));
        textoPremioConfirmado.setText(getResources().getString(R.string.email_enviado_para) + emailPremioMercadoPagoEditText.getText().toString() + getResources().getString(R.string.com_o_passo_a_passo));

        emailPremioMercadoPagoEditText.setText(KeycloakBO.getInstance().getFromToken(getResources().getString(R.string.email_minusculo)));
        emailPremioMercadoPagoEditText.setSelection(emailPremioMercadoPagoEditText.getText().length());
        valorPremioTextView.setText(valorPremio);

    }

    private void setaMetodos() {
        resgatarPremioButton.setOnClickListener(view -> {

            if (!ValidacaoUtils.validarEmail(emailPremioMercadoPagoEditText.getText().toString())) {
                ViewUtils.alertTitleButton(JogosConfirmadosPremioMercadoPagoActivity.this,
                                           R.string.label_atencao,
                                           getResources().getString(R.string.MA021),
                                           getResources().getString(R.string.ok));
                return;
            }
            callWebserviceResgatarPremio();
        });

        //voltarPremioButton.setOnClickListener(view -> finish());
        voltarPremioButton.setOnClickListener(view -> {
            Intent intent = IntentUtil.getIntentOrigemDestino(JogosConfirmadosPremioMercadoPagoActivity.this, PrincipalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void callWebserviceResgatarPremio() {
        InformacaoPagamentoDTO informacaoPagamentoDTO = new InformacaoPagamentoDTO();
        informacaoPagamentoDTO.setMeioPagamento(new Long(getResources().getString(R.string.um)));
        Map<String, String> mapPagamento = new HashMap<>();
        mapPagamento.put(getResources().getString(R.string.email_maiusculo), emailPremioMercadoPagoEditText.getText().toString());
        informacaoPagamentoDTO.setParametrosPagamento(mapPagamento);

        loadViewProgress = LoadingViewLoterias.show(this);


        ApostaBO.getInstance().postResgatarAposta(idAposta, informacaoPagamentoDTO, new RequestListener<RetornoPadraoResponse>() {
            @Override
            public void onResponse(RetornoPadraoResponse response) {
                setResult(RESULT_OK);
                contentTopoConfirmarLinearLayout.setVisibility(View.GONE);
                contentTopoConfirmadoLinearLayout.setVisibility(View.VISIBLE);
                contentConfirmarLinearLayout.setVisibility(View.GONE);
                contentConfirmadoLinearLayout.setVisibility(View.VISIBLE);
                resgatarPremioButton.setVisibility(View.GONE);
                voltarPremioButton.setVisibility(View.VISIBLE);
                textoPremioConfirmado.setText(getResources().getString(R.string.email_enviado_para) + emailPremioMercadoPagoEditText.getText().toString() + getResources().getString(R.string.com_o_passo_a_passo));
                comprovanteLayout.setVisibility(View.VISIBLE);

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                ((TextView) findViewById(R.id.voltarPremioButton)).setText(ViewUtils.textFuturaAndFuturaBold(getApplicationContext(), getString(R.string.btn_title_voltarAoInicio)));

                atualizaComprovante();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, JogosConfirmadosPremioMercadoPagoActivity.this);
            }
        });
    }

    private void atualizaComprovante(){
        ApostaBO.getInstance().getApostaComprovantePremio(idAposta, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                loadViewProgress.dismiss();
                detalhesPremioDTO = result.getPayload();
                comprovanteLayout.setOnClickListener(view -> PdfUtils.createComprovantePremioPagoPdf(detalhesPremioDTO, JogosConfirmadosPremioMercadoPagoActivity.this));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, JogosConfirmadosPremioMercadoPagoActivity.this);

            }
        });
    }

    public void redirectMp() {

        loadViewProgress = LoadingViewLoterias.show(this);
        ApostaBO.getInstance().getMercadoPagoToken(emailPremioMercadoPagoEditText.getText().toString(), new RequestListener<StringResponse>() {
            @Override
            public void onResponse(StringResponse result) {
                loadViewProgress.dismiss();
                String token = result.getPayload();
                token = token.replaceAll("[&\\/\\\\#,+()$~%.'\":*?<>{}]", "");
                Uri webpage = Uri.parse((getResources().getString(R.string.mp_token_url_redirect)).replace("{token}", token));
                Intent intentAc = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(intentAc);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, JogosConfirmadosPremioMercadoPagoActivity.this);
            }
        });

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


    private void abrirTermosUso() {
        Intent activity = new Intent(JogosConfirmadosPremioMercadoPagoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }

}