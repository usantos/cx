package br.gov.caixa.loterias.apostas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialNovaModalidadeFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialPassoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnTutorialListener;

public class TutorialMaisMilionariaActivity extends AppCompatActivity implements OnTutorialListener {

	private List<TutorialPassoFragment> fragmentList;

	private TutorialNovaModalidadeFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial_mais_milionaria);

		initFragmentList();
		fragment = TutorialNovaModalidadeFragment.newInstance();
		FragmentUtils.startFragment(getSupportFragmentManager(), R.id.frameTutorial, fragment);
	}

	private void initFragmentList() {
		fragmentList = new ArrayList<>();
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_introducao_tutorial_mais_milionaria));
		fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_final));
	}

	@Override
	public void pular() {
		TestVisao.toast("Pulando");
	}

	@Override
	public void comecar() {
		TestVisao.toast("Comecando");
	}

	@Override
	public List<TutorialPassoFragment> getFragments() {
		if (fragmentList == null){
			initFragmentList();
		}
		return fragmentList;
	}
}