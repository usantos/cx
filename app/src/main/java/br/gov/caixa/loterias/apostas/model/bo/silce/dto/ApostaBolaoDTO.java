package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.utils.AppUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ApostaBolaoDTO
 */
@ApiModel(description = "")
public class ApostaBolaoDTO<T> implements Serializable {

    @SerializedName("indicadorSurpresinha")
    private Boolean isSurpresinha = null;
    @SerializedName("dezenas")
    private T dezenas = null;
    @SerializedName("trevos")
    private List<Integer> trevos = null;
    @SerializedName("operacaoExecutadaComSucesso")
    private Boolean operacaoExecutadaComSucesso = null;
    @SerializedName("timeDoCoracao")
    private TimeDoCoracaoDTO timeCoracao;
    @SerializedName("mesDeSorte")
    private MesDeSorteDTO mesSorte;
    @SerializedName("partidasLoteca")
    private List<PartidaLotecaDTO> partidasLoteca;

    public Boolean isSurpresinha() {
        return isSurpresinha;
    }

    public void setSurpresinha(Boolean surpresinha) {
        isSurpresinha = surpresinha;
    }

    public List<Integer> getDezenas() {
        return (List<Integer>) dezenas;
    }

    public void setDezenas(T dezenas) {
        this.dezenas = dezenas;
    }

    public List<Integer> getTrevos() {
        return trevos;
    }

    public void setTrevos(List<Integer> trevos) {
        this.trevos = trevos;
    }

    public Boolean isOperacaoExecutadaComSucesso() {
        return operacaoExecutadaComSucesso;
    }

    public void setOperacaoExecutadaComSucesso(Boolean operacaoExecutadaComSucesso) {
        this.operacaoExecutadaComSucesso = operacaoExecutadaComSucesso;
    }

    public ArrayList<ArrayList<Integer>> getMatriz() {
        return AppUtils.converteMatrizInteiros((List<List<Integer>>) dezenas);
    }

    public TimeDoCoracaoDTO getTimeCoracao(){
        return this.timeCoracao;
    }

    public MesDeSorteDTO getMesSorte(){
        return this.mesSorte;
    }

    public List<PartidaLotecaDTO> getPartidasLoteca() {
        return partidasLoteca;
    }

    public void setPartidasLoteca(List<PartidaLotecaDTO> partidasLoteca) {
        this.partidasLoteca = partidasLoteca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApostaBolaoDTO bolaoDTO = (ApostaBolaoDTO) o;
        return Objects.equals(this.isSurpresinha, bolaoDTO.isSurpresinha) &&
                Objects.equals(this.dezenas, bolaoDTO.dezenas) &&
                Objects.equals(this.trevos, bolaoDTO.trevos) &&
                Objects.equals(this.operacaoExecutadaComSucesso, bolaoDTO.operacaoExecutadaComSucesso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSurpresinha, dezenas, trevos, operacaoExecutadaComSucesso);
    }

    @Override
    public String toString() {
        return "ApostaBolaoDTO{" +
                "isSurpresinha=" + isSurpresinha +
                ", dezenas=" + dezenas +
                ", trevos=" + trevos +
                ", operacaoExecutadaComSucesso=" + operacaoExecutadaComSucesso +
                '}';
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}


