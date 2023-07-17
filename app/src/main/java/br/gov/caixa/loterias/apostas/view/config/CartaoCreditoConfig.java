package br.gov.caixa.loterias.apostas.view.config;

import java.io.Serializable;

public class CartaoCreditoConfig implements Serializable {
	private String nomeMetodo;
	private String numero;
	private String nomeTitular;
	private String mes;
	private String ano;
	private boolean salvar;


	public CartaoCreditoConfig(String nomeMetodo, String numero, String nomeTitular, String mes, String ano, boolean salvar) {
		this.nomeMetodo = nomeMetodo;
		this.numero = numero;
		this.nomeTitular = nomeTitular;
		this.mes = mes;
		this.ano = ano;
		this.salvar = salvar;
	}

	public String getNomeMetodo() {
		return nomeMetodo;
	}

	public String getNumero() {
		return numero;
	}

	public String getNomeTitular() {
		return nomeTitular;
	}

	public String getValidade() {
		return mes + "/" + ano;
	}

	public boolean isSalvar() {
		return salvar;
	}

	public String getAno() {
		return ano;
	}

	public String getMes() {
		return mes;
	}
}
