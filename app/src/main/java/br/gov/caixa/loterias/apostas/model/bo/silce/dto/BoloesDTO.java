package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * BoloesDTO
 */
@ApiModel(description = "")
public class BoloesDTO {
    
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
    private UnidadeFederacaoToDelete uf = null;
    @SerializedName("municipioCota")
    private MunicipioToDelete municipioCota = null;
    @SerializedName("concurso")
    private Integer concurso = null;
    @SerializedName("idModalidade")
    private Integer idModalidade = null;
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
    private BigDecimal vrTarifaServicoCota = null;
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
    @SerializedName("qtdCotaBaixadas")
    private Integer qtdCotaBaixadas = null;
    @SerializedName("tipoConcurso")
    private Integer tipoConcurso = null;
    @SerializedName("vrCotaSemTarifa")
    private BigDecimal vrCotaSemTarifa = null;
    @SerializedName("vrCotaComTarifa")
    private BigDecimal vrCotaComTarifa = null;
    @SerializedName("vrUltimaCotaSemTarifa")
    private BigDecimal vrUltimaCotaSemTarifa = null;
    @SerializedName("vrUltimaCotaComTarifa")
    private BigDecimal vrUltimaCotaComTarifa = null;
    @SerializedName("vrTarifaServicoUltimaCota")
    private BigDecimal vrTarifaServicoUltimaCota = null;
    @SerializedName("vrTarifaBolao")
    private BigDecimal vrTarifaBolao = null;
    @SerializedName("vrTotalBolaoComTarifa")
    private BigDecimal vrTotalBolaoComTarifa = null;
    @SerializedName("vrTotalBolaoSemTarifa")
    private BigDecimal vrTotalBolaoSemTarifa = null;
    @SerializedName("apostas")
    private List<RetornoDetalhamentoBolaoApostas> apostas = null;

    /**
     * Quantidade de cota digital
     * @return qtdCotaDigital
     **/
    @ApiModelProperty(value = "Quantidade de cota digital")
    public Integer getQtdCotaDigital() {
        return qtdCotaDigital;
    }

    public void setQtdCotaDigital(Integer qtdCotaDigital) {
        this.qtdCotaDigital = qtdCotaDigital;
    }

    /**
     * Quantidade de cota total
     * @return qtdCotaTotal
     **/
    @ApiModelProperty(value = "Quantidade de cota total")
    public Integer getQtdCotaTotal() {
        return qtdCotaTotal;
    }

    public void setQtdCotaTotal(Integer qtdCotaTotal) {
        this.qtdCotaTotal = qtdCotaTotal;
    }

    /**
     * Quantidade de coda disponível
     * @return qtdCotaDisponivel
     **/
    @ApiModelProperty(value = "Quantidade de coda disponível")
    public Integer getQtdCotaDisponivel() {
        return qtdCotaDisponivel;
    }

    public void setQtdCotaDisponivel(Integer qtdCotaDisponivel) {
        this.qtdCotaDisponivel = qtdCotaDisponivel;
    }

    /**
     * Identificador da loterica
     * @return loterica
     **/
    @ApiModelProperty(value = "Identificador da loterica")
    public Long getLoterica() {
        return loterica;
    }

    public void setLoterica(Long loterica) {
        this.loterica = loterica;
    }

    /**
     * Nome fantasia da loterica
     * @return nomeFantasia
     **/
    @ApiModelProperty(value = "Nome fantasia da loterica")
    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    /**
     * Razão social da loterica
     * @return nomeRazaoSocial
     **/
    @ApiModelProperty(value = "Razão social da loterica")
    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    /**
     * UF Loterica
     * @return uf
     **/
    @ApiModelProperty(value = "UF Loterica")
    public UnidadeFederacaoToDelete getUf() {
        return uf;
    }

    public void setUf(UnidadeFederacaoToDelete uf) {
        this.uf = uf;
    }

    /**
     * Municipio loterica
     * @return municipioCota
     **/
    @ApiModelProperty(value = "Municipio loterica")
    public MunicipioToDelete getMunicipioCota() {
        return municipioCota;
    }

    public void setMunicipioCota(MunicipioToDelete municipioCota) {
        this.municipioCota = municipioCota;
    }

    /**
     * Concurso bolão concorrendo
     * @return concurso
     **/
    @ApiModelProperty(value = "Concurso bolão concorrendo")
    public Integer getConcurso() {
        return concurso;
    }

    public void setConcurso(Integer concurso) {
        this.concurso = concurso;
    }

    /**
     * ID modalidade do bolão
     * @return idModalidade
     **/
    @ApiModelProperty(value = "ID modalidade do bolão")
    public Integer getIdModalidade() {
        return idModalidade;
    }

    public void setIdModalidade(Integer idModalidade) {
        this.idModalidade = idModalidade;
    }

    /**
     * Modalidade do bolão
     * @return modalidade
     **/
    @ApiModelProperty(value = "Modalidade do bolão")
    public ModalidadeEnum getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
    }

    /**
     * Data do sorteio
     * @return dataSorteio
     **/
    @ApiModelProperty(value = "Data do sorteio")
    public String getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(String dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    /**
     * Dia da semana do sorteio
     * @return diaSorteio
     **/
    @ApiModelProperty(value = "Dia da semana do sorteio")
    public String getDiaSorteio() {
        return diaSorteio;
    }

    public void setDiaSorteio(String diaSorteio) {
        this.diaSorteio = diaSorteio;
    }

    /**
     * Hora do sorteio
     * @return horaSorteio
     **/
    @ApiModelProperty(value = "Hora do sorteio")
    public String getHoraSorteio() {
        return horaSorteio;
    }

    public void setHoraSorteio(String horaSorteio) {
        this.horaSorteio = horaSorteio;
    }

    /**
     * Valor estimado do premio
     * @return vrPremioEstimado
     **/
    @ApiModelProperty(value = "Valor estimado do premio")
    public BigDecimal getVrPremioEstimado() {
        return vrPremioEstimado;
    }

    public void setVrPremioEstimado(BigDecimal vrPremioEstimado) {
        this.vrPremioEstimado = vrPremioEstimado;
    }

    /**
     * Valor da tarifa de serviço
     * @return vrTarifaServicoCota
     **/
    @ApiModelProperty(value = "Valor da tarifa de serviço")
    public BigDecimal getVrTarifaServicoCota() {
        return vrTarifaServicoCota;
    }

    public void setVrTarifaServicoCota(BigDecimal vrTarifaServicoCota) {
        this.vrTarifaServicoCota = vrTarifaServicoCota;
    }

    /**
     * Quantidade de apostas
     * @return qtdApostas
     **/
    @ApiModelProperty(value = "Quantidade de apostas")
    public Integer getQtdApostas() {
        return qtdApostas;
    }

    public void setQtdApostas(Integer qtdApostas) {
        this.qtdApostas = qtdApostas;
    }

    /**
     * Quantidade de números da aposta
     * @return qtdNumeros
     **/
    @ApiModelProperty(value = "Quantidade de números da aposta")
    public Integer getQtdNumeros() {
        return qtdNumeros;
    }

    public void setQtdNumeros(Integer qtdNumeros) {
        this.qtdNumeros = qtdNumeros;
    }

    /**
     * Quantidade de cota fisica
     * @return qtdCotaFisica
     **/
    @ApiModelProperty(value = "Quantidade de cota fisica")
    public Integer getQtdCotaFisica() {
        return qtdCotaFisica;
    }

    public void setQtdCotaFisica(Integer qtdCotaFisica) {
        this.qtdCotaFisica = qtdCotaFisica;
    }

    /**
     * Quantidade de cota baixadas/impressas
     * @return qtdCotaBaixadasImpressas
     **/
    @ApiModelProperty(value = "Quantidade de cota baixadas/impressas")
    public Integer getQtdCotaBaixadasImpressas() {
        return qtdCotaBaixadasImpressas;
    }

    public void setQtdCotaBaixadasImpressas(Integer qtdCotaBaixadasImpressas) {
        this.qtdCotaBaixadasImpressas = qtdCotaBaixadasImpressas;
    }

    /**
     * Quantidade de cotas vendidas
     * @return qtdCotaVendidas
     **/
    @ApiModelProperty(value = "Quantidade de cotas vendidas")
    public Integer getQtdCotaVendidas() {
        return qtdCotaVendidas;
    }

    public void setQtdCotaVendidas(Integer qtdCotaVendidas) {
        this.qtdCotaVendidas = qtdCotaVendidas;
    }

    /**
     * Quantidade de cotas reservadas
     * @return qtdCotaReservada
     **/
    @ApiModelProperty(value = "Quantidade de cotas reservadas")
    public Integer getQtdCotaReservada() {
        return qtdCotaReservada;
    }

    public void setQtdCotaReservada(Integer qtdCotaReservada) {
        this.qtdCotaReservada = qtdCotaReservada;
    }

    /**
     * Quantidade de cotas baixadas
     * @return qtdCotaBaixadas
     **/
    @ApiModelProperty(value = "Quantidade de cotas baixadas")
    public Integer getQtdCotaBaixadas() {
        return qtdCotaBaixadas;
    }

    public void setQtdCotaBaixadas(Integer qtdCotaBaixadas) {
        this.qtdCotaBaixadas = qtdCotaBaixadas;
    }

    /**
     * Tipo concurso 1: NORMAL 2: ESPECIAL
     * @return tipoConcurso
     **/
    @ApiModelProperty(value = "Tipo concurso 1: NORMAL 2: ESPECIAL")
    public Integer getTipoConcurso() {
        return tipoConcurso;
    }

    public void setTipoConcurso(Integer tipoConcurso) {
        this.tipoConcurso = tipoConcurso;
    }

    /**
     * Valor da cota sem tarifa
     * @return vrCotaSemTarifa
     **/
    @ApiModelProperty(value = "Valor da cota sem tarifa")
    public BigDecimal getVrCotaSemTarifa() {
        return vrCotaSemTarifa;
    }

    public void setVrCotaSemTarifa(BigDecimal vrCotaSemTarifa) {
        this.vrCotaSemTarifa = vrCotaSemTarifa;
    }

    /**
     * Valor da cota com tarifa
     * @return vrCotaComTarifa
     **/
    @ApiModelProperty(value = "Valor da cota com tarifa")
    public BigDecimal getVrCotaComTarifa() {
        return vrCotaComTarifa;
    }

    public void setVrCotaComTarifa(BigDecimal vrCotaComTarifa) {
        this.vrCotaComTarifa = vrCotaComTarifa;
    }

    /**
     * Valor da ultima cota sem tarifa
     * @return vrUltimaCotaSemTarifa
     **/
    @ApiModelProperty(value = "Valor da ultima cota sem tarifa")
    public BigDecimal getVrUltimaCotaSemTarifa() {
        return vrUltimaCotaSemTarifa;
    }

    public void setVrUltimaCotaSemTarifa(BigDecimal vrUltimaCotaSemTarifa) {
        this.vrUltimaCotaSemTarifa = vrUltimaCotaSemTarifa;
    }

    /**
     * Valor da ultima cota com tarifa
     * @return vrUltimaCotaComTarifa
     **/
    @ApiModelProperty(value = "Valor da ultima cota com tarifa")
    public BigDecimal getVrUltimaCotaComTarifa() {
        return vrUltimaCotaComTarifa;
    }

    public void setVrUltimaCotaComTarifa(BigDecimal vrUltimaCotaComTarifa) {
        this.vrUltimaCotaComTarifa = vrUltimaCotaComTarifa;
    }

    /**
     * Valor da tarifa de serviço da ultima cota
     * @return vrTarifaServicoUltimaCota
     **/
    @ApiModelProperty(value = "Valor da tarifa de serviço da ultima cota")
    public BigDecimal getVrTarifaServicoUltimaCota() {
        return vrTarifaServicoUltimaCota;
    }

    public void setVrTarifaServicoUltimaCota(BigDecimal vrTarifaServicoUltimaCota) {
        this.vrTarifaServicoUltimaCota = vrTarifaServicoUltimaCota;
    }

    /**
     * Valor tarifa do bolão
     * @return vrTarifaBolao
     **/
    @ApiModelProperty(value = "Valor tarifa do bolão")
    public BigDecimal getVrTarifaBolao() {
        return vrTarifaBolao;
    }

    public void setVrTarifaBolao(BigDecimal vrTarifaBolao) {
        this.vrTarifaBolao = vrTarifaBolao;
    }

    /**
     * Valor total do bolão com tarifa
     * @return vrTotalBolaoComTarifa
     **/
    @ApiModelProperty(value = "Valor total do bolão com tarifa")
    public BigDecimal getVrTotalBolaoComTarifa() {
        return vrTotalBolaoComTarifa;
    }

    public void setVrTotalBolaoComTarifa(BigDecimal vrTotalBolaoComTarifa) {
        this.vrTotalBolaoComTarifa = vrTotalBolaoComTarifa;
    }

    /**
     * Valor total do bolão sem tarifa
     * @return vrTotalBolaoSemTarifa
     **/
    @ApiModelProperty(value = "Valor total do bolão sem tarifa")
    public BigDecimal getVrTotalBolaoSemTarifa() {
        return vrTotalBolaoSemTarifa;
    }

    public void setVrTotalBolaoSemTarifa(BigDecimal vrTotalBolaoSemTarifa) {
        this.vrTotalBolaoSemTarifa = vrTotalBolaoSemTarifa;
    }

    /**
     * Apostas contendo indicador de surpresinha e números
     * @return apostas
     **/
    @ApiModelProperty(value = "Apostas contendo indicador de surpresinha e números")
    public List<RetornoDetalhamentoBolaoApostas> getApostas() {
        return apostas;
    }

    public void setApostas(List<RetornoDetalhamentoBolaoApostas> apostas) {
        this.apostas = apostas;
    }



    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoloesDTO boloesDTO = (BoloesDTO) o;
        return Objects.equals(this.qtdCotaDigital, boloesDTO.qtdCotaDigital) &&
                Objects.equals(this.qtdCotaTotal, boloesDTO.qtdCotaTotal) &&
                Objects.equals(this.qtdCotaDisponivel, boloesDTO.qtdCotaDisponivel) &&
                Objects.equals(this.loterica, boloesDTO.loterica) &&
                Objects.equals(this.nomeFantasia, boloesDTO.nomeFantasia) &&
                Objects.equals(this.nomeRazaoSocial, boloesDTO.nomeRazaoSocial) &&
                Objects.equals(this.uf, boloesDTO.uf) &&
                Objects.equals(this.municipioCota, boloesDTO.municipioCota) &&
                Objects.equals(this.concurso, boloesDTO.concurso) &&
                Objects.equals(this.idModalidade, boloesDTO.idModalidade) &&
                Objects.equals(this.modalidade, boloesDTO.modalidade) &&
                Objects.equals(this.dataSorteio, boloesDTO.dataSorteio) &&
                Objects.equals(this.diaSorteio, boloesDTO.diaSorteio) &&
                Objects.equals(this.horaSorteio, boloesDTO.horaSorteio) &&
                Objects.equals(this.vrPremioEstimado, boloesDTO.vrPremioEstimado) &&
                Objects.equals(this.vrTarifaServicoCota, boloesDTO.vrTarifaServicoCota) &&
                Objects.equals(this.qtdApostas, boloesDTO.qtdApostas) &&
                Objects.equals(this.qtdNumeros, boloesDTO.qtdNumeros) &&
                Objects.equals(this.qtdCotaFisica, boloesDTO.qtdCotaFisica) &&
                Objects.equals(this.qtdCotaBaixadasImpressas, boloesDTO.qtdCotaBaixadasImpressas) &&
                Objects.equals(this.qtdCotaVendidas, boloesDTO.qtdCotaVendidas) &&
                Objects.equals(this.qtdCotaReservada, boloesDTO.qtdCotaReservada) &&
                Objects.equals(this.qtdCotaBaixadas, boloesDTO.qtdCotaBaixadas) &&
                Objects.equals(this.tipoConcurso, boloesDTO.tipoConcurso) &&
                Objects.equals(this.vrCotaSemTarifa, boloesDTO.vrCotaSemTarifa) &&
                Objects.equals(this.vrCotaComTarifa, boloesDTO.vrCotaComTarifa) &&
                Objects.equals(this.vrUltimaCotaSemTarifa, boloesDTO.vrUltimaCotaSemTarifa) &&
                Objects.equals(this.vrUltimaCotaComTarifa, boloesDTO.vrUltimaCotaComTarifa) &&
                Objects.equals(this.vrTarifaServicoUltimaCota, boloesDTO.vrTarifaServicoUltimaCota) &&
                Objects.equals(this.vrTarifaBolao, boloesDTO.vrTarifaBolao) &&
                Objects.equals(this.vrTotalBolaoComTarifa, boloesDTO.vrTotalBolaoComTarifa) &&
                Objects.equals(this.vrTotalBolaoSemTarifa, boloesDTO.vrTotalBolaoSemTarifa) &&
                Objects.equals(this.apostas, boloesDTO.apostas);
    }

    @Override
    public int hashCode() {
        return Objects.hash( qtdCotaDigital, qtdCotaTotal, qtdCotaDisponivel, loterica, nomeFantasia, nomeRazaoSocial, uf, municipioCota, concurso, idModalidade, modalidade, dataSorteio, diaSorteio, horaSorteio, vrPremioEstimado, vrTarifaServicoCota, qtdApostas, qtdNumeros, qtdCotaFisica, qtdCotaBaixadasImpressas, qtdCotaVendidas, qtdCotaReservada, qtdCotaBaixadas, tipoConcurso, vrCotaSemTarifa, vrCotaComTarifa, vrUltimaCotaSemTarifa, vrUltimaCotaComTarifa, vrTarifaServicoUltimaCota, vrTarifaBolao, vrTotalBolaoComTarifa, vrTotalBolaoSemTarifa, apostas);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BoloesDTO {\n");

        sb.append("    qtdCotaDigital: ").append(toIndentedString(qtdCotaDigital)).append("\n");
        sb.append("    qtdCotaTotal: ").append(toIndentedString(qtdCotaTotal)).append("\n");
        sb.append("    qtdCotaDisponivel: ").append(toIndentedString(qtdCotaDisponivel)).append("\n");
        sb.append("    loterica: ").append(toIndentedString(loterica)).append("\n");
        sb.append("    nomeFantasia: ").append(toIndentedString(nomeFantasia)).append("\n");
        sb.append("    nomeRazaoSocial: ").append(toIndentedString(nomeRazaoSocial)).append("\n");
        sb.append("    uf: ").append(toIndentedString(uf)).append("\n");
        sb.append("    municipioCota: ").append(toIndentedString(municipioCota)).append("\n");
        sb.append("    concurso: ").append(toIndentedString(concurso)).append("\n");
        sb.append("    idModalidade: ").append(toIndentedString(idModalidade)).append("\n");
        sb.append("    modalidade: ").append(toIndentedString(modalidade)).append("\n");
        sb.append("    dataSorteio: ").append(toIndentedString(dataSorteio)).append("\n");
        sb.append("    diaSorteio: ").append(toIndentedString(diaSorteio)).append("\n");
        sb.append("    horaSorteio: ").append(toIndentedString(horaSorteio)).append("\n");
        sb.append("    vrPremioEstimado: ").append(toIndentedString(vrPremioEstimado)).append("\n");
        sb.append("    vrTarifaServicoCota: ").append(toIndentedString(vrTarifaServicoCota)).append("\n");
        sb.append("    qtdApostas: ").append(toIndentedString(qtdApostas)).append("\n");
        sb.append("    qtdNumeros: ").append(toIndentedString(qtdNumeros)).append("\n");
        sb.append("    qtdCotaFisica: ").append(toIndentedString(qtdCotaFisica)).append("\n");
        sb.append("    qtdCotaBaixadasImpressas: ").append(toIndentedString(qtdCotaBaixadasImpressas)).append("\n");
        sb.append("    qtdCotaVendidas: ").append(toIndentedString(qtdCotaVendidas)).append("\n");
        sb.append("    qtdCotaReservada: ").append(toIndentedString(qtdCotaReservada)).append("\n");
        sb.append("    qtdCotaBaixadas: ").append(toIndentedString(qtdCotaBaixadas)).append("\n");
        sb.append("    tipoConcurso: ").append(toIndentedString(tipoConcurso)).append("\n");
        sb.append("    vrCotaSemTarifa: ").append(toIndentedString(vrCotaSemTarifa)).append("\n");
        sb.append("    vrCotaComTarifa: ").append(toIndentedString(vrCotaComTarifa)).append("\n");
        sb.append("    vrUltimaCotaSemTarifa: ").append(toIndentedString(vrUltimaCotaSemTarifa)).append("\n");
        sb.append("    vrUltimaCotaComTarifa: ").append(toIndentedString(vrUltimaCotaComTarifa)).append("\n");
        sb.append("    vrTarifaServicoUltimaCota: ").append(toIndentedString(vrTarifaServicoUltimaCota)).append("\n");
        sb.append("    vrTarifaBolao: ").append(toIndentedString(vrTarifaBolao)).append("\n");
        sb.append("    vrTotalBolaoComTarifa: ").append(toIndentedString(vrTotalBolaoComTarifa)).append("\n");
        sb.append("    vrTotalBolaoSemTarifa: ").append(toIndentedString(vrTotalBolaoSemTarifa)).append("\n");
        sb.append("    apostas: ").append(toIndentedString(apostas)).append("\n");
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


