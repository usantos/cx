package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialNovaModalidadeFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialPassoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnTutorialListener;

public class TutorialMaisMilionariaActivity extends LoteriasBaseAppActivity implements OnTutorialListener {

	private List<TutorialPassoFragment> fragmentList;

	private ParametroJogoDTO parametroJogo;
	private ModalidadeEnum modalidade;

	private TutorialNovaModalidadeFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial_mais_milionaria);

		getBundle();

		initFragmentList();
		fragment = TutorialNovaModalidadeFragment.newInstance();
		FragmentUtils.startFragmentAllowingStateLoss(getSupportFragmentManager(), R.id.frameTutorial, fragment);
	}

	private void getBundle() {
		parametroJogo = new Gson().fromJson(getIntent().getStringExtra(getResources().getString(R.string.extra_modalidade)), ParametroJogoDTO.class);
		modalidade = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
	}

	private void initFragmentList() {
		fragmentList = new ArrayList<>();
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_introducao_tutorial_mais_milionaria, true));
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_1, false));
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_2, false));
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_3, false));
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_4, false));
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_5, false));
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_final, false));
	}

	@Override
	public void apostar(Boolean naoVerNovamente) {

		if (naoVerNovamente) {
			DadosUsuarioBO.updateTutorial(naoVerNovamente, DadosUsuarioBO.MAIS_MILIONARIA_TUTORIAL);
		}

		Intent activity = new Intent(TutorialMaisMilionariaActivity.this, SimulaActivity.class);
		activity.putExtra(getString(R.string.extra_tipo_aposta), modalidade);
		activity.putExtra(getString(R.string.extra_modalidade), (new Gson()).toJson(parametroJogo));
		activity.putExtra(getString(R.string.extra_especial),parametroJogo.getConcurso().getTipoConcurso().toString().equals(getResources().getString(R.string.especial_maiusculo)));
		startActivity(activity);
		finish();
	}

	@Override
	public void pular(boolean naoVerNovamente) {
		if (naoVerNovamente) {
			DadosUsuarioBO.updateTutorial(naoVerNovamente, DadosUsuarioBO.MAIS_MILIONARIA_TUTORIAL);
		}
		finish();
	}

	@Override
	public List<TutorialPassoFragment> getFragments() {
		if (fragmentList == null){
			initFragmentList();
		}
		return fragmentList;
	}

	@Override
	public ModalidadeEnum getModalidade() {
		return modalidade;
	}
}