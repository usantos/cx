package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**DTO
 */
@ApiModel(description = "")
public class CotaDTO implements Serializable {
    @SerializedName("situacaoReserva")
    private SituacaoReservaCotaBolao situacaoReserva = null;
    @SerializedName("numeroLoterica")
    private LotericaDTO numeroLoterica = null;
    @SerializedName("codigoBolaoReserva")
    private String codigoBolaoReserva = null;
    @SerializedName("dataHoraReserva")
    private String dataHoraReserva = null;
    @SerializedName("dataHoraAtual")
    private String dataHoraAtual = null;
    @SerializedName("dataRegistroBolao")
    private String dataRegistroBolao = null;
    @SerializedName("horaRegistroBolao")
    private String horaRegistroBolao = null;
    @SerializedName("dataHoraExpiracaoReserva")
    private String dataHoraExpiracaoReserva = null;
    @SerializedName("numeroCotaReservada")
    private Integer numeroCotaReservada = null;
    @SerializedName("qtdCotaTotalBolao")
    private Integer qtdCotaTotalBolao = null;
    @SerializedName("vrCotaReservada")
    private BigDecimal vrCotaReservada = null;
    @SerializedName("vrTarifaServico")
    private BigDecimal vrTarifaServico = null;
    @SerializedName("vrTotalCota")
    private BigDecimal vrTotalCota = null;
    @SerializedName("numeroTerminalLoterico")
    private Integer numeroTerminalLoterico = null;
    @SerializedName("dataHoraAlteracaoSituacao")
    private String dataHoraAlteracaoSituacao = null;
    @SerializedName("descricaoCotaBolao")
    private DescricaoCotaBolao descricaoCotaBolao = null;


    @ApiModelProperty(value = "Situação da reserva")
    public SituacaoReservaCotaBolao getSituacaoReserva() {
        return situacaoReserva;
    }

    public void setSituacaoReserva(SituacaoReservaCotaBolao situacaoReserva) {
        this.situacaoReserva = situacaoReserva;
    }

    @ApiModelProperty(value = "Número loterica")
    public LotericaDTO getNumeroLoterica() {
        return numeroLoterica;
    }

    public void setNumeroLoterica(LotericaDTO numeroLoterica) {
        this.numeroLoterica = numeroLoterica;
    }

    @ApiModelProperty(value = "Código bolão reserva (id do bolão)")
    public String getCodigoBolaoReserva() {
        return codigoBolaoReserva;
    }

    public void setCodigoBolaoReserva(String codigoBolaoReserva) {
        this.codigoBolaoReserva = codigoBolaoReserva;
    }

    @ApiModelProperty(value = "Data e hora da reserva")
    public String getDataHoraReserva() {
        return dataHoraReserva;
    }

    public void setDataHoraReserva(String dataHoraReserva) {
        this.dataHoraReserva = dataHoraReserva;
    }

    public String getDataHoraAtual() {
        return dataHoraAtual;
    }

    public void setDataHoraAtual(String dataHoraAtual) {
        this.dataHoraAtual = dataHoraAtual;
    }

    public String getDataRegistroBolao() {
        return dataRegistroBolao;
    }

    public void setDataRegistroBolao(String dataRegistroBolao) {
        this.dataRegistroBolao = dataRegistroBolao;
    }

    public String getHoraRegistroBolao() {
        return horaRegistroBolao;
    }

    public void setHoraRegistroBolao(String horaRegistroBolao) {
        this.horaRegistroBolao = horaRegistroBolao;
    }

    @ApiModelProperty(value = "Data e hora da expiração da reserva")
    public String getDataHoraExpiracaoReserva() {
        return dataHoraExpiracaoReserva;
    }

    public void setDataHoraExpiracaoReserva(String dataHoraExpiracaoReserva) {
        this.dataHoraExpiracaoReserva = dataHoraExpiracaoReserva;
    }

    @ApiModelProperty(value = "Número de cota reservada")
    public Integer getNumeroCotaReservada() {
        return numeroCotaReservada;
    }

    public void setNumeroCotaReservada(Integer numeroCotaReservada) {
        this.numeroCotaReservada = numeroCotaReservada;
    }

    @ApiModelProperty(value = "Quantidade de cota total do bolão")
    public Integer getQtdCotaTotalBolao() {
        return qtdCotaTotalBolao;
    }

    public void setQtdCotaTotalBolao(Integer qtdCotaTotalBolao) {
        this.qtdCotaTotalBolao = qtdCotaTotalBolao;
    }

    @ApiModelProperty(value = "Valor da cota reservada")
    public BigDecimal getVrCotaReservada() {
        return vrCotaReservada;
    }

    public void setVrCotaReservada(BigDecimal vrCotaReservada) {
        this.vrCotaReservada = vrCotaReservada;
    }

    @ApiModelProperty(value = "Valor tarifa de serviço")
    public BigDecimal getVrTarifaServico() {
        return vrTarifaServico;
    }

    public void setVrTarifaServico(BigDecimal vrTarifaServico) {
        this.vrTarifaServico = vrTarifaServico;
    }

    @ApiModelProperty(value = "Valor total da cota")
    public BigDecimal getVrTotalCota() {
        return vrTotalCota;
    }

    public void setVrTotalCota(BigDecimal vrTotalCota) {
        this.vrTotalCota = vrTotalCota;
    }

    @ApiModelProperty(value = "Número terminal loterico")
    public Integer getNumeroTerminalLoterico() {
        return numeroTerminalLoterico;
    }

    public void setNumeroTerminalLoterico(Integer numeroTerminalLoterico) {
        this.numeroTerminalLoterico = numeroTerminalLoterico;
    }

    @ApiModelProperty(value = "Data e hora da ultima alteração de situação")
    public String getDataHoraAlteracaoSituacao() {
        return dataHoraAlteracaoSituacao;
    }

    public void setDataHoraAlteracaoSituacao(String dataHoraAlteracaoSituacao) {
        this.dataHoraAlteracaoSituacao = dataHoraAlteracaoSituacao;
    }

    @ApiModelProperty(value = "Descrição da cota de bolão")
    public DescricaoCotaBolao getDescricaoCotaBolao() {
        return descricaoCotaBolao;
    }

    public void setDescricaoCotaBolao(DescricaoCotaBolao descricaoCotaBolao) {
        this.descricaoCotaBolao = descricaoCotaBolao;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CotaDTO cotaDTO = (CotaDTO) o;
        return Objects.equals(this.situacaoReserva, cotaDTO.situacaoReserva) &&
                Objects.equals(this.numeroLoterica, cotaDTO.numeroLoterica) &&
                Objects.equals(this.codigoBolaoReserva, cotaDTO.codigoBolaoReserva) &&
                Objects.equals(this.dataHoraReserva, cotaDTO.dataHoraReserva) &&
                Objects.equals(this.dataHoraAtual, cotaDTO.dataHoraAtual) &&
                Objects.equals(this.dataRegistroBolao, cotaDTO.dataRegistroBolao) &&
                Objects.equals(this.horaRegistroBolao, cotaDTO.horaRegistroBolao) &&
                Objects.equals(this.dataHoraExpiracaoReserva, cotaDTO.dataHoraExpiracaoReserva) &&
                Objects.equals(this.numeroCotaReservada, cotaDTO.numeroCotaReservada) &&
                Objects.equals(this.qtdCotaTotalBolao, cotaDTO.qtdCotaTotalBolao) &&
                Objects.equals(this.vrCotaReservada, cotaDTO.vrCotaReservada) &&
                Objects.equals(this.vrTarifaServico, cotaDTO.vrTarifaServico) &&
                Objects.equals(this.vrTotalCota, cotaDTO.vrTotalCota) &&
                Objects.equals(this.numeroTerminalLoterico, cotaDTO.numeroTerminalLoterico) &&
                Objects.equals(this.dataHoraAlteracaoSituacao, cotaDTO.dataHoraAlteracaoSituacao) &&
                Objects.equals(this.descricaoCotaBolao, cotaDTO.descricaoCotaBolao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(situacaoReserva, numeroLoterica, codigoBolaoReserva, dataHoraAtual, dataRegistroBolao, horaRegistroBolao, dataHoraExpiracaoReserva, numeroCotaReservada, qtdCotaTotalBolao, vrCotaReservada, vrTarifaServico, vrTotalCota, numeroTerminalLoterico, dataHoraAlteracaoSituacao, descricaoCotaBolao);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CotaDTO {\n");
        sb.append("    situacaoReserva: ").append(toIndentedString(situacaoReserva)).append("\n");
        sb.append("    numeroLoterica: ").append(toIndentedString(numeroLoterica)).append("\n");
        sb.append("    codigoBolaoReserva: ").append(toIndentedString(codigoBolaoReserva)).append("\n");
        sb.append("    dataHoraReserva: ").append(toIndentedString(dataHoraReserva)).append("\n");
        sb.append("    dataHoraAtual: ").append(toIndentedString(dataHoraAtual)).append("\n");
        sb.append("    dataRegistroBolao: ").append(toIndentedString(dataRegistroBolao)).append("\n");
        sb.append("    horaRegistroBolao: ").append(toIndentedString(horaRegistroBolao)).append("\n");
        sb.append("    dataHoraExpiracaoReserva: ").append(toIndentedString(dataHoraExpiracaoReserva)).append("\n");
        sb.append("    numeroCotaReservada: ").append(toIndentedString(numeroCotaReservada)).append("\n");
        sb.append("    qtdCotaTotalBolao: ").append(toIndentedString(qtdCotaTotalBolao)).append("\n");
        sb.append("    vrCotaReservada: ").append(toIndentedString(vrCotaReservada)).append("\n");
        sb.append("    vrTarifaServico: ").append(toIndentedString(vrTarifaServico)).append("\n");
        sb.append("    vrTotalCota: ").append(toIndentedString(vrTotalCota)).append("\n");
        sb.append("    numeroTerminalLoterico: ").append(toIndentedString(numeroTerminalLoterico)).append("\n");
        sb.append("    dataHoraAlteracaoSituacao: ").append(toIndentedString(dataHoraAlteracaoSituacao)).append("\n");
        sb.append("    descricaoCotaBolao: ").append(toIndentedString(descricaoCotaBolao)).append("\n");
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


