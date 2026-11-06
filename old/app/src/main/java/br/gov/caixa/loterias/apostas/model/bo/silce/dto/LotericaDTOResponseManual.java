package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * Created by cedesbr450 on 14/12/17.
 */
@ApiModel(description = "")

public class LotericaDTOResponseManual {
    @SerializedName("lotericas")
    private List<LotericaDTO> lotericas = null;
    @SerializedName("municipio")
    private MunicipioToDelete municipio = null;
    @SerializedName("uf")
    private UnidadeFederacaoToDelete uf = null;
    @SerializedName("bairro")
    private BairroDTO bairro = null;

    public List<LotericaDTO> getLotericas() {
        return lotericas;
    }

    public void setLotericas(List<LotericaDTO> lotericas) {
        this.lotericas = lotericas;
    }

    public MunicipioToDelete getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioToDelete municipio) {
        this.municipio = municipio;
    }

    public UnidadeFederacaoToDelete getUf() {
        return uf;
    }

    public void setUf(UnidadeFederacaoToDelete uf) {
        this.uf = uf;
    }

    public BairroDTO getBairro() {
        return bairro;
    }

    public void setBairro(BairroDTO bairro) {
        this.bairro = bairro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LotericaDTOResponseManual)) return false;

        LotericaDTOResponseManual that = (LotericaDTOResponseManual) o;

        if (lotericas != null ? !lotericas.equals(that.lotericas) : that.lotericas != null)
            return false;
        if (municipio != null ? !municipio.equals(that.municipio) : that.municipio != null)
            return false;
        if (uf != null ? !uf.equals(that.uf) : that.uf != null) return false;
        return bairro != null ? bairro.equals(that.bairro) : that.bairro == null;
    }

    @Override
    public int hashCode() {
        int result = lotericas != null ? lotericas.hashCode() : 0;
        result = 31 * result + (municipio != null ? municipio.hashCode() : 0);
        result = 31 * result + (uf != null ? uf.hashCode() : 0);
        result = 31 * result + (bairro != null ? bairro.hashCode() : 0);
        return result;
    }
}








