package br.gov.caixa.loterias.apostas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialNovaModalidadeFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnTutorialListener;

public class TutorialMaisMilionariaActivity extends AppCompatActivity implements OnTutorialListener {

	private List<Fragment> fragmentList;

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
	}

	@Override
	public void pular() {

	}

	@Override
	public void comecar() {

	}

	@Override
	public List<Fragment> getFragments() {
		if (fragmentList == null){
			initFragmentList();
		}
		return fragmentList;
	}
}