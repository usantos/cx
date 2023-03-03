package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;

public class BiometriaDTO implements Serializable {
	private Long id;
	private String refresh;
	private String nome;
	private String cpf;

	public BiometriaDTO(String refresh, String nome, String cpf) {
		this.refresh = refresh;
		this.nome = nome;
		this.cpf = cpf;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRefresh() {
		return refresh;
	}

	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
}
