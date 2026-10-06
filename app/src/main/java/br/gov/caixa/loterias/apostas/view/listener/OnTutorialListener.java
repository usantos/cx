package br.gov.caixa.loterias.apostas.view.listener;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialPassoFragment;

public interface OnTutorialListener {

	void apostar(Boolean naoVerNovamente);

	void pular(boolean naoVerNovamente);

	List<TutorialPassoFragment> getFragments();

	ModalidadeEnum getModalidade();

}
