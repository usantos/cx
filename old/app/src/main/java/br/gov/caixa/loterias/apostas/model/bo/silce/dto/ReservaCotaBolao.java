package br.gov.caixa.loterias.apostas.model.bo.silce.dto;


import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ReservaCotaBolao
 */
@ApiModel(description = "")
public class ReservaCotaBolao {
    @SerializedName("id")
    private Long id = null;
    @SerializedName("mes")
    private Integer mes = null;
    @SerializedName("ano")
    private Integer ano = null;
    @SerializedName("particao")
    private Long particao = null;
    @SerializedName("codBolao")
    private String codBolao = null;
    @SerializedName("codCota")
    private String codCota = null;
    @SerializedName("loterica")
    private Loterica loterica = null;
    @SerializedName("dataHoraReserva")
    private Data dataHoraReserva = null;
    @SerializedName("dataExpiracaoReservaCotaBolao")
    private Data dataExpiracaoReservaCotaBolao = null;
    @SerializedName("horaExpiracaoReservaCotaBolao")
    private Hora horaExpiracaoReservaCotaBolao = null;
    @SerializedName("numeroCotaReservada")
    private Integer numeroCotaReservada = null;
    @SerializedName("qtdCotaTotal")
    private Integer qtdCotaTotal = null;
    @SerializedName("valorCotaReservada")
    private Decimal valorCotaReservada = null;
    @SerializedName("valorTarifaServico")
    private Decimal valorTarifaServico = null;
    @SerializedName("dataRegistroBolao")
    private Data dataRegistroBolao = null;
    @SerializedName("horaRegistroBolao")
    private Hora horaRegistroBolao = null;
    @SerializedName("numeroTerminalLoterico")
    private Integer numeroTerminalLoterico = null;
    @SerializedName("dadosCotaBolao")
    private String dadosCotaBolao = null;
    @SerializedName("situacao")
    private SituacaoReservaCotaBolao situacao = null;
    @SerializedName("dataUltimaSituacao")
    private Data dataUltimaSituacao = null;
    @SerializedName("valorCotaCusteio")
    private Decimal valorCotaCusteio = null;
    @SerializedName("valorTarifaCusteio")
    private Decimal valorTarifaCusteio = null;
    @SerializedName("nsu")
    private Long nsu = null;
    @SerializedName("cpfApostador")
    private String cpfApostador = null;

//    @SerializedName("aposta")
//    private ApostaObject aposta = null;

    @SerializedName("valorTotal")
    private Decimal valorTotal = null;
    @SerializedName("dataHoraExpiracao")
    private Data dataHoraExpiracao = null;


    /**
     * Get id
     * @return id
     **/
    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get mes
     * @return mes
     **/
    @ApiModelProperty(value = "")
    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    /**
     * Get ano
     * @return ano
     **/
    @ApiModelProperty(value = "")
    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    /**
     * Get particao
     * @return particao
     **/
    @ApiModelProperty(value = "")
    public Long getParticao() {
        return particao;
    }

    public void setParticao(Long particao) {
        this.particao = particao;
    }

    /**
     * Get codBolao
     * @return codBolao
     **/
    @ApiModelProperty(value = "")
    public String getCodBolao() {
        return codBolao;
    }

    public void setCodBolao(String codBolao) {
        this.codBolao = codBolao;
    }

    /**
     * Get codCota
     * @return codCota
     **/
    @ApiModelProperty(value = "")
    public String getCodCota() {
        return codCota;
    }

    public void setCodCota(String codCota) {
        this.codCota = codCota;
    }

    /**
     * Get loterica
     * @return loterica
     **/
    @ApiModelProperty(value = "")
    public Loterica getLoterica() {
        return loterica;
    }

    public void setLoterica(Loterica loterica) {
        this.loterica = loterica;
    }

    /**
     * Get dataHoraReserva
     * @return dataHoraReserva
     **/
    @ApiModelProperty(value = "")
    public Data getDataHoraReserva() {
        return dataHoraReserva;
    }

    public void setDataHoraReserva(Data dataHoraReserva) {
        this.dataHoraReserva = dataHoraReserva;
    }

    /**
     * Get dataExpiracaoReservaCotaBolao
     * @return dataExpiracaoReservaCotaBolao
     **/
    @ApiModelProperty(value = "")
    public Data getDataExpiracaoReservaCotaBolao() {
        return dataExpiracaoReservaCotaBolao;
    }

    public void setDataExpiracaoReservaCotaBolao(Data dataExpiracaoReservaCotaBolao) {
        this.dataExpiracaoReservaCotaBolao = dataExpiracaoReservaCotaBolao;
    }

    /**
     * Get horaExpiracaoReservaCotaBolao
     * @return horaExpiracaoReservaCotaBolao
     **/
    @ApiModelProperty(value = "")
    public Hora getHoraExpiracaoReservaCotaBolao() {
        return horaExpiracaoReservaCotaBolao;
    }

    public void setHoraExpiracaoReservaCotaBolao(Hora horaExpiracaoReservaCotaBolao) {
        this.horaExpiracaoReservaCotaBolao = horaExpiracaoReservaCotaBolao;
    }

    /**
     * Get numeroCotaReservada
     * @return numeroCotaReservada
     **/
    @ApiModelProperty(value = "")
    public Integer getNumeroCotaReservada() {
        return numeroCotaReservada;
    }

    public void setNumeroCotaReservada(Integer numeroCotaReservada) {
        this.numeroCotaReservada = numeroCotaReservada;
    }

    /**
     * Get qtdCotaTotal
     * @return qtdCotaTotal
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotaTotal() {
        return qtdCotaTotal;
    }

    public void setQtdCotaTotal(Integer qtdCotaTotal) {
        this.qtdCotaTotal = qtdCotaTotal;
    }

    /**
     * Get valorCotaReservada
     * @return valorCotaReservada
     **/
    @ApiModelProperty(value = "")
    public Decimal getValorCotaReservada() {
        return valorCotaReservada;
    }

    public void setValorCotaReservada(Decimal valorCotaReservada) {
        this.valorCotaReservada = valorCotaReservada;
    }

    /**
     * Get valorTarifaServico
     * @return valorTarifaServico
     **/
    @ApiModelProperty(value = "")
    public Decimal getValorTarifaServico() {
        return valorTarifaServico;
    }

    public void setValorTarifaServico(Decimal valorTarifaServico) {
        this.valorTarifaServico = valorTarifaServico;
    }

    /**
     * Get dataRegistroBolao
     * @return dataRegistroBolao
     **/
    @ApiModelProperty(value = "")
    public Data getDataRegistroBolao() {
        return dataRegistroBolao;
    }

    public void setDataRegistroBolao(Data dataRegistroBolao) {
        this.dataRegistroBolao = dataRegistroBolao;
    }

    /**
     * Get horaRegistroBolao
     * @return horaRegistroBolao
     **/
    @ApiModelProperty(value = "")
    public Hora getHoraRegistroBolao() {
        return horaRegistroBolao;
    }

    public void setHoraRegistroBolao(Hora horaRegistroBolao) {
        this.horaRegistroBolao = horaRegistroBolao;
    }

    /**
     * Get numeroTerminalLoterico
     * @return numeroTerminalLoterico
     **/
    @ApiModelProperty(value = "")
    public Integer getNumeroTerminalLoterico() {
        return numeroTerminalLoterico;
    }

    public void setNumeroTerminalLoterico(Integer numeroTerminalLoterico) {
        this.numeroTerminalLoterico = numeroTerminalLoterico;
    }

    /**
     * Get dadosCotaBolao
     * @return dadosCotaBolao
     **/
    @ApiModelProperty(value = "")
    public String getDadosCotaBolao() {
        return dadosCotaBolao;
    }

    public void setDadosCotaBolao(String dadosCotaBolao) {
        this.dadosCotaBolao = dadosCotaBolao;
    }

    /**
     * Get situacao
     * @return situacao
     **/
    @ApiModelProperty(value = "")
    public SituacaoReservaCotaBolao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoReservaCotaBolao situacao) {
        this.situacao = situacao;
    }

    /**
     * Get dataUltimaSituacao
     * @return dataUltimaSituacao
     **/
    @ApiModelProperty(value = "")
    public Data getDataUltimaSituacao() {
        return dataUltimaSituacao;
    }

    public void setDataUltimaSituacao(Data dataUltimaSituacao) {
        this.dataUltimaSituacao = dataUltimaSituacao;
    }

    /**
     * Get valorCotaCusteio
     * @return valorCotaCusteio
     **/
    @ApiModelProperty(value = "")
    public Decimal getValorCotaCusteio() {
        return valorCotaCusteio;
    }

    public void setValorCotaCusteio(Decimal valorCotaCusteio) {
        this.valorCotaCusteio = valorCotaCusteio;
    }

    /**
     * Get valorTarifaCusteio
     * @return valorTarifaCusteio
     **/
    @ApiModelProperty(value = "")
    public Decimal getValorTarifaCusteio() {
        return valorTarifaCusteio;
    }

    public void setValorTarifaCusteio(Decimal valorTarifaCusteio) {
        this.valorTarifaCusteio = valorTarifaCusteio;
    }

    /**
     * Get nsu
     * @return nsu
     **/
    @ApiModelProperty(value = "")
    public Long getNsu() {
        return nsu;
    }

    public void setNsu(Long nsu) {
        this.nsu = nsu;
    }

    /**
     * Get cpfApostador
     * @return cpfApostador
     **/
    @ApiModelProperty(value = "")
    public String getCpfApostador() {
        return cpfApostador;
    }

    public void setCpfApostador(String cpfApostador) {
        this.cpfApostador = cpfApostador;
    }

    /**
     * Get valorTotal
     * @return valorTotal
     **/
    @ApiModelProperty(value = "")
    public Decimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Decimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * Get dataHoraExpiracao
     * @return dataHoraExpiracao
     **/
    @ApiModelProperty(value = "")
    public Data getDataHoraExpiracao() {
        return dataHoraExpiracao;
    }

    public void setDataHoraExpiracao(Data dataHoraExpiracao) {
        this.dataHoraExpiracao = dataHoraExpiracao;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReservaCotaBolao reservaCotaBolao = (ReservaCotaBolao) o;
        return Objects.equals(this.id, reservaCotaBolao.id) &&
                Objects.equals(this.mes, reservaCotaBolao.mes) &&
                Objects.equals(this.ano, reservaCotaBolao.ano) &&
                Objects.equals(this.particao, reservaCotaBolao.particao) &&
                Objects.equals(this.codBolao, reservaCotaBolao.codBolao) &&
                Objects.equals(this.codCota, reservaCotaBolao.codCota) &&
                Objects.equals(this.loterica, reservaCotaBolao.loterica) &&
                Objects.equals(this.dataHoraReserva, reservaCotaBolao.dataHoraReserva) &&
                Objects.equals(this.dataExpiracaoReservaCotaBolao, reservaCotaBolao.dataExpiracaoReservaCotaBolao) &&
                Objects.equals(this.horaExpiracaoReservaCotaBolao, reservaCotaBolao.horaExpiracaoReservaCotaBolao) &&
                Objects.equals(this.numeroCotaReservada, reservaCotaBolao.numeroCotaReservada) &&
                Objects.equals(this.qtdCotaTotal, reservaCotaBolao.qtdCotaTotal) &&
                Objects.equals(this.valorCotaReservada, reservaCotaBolao.valorCotaReservada) &&
                Objects.equals(this.valorTarifaServico, reservaCotaBolao.valorTarifaServico) &&
                Objects.equals(this.dataRegistroBolao, reservaCotaBolao.dataRegistroBolao) &&
                Objects.equals(this.horaRegistroBolao, reservaCotaBolao.horaRegistroBolao) &&
                Objects.equals(this.numeroTerminalLoterico, reservaCotaBolao.numeroTerminalLoterico) &&
                Objects.equals(this.dadosCotaBolao, reservaCotaBolao.dadosCotaBolao) &&
                Objects.equals(this.situacao, reservaCotaBolao.situacao) &&
                Objects.equals(this.dataUltimaSituacao, reservaCotaBolao.dataUltimaSituacao) &&
                Objects.equals(this.valorCotaCusteio, reservaCotaBolao.valorCotaCusteio) &&
                Objects.equals(this.valorTarifaCusteio, reservaCotaBolao.valorTarifaCusteio) &&
                Objects.equals(this.nsu, reservaCotaBolao.nsu) &&
                Objects.equals(this.cpfApostador, reservaCotaBolao.cpfApostador) &&
//                Objects.equals(this.aposta, reservaCotaBolao.aposta) &&
                Objects.equals(this.valorTotal, reservaCotaBolao.valorTotal) &&
                Objects.equals(this.dataHoraExpiracao, reservaCotaBolao.dataHoraExpiracao);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(id, mes, ano, particao, codBolao, codCota, loterica, dataHoraReserva, dataExpiracaoReservaCotaBolao, horaExpiracaoReservaCotaBolao, numeroCotaReservada, qtdCotaTotal, valorCotaReservada, valorTarifaServico, dataRegistroBolao, horaRegistroBolao, numeroTerminalLoterico, dadosCotaBolao, situacao, dataUltimaSituacao, valorCotaCusteio, valorTarifaCusteio, nsu, cpfApostador, aposta, valorTotal, dataHoraExpiracao);
        return Objects.hash(id, mes, ano, particao, codBolao, codCota, loterica, dataHoraReserva, dataExpiracaoReservaCotaBolao, horaExpiracaoReservaCotaBolao, numeroCotaReservada, qtdCotaTotal, valorCotaReservada, valorTarifaServico, dataRegistroBolao, horaRegistroBolao, numeroTerminalLoterico, dadosCotaBolao, situacao, dataUltimaSituacao, valorCotaCusteio, valorTarifaCusteio, nsu, cpfApostador, valorTotal, dataHoraExpiracao);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReservaCotaBolao {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    mes: ").append(toIndentedString(mes)).append("\n");
        sb.append("    ano: ").append(toIndentedString(ano)).append("\n");
        sb.append("    particao: ").append(toIndentedString(particao)).append("\n");
        sb.append("    codBolao: ").append(toIndentedString(codBolao)).append("\n");
        sb.append("    codCota: ").append(toIndentedString(codCota)).append("\n");
        sb.append("    loterica: ").append(toIndentedString(loterica)).append("\n");
        sb.append("    dataHoraReserva: ").append(toIndentedString(dataHoraReserva)).append("\n");
        sb.append("    dataExpiracaoReservaCotaBolao: ").append(toIndentedString(dataExpiracaoReservaCotaBolao)).append("\n");
        sb.append("    horaExpiracaoReservaCotaBolao: ").append(toIndentedString(horaExpiracaoReservaCotaBolao)).append("\n");
        sb.append("    numeroCotaReservada: ").append(toIndentedString(numeroCotaReservada)).append("\n");
        sb.append("    qtdCotaTotal: ").append(toIndentedString(qtdCotaTotal)).append("\n");
        sb.append("    valorCotaReservada: ").append(toIndentedString(valorCotaReservada)).append("\n");
        sb.append("    valorTarifaServico: ").append(toIndentedString(valorTarifaServico)).append("\n");
        sb.append("    dataRegistroBolao: ").append(toIndentedString(dataRegistroBolao)).append("\n");
        sb.append("    horaRegistroBolao: ").append(toIndentedString(horaRegistroBolao)).append("\n");
        sb.append("    numeroTerminalLoterico: ").append(toIndentedString(numeroTerminalLoterico)).append("\n");
        sb.append("    dadosCotaBolao: ").append(toIndentedString(dadosCotaBolao)).append("\n");
        sb.append("    situacao: ").append(toIndentedString(situacao)).append("\n");
        sb.append("    dataUltimaSituacao: ").append(toIndentedString(dataUltimaSituacao)).append("\n");
        sb.append("    valorCotaCusteio: ").append(toIndentedString(valorCotaCusteio)).append("\n");
        sb.append("    valorTarifaCusteio: ").append(toIndentedString(valorTarifaCusteio)).append("\n");
        sb.append("    nsu: ").append(toIndentedString(nsu)).append("\n");
        sb.append("    cpfApostador: ").append(toIndentedString(cpfApostador)).append("\n");
//        sb.append("    aposta: ").append(toIndentedString(aposta)).append("\n");
        sb.append("    valorTotal: ").append(toIndentedString(valorTotal)).append("\n");
        sb.append("    dataHoraExpiracao: ").append(toIndentedString(dataHoraExpiracao)).append("\n");
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

