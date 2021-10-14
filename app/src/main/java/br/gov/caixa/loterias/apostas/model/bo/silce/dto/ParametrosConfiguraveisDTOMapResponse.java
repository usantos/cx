package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ParametrosConfiguraveisDTOMapResponse {
    @SerializedName("mapa")
    private ParametrosConfiguraveisDTO mapa = null;

    public ParametrosConfiguraveisDTO getMapa() {
        return mapa;
    }

    public void setMapa(ParametrosConfiguraveisDTO mapa) {
        this.mapa = mapa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParametrosConfiguraveisDTOMapResponse that = (ParametrosConfiguraveisDTOMapResponse) o;
        return Objects.equals(mapa, that.mapa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapa);
    }

    @Override
    public String toString() {
        return "ParametrosConfiguraveisDTOMapResponse{" +
                "mapa=" + mapa +
                '}';
    }
}
