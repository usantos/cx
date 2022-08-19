package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

public class LerQrCodeDTO {

    @SerializedName("nsb")
    protected String nsb = null;

    public String getNsb() {
        return nsb;
    }
    public void setNsb(String nsb) {
        this.nsb = nsb;
    }
}
