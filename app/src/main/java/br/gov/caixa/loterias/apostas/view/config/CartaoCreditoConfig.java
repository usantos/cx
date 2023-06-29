package br.gov.caixa.loterias.apostas.view.config;

import java.io.Serializable;

public class CartaoCreditoConfig implements Serializable {
	private String nomeMetodo;
	private String numero;
	private String nomeTitular;
	private String validade;


	public CartaoCreditoConfig(String nomeMetodo, String numero, String nomeTitular, String validade) {
		this.nomeMetodo = nomeMetodo;
		this.numero = numero;
		this.nomeTitular = nomeTitular;
		this.validade = validade;
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
		return validade;
	}
}
