package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {
	private Long id;
	private String nome;
	private String cpf;

	public UsuarioDTO(String  nome, String cpf) {
		this.nome = nome;
		this.cpf = cpf;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
