package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by cedesbr450 on 15/12/17.
 */
@ApiModel(description = "")
public class LotericaCodigoWrapperResponse {

    @SerializedName("payload")
    private LotericaDTO payload = null;

    /**
     **/
    @ApiModelProperty(value = "")
    public LotericaDTO getPayload() {
        return payload;
    }

    public void setPayload(LotericaDTO payload) {
        this.payload = payload;
    }
}
