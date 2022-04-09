package br.gov.caixa.loterias.apostas.view.listener;

import java.util.List;

import br.gov.caixa.loterias.apostas.view.fragment.TutorialPassoFragment;

public interface OnTutorialListener {

	void pular();

	void comecar();

	List<TutorialPassoFragment> getFragments();
}
