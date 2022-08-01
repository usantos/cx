package br.gov.caixa.loterias.apostas.view.config;

import java.io.Serializable;

public class MeusCartoesConfig implements Serializable {
	private int idCorBarra;
	private int idImagem;

	public MeusCartoesConfig(int idCorBarra, int idImagem) {
		this.idCorBarra = idCorBarra;
		this.idImagem = idImagem;
	}

	public int getIdCorBarra() {
		return idCorBarra;
	}

	public int getIdImagem() {
		return idImagem;
	}
}
