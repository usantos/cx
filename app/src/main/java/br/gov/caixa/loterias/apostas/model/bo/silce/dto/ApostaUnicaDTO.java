package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class ApostaUnicaDTO implements Serializable {
    @SerializedName("aposta")
    private ApostaDTO apostaDTO;


    public ApostaDTO getApostaDTO() {
        return apostaDTO;
    }

    public void setApostaDTO(ApostaDTO apostaDTO) {
        this.apostaDTO = apostaDTO;
    }
}
