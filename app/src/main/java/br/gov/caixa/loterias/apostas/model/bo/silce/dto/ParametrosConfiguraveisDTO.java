package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;


@ApiModel(description = "")
public class ParametrosConfiguraveisDTO {

    @SerializedName("mostrar6meses+")
    private Boolean mostrar6meses = null;
    @SerializedName("hostMicroServicoConferenciaAposta")
    private String urlBase;
    @SerializedName("habilitarConferenciaAutomaticaApostas")
    private Boolean isMicroServico;

    public Boolean getMicroServico() {
        return isMicroServico;
    }

    public void setMicroServico(Boolean microServico) {
        isMicroServico = microServico;
    }

    public Boolean getMostrar6meses() {
        return mostrar6meses;
    }

    public void setMostrar6meses(Boolean mostrar6meses) {
        this.mostrar6meses = mostrar6meses;
    }

    public String getUrlBase(){
        return urlBase;
    }

    public void setUrlBase(String urlBase){
        this.urlBase = urlBase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ParametrosConfiguraveisDTO that = (ParametrosConfiguraveisDTO) o;
        return Objects.equals(mostrar6meses, that.mostrar6meses) &&
                Objects.equals(urlBase, that.urlBase) &&
                Objects.equals(isMicroServico, that.isMicroServico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mostrar6meses, urlBase, isMicroServico);
    }
}
