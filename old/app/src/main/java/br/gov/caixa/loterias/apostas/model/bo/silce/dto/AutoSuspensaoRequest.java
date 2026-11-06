package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class AutoSuspensaoRequest {

    @SerializedName("cpfApostador")
    private String cpfApostador = null;
    @SerializedName("periodoSuspensao")
    private Integer periodoSuspensao = null;

    public AutoSuspensaoRequest(String cpfApostador, Integer periodoSuspensao) {
        this.cpfApostador = cpfApostador;
        this.periodoSuspensao = periodoSuspensao;
    }

    public String getCpfApostador() {
        return cpfApostador;
    }

    public void setCpfApostador(String cpfApostador) {
        this.cpfApostador = cpfApostador;
    }

    public Integer getPeriodoSuspensao() {
        return periodoSuspensao;
    }

    public void setPeriodoSuspensao(Integer periodoSuspensao) {
        this.periodoSuspensao = periodoSuspensao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoSuspensaoRequest that = (AutoSuspensaoRequest) o;
        return Objects.equals(cpfApostador, that.cpfApostador) &&
               Objects.equals(periodoSuspensao, that.periodoSuspensao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpfApostador, periodoSuspensao);
    }

    @Override
    public String toString() {
        return "AutoSuspensaoRequest{" +
                "cpfApostador='" + cpfApostador + '\'' +
                ", periodoSuspensao=" + periodoSuspensao +
                '}';
    }
}