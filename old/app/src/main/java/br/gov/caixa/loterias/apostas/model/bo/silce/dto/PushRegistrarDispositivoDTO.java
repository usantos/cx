package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects;
import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class PushRegistrarDispositivoDTO {
    @SerializedName("plataforma")
    private String plataforma = null;
    @SerializedName("identificadores")
    private List<Long> identificadores = null;
    @SerializedName("canal")
    private String canal = null;
    @SerializedName("token")
    private String token = null;

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public List<Long> getIdentificadores() {
        return identificadores;
    }

    public void setIdentificadores(List<Long> identificadores) {
        this.identificadores = identificadores;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PushRegistrarDispositivoDTO that = (PushRegistrarDispositivoDTO) o;
        return Objects.equals(plataforma, that.plataforma) && Objects.equals(identificadores, that.identificadores) && Objects.equals(canal, that.canal) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plataforma, identificadores, canal, token);
    }

    @Override
    public String toString() {
        return "PushRegistrarTokenDTO{" +
                "plataforma='" + plataforma + '\'' +
                ", identificadores=" + identificadores +
                ", canal='" + canal + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
