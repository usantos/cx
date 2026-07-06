package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;

public class ConfiguracaoLoteca implements Serializable {
	private int corTexto;
	private int corTextoSelecionado;
	private int backgroundSelecionado;

	public ConfiguracaoLoteca(int corTexto){
		this.corTexto = corTexto;
	}

	public ConfiguracaoLoteca(int corTexto, int corTextoSelecionado){
		this.corTexto = corTexto;
		this.corTextoSelecionado = corTextoSelecionado;
	}

	public ConfiguracaoLoteca(int corTexto, int corTextoSelecionado, int backgroundSelecionado) {
		this.corTexto = corTexto;
		this.corTextoSelecionado = corTextoSelecionado;
		this.backgroundSelecionado = backgroundSelecionado;
	}

	public int getCorTextoSelecionado() {
		return corTextoSelecionado;
	}

	public int getBackgroundSelecionado() {
		return backgroundSelecionado;
	}

	public int getCorTexto() {
		return corTexto;
	}

}