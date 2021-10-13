package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;


@ApiModel(description = "")
public class ParametrosConfiguraveisDTO {

    @SerializedName("mostrar6meses+")
    private Boolean mostrar6meses = null;

    public Boolean getMostrar6meses() {
        return mostrar6meses;
    }

    public void setMostrar6meses(Boolean mostrar6meses) {
        this.mostrar6meses = mostrar6meses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParametrosConfiguraveisDTO that = (ParametrosConfiguraveisDTO) o;
        return Objects.equals(mostrar6meses, that.mostrar6meses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mostrar6meses);
    }
}
