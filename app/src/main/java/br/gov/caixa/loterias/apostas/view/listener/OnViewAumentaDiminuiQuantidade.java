package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;

public interface OnViewAumentaDiminuiQuantidade {

	void atualizaQuantidadeTextView(int quantidade, int maxPermitido);
	void atualizaBotoesQtdCotas(int quantidade, int maxPermitido);
	void atualizaValorTotal(CotasBolaoDTO cotasBolaoDTO);

}
