package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

/**
 * Created by joafilho on 16/02/2018.
 * Class DuvidaDTO
 */
@ApiModel(description = "")
public class DuvidaDTO {

    @SerializedName("id")
    private Long id;
    @SerializedName("pergunta")
    private String pergunta;
    @SerializedName("resposta")
    private String resposta;

    public DuvidaDTO() {
    }

    public DuvidaDTO(Long id, String pergunta, String resposta) {
        this.id = id;
        this.pergunta = pergunta;
        this.resposta = resposta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }
}
