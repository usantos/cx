package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public enum TipoEnum {
    NULL,
    @SerializedName("1") @ApiModelProperty(value = "Mensagem de usuário")MENSAGEM_USUARIO,
    @SerializedName("2") @ApiModelProperty(value = "Mensagem de sistema")MENSAGEM_SISTEMA,
    @SerializedName("3") @ApiModelProperty(value = "Erro de validação")ERRO_VALIDACAO;
}
