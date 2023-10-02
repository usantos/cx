package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.model.TutorialModel;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialNovaModalidadeFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialPassoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnTutorialListener;

public class TutorialActivity extends AppCompatActivity implements OnTutorialListener {

	private ParametroJogoDTO parametroJogo;
	private ModalidadeEnum modalidade;
	private TutorialModel model;

	private TutorialNovaModalidadeFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		getBundle();

		model = new TutorialModel(this);
		fragment = TutorialNovaModalidadeFragment.newInstance();
		FragmentUtils.startFragment(getSupportFragmentManager(), R.id.frameTutorial, fragment);
	}

	private void getBundle() {
		parametroJogo = new Gson().fromJson(getIntent().getStringExtra(getResources().getString(R.string.extra_modalidade)), ParametroJogoDTO.class);
		modalidade = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
	}

	@Override
	public void apostar(Boolean naoVerNovamente) {
		atualizaStatusTutorial(naoVerNovamente);

		startActivity(model.getIntentModalidade(modalidade, parametroJogo));
		finish();
	}

	@Override
	public void pular(boolean naoVerNovamente) {
		atualizaStatusTutorial(naoVerNovamente);
		finish();
	}

	private void atualizaStatusTutorial(boolean naoVerNovamente) {
		if (naoVerNovamente) {
			DadosUsuarioBO.updateTutorial(naoVerNovamente, model.getSPModalidade(modalidade));
		}
	}

	@Override
	public List<TutorialPassoFragment> getFragments() {
		return model.getFragments(modalidade);
	}
}