package br.gov.caixa.loterias.apostas.view.config;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;

public class MeioPagamentoConfig implements Serializable {
	private int idCorBarra;
	private int idImagem;
	private DTOEnumLong meioPagamento;

	public MeioPagamentoConfig(int idCorBarra, int idImagem, DTOEnumLong meioPagamento) {
		this.idCorBarra = idCorBarra;
		this.idImagem = idImagem;
		this.meioPagamento = meioPagamento;
	}

	public int getIdCorBarra() {
		return idCorBarra;
	}

	public int getIdImagem() {
		return idImagem;
	}

	public DTOEnumLong getMeioPagamento(){
		return meioPagamento;
	}
}
