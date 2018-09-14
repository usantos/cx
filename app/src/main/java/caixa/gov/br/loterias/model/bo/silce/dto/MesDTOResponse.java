package caixa.gov.br.loterias.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
@Parcel
public class MesDTOResponse {

    @SerializedName("numero")
    protected Integer numero;

    @SerializedName("nome")
    protected String nome;

    @SerializedName("abreviacao")
    protected String abreviacao;

    public MesDTOResponse() {
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAbreviacao() {
        return abreviacao;
    }

    public void setAbreviacao(String abreviacao) {
        this.abreviacao = abreviacao;
    }
}
