package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;

@ApiModel(description = "")
public class RepresaDTOResponse implements Serializable{

    @SerializedName("mensagem")
    protected String mensagem = null;

    @SerializedName("payload")
    protected Boolean payload = false;

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Boolean getPayload() {
        return payload;
    }

    public void setPayload(Boolean payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RepresaDTOResponse represaDTOResponse = (RepresaDTOResponse) o;
        return (this.mensagem == null ? represaDTOResponse.mensagem == null : this.mensagem.equals(represaDTOResponse.mensagem)) &&
                (this.payload == null ? represaDTOResponse.payload == null : this.payload.equals(represaDTOResponse.payload));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.mensagem == null ? 0 : this.mensagem.hashCode());
        result = 31 * result + (this.payload == null ? 0 : this.payload.hashCode());

        return result;
    }

    @Override
    public String toString() {

        String sb = "class RepresaDTOResponse {\n" +
                "  mensagem: " + mensagem + "\n" +
                "  payload: " + payload + "\n" +
                "}\n";
        return sb;
    }

}

