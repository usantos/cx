package br.gov.caixa.loterias.apostar.interno.controllers;

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

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.ValidacaoUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class JogosConfirmadosPremioMercadoPagoActivity extends AppCompatActivity {

	//region Layout Variables
	private EditText emailPremioMercadoPagoEditText;
	private TextView valorPremioTextView;
	private Button resgatarPremioButton, voltarPremioButton;
	private TextView textoTituloPremioConfirmado, textoPremioConfirmado;
	private LinearLayout contentTopoConfirmarLinearLayout, contentTopoConfirmadoLinearLayout, contentConfirmarLinearLayout, contentConfirmadoLinearLayout;
	//endregion

	//region Variables
	private Long idAposta;
	private String valorPremio;
	//endregion

	//region Life Cicle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jogos_confirmados_premio_mercado_pago);

		pegaExtras();
		setaViews();
		setaMetodos();
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

	//endregion

	//region Initialization Methods
	private void pegaExtras() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			valorPremio = extras.getString("valorPremio") == null ? "" : extras.getString("valorPremio");
			idAposta = extras.getLong("idAposta");
		}

	}

	private void setaViews() {
		emailPremioMercadoPagoEditText = findViewById(R.id.emailPremioMercadoPagoEditText);
		textoTituloPremioConfirmado = findViewById(R.id.textoTituloPremioConfirmado);
		textoPremioConfirmado = findViewById(R.id.textoPremioConfirmado);
		valorPremioTextView = findViewById(R.id.valorPremioTextView);
		resgatarPremioButton = findViewById(R.id.resgatarPremioButton);
		voltarPremioButton = findViewById(R.id.voltarPremioButton);

		contentTopoConfirmarLinearLayout = findViewById(R.id.contentTopoConfirmarLinearLayout);
		contentTopoConfirmadoLinearLayout = findViewById(R.id.contentTopoConfirmadoLinearLayout);
		contentConfirmarLinearLayout = findViewById(R.id.contentConfirmarLinearLayout);
		contentConfirmadoLinearLayout = findViewById(R.id.contentConfirmadoLinearLayout);

		textoTituloPremioConfirmado.setText("Em breve seu prêmio estará disponível\n em sua conta no mercado pago");
		textoPremioConfirmado.setText("Um email foi enviado para\n" + emailPremioMercadoPagoEditText.getText().toString() + "\n\n\ncom todas as informações e passo-a-passo para o recebimento de seu prêmio");


		emailPremioMercadoPagoEditText.setText(KeycloakBO_.getInstance_(this).getFromToken("email"));
		emailPremioMercadoPagoEditText.setSelection(emailPremioMercadoPagoEditText.getText().length());

		valorPremioTextView.setText(valorPremio);

		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

		voltarPremioButton.setOnClickListener(view -> finish());
	}
	//endregion

	//region Network Methods
	private void callWebserviceResgatarPremio() {
		InformacaoPagamentoDTO informacaoPagamentoDTO = new InformacaoPagamentoDTO();
		informacaoPagamentoDTO.setMeioPagamento(new Long("1"));
		Map<String, String> mapPagamento = new HashMap<>();
		mapPagamento.put("EMAIL", emailPremioMercadoPagoEditText.getText().toString());
		informacaoPagamentoDTO.setParametrosPagamento(mapPagamento);

		final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);

		ApostaBO apostaBO = ApostaBO_.getInstance_(JogosConfirmadosPremioMercadoPagoActivity.this);
		apostaBO.postResgatarAposta(idAposta, informacaoPagamentoDTO, new RequestListener<RetornoPadraoDTO>() {
			@Override
			public void onResponse(RetornoPadraoDTO response) {
				loadViewProgress.dismiss();
				setResult(RESULT_OK);
				configuraSucessoResgate();
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				loadViewProgress.dismiss();
				RedirectNetwork.checkRedirect(error, JogosConfirmadosPremioMercadoPagoActivity.this);
			}
		});
	}
	//endregion

	//region Layout Configuration Methods
	private void configuraSucessoResgate(){
		contentTopoConfirmarLinearLayout.setVisibility(View.GONE);
		contentTopoConfirmadoLinearLayout.setVisibility(View.VISIBLE);
		contentConfirmarLinearLayout.setVisibility(View.GONE);
		contentConfirmadoLinearLayout.setVisibility(View.VISIBLE);
		resgatarPremioButton.setVisibility(View.GONE);
		voltarPremioButton.setVisibility(View.VISIBLE);
		textoPremioConfirmado.setText("Um email foi enviado para\n" + emailPremioMercadoPagoEditText.getText().toString() + "\n\n\ncom todas as informações e passo-a-passo para o recebimento de seu prêmio");
	}
	//endregion

	//region Support Methods
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			abrirTermosUso();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}


	private void abrirTermosUso() {
		Intent activity = new Intent(JogosConfirmadosPremioMercadoPagoActivity.this, TermosUsoActivity.class);
		startActivity(activity);
	}
	//endregion

}
