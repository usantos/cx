package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class AutoavaliacaoDTO {

    @SerializedName("retornoPerguntaResposta")
    private List<AutoavaliacaoPerguntaDTO> retornoPerguntaResposta = null;


    public AutoavaliacaoDTO retornoPerguntaResposta(List<AutoavaliacaoPerguntaDTO> retornoPerguntaResposta) {
        this.retornoPerguntaResposta = retornoPerguntaResposta;
        return this;
    }

    public AutoavaliacaoDTO addRetornoPerguntaRespostaItem(AutoavaliacaoPerguntaDTO retornoPerguntaRespostaItem) {
        if (this.retornoPerguntaResposta == null) {
            this.retornoPerguntaResposta = new ArrayList<AutoavaliacaoPerguntaDTO>();
        }
        this.retornoPerguntaResposta.add(retornoPerguntaRespostaItem);
        return this;
    }

    /**
     * Retorno de perguntas ou respostas
     * @return retornoPerguntaResposta
     **/
    @ApiModelProperty(value = "Retorno de perguntas ou respostas")
    public List<AutoavaliacaoPerguntaDTO> getRetornoPerguntaResposta() {
        return retornoPerguntaResposta;
    }

    public void setRetornoPerguntaResposta(List<AutoavaliacaoPerguntaDTO> retornoPerguntaResposta) {
        this.retornoPerguntaResposta = retornoPerguntaResposta;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AutoavaliacaoDTO autoAvaliacaoDTO = (AutoavaliacaoDTO) o;
        return Objects.equals(this.retornoPerguntaResposta, autoAvaliacaoDTO.retornoPerguntaResposta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retornoPerguntaResposta);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AutoAvaliacaoDTO {\n");

        sb.append("    retornoPerguntaResposta: ").append(toIndentedString(retornoPerguntaResposta)).append("\n");
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