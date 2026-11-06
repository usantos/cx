package br.gov.caixa.loterias.apostas.view.config;

import java.io.Serializable;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;

public class MeioPagamentoConfig implements Serializable {
	private String titulo;
	private int idCorBarra;
	private int idImagem;
	private DTOEnumLong meioPagamento;
	private List<Integer> bandeirasMeioPagamento;

	private boolean showSeta = false;

	public MeioPagamentoConfig(String titulo, int idCorBarra, int idImagem, DTOEnumLong meioPagamento, boolean showSeta) {
		this.titulo = titulo;
		this.idCorBarra = idCorBarra;
		this.idImagem = idImagem;
		this.meioPagamento = meioPagamento;
		this.showSeta = showSeta;
	}

	public MeioPagamentoConfig(int idImagem, DTOEnumLong meioPagamento, List<Integer> bandeirasMeioPagamento,
							   int idCorBarra) {
		this.idImagem = idImagem;
		this.meioPagamento = meioPagamento;
		this.bandeirasMeioPagamento = bandeirasMeioPagamento;
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

	public List<Integer> getBandeirasMeioPagamento() {
		return bandeirasMeioPagamento;
	}

	public String getTitulo() { 
		return titulo;
	}

	public boolean isShowSeta(){
		return showSeta;
	}
}
