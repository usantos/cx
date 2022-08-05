package br.gov.caixa.loterias.apostas.view.config;

import java.io.Serializable;

public class MeusCartoesConfig implements Serializable {
	private int idCorBarra;
	private int idImagem;
	private int idMensagem;

	public MeusCartoesConfig(int idCorBarra, int idImagem, int idMensagem) {
		this.idCorBarra = idCorBarra;
		this.idImagem = idImagem;
		this.idMensagem = idMensagem;
	}

	public int getIdCorBarra() {
		return idCorBarra;
	}

	public int getIdImagem() {
		return idImagem;
	}

	public int getIdMensagem(){
		return idMensagem;
	}
}
