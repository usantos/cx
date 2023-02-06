package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;

public class BiometriaDTO implements Serializable {
	private Long id;
	private String refresh;

	public BiometriaDTO(String refresh) {
		this.refresh = refresh;
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
}
