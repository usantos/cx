package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.SimulaActivity;

public class IntroducaoTutorialActivity extends LoteriasBaseAppActivity implements View.OnClickListener {

	private Button btnComecar, btnPular;
	private AppCompatCheckBox checkBox;

	private ParametroJogoDTO parametroJogo;
	private ModalidadeEnum modalidade;
	private boolean isEspecial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introducao_tutorial);
		setViews();
		setMethods();
		getBundle();
	}

	private void setViews() {
		btnComecar = findViewById(R.id.btnComecar);
		SpannableStringBuilder strBtnComecar = ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.comecar_tutorial));
		btnComecar.setText(strBtnComecar);

		btnPular = findViewById(R.id.btnPular);
		SpannableStringBuilder strBtnPular = ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.pular_tutorial));
		btnPular.setText(strBtnPular);

		checkBox = findViewById(R.id.checkboxMostrar);
	}

	private void setMethods() {
		btnComecar.setOnClickListener(this);
		btnPular.setOnClickListener(this);
	}

	private void getBundle() {
		parametroJogo = new Gson().fromJson(getIntent().getStringExtra(getResources().getString(R.string.extra_modalidade)), ParametroJogoDTO.class);
		modalidade = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
		isEspecial =  getIntent().getBooleanExtra(getResources().getString(R.string.extra_especial), false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnComecar:
				if (checkBox.isChecked()) {
					DadosUsuarioBO.updateTutorial(checkBox.isChecked(), DadosUsuarioBO.MAIS_MILIONARIA_TUTORIAL);
				}
				comecarTurorial();
				break;
			case R.id.btnPular:
				pularTurotial();
				break;
		}
	}

	private void comecarTurorial() {
		startActivity(getIntentNewActivity(TutorialMaisMilionariaActivity.class));
		finish();
	}

	private void pularTurotial() {
		AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_simular_aposta_modalidade) + modalidade);

		startActivity(getIntentNewActivity(SimulaActivity.class));
		finish();
	}

	@NotNull
	private Intent getIntentNewActivity(Class clasz) {
		Intent activity = new Intent(IntroducaoTutorialActivity.this, clasz);
		activity.putExtra(getString(R.string.extra_tipo_aposta), modalidade);
		activity.putExtra(getString(R.string.extra_modalidade), (new Gson()).toJson(parametroJogo));
		activity.putExtra(getString(R.string.extra_especial),parametroJogo.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)));
		return activity;
	}
}