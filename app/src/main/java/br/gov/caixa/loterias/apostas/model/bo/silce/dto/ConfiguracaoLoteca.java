package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;

public class ConfiguracaoLoteca implements Serializable {
	private int corTexto;

	public ConfiguracaoLoteca(int corTexto){
		this.corTexto = corTexto;
	}

	public int getCorTexto() {
		return corTexto;
	}

	public void setCorTexto(int corTexto) {
		this.corTexto = corTexto;
	}

}