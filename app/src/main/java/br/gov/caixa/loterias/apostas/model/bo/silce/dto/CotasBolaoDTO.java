package br.gov.caixa.loterias.apostas.model.bo.silce.dto;


import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.utils.SituacaoCotaEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DadosCotaBolao
 */
@ApiModel(description = "")
public class CotasBolaoDTO {

    @SerializedName("index")
    private Integer index = null;
    @SerializedName("codigoBolao")
    private String codigoBolao = null;
    @SerializedName("qtdCotaDigital")
    private Integer qtdCotaDigital = null;
    @SerializedName("qtdCotaTotal")
    private Integer qtdCotaTotal = null;
    @SerializedName("qtdCotaDisponivel")
    private Integer qtdCotaDisponivel = null;
    @SerializedName("qtdCotaFisica")
    private Integer qtdCotaFisica = null;
    @SerializedName("loterica")
    private Long loterica = null;
    @SerializedName("nomeFantasia")
    private String nomeFantasia = null;
    @SerializedName("nomeRazaoSocial")
    private String nomeRazaoSocial = null;
    @SerializedName("numeroUF")
    private Long numeroUF = null;
    @SerializedName("uf")
    private UnidadeFederacaoDTO uf = null;
    @SerializedName("idMunicipio")
    private Long idMunicipio = null;
    @SerializedName("municipio")
    private MunicipioDTO municipio = null;
    @SerializedName("concurso")
    private Integer concurso = null;
    @SerializedName("tipoConcurso")
    private Integer tipoConcurso = null;
    @SerializedName("idModalidade")
    private Integer idModalidade = null;
    @SerializedName("modalidade")
    private ModalidadeEnum modalidade = null;
    @SerializedName("dataSorteio")
    private String dataSorteio = null;
    @SerializedName("horaSorteio")
    private String horaSorteio = null;
    @SerializedName("diaSorteio")
    private String diaSorteio = null;
    @SerializedName("vrPremioEstimado")
    private BigDecimal vrPremioEstimado = null;
    @SerializedName("valorCota")
    private BigDecimal valorCota = null;
    @SerializedName("valorTarifaServico")
    private BigDecimal valorTarifaServico = null;
    @SerializedName("qtdApostas")
    private Integer qtdApostas = null;
    @SerializedName("qtdNumeros")
    private Integer qtdNumeros = null;
    @SerializedName("operacaoExecutadaComSucesso")
    private Boolean operacaoExecutadaComSucesso = null;
    @SerializedName("codigoCota")
    private String codigoCota = null;
    @SerializedName("qtdCotaVendidas")
    private Integer qtdCotaVendidas = null;
    @SerializedName("qtdCotaReservada")
    private Integer qtdCotaReservada = null;
    @SerializedName("qtdCotasBolao")
    private Integer qtdCotasBolao = 1;
    @SerializedName("vrCotaSemTarifa")
    private BigDecimal vrCotaSemTarifa = null;
    @SerializedName("vrCotaComTarifa")
    private BigDecimal vrCotaComTarifa = null;
    @SerializedName("vrTarifaBolao")
    private BigDecimal vrTarifaBolao = null;
    @SerializedName("vrTotalBolaoComTarifa")
    private BigDecimal vrTotalBolaoComTarifa = null;
    @SerializedName("vrTotalBolaoSemTarifa")
    private BigDecimal vrTotalBolaoSemTarifa = null;
    @SerializedName("vrUltimaCotaComTarifa")
    private BigDecimal vrUltimaCotaComTarifa = null;
    @SerializedName("numeroCota")
    private Integer numeroCota = null;
    @SerializedName("dataHoraReserva")
    private String dataHoraReserva = null;
    @SerializedName("dataRegistroBolao")
    private Data dataRegistroBolao = null;
    @SerializedName("horaRegistroBolao")
    private Hora horaRegistroBolao = null;
    @SerializedName("numeroTerminalLoterico")
    private Integer numeroTerminalLoterico = null;
    @SerializedName("apostasBolao")
    private String apostasBolao = null;
    @SerializedName("situacaoCota")
    private SituacaoCotaEnum situacaoCota = null;
    @SerializedName("tempoExpiracao")
    private Integer tempoExpiracao = null;
    @SerializedName("lotericaFavorita")
    private boolean lotericaFavorita = false;
    private Boolean isAbertoParaMostrarDetalhes = Boolean.FALSE;

    public void aumentaQuantidade() {
        if (qtdCotasBolao < qtdCotaDisponivel){
            qtdCotasBolao++;
        }
    }

    public void diminuiQuantidade() {
        if (qtdCotasBolao > 0){
            qtdCotasBolao--;
        }
    }

    /**
     * Get index
     * @return index
     **/
    @ApiModelProperty(value = "")
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Get codigoBolao
     * @return codigoBolao
     **/
    @ApiModelProperty(value = "")
    public String getCodigoBolao() {
        return codigoBolao;
    }

    public void setCodigoBolao(String codigoBolao) {
        this.codigoBolao = codigoBolao;
    }

    /**
     * Get codigoCota
     * @return codigoCota
     **/
    @ApiModelProperty(value = "")
    public String getCodigoCota() {
        return codigoCota;
    }

    public void setCodigoCota(String codigoCota) {
        this.codigoCota = codigoCota;
    }

    /**
     * Get qtdCotaDigital
     * @return qtdCotaDigital
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotaDigital() {
        return qtdCotaDigital;
    }

    public void setQtdCotaDigital(Integer qtdCotaDigital) {
        this.qtdCotaDigital = qtdCotaDigital;
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
     * Get qtdCotaDisponivel
     * @return qtdCotaDisponivel
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotaDisponivel() {
        return qtdCotaDisponivel;
    }

    public void setQtdCotaDisponivel(Integer qtdCotaDisponivel) {
        this.qtdCotaDisponivel = qtdCotaDisponivel;
    }

    /**
     * Get qtdCotaFisica
     * @return qtdCotaFisica
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotaFisica() {
        return qtdCotaFisica;
    }

    public void setQtdCotaFisica(Integer qtdCotaFisica) {
        this.qtdCotaFisica = qtdCotaFisica;
    }

    /**
     * Get qtdCotaVendidas
     * @return qtdCotaVendidas
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotaVendidas() {
        return qtdCotaVendidas;
    }

    public void setQtdCotaVendidas(Integer qtdCotaVendidas) {
        this.qtdCotaVendidas = qtdCotaVendidas;
    }

    /**
     * Get qtdCotaReservada
     * @return qtdCotaReservada
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotaReservada() {
        return qtdCotaReservada;
    }

    public void setQtdCotaReservada(Integer qtdCotaReservada) {
        this.qtdCotaReservada = qtdCotaReservada;
    }

    /**
     * Get qtdCotasBolao
     * @return qtdCotasBolao
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotasBolao() {
        return qtdCotasBolao;
    }

    public void setQtdCotasBolao(Integer qtd) {
        this.qtdCotasBolao = qtd;
    }

    /**
     * Get loterica
     * @return loterica
     **/
    @ApiModelProperty(value = "")
    public Long getLoterica() {
        return loterica;
    }

    public void setLoterica(Long loterica) {
        this.loterica = loterica;
    }

    /**
     * Get nomeFantasia
     * @return nomeFantasia
     **/
    @ApiModelProperty(value = "")
    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    /**
     * Get nomeRazaoSocial
     * @return nomeRazaoSocial
     **/
    @ApiModelProperty(value = "")
    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    /**
     * Get numeroUF
     * @return numeroUF
     **/
    @ApiModelProperty(value = "")
    public Long getNumeroUF() {
        return numeroUF;
    }

    public void setNumeroUF(Long numeroUF) {
        this.numeroUF = numeroUF;
    }

    /**
     * Get uf
     * @return uf
     **/
    @ApiModelProperty(value = "")
    public UnidadeFederacaoDTO getUf() {
        return uf;
    }

    public void setUf(UnidadeFederacaoDTO uf) {
        this.uf = uf;
    }

    /**
     * Get idMunicipio
     * @return idMunicipio
     **/
    @ApiModelProperty(value = "")
    public Long getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Long idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    /**
     * Get municipio
     * @return municipio
     **/
    @ApiModelProperty(value = "")
    public MunicipioDTO getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioDTO municipio) {
        this.municipio = municipio;
    }

    /**
     * Get concurso
     * @return concurso
     **/
    @ApiModelProperty(value = "")
    public Integer getConcurso() {
        return concurso;
    }

    public void setConcurso(Integer concurso) {
        this.concurso = concurso;
    }

    /**
     * Get tipoConcurso
     * @return tipoConcurso
     **/
    @ApiModelProperty(value = "")
    public Integer getTipoConcurso() {
        return tipoConcurso;
    }

    public void setTipoConcurso(Integer tipoConcurso) {
        this.tipoConcurso = tipoConcurso;
    }

    /**
     * Get idModalidade
     * @return idModalidade
     **/
    @ApiModelProperty(value = "")
    public Integer getIdModalidade() {
        return idModalidade;
    }

    public void setIdModalidade(Integer idModalidade) {
        this.idModalidade = idModalidade;
    }

    /**
     * Get modalidade
     * @return modalidade
     **/
    @ApiModelProperty(value = "")
    public ModalidadeEnum getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
    }

    /**
     * Get dataSorteio
     * @return dataSorteio
     **/
    @ApiModelProperty(value = "")
    public String getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(String dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    /**
     * Get horaSorteio
     * @return horaSorteio
     **/
    @ApiModelProperty(value = "")
    public String getHoraSorteio() {
        return horaSorteio;
    }

    public void setHoraSorteio(String horaSorteio) {
        this.horaSorteio = horaSorteio;
    }

    /**
     * Get diaSorteio
     * @return diaSorteio
     **/
    @ApiModelProperty(value = "")
    public String getDiaSorteio() {
        return diaSorteio;
    }

    public void setDiaSorteio(String diaSorteio) {
        this.diaSorteio = diaSorteio;
    }

    /**
     * Get vrPremioEstimado
     * @return vrPremioEstimado
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrPremioEstimado() {
        return vrPremioEstimado;
    }

    public void setVrPremioEstimado(BigDecimal vrPremioEstimado) {
        this.vrPremioEstimado = vrPremioEstimado;
    }

    /**
     * Get vrCotaSemTarifa
     * @return vrCotaSemTarifa
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrCotaSemTarifa() {
        return vrCotaSemTarifa;
    }

    public void setVrCotaSemTarifa(BigDecimal vrCotaSemTarifa) {
        this.vrCotaSemTarifa = vrCotaSemTarifa;
    }

    /**
     * Get vrCotaComTarifa
     * @return vrCotaComTarifa
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrCotaComTarifa() {
        if (vrCotaComTarifa != null){
            return vrCotaComTarifa;
        } else {
            return valorCota.add(valorTarifaServico);
        }
    }

    public void setVrCotaComTarifa(BigDecimal vrCotaComTarifa) {
        this.vrCotaComTarifa = vrCotaComTarifa;
    }

    /**
     * Get valorCota
     * @return valorCota
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getValorCota() {
        return valorCota;
    }

    public void setValorCota(BigDecimal valorCota) {
        this.valorCota = valorCota;
    }

    /**
     * Get valorTarifaServico
     * @return valorTarifaServico
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getValorTarifaServico() {
        return valorTarifaServico;
    }

    public void setValorTarifaServico(BigDecimal valorTarifaServico) {
        this.valorTarifaServico = valorTarifaServico;
    }

    /**
     * Get vrTarifaBolao
     * @return vrTarifaBolao
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrTarifaBolao() {
        return vrTarifaBolao;
    }

    public void setVrTarifaBolao(BigDecimal vrTarifaBolao) {
        this.vrTarifaBolao = vrTarifaBolao;
    }

    /**
     * Get vrTotalBolaoComTarifa
     * @return vrTotalBolaoComTarifa
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrTotalBolaoComTarifa() {
        return vrTotalBolaoComTarifa;
    }

    public void setVrTotalBolaoComTarifa(BigDecimal vrTotalBolaoComTarifa) {
        this.vrTotalBolaoComTarifa = vrTotalBolaoComTarifa;
    }

    /**
     * Get vrTotalBolaoSemTarifa
     * @return vrTotalBolaoSemTarifa
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrTotalBolaoSemTarifa() {
        return vrTotalBolaoSemTarifa;
    }

    public void setVrTotalBolaoSemTarifa(BigDecimal vrTotalBolaoSemTarifa) {
        this.vrTotalBolaoSemTarifa = vrTotalBolaoSemTarifa;
    }

    /**
     * Get qtdApostas
     * @return qtdApostas
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdApostas() {
        return qtdApostas;
    }

    public void setQtdApostas(Integer qtdApostas) {
        this.qtdApostas = qtdApostas;
    }

    /**
     * Get qtdNumeros
     * @return qtdNumeros
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdNumeros() {
        return qtdNumeros;
    }

    public void setQtdNumeros(Integer qtdNumeros) {
        this.qtdNumeros = qtdNumeros;
    }

    /**
     * Get numeroCota
     * @return numeroCota
     **/
    @ApiModelProperty(value = "")
    public Integer getNumeroCota() {
        return numeroCota;
    }

    public void setNumeroCota(Integer numeroCota) {
        this.numeroCota = numeroCota;
    }

    /**
     * Get dataHoraReserva
     * @return dataHoraReserva
     **/
    @ApiModelProperty(value = "")
    public String getDataHoraReserva() {
        return dataHoraReserva;
    }

    public void setDataHoraReserva(String dataHoraReserva) {
        this.dataHoraReserva = dataHoraReserva;
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
     * Get apostasBolao
     * @return apostasBolao
     **/
    @ApiModelProperty(value = "")
    public String getApostasBolao() {
        return apostasBolao;
    }

    public void setApostasBolao(String apostasBolao) {
        this.apostasBolao = apostasBolao;
    }

    /**
     * Get situacaoCota
     * @return situacaoCota
     **/
    @ApiModelProperty(value = "")
    public SituacaoCotaEnum getSituacaoCota() {
        return situacaoCota;
    }

    public void setSituacaoCota(SituacaoCotaEnum situacaoCota) {
        this.situacaoCota = situacaoCota;
    }

    /**
     * Get tempoExpiracao
     * @return tempoExpiracao
     **/
    @ApiModelProperty(value = "")
    public Integer getTempoExpiracao() {
        return tempoExpiracao;
    }

    public void setTempoExpiracao(Integer tempoExpiracao) {
        this.tempoExpiracao = tempoExpiracao;
    }

    /**
     * isLotericaFavorita
     * @return lotericaFavorita
     **/
    public boolean isLotericaFavorita() {
        return lotericaFavorita;
    }

    public void setLotericaFavorita(boolean lotericaFavorita) {
        this.lotericaFavorita = lotericaFavorita;
    }

    /**
     * Get operacaoExecutadaComSucesso
     * @return operacaoExecutadaComSucesso
     **/
    @ApiModelProperty(value = "")
    public Boolean isOperacaoExecutadaComSucesso() {
        return operacaoExecutadaComSucesso;
    }

    public void setOperacaoExecutadaComSucesso(Boolean operacaoExecutadaComSucesso) {
        this.operacaoExecutadaComSucesso = operacaoExecutadaComSucesso;
    }

    public Boolean isAbertoParaMostrarDetalhes() {
        return isAbertoParaMostrarDetalhes;
    }

    public void setAbertoParaMostrarDetalhes(Boolean abertoParaMostrarDetalhes) {
        isAbertoParaMostrarDetalhes = abertoParaMostrarDetalhes;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CotasBolaoDTO dadosCotaBolao = (CotasBolaoDTO) o;
        return Objects.equals(this.index, dadosCotaBolao.index) &&
                Objects.equals(this.codigoBolao, dadosCotaBolao.codigoBolao) &&
                Objects.equals(this.codigoCota, dadosCotaBolao.codigoCota) &&
                Objects.equals(this.qtdCotaDigital, dadosCotaBolao.qtdCotaDigital) &&
                Objects.equals(this.qtdCotaTotal, dadosCotaBolao.qtdCotaTotal) &&
                Objects.equals(this.qtdCotaDisponivel, dadosCotaBolao.qtdCotaDisponivel) &&
                Objects.equals(this.qtdCotaFisica, dadosCotaBolao.qtdCotaFisica) &&
                Objects.equals(this.qtdCotaVendidas, dadosCotaBolao.qtdCotaVendidas) &&
                Objects.equals(this.qtdCotaReservada, dadosCotaBolao.qtdCotaReservada) &&
                Objects.equals(this.loterica, dadosCotaBolao.loterica) &&
                Objects.equals(this.nomeFantasia, dadosCotaBolao.nomeFantasia) &&
                Objects.equals(this.nomeRazaoSocial, dadosCotaBolao.nomeRazaoSocial) &&
                Objects.equals(this.numeroUF, dadosCotaBolao.numeroUF) &&
                Objects.equals(this.uf, dadosCotaBolao.uf) &&
                Objects.equals(this.idMunicipio, dadosCotaBolao.idMunicipio) &&
                Objects.equals(this.municipio, dadosCotaBolao.municipio) &&
                Objects.equals(this.concurso, dadosCotaBolao.concurso) &&
                Objects.equals(this.tipoConcurso, dadosCotaBolao.tipoConcurso) &&
                Objects.equals(this.idModalidade, dadosCotaBolao.idModalidade) &&
                Objects.equals(this.modalidade, dadosCotaBolao.modalidade) &&
                Objects.equals(this.dataSorteio, dadosCotaBolao.dataSorteio) &&
                Objects.equals(this.horaSorteio, dadosCotaBolao.horaSorteio) &&
                Objects.equals(this.diaSorteio, dadosCotaBolao.diaSorteio) &&
                Objects.equals(this.vrPremioEstimado, dadosCotaBolao.vrPremioEstimado) &&
                Objects.equals(this.vrCotaSemTarifa, dadosCotaBolao.vrCotaSemTarifa) &&
                Objects.equals(this.vrCotaComTarifa, dadosCotaBolao.vrCotaComTarifa) &&
                Objects.equals(this.valorCota, dadosCotaBolao.valorCota) &&
                Objects.equals(this.valorTarifaServico, dadosCotaBolao.valorTarifaServico) &&
                Objects.equals(this.vrTarifaBolao, dadosCotaBolao.vrTarifaBolao) &&
                Objects.equals(this.vrTotalBolaoComTarifa, dadosCotaBolao.vrTotalBolaoComTarifa) &&
                Objects.equals(this.vrTotalBolaoSemTarifa, dadosCotaBolao.vrTotalBolaoSemTarifa) &&
                Objects.equals(this.qtdApostas, dadosCotaBolao.qtdApostas) &&
                Objects.equals(this.qtdNumeros, dadosCotaBolao.qtdNumeros) &&
                Objects.equals(this.numeroCota, dadosCotaBolao.numeroCota) &&
                Objects.equals(this.dataHoraReserva, dadosCotaBolao.dataHoraReserva) &&
                Objects.equals(this.dataRegistroBolao, dadosCotaBolao.dataRegistroBolao) &&
                Objects.equals(this.horaRegistroBolao, dadosCotaBolao.horaRegistroBolao) &&
                Objects.equals(this.numeroTerminalLoterico, dadosCotaBolao.numeroTerminalLoterico) &&
                Objects.equals(this.apostasBolao, dadosCotaBolao.apostasBolao) &&
                Objects.equals(this.situacaoCota, dadosCotaBolao.situacaoCota) &&
                Objects.equals(this.tempoExpiracao, dadosCotaBolao.tempoExpiracao) &&
                Objects.equals(this.lotericaFavorita, dadosCotaBolao.lotericaFavorita) &&
                Objects.equals(this.operacaoExecutadaComSucesso, dadosCotaBolao.operacaoExecutadaComSucesso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, codigoBolao, codigoCota,
                            qtdCotaDigital, qtdCotaTotal, qtdCotaDisponivel,
                            qtdCotaFisica, qtdCotaVendidas,
                            qtdCotaReservada, loterica, nomeFantasia,
                            nomeRazaoSocial, numeroUF, uf, idMunicipio, municipio,
                            concurso, tipoConcurso, idModalidade, modalidade, dataSorteio,
                            horaSorteio, diaSorteio, vrPremioEstimado, vrCotaSemTarifa,
                            vrCotaComTarifa, valorCota, valorTarifaServico, vrTarifaBolao,
                            vrTotalBolaoComTarifa, vrTotalBolaoSemTarifa, qtdApostas,
                            numeroCota, dataHoraReserva, dataRegistroBolao, horaRegistroBolao,
                            numeroTerminalLoterico, apostasBolao, situacaoCota, tempoExpiracao, lotericaFavorita, operacaoExecutadaComSucesso);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DadosCotaBolao {\n");

        sb.append("    index: ").append(toIndentedString(index)).append("\n");
        sb.append("    codigoBolao: ").append(toIndentedString(codigoBolao)).append("\n");
        sb.append("    codigoCota: ").append(toIndentedString(codigoCota)).append("\n");
        sb.append("    qtdCotaDigital: ").append(toIndentedString(qtdCotaDigital)).append("\n");
        sb.append("    qtdCotaTotal: ").append(toIndentedString(qtdCotaTotal)).append("\n");
        sb.append("    qtdCotaDisponivel: ").append(toIndentedString(qtdCotaDisponivel)).append("\n");
        sb.append("    qtdCotaFisica: ").append(toIndentedString(qtdCotaFisica)).append("\n");
        sb.append("    qtdCotaVendidas: ").append(toIndentedString(qtdCotaVendidas)).append("\n");
        sb.append("    qtdCotaReservada: ").append(toIndentedString(qtdCotaReservada)).append("\n");
        sb.append("    loterica: ").append(toIndentedString(loterica)).append("\n");
        sb.append("    nomeFantasia: ").append(toIndentedString(nomeFantasia)).append("\n");
        sb.append("    nomeRazaoSocial: ").append(toIndentedString(nomeRazaoSocial)).append("\n");
        sb.append("    numeroUF: ").append(toIndentedString(numeroUF)).append("\n");
        sb.append("    uf: ").append(toIndentedString(uf)).append("\n");
        sb.append("    idMunicipio: ").append(toIndentedString(idMunicipio)).append("\n");
        sb.append("    municipio: ").append(toIndentedString(municipio)).append("\n");
        sb.append("    concurso: ").append(toIndentedString(concurso)).append("\n");
        sb.append("    tipoConcurso: ").append(toIndentedString(tipoConcurso)).append("\n");
        sb.append("    idModalidade: ").append(toIndentedString(idModalidade)).append("\n");
        sb.append("    modalidade: ").append(toIndentedString(modalidade)).append("\n");
        sb.append("    dataSorteio: ").append(toIndentedString(dataSorteio)).append("\n");
        sb.append("    horaSorteio: ").append(toIndentedString(horaSorteio)).append("\n");
        sb.append("    diaSorteio: ").append(toIndentedString(diaSorteio)).append("\n");
        sb.append("    vrPremioEstimado: ").append(toIndentedString(vrPremioEstimado)).append("\n");
        sb.append("    vrCotaSemTarifa: ").append(toIndentedString(vrCotaSemTarifa)).append("\n");
        sb.append("    vrCotaComTarifa: ").append(toIndentedString(vrCotaComTarifa)).append("\n");
        sb.append("    valorCota: ").append(toIndentedString(valorCota)).append("\n");
        sb.append("    valorTarifaServico: ").append(toIndentedString(valorTarifaServico)).append("\n");
        sb.append("    vrTarifaBolao: ").append(toIndentedString(vrTarifaBolao)).append("\n");
        sb.append("    vrTotalBolaoComTarifa: ").append(toIndentedString(vrTotalBolaoComTarifa)).append("\n");
        sb.append("    vrTotalBolaoSemTarifa: ").append(toIndentedString(vrTotalBolaoSemTarifa)).append("\n");
        sb.append("    qtdApostas: ").append(toIndentedString(qtdApostas)).append("\n");
        sb.append("    qtdNumeros: ").append(toIndentedString(qtdNumeros)).append("\n");
        sb.append("    numeroCota: ").append(toIndentedString(numeroCota)).append("\n");
        sb.append("    dataHoraReserva: ").append(toIndentedString(dataHoraReserva)).append("\n");
        sb.append("    dataRegistroBolao: ").append(toIndentedString(dataRegistroBolao)).append("\n");
        sb.append("    horaRegistroBolao: ").append(toIndentedString(horaRegistroBolao)).append("\n");
        sb.append("    numeroTerminalLoterico: ").append(toIndentedString(numeroTerminalLoterico)).append("\n");
        sb.append("    apostasBolao: ").append(toIndentedString(apostasBolao)).append("\n");
        sb.append("    situacaoCota: ").append(toIndentedString(situacaoCota)).append("\n");
        sb.append("    tempoExpiracao: ").append(toIndentedString(tempoExpiracao)).append("\n");
        sb.append("    lotericaFavorita: ").append(toIndentedString(lotericaFavorita)).append("\n");
        sb.append("    operacaoExecutadaComSucesso: ").append(toIndentedString(operacaoExecutadaComSucesso)).append("\n");
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

    public BigDecimal getVrUltimaCotaComTarifa() {
        return vrUltimaCotaComTarifa;
    }
}


