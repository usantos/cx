package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

import io.swagger.annotations.ApiModel;

/**
 * ApostaBolaoDTO
 */
@ApiModel(description = "")
public class MesDeSorteDTO implements Serializable {

    @SerializedName("numero")
    private Long numero = null;
    @SerializedName("abreviacao")
    private String  abreviacao = null;
    @SerializedName("nome")
    private String  nome = null;

    public Long getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MesDeSorteDTO time = (MesDeSorteDTO) o;
        return Objects.equals(this.numero, time.numero) &&
                Objects.equals(this.nome, time.nome) &&
                Objects.equals(this.abreviacao, time.abreviacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, nome, abreviacao);
    }

    @Override
    public String toString() {
        return "MesDeSorteDTO{" +
                "numero=" + numero +
                ", abreviacao='" + abreviacao + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}


