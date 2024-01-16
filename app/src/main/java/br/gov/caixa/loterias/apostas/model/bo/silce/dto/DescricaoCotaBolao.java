package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class DescricaoCotaBolao implements Serializable {

  @SerializedName("contemResiduo")
  private Boolean contemResiduo = null;

  @SerializedName("listApostasBolao")
  private List<ApostaBolaoDTO> listApostasBolao = null;

  @SerializedName("operacaoExecutadaComSucesso")
  private Boolean operacaoExecutadaComSucesso = null;

  @ApiModelProperty(value = "")
  public Boolean isContemResiduo() {
    return contemResiduo;
  }

  public void setContemResiduo(Boolean contemResiduo) {
    this.contemResiduo = contemResiduo;
  }

  @ApiModelProperty(value = "")
  public List<ApostaBolaoDTO> getListApostasBolao() {
    return listApostasBolao;
  }

  public void setListApostasBolao(List<ApostaBolaoDTO> listApostasBolao) {
    this.listApostasBolao = listApostasBolao;
  }

  @ApiModelProperty(value = "")
  public Boolean isOperacaoExecutadaComSucesso() {
    return operacaoExecutadaComSucesso;
  }

  public void setOperacaoExecutadaComSucesso(Boolean operacaoExecutadaComSucesso) {
    this.operacaoExecutadaComSucesso = operacaoExecutadaComSucesso;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DescricaoCotaBolao descricaoCotaBolao = (DescricaoCotaBolao) o;
    return
            Objects.equals(this.contemResiduo, descricaoCotaBolao.contemResiduo) &&
            Objects.equals(this.listApostasBolao, descricaoCotaBolao.listApostasBolao) &&
            Objects.equals(this.operacaoExecutadaComSucesso, descricaoCotaBolao.operacaoExecutadaComSucesso);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contemResiduo, listApostasBolao, operacaoExecutadaComSucesso);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DescricaoCotaBolao {\n");
    sb.append("    contemResiduo: ").append(toIndentedString(contemResiduo)).append("\n");
    sb.append("    listApostasBolao: ").append(toIndentedString(listApostasBolao)).append("\n");
    sb.append("    operacaoExecutadaComSucesso: ").append(toIndentedString(operacaoExecutadaComSucesso)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}


