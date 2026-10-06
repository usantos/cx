package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class CompraAsyncDTO implements Serializable {

    @SerializedName("id")
    protected Long id = null;

    @SerializedName("periodicidade")
    protected Long periodicidade = null;

    @SerializedName("repeticoes")
    protected Long repeticoes = null;

    @SerializedName("idSituacaoCompra")
    private Integer idSituacaoCompra = null;

    /**
     **/
    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    public Long getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(Long periodicidade) {
        this.periodicidade = periodicidade;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    public Long getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(Long repeticoes) {
        this.repeticoes = repeticoes;
    }

    public Integer getIdSituacaoCompra() {
        return idSituacaoCompra;
    }

    public void setIdSituacaoCompra(Integer idSituacaoCompra) {
        this.idSituacaoCompra = idSituacaoCompra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CompraAsyncDTO that = (CompraAsyncDTO) o;
        return id.equals(that.id) &&
                periodicidade.equals(that.periodicidade) &&
                repeticoes.equals(that.repeticoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, periodicidade, repeticoes);
    }

    @Override
    public String toString() {
        return "class CompraAsyncDTO {\n" +
                "id=" + id +
                "\n, periodicidade=" + periodicidade +
                "\n, repeticoes=" + repeticoes +
                "\n}\n";
    }
}
