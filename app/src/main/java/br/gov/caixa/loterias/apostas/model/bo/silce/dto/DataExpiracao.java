package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class DataExpiracao {

	@SerializedName("month")
	private int mes;
	@SerializedName("year")
	private int ano;

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DataExpiracao that = (DataExpiracao) o;
		return mes == that.mes && ano == that.ano;
	}

	@Override
	public int hashCode() {
		return Objects.hash(mes, ano);
	}

	@Override
	public String toString() {
		return "DataExpiracao{" +
				"mes=" + mes +
				", ano=" + ano +
				'}';
	}
}
