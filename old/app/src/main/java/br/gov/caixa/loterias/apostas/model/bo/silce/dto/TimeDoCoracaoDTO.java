package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;

/**
 * ApostaBolaoDTO
 */
@ApiModel(description = "")
public class TimeDoCoracaoDTO implements Serializable {

    @SerializedName("numero")
    private Long numero = null;
    @SerializedName("descricaoCurta")
    private String descricaoCurta = null;
    @SerializedName("uf")
    private String  uf = null;
    @SerializedName("numeroPais")
    private Long numeroPais = null;
    @SerializedName("indicadorSelecao")
    private Boolean indicadorSelecao;
    @SerializedName("nome")
    private String  nome;
    @SerializedName("nomeClass")
    private String nomeClass;

    public Long getNumero() {
        return numero;
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public String getUf() {
        return uf;
    }

    public Long getNumeroPais() {
        return numeroPais;
    }

    public Boolean getIndicadorSelecao() {
        return indicadorSelecao;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeClass() {
        return nomeClass;
    }

    public String getNomeComUf(){
        String descricao = "";
        if (nome != null){
            descricao = nome;
        }
        if (uf != null){
            if (descricao.isEmpty()){
                descricao = "-";
            }else {
                descricao = descricao + "-" + uf;
            }
        }
        return descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeDoCoracaoDTO time = (TimeDoCoracaoDTO) o;
        return Objects.equals(this.numero, time.numero) &&
                Objects.equals(this.descricaoCurta, time.descricaoCurta) &&
                Objects.equals(this.uf, time.uf) &&
                Objects.equals(this.indicadorSelecao, time.indicadorSelecao) &&
                Objects.equals(this.nome, time.nome) &&
                Objects.equals(this.nomeClass, time.nomeClass) &&
                Objects.equals(this.numeroPais, time.numeroPais);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, descricaoCurta, uf, numeroPais, indicadorSelecao, nome, nomeClass);
    }

    @Override
    public String toString() {
        return "TimeDoCoracaoDTO{" +
                "numero=" + numero +
                ", descricaoCurta='" + descricaoCurta + '\'' +
                ", uf='" + uf + '\'' +
                ", numeroPais=" + numeroPais +
                ", indicadorSelecao=" + indicadorSelecao +
                ", nome='" + nome + '\'' +
                ", nomeClass='" + nomeClass + '\'' +
                '}';
    }

}


