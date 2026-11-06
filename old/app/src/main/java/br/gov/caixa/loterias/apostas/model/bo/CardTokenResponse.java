package br.gov.caixa.loterias.apostas.model.bo;

import java.io.Serializable;

public class CardTokenResponse implements Serializable {
	private String id;
	private Boolean luhn_validation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getLuhn_validation() {
		return luhn_validation;
	}

	public void setLuhn_validation(Boolean luhn_validation) {
		this.luhn_validation = luhn_validation;
	}
}
