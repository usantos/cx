package br.gov.caixa.loterias.apostas.model.bean;


public class PreferenciaNotificacao {
	private String titulo;
	private String descricao;
	private Boolean isAtivo;

	public PreferenciaNotificacao(String titulo, String descricao, Boolean isAtivo) {
		this.titulo = titulo;
		this.descricao = descricao;
		this.isAtivo = isAtivo;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public Boolean getAtivo() {
		return isAtivo;
	}
}
