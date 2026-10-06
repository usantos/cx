package br.gov.caixa.loterias.apostas.model.bo.silce.dto;


import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Decimal
 */
@ApiModel(description = "")
public class Decimal {
    @SerializedName("valor")
    private BigDecimal valor = null;

    @SerializedName("valorFormatado")
    private String valorFormatado = null;

    @SerializedName("valorSemSeparador")
    private String valorSemSeparador = null;

    public Decimal valor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    /**
     * Get valor
     * @return valor
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Decimal valorFormatado(String valorFormatado) {
        this.valorFormatado = valorFormatado;
        return this;
    }

    /**
     * Get valorFormatado
     * @return valorFormatado
     **/
    @ApiModelProperty(value = "")
    public String getValorFormatado() {
        return valorFormatado;
    }

    public void setValorFormatado(String valorFormatado) {
        this.valorFormatado = valorFormatado;
    }

    public Decimal valorSemSeparador(String valorSemSeparador) {
        this.valorSemSeparador = valorSemSeparador;
        return this;
    }

    /**
     * Get valorSemSeparador
     * @return valorSemSeparador
     **/
    @ApiModelProperty(value = "")
    public String getValorSemSeparador() {
        return valorSemSeparador;
    }

    public void setValorSemSeparador(String valorSemSeparador) {
        this.valorSemSeparador = valorSemSeparador;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Decimal decimal = (Decimal) o;
        return Objects.equals(this.valor, decimal.valor) &&
                Objects.equals(this.valorFormatado, decimal.valorFormatado) &&
                Objects.equals(this.valorSemSeparador, decimal.valorSemSeparador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, valorFormatado, valorSemSeparador);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Decimal {\n");

        sb.append("    valor: ").append(toIndentedString(valor)).append("\n");
        sb.append("    valorFormatado: ").append(toIndentedString(valorFormatado)).append("\n");
        sb.append("    valorSemSeparador: ").append(toIndentedString(valorSemSeparador)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
