package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class SituacoesCompraDTO {

    @SerializedName("id")
    private Long id = null;

    @SerializedName("descricao")
    private String descricao = null;

    /**
     * Id da situação da compra
     * @return id
     */
    @ApiModelProperty("Id da situacao da compra")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Descricao da situação da compra
     * @return descricao
     */
    @ApiModelProperty("Descricao da situacao da compra")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(java.lang.Object o){
        if(this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SituacoesCompraDTO situacoesCompraDTO = (SituacoesCompraDTO) o;

        return Objects.equals(this.id, situacoesCompraDTO.id) &&
                Objects.equals(this.descricao, situacoesCompraDTO.descricao);

    }

    @Override
    public int hashCode(){
        return Objects.hash(id, descricao);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("class SituacoesCompraDTO {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    descricao: ").append(toIndentedString(descricao)).append("\n");
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
