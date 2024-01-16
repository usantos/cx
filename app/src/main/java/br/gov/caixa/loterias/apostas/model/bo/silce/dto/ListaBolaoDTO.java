package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * BoloesDTO
 */
@ApiModel(description = "")
public class ListaBolaoDTO {

    @SerializedName("totalRegistros")
    private Integer totalRegistros = null;
    @SerializedName("paginaAtual")
    private Integer paginaAtual = null;
    @SerializedName("ultimaPagina")
    private Integer ultimaPagina = null;
    @SerializedName("cotas")
    private List<CotasBolaoDTO> cotas = null;

    /**
     * Cotas disponíveis total
     * @return totalRegistros
     **/
    @ApiModelProperty(value = "Cotas disponíveis total")
    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    /**
     * Cotas disponíveis total
     * @return paginaAtual
     **/
    @ApiModelProperty(value = "Cotas disponíveis total")
    public Integer getPaginaAtual() {
        return paginaAtual;
    }

    public void setPaginaAtual(Integer paginaAtual) {
        this.paginaAtual = paginaAtual;
    }

    /**
     * Cotas disponíveis total
     * @return ultimaPagina
     **/
    @ApiModelProperty(value = "Cotas disponíveis total")
    public Integer getUltimaPagina() {
        return ultimaPagina;
    }

    public void setUltimaPagina(Integer ultimaPagina) {
        this.ultimaPagina = ultimaPagina;
    }

    /**
     * Cotas disponiveis para compra
     * @return cotas
     **/
    @ApiModelProperty(value = "Cotas disponiveis para compra")
    public List<CotasBolaoDTO> getCotas() {
        return cotas;
    }

    public void setCotas(List<CotasBolaoDTO> cotas) {
        this.cotas = cotas;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListaBolaoDTO boloesDTO = (ListaBolaoDTO) o;
        return Objects.equals(this.totalRegistros, boloesDTO.totalRegistros) &&
                Objects.equals(this.paginaAtual, boloesDTO.paginaAtual) &&
                Objects.equals(this.ultimaPagina, boloesDTO.ultimaPagina) &&
                Objects.equals(this.cotas, boloesDTO.cotas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalRegistros, paginaAtual, ultimaPagina, cotas);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BoloesDTO {\n");

        sb.append("    totalRegistros: ").append(toIndentedString(totalRegistros)).append("\n");
        sb.append("    paginaAtual: ").append(toIndentedString(paginaAtual)).append("\n");
        sb.append("    ultimaPagina: ").append(toIndentedString(ultimaPagina)).append("\n");
        sb.append("    cotas: ").append(toIndentedString(cotas)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}


