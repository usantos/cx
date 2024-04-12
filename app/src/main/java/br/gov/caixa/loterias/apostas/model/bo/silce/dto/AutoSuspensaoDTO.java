package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class AutoSuspensaoDTO {

    @SerializedName("apostadorSuspenso")
    private Boolean apostadorSuspenso = null;
    @SerializedName("prazoHorasDias")
    private String prazoHorasDias = null;

    public Boolean getApostadorSuspenso() {
        return apostadorSuspenso;
    }

    public void setApostadorSuspenso(Boolean apostadorSuspenso) {
        this.apostadorSuspenso = apostadorSuspenso;
    }

    public String getPrazoHorasDias() {
        return prazoHorasDias;
    }

    public void setPrazoHorasDias(String prazoHorasDias) {
        this.prazoHorasDias = prazoHorasDias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoSuspensaoDTO that = (AutoSuspensaoDTO) o;
        return Objects.equals(apostadorSuspenso, that.apostadorSuspenso) &&
               Objects.equals(prazoHorasDias, that.prazoHorasDias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apostadorSuspenso, prazoHorasDias);
    }

    @Override
    public String toString() {
        return "AutoSuspensaoDTO{" +
                "apostadorSuspenso=" + apostadorSuspenso +
                ", prazoHorasDias='" + prazoHorasDias + '\'' +
                '}';
    }
}