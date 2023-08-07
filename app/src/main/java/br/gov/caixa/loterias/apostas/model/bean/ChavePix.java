package br.gov.caixa.loterias.apostas.model.bean;

import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;

public class ChavePix {
	private Long id;
	private ChavePixEnum chave;
	private String value;

	private static int uiId = 0;

	public ChavePix(ChavePixEnum chave, String value) {
		this.chave = chave;
		this.value = value;
		this.id = new Long(uiId++);
	}

	public String getValue() {
		return value;
	}

	public ChavePixEnum getChave(){
		return chave;
	}

	public Long getId() {
		return id;
	}
}
