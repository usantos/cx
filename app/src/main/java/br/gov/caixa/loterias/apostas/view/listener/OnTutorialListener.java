package br.gov.caixa.loterias.apostas.view.listener;

import androidx.fragment.app.Fragment;

import java.util.List;

public interface OnTutorialListener {

	void pular();

	void comecar();

	List<Fragment> getFragments();
}
