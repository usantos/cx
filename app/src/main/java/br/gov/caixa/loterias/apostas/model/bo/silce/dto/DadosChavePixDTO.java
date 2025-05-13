package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class DadosChavePixDTO implements Serializable {
    @SerializedName("cpfMascarado")
    protected String cpfMascarado = null;
    @SerializedName("nome")
    protected String nome = null;
    @SerializedName("chave")
    protected String chave = null;
    @SerializedName("nomeBanco")
    protected String nomeBanco = null;

    public String getCpfMascarado() {
        return cpfMascarado;
    }

    public void setCpfMascarado(String cpfMascarado) {
        this.cpfMascarado = cpfMascarado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getNomeBanco() {
        return nomeBanco;
    }

    public void setNomeBanco(String nomeBanco) {
        this.nomeBanco = nomeBanco;
    }
}
