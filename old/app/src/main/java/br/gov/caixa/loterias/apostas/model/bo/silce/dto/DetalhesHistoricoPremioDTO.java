package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;

/**
 * Created by joafilho on 19/04/2018.
 * Class DetalhesHistoricoPremioDTO
 */

@ApiModel(description = "")
public class DetalhesHistoricoPremioDTO implements Serializable {
    @SerializedName("aposta")
    protected IdentificaoDeUmaApostaDas8Modalidades aposta = null;
    @SerializedName("premio")
    protected PremioDTO premioDTO = null;

    public IdentificaoDeUmaApostaDas8Modalidades getAposta() {
        return aposta;
    }

    public void setAposta(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        this.aposta = aposta;
    }

    public PremioDTO getPremioDTO() {
        return premioDTO;
    }

    public void setPremioDTO(PremioDTO premioDTO) {
        this.premioDTO = premioDTO;
    }
}
