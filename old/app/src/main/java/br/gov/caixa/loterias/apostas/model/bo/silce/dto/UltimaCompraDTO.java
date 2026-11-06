package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class UltimaCompraDTO implements Serializable {
	private Long id;
	private BigDecimal valor;
	private String dataCompra;

	public UltimaCompraDTO(BigDecimal valor, String data) {
		this.valor = valor;
		this.dataCompra = data;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public String getDataCompra() {
		return dataCompra;
	}
}
