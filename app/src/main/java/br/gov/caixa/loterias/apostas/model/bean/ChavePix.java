package br.gov.caixa.loterias.apostas.model.bean;

import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;

public class ChavePix {
	private ChavePixEnum chave;
	private String value;

	public ChavePix(ChavePixEnum chave, String value) {
		this.chave = chave;
		this.value = value;
	}

}
