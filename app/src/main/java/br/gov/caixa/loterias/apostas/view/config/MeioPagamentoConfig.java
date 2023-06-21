package br.gov.caixa.loterias.apostas.view.config;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;

public class MeioPagamentoConfig implements Serializable {
	private int idCorBarra;
	private int idImagem;
	private DTOEnumLong meioPagamento;
	private int idBandeira1;
	private int idBandeira2;
	private int idBandeira3;
	private int idBandeira4;
	private int idBandeira5;
	private int idBandeira6;


	public MeioPagamentoConfig(int idCorBarra, int idImagem, DTOEnumLong meioPagamento) {
		this.idCorBarra = idCorBarra;
		this.idImagem = idImagem;
		this.meioPagamento = meioPagamento;
	}

	public MeioPagamentoConfig(int idImagem, DTOEnumLong meioPagamento, int idBandeira1, int idBandeira2,
							   int idBandeira3, int idBandeira4, int idBandeira5, int idBandeira6,
							   int idCorBarra) {
		this.idImagem = idImagem;
		this.meioPagamento = meioPagamento;
		this.idBandeira1 = idBandeira1;
		this.idBandeira2 = idBandeira2;
		this.idBandeira3 = idBandeira3;
		this.idBandeira4 = idBandeira4;
		this.idBandeira5 = idBandeira5;
		this.idBandeira6 = idBandeira6;
		this.idCorBarra = idCorBarra; //Compatibilidade com dependentes
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

	public int getIdBandeira1() {
		return idBandeira1;
	}

	public int getIdBandeira2() {
		return idBandeira2;
	}

	public int getIdBandeira3() {
		return idBandeira3;
	}

	public int getIdBandeira4() {
		return idBandeira4;
	}

	public int getIdBandeira5() {
		return idBandeira5;
	}

	public int getIdBandeira6() {
		return idBandeira6;
	}
}
