package br.gov.caixa.loterias.apostas.model.bo.silce.dto;


import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.utils.SituacaoCotaEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DadosCotaBolao
 */
@ApiModel(description = "")
public class DetalheBolaoDTO {

    @SerializedName("qtdCotaDigital")
    private Integer qtdCotaDigital = null;
    @SerializedName("qtdCotaTotal")
    private Integer qtdCotaTotal = null;
    @SerializedName("qtdCotaDisponivel")
    private Integer qtdCotaDisponivel = null;
    @SerializedName("loterica")
    private Long loterica = null;
    @SerializedName("nomeFantasia")
    private String nomeFantasia = null;
    @SerializedName("nomeRazaoSocial")
    private String nomeRazaoSocial = null;
    @SerializedName("uf")
    private UnidadeFederacaoDTO uf = null;
    @SerializedName("municipioCota")
    private MunicipioDTO municipio = null;
    @SerializedName("concurso")
    private Integer concurso = null;
    @SerializedName("modalidade")
    private ModalidadeEnum modalidade = null;
    @SerializedName("dataSorteio")
    private String dataSorteio = null;
    @SerializedName("diaSorteio")
    private String diaSorteio = null;
    @SerializedName("horaSorteio")
    private String horaSorteio = null;
    @SerializedName("vrPremioEstimado")
    private BigDecimal vrPremioEstimado = null;
    @SerializedName("vrTarifaServicoCota")
    private BigDecimal valorTarifaServico = null;
    @SerializedName("qtdApostas")
    private Integer qtdApostas = null;
    @SerializedName("qtdNumeros")
    private Integer qtdNumeros = null;
    @SerializedName("qtdCotaFisica")
    private Integer qtdCotaFisica = null;
    @SerializedName("qtdCotaBaixadasImpressas")
    private Integer qtdCotaBaixadasImpressas = null;
    @SerializedName("qtdCotaVendidas")
    private Integer qtdCotaVendidas = null;
    @SerializedName("qtdCotaReservada")
    private Integer qtdCotaReservada = null;
    @SerializedName("")
    private Integer qtdCotaDesejada = 1;
    @SerializedName("qtdCotaBaixada")
    private Integer qtdCotaBaixada = null;
    @SerializedName("tipoConcurso")
    private Integer tipoConcurso = null;
    @SerializedName("vrCotaSemTarifa")
    private BigDecimal vrCotaSemTarifa = null;
    @SerializedName("vrCotaComTarifa")
    private BigDecimal vrCotaComTarifa = null;
    @SerializedName("vrUltimaCotaComTarifa")
    private BigDecimal vrUltimaCotaComTarifa = null;
    @SerializedName("vrUltimaCotaSemTarifa")
    private BigDecimal vrUltimaCotaSemTarifa;
    @SerializedName("vrTarifaBolao")
    private BigDecimal vrTarifaBolao = null;
    @SerializedName("vrTotalBolaoComTarifa")
    private BigDecimal vrTotalBolaoComTarifa = null;
    @SerializedName("vrTotalBolaoSemTarifa")
    private BigDecimal vrTotalBolaoSemTarifa = null;
    @SerializedName("vrTarifaServicoUltimaCota")
    private BigDecimal vrTarifaServicoUltimaCota;
    @SerializedName("apostas")
    private List<ApostaBolaoDTO> apostas;
    @SerializedName("lotericaFormatada")
    private LotericaDTO lotericaFormatada = null;

    public void aumentaQuantidade() {
        if (qtdCotaDesejada < qtdCotaDisponivel){
            qtdCotaDesejada++;
        }
    }

    public void diminuiQuantidade() {
        if (qtdCotaDesejada > 0){
            qtdCotaDesejada--;
        }
    }

    public List<ApostaBolaoDTO> getApostas() {
        return apostas;
    }

    public void setApostas(List<ApostaBolaoDTO> apostas) {
        this.apostas = apostas;
    }

    /**
     * Get lotericaFormatada
     * @return lotericaFormatada
     **/
    public LotericaDTO getLotericaFormatada() {
        return lotericaFormatada;
    }

    public void setLotericaFormatada(LotericaDTO lotericaFormatada) {
        this.lotericaFormatada = lotericaFormatada;
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

    public Integer getQtdCotaDesejada() {
        return qtdCotaDesejada;
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
        return vrCotaComTarifa;
    }

    public void setVrCotaComTarifa(BigDecimal vrCotaComTarifa) {
        this.vrCotaComTarifa = vrCotaComTarifa;
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

    public BigDecimal getVrTarifaServicoUltimaCota() {
        return vrTarifaServicoUltimaCota;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DetalheBolaoDTO dadosCotaBolao = (DetalheBolaoDTO) o;
        return Objects.equals(this.qtdCotaDigital, dadosCotaBolao.qtdCotaDigital) &&
                Objects.equals(this.qtdCotaTotal, dadosCotaBolao.qtdCotaTotal) &&
                Objects.equals(this.qtdCotaDisponivel, dadosCotaBolao.qtdCotaDisponivel) &&
                Objects.equals(this.qtdCotaFisica, dadosCotaBolao.qtdCotaFisica) &&
                Objects.equals(this.qtdCotaVendidas, dadosCotaBolao.qtdCotaVendidas) &&
                Objects.equals(this.qtdCotaReservada, dadosCotaBolao.qtdCotaReservada) &&
                Objects.equals(this.loterica, dadosCotaBolao.loterica) &&
                Objects.equals(this.nomeFantasia, dadosCotaBolao.nomeFantasia) &&
                Objects.equals(this.nomeRazaoSocial, dadosCotaBolao.nomeRazaoSocial) &&
                Objects.equals(this.uf, dadosCotaBolao.uf) &&
                Objects.equals(this.municipio, dadosCotaBolao.municipio) &&
                Objects.equals(this.concurso, dadosCotaBolao.concurso) &&
                Objects.equals(this.tipoConcurso, dadosCotaBolao.tipoConcurso) &&
                Objects.equals(this.modalidade, dadosCotaBolao.modalidade) &&
                Objects.equals(this.dataSorteio, dadosCotaBolao.dataSorteio) &&
                Objects.equals(this.horaSorteio, dadosCotaBolao.horaSorteio) &&
                Objects.equals(this.diaSorteio, dadosCotaBolao.diaSorteio) &&
                Objects.equals(this.vrPremioEstimado, dadosCotaBolao.vrPremioEstimado) &&
                Objects.equals(this.vrCotaSemTarifa, dadosCotaBolao.vrCotaSemTarifa) &&
                Objects.equals(this.vrCotaComTarifa, dadosCotaBolao.vrCotaComTarifa) &&
                Objects.equals(this.valorTarifaServico, dadosCotaBolao.valorTarifaServico) &&
                Objects.equals(this.vrTarifaBolao, dadosCotaBolao.vrTarifaBolao) &&
                Objects.equals(this.vrTotalBolaoComTarifa, dadosCotaBolao.vrTotalBolaoComTarifa) &&
                Objects.equals(this.vrTotalBolaoSemTarifa, dadosCotaBolao.vrTotalBolaoSemTarifa) &&
                Objects.equals(this.qtdApostas, dadosCotaBolao.qtdApostas) &&
                Objects.equals(this.qtdNumeros, dadosCotaBolao.qtdNumeros)&&
                Objects.equals(this.apostas, dadosCotaBolao.apostas) &&
                Objects.equals(this.lotericaFormatada, dadosCotaBolao.lotericaFormatada);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qtdCotaDigital, qtdCotaTotal, qtdCotaDisponivel,
                            qtdCotaFisica, qtdCotaVendidas,
                            qtdCotaReservada, loterica, nomeFantasia,
                            nomeRazaoSocial, uf, municipio,
                            concurso, tipoConcurso, modalidade, dataSorteio,
                            horaSorteio, diaSorteio, vrPremioEstimado, vrCotaSemTarifa,
                            vrCotaComTarifa, valorTarifaServico, vrTarifaBolao,
                            vrTotalBolaoComTarifa, vrTotalBolaoSemTarifa, qtdApostas, apostas, lotericaFormatada);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DadosCotaBolao {\n");
        sb.append("    qtdCotaDigital: ").append(toIndentedString(qtdCotaDigital)).append("\n");
        sb.append("    qtdCotaTotal: ").append(toIndentedString(qtdCotaTotal)).append("\n");
        sb.append("    qtdCotaDisponivel: ").append(toIndentedString(qtdCotaDisponivel)).append("\n");
        sb.append("    qtdCotaFisica: ").append(toIndentedString(qtdCotaFisica)).append("\n");
        sb.append("    qtdCotaVendidas: ").append(toIndentedString(qtdCotaVendidas)).append("\n");
        sb.append("    qtdCotaReservada: ").append(toIndentedString(qtdCotaReservada)).append("\n");
        sb.append("    loterica: ").append(toIndentedString(loterica)).append("\n");
        sb.append("    nomeFantasia: ").append(toIndentedString(nomeFantasia)).append("\n");
        sb.append("    nomeRazaoSocial: ").append(toIndentedString(nomeRazaoSocial)).append("\n");
        sb.append("    uf: ").append(toIndentedString(uf)).append("\n");
        sb.append("    municipio: ").append(toIndentedString(municipio)).append("\n");
        sb.append("    concurso: ").append(toIndentedString(concurso)).append("\n");
        sb.append("    tipoConcurso: ").append(toIndentedString(tipoConcurso)).append("\n");
        sb.append("    modalidade: ").append(toIndentedString(modalidade)).append("\n");
        sb.append("    dataSorteio: ").append(toIndentedString(dataSorteio)).append("\n");
        sb.append("    horaSorteio: ").append(toIndentedString(horaSorteio)).append("\n");
        sb.append("    diaSorteio: ").append(toIndentedString(diaSorteio)).append("\n");
        sb.append("    vrPremioEstimado: ").append(toIndentedString(vrPremioEstimado)).append("\n");
        sb.append("    vrCotaSemTarifa: ").append(toIndentedString(vrCotaSemTarifa)).append("\n");
        sb.append("    vrCotaComTarifa: ").append(toIndentedString(vrCotaComTarifa)).append("\n");
        sb.append("    valorTarifaServico: ").append(toIndentedString(valorTarifaServico)).append("\n");
        sb.append("    vrTarifaBolao: ").append(toIndentedString(vrTarifaBolao)).append("\n");
        sb.append("    vrTotalBolaoComTarifa: ").append(toIndentedString(vrTotalBolaoComTarifa)).append("\n");
        sb.append("    vrTotalBolaoSemTarifa: ").append(toIndentedString(vrTotalBolaoSemTarifa)).append("\n");
        sb.append("    qtdApostas: ").append(toIndentedString(qtdApostas)).append("\n");
        sb.append("    qtdNumeros: ").append(toIndentedString(qtdNumeros)).append("\n");
        sb.append("    apostas: ").append(toIndentedString(apostas)).append("\n");
        sb.append("    lotericaFormatada: ").append(toIndentedString(lotericaFormatada)).append("\n");
        sb.append("}");
        return sb.toString();
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

    public BigDecimal getVrUltimaCotaComTarifa() {
        return vrUltimaCotaComTarifa;
    }

    public BigDecimal getVrUltimaCotaSemTarifa() {
        return vrUltimaCotaSemTarifa;
    }
}


