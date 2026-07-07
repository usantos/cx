package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;

public interface OnEscolheModalidadeDialogListener  {

	void selecionado(Integer tipoConcurso, int teimosinha, Boolean espelho);

}
