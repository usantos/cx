package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * RetornoDetalhamentoBolaoApostas
 */
@ApiModel(description = "")
public class RetornoDetalhamentoBolaoApostas {
    @SerializedName("indicadorSurpresinha")
    private Boolean indicadorSurpresinha = null;

    @SerializedName("dezenas")
    private List<Integer> dezenas = null;

    @SerializedName("trevos")
    private List<Integer> trevos = null;

    public RetornoDetalhamentoBolaoApostas indicadorSurpresinha(Boolean indicadorSurpresinha) {
        this.indicadorSurpresinha = indicadorSurpresinha;
        return this;
    }

    /**
     * Get indicadorSurpresinha
     * @return indicadorSurpresinha
     **/
    @ApiModelProperty(value = "")
    public Boolean isIndicadorSurpresinha() {
        return indicadorSurpresinha;
    }

    public void setIndicadorSurpresinha(Boolean indicadorSurpresinha) {
        this.indicadorSurpresinha = indicadorSurpresinha;
    }

    public RetornoDetalhamentoBolaoApostas dezenas(List<Integer> dezenas) {
        this.dezenas = dezenas;
        return this;
    }

    public RetornoDetalhamentoBolaoApostas addDezenasItem(Integer dezenasItem) {
        if (this.dezenas == null) {
            this.dezenas = new ArrayList<Integer>();
        }
        this.dezenas.add(dezenasItem);
        return this;
    }

    /**
     * Get dezenas
     * @return dezenas
     **/
    @ApiModelProperty(value = "")
    public List<Integer> getDezenas() {
        return dezenas;
    }

    public void setDezenas(List<Integer> dezenas) {
        this.dezenas = dezenas;
    }

    public RetornoDetalhamentoBolaoApostas trevos(List<Integer> trevos) {
        this.trevos = trevos;
        return this;
    }

    public RetornoDetalhamentoBolaoApostas addTrevosItem(Integer trevosItem) {
        if (this.trevos == null) {
            this.trevos = new ArrayList<Integer>();
        }
        this.trevos.add(trevosItem);
        return this;
    }

    /**
     * Get trevos
     * @return trevos
     **/
    @ApiModelProperty(value = "")
    public List<Integer> getTrevos() {
        return trevos;
    }

    public void setTrevos(List<Integer> trevos) {
        this.trevos = trevos;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RetornoDetalhamentoBolaoApostas retornoDetalhamentoBolaoApostas = (RetornoDetalhamentoBolaoApostas) o;
        return Objects.equals(this.indicadorSurpresinha, retornoDetalhamentoBolaoApostas.indicadorSurpresinha) &&
                Objects.equals(this.dezenas, retornoDetalhamentoBolaoApostas.dezenas) &&
                Objects.equals(this.trevos, retornoDetalhamentoBolaoApostas.trevos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indicadorSurpresinha, dezenas, trevos);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RetornoDetalhamentoBolaoApostas {\n");

        sb.append("    indicadorSurpresinha: ").append(toIndentedString(indicadorSurpresinha)).append("\n");
        sb.append("    dezenas: ").append(toIndentedString(dezenas)).append("\n");
        sb.append("    trevos: ").append(toIndentedString(trevos)).append("\n");
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

