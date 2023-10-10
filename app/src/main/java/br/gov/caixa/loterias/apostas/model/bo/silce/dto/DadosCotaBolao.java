package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DadosCotaBolao
 */
@ApiModel(description = "")
public class DadosCotaBolao {
    @SerializedName("statusCodeHttp")
    private Integer statusCodeHttp = null;

    @SerializedName("index")
    private Integer index = null;

    @SerializedName("codigoBolao")
    private String codigoBolao = null;

    @SerializedName("codigoCota")
    private String codigoCota = null;

    @SerializedName("qtdCotaDigital")
    private Integer qtdCotaDigital = null;

    @SerializedName("qtdCotaTotal")
    private Integer qtdCotaTotal = null;

    @SerializedName("qtdCotaDisponivel")
    private Integer qtdCotaDisponivel = null;

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

    @SerializedName("loterica")
    private Long loterica = null;

    @SerializedName("nomeFantasia")
    private String nomeFantasia = null;

    @SerializedName("nomeRazaoSocial")
    private String nomeRazaoSocial = null;

    @SerializedName("numeroUF")
    private Long numeroUF = null;

    @SerializedName("uf")
    private UnidadeFederacao uf = null;

    @SerializedName("idMunicipio")
    private Long idMunicipio = null;

    @SerializedName("municipio")
    private Municipio municipio = null;

    @SerializedName("concurso")
    private Integer concurso = null;

    @SerializedName("tipoConcurso")
    private Integer tipoConcurso = null;

    @SerializedName("idModalidade")
    private Integer idModalidade = null;

    /**
     * Gets or Sets modalidade
     */
    @JsonAdapter(ModalidadeEnum.Adapter.class)
    public enum ModalidadeEnum {
        DIA_DE_SORTE("DIA_DE_SORTE"),

        DUPLA_SENA("DUPLA_SENA"),

        LOTECA("LOTECA"),

        LOTOFACIL("LOTOFACIL"),

        LOTOGOL("LOTOGOL"),

        LOTOMANIA("LOTOMANIA"),

        MAIS_MILIONARIA("MAIS_MILIONARIA"),

        MEGA_SENA("MEGA_SENA"),

        QUINA("QUINA"),

        SUPER_7("SUPER_7"),

        TIMEMANIA("TIMEMANIA");

        private String value;

        ModalidadeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static ModalidadeEnum fromValue(String text) {
            for (ModalidadeEnum b : ModalidadeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<ModalidadeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final ModalidadeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public ModalidadeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return ModalidadeEnum.fromValue(String.valueOf(value));
            }
        }
    }

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

    @SerializedName("vrCotaSemTarifa")
    private BigDecimal vrCotaSemTarifa = null;

    @SerializedName("vrCotaComTarifa")
    private BigDecimal vrCotaComTarifa = null;

    @SerializedName("valorCota")
    private BigDecimal valorCota = null;

    @SerializedName("valorTarifaServico")
    private BigDecimal valorTarifaServico = null;

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

    @SerializedName("qtdApostas")
    private Integer qtdApostas = null;

    @SerializedName("qtdNumeros")
    private Integer qtdNumeros = null;

    @SerializedName("qtdSimplesLoteca")
    private Integer qtdSimplesLoteca = null;

    @SerializedName("qtdDuplosLoteca")
    private Integer qtdDuplosLoteca = null;

    @SerializedName("qtdTriplosLoteca")
    private Integer qtdTriplosLoteca = null;

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

    /**
     * Gets or Sets situacaoCota
     */
    @JsonAdapter(SituacaoCotaEnum.Adapter.class)
    public enum SituacaoCotaEnum {
        CRIADA("CRIADA"),

        DISPONIVEL("DISPONIVEL"),

        RESERVADA("RESERVADA"),

        BAIXADA_NAO_IMPRESSA("BAIXADA_NAO_IMPRESSA"),

        BAIXADA_NO_ENCERRAMENTO("BAIXADA_NO_ENCERRAMENTO"),

        FISICA_IMPRESSA("FISICA_IMPRESSA"),

        FISICA_NAO_IMPRESSA("FISICA_NAO_IMPRESSA"),

        BAIXADA_IMPRESSA("BAIXADA_IMPRESSA"),

        VENDIDA("VENDIDA"),

        ESTORNADA("ESTORNADA"),

        SEM_NSBC("SEM_NSBC"),

        FISICA_REIMPRESSA("FISICA_REIMPRESSA");

        private String value;

        SituacaoCotaEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static SituacaoCotaEnum fromValue(String text) {
            for (SituacaoCotaEnum b : SituacaoCotaEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<SituacaoCotaEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final SituacaoCotaEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public SituacaoCotaEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return SituacaoCotaEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("situacaoCota")
    private SituacaoCotaEnum situacaoCota = null;

    @SerializedName("tempoExpiracao")
    private Integer tempoExpiracao = null;

    @SerializedName("cotas")
    private List<DadosCotaBolao> cotas = null;

    @SerializedName("totalRegistros")
    private Integer totalRegistros = null;

    @SerializedName("paginaAtual")
    private Integer paginaAtual = null;

    @SerializedName("ultimaPagina")
    private Integer ultimaPagina = null;

    @SerializedName("cpf")
    private String cpf = null;

    @SerializedName("nsu")
    private Long nsu = null;

    @SerializedName("nsus")
    private List<Long> nsus = null;

    @SerializedName("reservaCotaBolao")
    private ReservaCotaBolao reservaCotaBolao = null;

    @SerializedName("nsbc")
    private String nsbc = null;

    @SerializedName("retornoErro")
    private RetornoErro retornoErro = null;

    @SerializedName("operacaoExecutadaComSucesso")
    private Boolean operacaoExecutadaComSucesso = null;

    public DadosCotaBolao statusCodeHttp(Integer statusCodeHttp) {
        this.statusCodeHttp = statusCodeHttp;
        return this;
    }

    /**
     * Get statusCodeHttp
     * @return statusCodeHttp
     **/
    @ApiModelProperty(value = "")
    public Integer getStatusCodeHttp() {
        return statusCodeHttp;
    }

    public void setStatusCodeHttp(Integer statusCodeHttp) {
        this.statusCodeHttp = statusCodeHttp;
    }

    public DadosCotaBolao index(Integer index) {
        this.index = index;
        return this;
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

    public DadosCotaBolao codigoBolao(String codigoBolao) {
        this.codigoBolao = codigoBolao;
        return this;
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

    public DadosCotaBolao codigoCota(String codigoCota) {
        this.codigoCota = codigoCota;
        return this;
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

    public DadosCotaBolao qtdCotaDigital(Integer qtdCotaDigital) {
        this.qtdCotaDigital = qtdCotaDigital;
        return this;
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

    public DadosCotaBolao qtdCotaTotal(Integer qtdCotaTotal) {
        this.qtdCotaTotal = qtdCotaTotal;
        return this;
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

    public DadosCotaBolao qtdCotaDisponivel(Integer qtdCotaDisponivel) {
        this.qtdCotaDisponivel = qtdCotaDisponivel;
        return this;
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

    public DadosCotaBolao qtdCotaFisica(Integer qtdCotaFisica) {
        this.qtdCotaFisica = qtdCotaFisica;
        return this;
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

    public DadosCotaBolao qtdCotaBaixadasImpressas(Integer qtdCotaBaixadasImpressas) {
        this.qtdCotaBaixadasImpressas = qtdCotaBaixadasImpressas;
        return this;
    }

    /**
     * Get qtdCotaBaixadasImpressas
     * @return qtdCotaBaixadasImpressas
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotaBaixadasImpressas() {
        return qtdCotaBaixadasImpressas;
    }

    public void setQtdCotaBaixadasImpressas(Integer qtdCotaBaixadasImpressas) {
        this.qtdCotaBaixadasImpressas = qtdCotaBaixadasImpressas;
    }

    public DadosCotaBolao qtdCotaVendidas(Integer qtdCotaVendidas) {
        this.qtdCotaVendidas = qtdCotaVendidas;
        return this;
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

    public DadosCotaBolao qtdCotaReservada(Integer qtdCotaReservada) {
        this.qtdCotaReservada = qtdCotaReservada;
        return this;
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

    public DadosCotaBolao qtdCotaBaixadas(Integer qtdCotaBaixadas) {
        this.qtdCotaBaixadas = qtdCotaBaixadas;
        return this;
    }

    /**
     * Get qtdCotaBaixadas
     * @return qtdCotaBaixadas
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdCotaBaixadas() {
        return qtdCotaBaixadas;
    }

    public void setQtdCotaBaixadas(Integer qtdCotaBaixadas) {
        this.qtdCotaBaixadas = qtdCotaBaixadas;
    }

    public DadosCotaBolao loterica(Long loterica) {
        this.loterica = loterica;
        return this;
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

    public DadosCotaBolao nomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
        return this;
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

    public DadosCotaBolao nomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
        return this;
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

    public DadosCotaBolao numeroUF(Long numeroUF) {
        this.numeroUF = numeroUF;
        return this;
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

    public DadosCotaBolao uf(UnidadeFederacao uf) {
        this.uf = uf;
        return this;
    }

    /**
     * Get uf
     * @return uf
     **/
    @ApiModelProperty(value = "")
    public UnidadeFederacao getUf() {
        return uf;
    }

    public void setUf(UnidadeFederacao uf) {
        this.uf = uf;
    }

    public DadosCotaBolao idMunicipio(Long idMunicipio) {
        this.idMunicipio = idMunicipio;
        return this;
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

    public DadosCotaBolao municipio(Municipio municipio) {
        this.municipio = municipio;
        return this;
    }

    /**
     * Get municipio
     * @return municipio
     **/
    @ApiModelProperty(value = "")
    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public DadosCotaBolao concurso(Integer concurso) {
        this.concurso = concurso;
        return this;
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

    public DadosCotaBolao tipoConcurso(Integer tipoConcurso) {
        this.tipoConcurso = tipoConcurso;
        return this;
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

    public DadosCotaBolao idModalidade(Integer idModalidade) {
        this.idModalidade = idModalidade;
        return this;
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

    public DadosCotaBolao modalidade(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
        return this;
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

    public DadosCotaBolao dataSorteio(String dataSorteio) {
        this.dataSorteio = dataSorteio;
        return this;
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

    public DadosCotaBolao horaSorteio(String horaSorteio) {
        this.horaSorteio = horaSorteio;
        return this;
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

    public DadosCotaBolao diaSorteio(String diaSorteio) {
        this.diaSorteio = diaSorteio;
        return this;
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

    public DadosCotaBolao vrPremioEstimado(BigDecimal vrPremioEstimado) {
        this.vrPremioEstimado = vrPremioEstimado;
        return this;
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

    public DadosCotaBolao vrCotaSemTarifa(BigDecimal vrCotaSemTarifa) {
        this.vrCotaSemTarifa = vrCotaSemTarifa;
        return this;
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

    public DadosCotaBolao vrCotaComTarifa(BigDecimal vrCotaComTarifa) {
        this.vrCotaComTarifa = vrCotaComTarifa;
        return this;
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

    public DadosCotaBolao valorCota(BigDecimal valorCota) {
        this.valorCota = valorCota;
        return this;
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

    public DadosCotaBolao valorTarifaServico(BigDecimal valorTarifaServico) {
        this.valorTarifaServico = valorTarifaServico;
        return this;
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

    public DadosCotaBolao vrUltimaCotaSemTarifa(BigDecimal vrUltimaCotaSemTarifa) {
        this.vrUltimaCotaSemTarifa = vrUltimaCotaSemTarifa;
        return this;
    }

    /**
     * Get vrUltimaCotaSemTarifa
     * @return vrUltimaCotaSemTarifa
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrUltimaCotaSemTarifa() {
        return vrUltimaCotaSemTarifa;
    }

    public void setVrUltimaCotaSemTarifa(BigDecimal vrUltimaCotaSemTarifa) {
        this.vrUltimaCotaSemTarifa = vrUltimaCotaSemTarifa;
    }

    public DadosCotaBolao vrUltimaCotaComTarifa(BigDecimal vrUltimaCotaComTarifa) {
        this.vrUltimaCotaComTarifa = vrUltimaCotaComTarifa;
        return this;
    }

    /**
     * Get vrUltimaCotaComTarifa
     * @return vrUltimaCotaComTarifa
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrUltimaCotaComTarifa() {
        return vrUltimaCotaComTarifa;
    }

    public void setVrUltimaCotaComTarifa(BigDecimal vrUltimaCotaComTarifa) {
        this.vrUltimaCotaComTarifa = vrUltimaCotaComTarifa;
    }

    public DadosCotaBolao vrTarifaServicoUltimaCota(BigDecimal vrTarifaServicoUltimaCota) {
        this.vrTarifaServicoUltimaCota = vrTarifaServicoUltimaCota;
        return this;
    }

    /**
     * Get vrTarifaServicoUltimaCota
     * @return vrTarifaServicoUltimaCota
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getVrTarifaServicoUltimaCota() {
        return vrTarifaServicoUltimaCota;
    }

    public void setVrTarifaServicoUltimaCota(BigDecimal vrTarifaServicoUltimaCota) {
        this.vrTarifaServicoUltimaCota = vrTarifaServicoUltimaCota;
    }

    public DadosCotaBolao vrTarifaBolao(BigDecimal vrTarifaBolao) {
        this.vrTarifaBolao = vrTarifaBolao;
        return this;
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

    public DadosCotaBolao vrTotalBolaoComTarifa(BigDecimal vrTotalBolaoComTarifa) {
        this.vrTotalBolaoComTarifa = vrTotalBolaoComTarifa;
        return this;
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

    public DadosCotaBolao vrTotalBolaoSemTarifa(BigDecimal vrTotalBolaoSemTarifa) {
        this.vrTotalBolaoSemTarifa = vrTotalBolaoSemTarifa;
        return this;
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

    public DadosCotaBolao apostas(List<RetornoDetalhamentoBolaoApostas> apostas) {
        this.apostas = apostas;
        return this;
    }

    public DadosCotaBolao addApostasItem(RetornoDetalhamentoBolaoApostas apostasItem) {
        if (this.apostas == null) {
            this.apostas = new ArrayList<RetornoDetalhamentoBolaoApostas>();
        }
        this.apostas.add(apostasItem);
        return this;
    }

    /**
     * Get apostas
     * @return apostas
     **/
    @ApiModelProperty(value = "")
    public List<RetornoDetalhamentoBolaoApostas> getApostas() {
        return apostas;
    }

    public void setApostas(List<RetornoDetalhamentoBolaoApostas> apostas) {
        this.apostas = apostas;
    }

    public DadosCotaBolao qtdApostas(Integer qtdApostas) {
        this.qtdApostas = qtdApostas;
        return this;
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

    public DadosCotaBolao qtdNumeros(Integer qtdNumeros) {
        this.qtdNumeros = qtdNumeros;
        return this;
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

    public DadosCotaBolao qtdSimplesLoteca(Integer qtdSimplesLoteca) {
        this.qtdSimplesLoteca = qtdSimplesLoteca;
        return this;
    }

    /**
     * Get qtdSimplesLoteca
     * @return qtdSimplesLoteca
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdSimplesLoteca() {
        return qtdSimplesLoteca;
    }

    public void setQtdSimplesLoteca(Integer qtdSimplesLoteca) {
        this.qtdSimplesLoteca = qtdSimplesLoteca;
    }

    public DadosCotaBolao qtdDuplosLoteca(Integer qtdDuplosLoteca) {
        this.qtdDuplosLoteca = qtdDuplosLoteca;
        return this;
    }

    /**
     * Get qtdDuplosLoteca
     * @return qtdDuplosLoteca
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdDuplosLoteca() {
        return qtdDuplosLoteca;
    }

    public void setQtdDuplosLoteca(Integer qtdDuplosLoteca) {
        this.qtdDuplosLoteca = qtdDuplosLoteca;
    }

    public DadosCotaBolao qtdTriplosLoteca(Integer qtdTriplosLoteca) {
        this.qtdTriplosLoteca = qtdTriplosLoteca;
        return this;
    }

    /**
     * Get qtdTriplosLoteca
     * @return qtdTriplosLoteca
     **/
    @ApiModelProperty(value = "")
    public Integer getQtdTriplosLoteca() {
        return qtdTriplosLoteca;
    }

    public void setQtdTriplosLoteca(Integer qtdTriplosLoteca) {
        this.qtdTriplosLoteca = qtdTriplosLoteca;
    }

    public DadosCotaBolao numeroCota(Integer numeroCota) {
        this.numeroCota = numeroCota;
        return this;
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

    public DadosCotaBolao dataHoraReserva(String dataHoraReserva) {
        this.dataHoraReserva = dataHoraReserva;
        return this;
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

    public DadosCotaBolao dataRegistroBolao(Data dataRegistroBolao) {
        this.dataRegistroBolao = dataRegistroBolao;
        return this;
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

    public DadosCotaBolao horaRegistroBolao(Hora horaRegistroBolao) {
        this.horaRegistroBolao = horaRegistroBolao;
        return this;
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

    public DadosCotaBolao numeroTerminalLoterico(Integer numeroTerminalLoterico) {
        this.numeroTerminalLoterico = numeroTerminalLoterico;
        return this;
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

    public DadosCotaBolao apostasBolao(String apostasBolao) {
        this.apostasBolao = apostasBolao;
        return this;
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

    public DadosCotaBolao situacaoCota(SituacaoCotaEnum situacaoCota) {
        this.situacaoCota = situacaoCota;
        return this;
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

    public DadosCotaBolao tempoExpiracao(Integer tempoExpiracao) {
        this.tempoExpiracao = tempoExpiracao;
        return this;
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

    public DadosCotaBolao cotas(List<DadosCotaBolao> cotas) {
        this.cotas = cotas;
        return this;
    }

    public DadosCotaBolao addCotasItem(DadosCotaBolao cotasItem) {
        if (this.cotas == null) {
            this.cotas = new ArrayList<DadosCotaBolao>();
        }
        this.cotas.add(cotasItem);
        return this;
    }

    /**
     * Get cotas
     * @return cotas
     **/
    @ApiModelProperty(value = "")
    public List<DadosCotaBolao> getCotas() {
        return cotas;
    }

    public void setCotas(List<DadosCotaBolao> cotas) {
        this.cotas = cotas;
    }

    public DadosCotaBolao totalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
        return this;
    }

    /**
     * Get totalRegistros
     * @return totalRegistros
     **/
    @ApiModelProperty(value = "")
    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public DadosCotaBolao paginaAtual(Integer paginaAtual) {
        this.paginaAtual = paginaAtual;
        return this;
    }

    /**
     * Get paginaAtual
     * @return paginaAtual
     **/
    @ApiModelProperty(value = "")
    public Integer getPaginaAtual() {
        return paginaAtual;
    }

    public void setPaginaAtual(Integer paginaAtual) {
        this.paginaAtual = paginaAtual;
    }

    public DadosCotaBolao ultimaPagina(Integer ultimaPagina) {
        this.ultimaPagina = ultimaPagina;
        return this;
    }

    /**
     * Get ultimaPagina
     * @return ultimaPagina
     **/
    @ApiModelProperty(value = "")
    public Integer getUltimaPagina() {
        return ultimaPagina;
    }

    public void setUltimaPagina(Integer ultimaPagina) {
        this.ultimaPagina = ultimaPagina;
    }

    public DadosCotaBolao cpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    /**
     * Get cpf
     * @return cpf
     **/
    @ApiModelProperty(value = "")
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public DadosCotaBolao nsu(Long nsu) {
        this.nsu = nsu;
        return this;
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

    public DadosCotaBolao nsus(List<Long> nsus) {
        this.nsus = nsus;
        return this;
    }

    public DadosCotaBolao addNsusItem(Long nsusItem) {
        if (this.nsus == null) {
            this.nsus = new ArrayList<Long>();
        }
        this.nsus.add(nsusItem);
        return this;
    }

    /**
     * Get nsus
     * @return nsus
     **/
    @ApiModelProperty(value = "")
    public List<Long> getNsus() {
        return nsus;
    }

    public void setNsus(List<Long> nsus) {
        this.nsus = nsus;
    }

    public DadosCotaBolao reservaCotaBolao(ReservaCotaBolao reservaCotaBolao) {
        this.reservaCotaBolao = reservaCotaBolao;
        return this;
    }

    /**
     * Get reservaCotaBolao
     * @return reservaCotaBolao
     **/
    @ApiModelProperty(value = "")
    public ReservaCotaBolao getReservaCotaBolao() {
        return reservaCotaBolao;
    }

    public void setReservaCotaBolao(ReservaCotaBolao reservaCotaBolao) {
        this.reservaCotaBolao = reservaCotaBolao;
    }

    public DadosCotaBolao nsbc(String nsbc) {
        this.nsbc = nsbc;
        return this;
    }

    /**
     * Get nsbc
     * @return nsbc
     **/
    @ApiModelProperty(value = "")
    public String getNsbc() {
        return nsbc;
    }

    public void setNsbc(String nsbc) {
        this.nsbc = nsbc;
    }

    public DadosCotaBolao retornoErro(RetornoErro retornoErro) {
        this.retornoErro = retornoErro;
        return this;
    }

    /**
     * Get retornoErro
     * @return retornoErro
     **/
    @ApiModelProperty(value = "")
    public RetornoErro getRetornoErro() {
        return retornoErro;
    }

    public void setRetornoErro(RetornoErro retornoErro) {
        this.retornoErro = retornoErro;
    }

    public DadosCotaBolao operacaoExecutadaComSucesso(Boolean operacaoExecutadaComSucesso) {
        this.operacaoExecutadaComSucesso = operacaoExecutadaComSucesso;
        return this;
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


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DadosCotaBolao dadosCotaBolao = (DadosCotaBolao) o;
        return Objects.equals(this.statusCodeHttp, dadosCotaBolao.statusCodeHttp) &&
                Objects.equals(this.index, dadosCotaBolao.index) &&
                Objects.equals(this.codigoBolao, dadosCotaBolao.codigoBolao) &&
                Objects.equals(this.codigoCota, dadosCotaBolao.codigoCota) &&
                Objects.equals(this.qtdCotaDigital, dadosCotaBolao.qtdCotaDigital) &&
                Objects.equals(this.qtdCotaTotal, dadosCotaBolao.qtdCotaTotal) &&
                Objects.equals(this.qtdCotaDisponivel, dadosCotaBolao.qtdCotaDisponivel) &&
                Objects.equals(this.qtdCotaFisica, dadosCotaBolao.qtdCotaFisica) &&
                Objects.equals(this.qtdCotaBaixadasImpressas, dadosCotaBolao.qtdCotaBaixadasImpressas) &&
                Objects.equals(this.qtdCotaVendidas, dadosCotaBolao.qtdCotaVendidas) &&
                Objects.equals(this.qtdCotaReservada, dadosCotaBolao.qtdCotaReservada) &&
                Objects.equals(this.qtdCotaBaixadas, dadosCotaBolao.qtdCotaBaixadas) &&
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
                Objects.equals(this.vrUltimaCotaSemTarifa, dadosCotaBolao.vrUltimaCotaSemTarifa) &&
                Objects.equals(this.vrUltimaCotaComTarifa, dadosCotaBolao.vrUltimaCotaComTarifa) &&
                Objects.equals(this.vrTarifaServicoUltimaCota, dadosCotaBolao.vrTarifaServicoUltimaCota) &&
                Objects.equals(this.vrTarifaBolao, dadosCotaBolao.vrTarifaBolao) &&
                Objects.equals(this.vrTotalBolaoComTarifa, dadosCotaBolao.vrTotalBolaoComTarifa) &&
                Objects.equals(this.vrTotalBolaoSemTarifa, dadosCotaBolao.vrTotalBolaoSemTarifa) &&
                Objects.equals(this.apostas, dadosCotaBolao.apostas) &&
                Objects.equals(this.qtdApostas, dadosCotaBolao.qtdApostas) &&
                Objects.equals(this.qtdNumeros, dadosCotaBolao.qtdNumeros) &&
                Objects.equals(this.qtdSimplesLoteca, dadosCotaBolao.qtdSimplesLoteca) &&
                Objects.equals(this.qtdDuplosLoteca, dadosCotaBolao.qtdDuplosLoteca) &&
                Objects.equals(this.qtdTriplosLoteca, dadosCotaBolao.qtdTriplosLoteca) &&
                Objects.equals(this.numeroCota, dadosCotaBolao.numeroCota) &&
                Objects.equals(this.dataHoraReserva, dadosCotaBolao.dataHoraReserva) &&
                Objects.equals(this.dataRegistroBolao, dadosCotaBolao.dataRegistroBolao) &&
                Objects.equals(this.horaRegistroBolao, dadosCotaBolao.horaRegistroBolao) &&
                Objects.equals(this.numeroTerminalLoterico, dadosCotaBolao.numeroTerminalLoterico) &&
                Objects.equals(this.apostasBolao, dadosCotaBolao.apostasBolao) &&
                Objects.equals(this.situacaoCota, dadosCotaBolao.situacaoCota) &&
                Objects.equals(this.tempoExpiracao, dadosCotaBolao.tempoExpiracao) &&
                Objects.equals(this.cotas, dadosCotaBolao.cotas) &&
                Objects.equals(this.totalRegistros, dadosCotaBolao.totalRegistros) &&
                Objects.equals(this.paginaAtual, dadosCotaBolao.paginaAtual) &&
                Objects.equals(this.ultimaPagina, dadosCotaBolao.ultimaPagina) &&
                Objects.equals(this.cpf, dadosCotaBolao.cpf) &&
                Objects.equals(this.nsu, dadosCotaBolao.nsu) &&
                Objects.equals(this.nsus, dadosCotaBolao.nsus) &&
                Objects.equals(this.reservaCotaBolao, dadosCotaBolao.reservaCotaBolao) &&
                Objects.equals(this.nsbc, dadosCotaBolao.nsbc) &&
                Objects.equals(this.retornoErro, dadosCotaBolao.retornoErro) &&
                Objects.equals(this.operacaoExecutadaComSucesso, dadosCotaBolao.operacaoExecutadaComSucesso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCodeHttp, index, codigoBolao, codigoCota, qtdCotaDigital, qtdCotaTotal, qtdCotaDisponivel, qtdCotaFisica, qtdCotaBaixadasImpressas, qtdCotaVendidas, qtdCotaReservada, qtdCotaBaixadas, loterica, nomeFantasia, nomeRazaoSocial, numeroUF, uf, idMunicipio, municipio, concurso, tipoConcurso, idModalidade, modalidade, dataSorteio, horaSorteio, diaSorteio, vrPremioEstimado, vrCotaSemTarifa, vrCotaComTarifa, valorCota, valorTarifaServico, vrUltimaCotaSemTarifa, vrUltimaCotaComTarifa, vrTarifaServicoUltimaCota, vrTarifaBolao, vrTotalBolaoComTarifa, vrTotalBolaoSemTarifa, apostas, qtdApostas, qtdNumeros, qtdSimplesLoteca, qtdDuplosLoteca, qtdTriplosLoteca, numeroCota, dataHoraReserva, dataRegistroBolao, horaRegistroBolao, numeroTerminalLoterico, apostasBolao, situacaoCota, tempoExpiracao, cotas, totalRegistros, paginaAtual, ultimaPagina, cpf, nsu, nsus, reservaCotaBolao, nsbc, retornoErro, operacaoExecutadaComSucesso);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DadosCotaBolao {\n");

        sb.append("    statusCodeHttp: ").append(toIndentedString(statusCodeHttp)).append("\n");
        sb.append("    index: ").append(toIndentedString(index)).append("\n");
        sb.append("    codigoBolao: ").append(toIndentedString(codigoBolao)).append("\n");
        sb.append("    codigoCota: ").append(toIndentedString(codigoCota)).append("\n");
        sb.append("    qtdCotaDigital: ").append(toIndentedString(qtdCotaDigital)).append("\n");
        sb.append("    qtdCotaTotal: ").append(toIndentedString(qtdCotaTotal)).append("\n");
        sb.append("    qtdCotaDisponivel: ").append(toIndentedString(qtdCotaDisponivel)).append("\n");
        sb.append("    qtdCotaFisica: ").append(toIndentedString(qtdCotaFisica)).append("\n");
        sb.append("    qtdCotaBaixadasImpressas: ").append(toIndentedString(qtdCotaBaixadasImpressas)).append("\n");
        sb.append("    qtdCotaVendidas: ").append(toIndentedString(qtdCotaVendidas)).append("\n");
        sb.append("    qtdCotaReservada: ").append(toIndentedString(qtdCotaReservada)).append("\n");
        sb.append("    qtdCotaBaixadas: ").append(toIndentedString(qtdCotaBaixadas)).append("\n");
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
        sb.append("    vrUltimaCotaSemTarifa: ").append(toIndentedString(vrUltimaCotaSemTarifa)).append("\n");
        sb.append("    vrUltimaCotaComTarifa: ").append(toIndentedString(vrUltimaCotaComTarifa)).append("\n");
        sb.append("    vrTarifaServicoUltimaCota: ").append(toIndentedString(vrTarifaServicoUltimaCota)).append("\n");
        sb.append("    vrTarifaBolao: ").append(toIndentedString(vrTarifaBolao)).append("\n");
        sb.append("    vrTotalBolaoComTarifa: ").append(toIndentedString(vrTotalBolaoComTarifa)).append("\n");
        sb.append("    vrTotalBolaoSemTarifa: ").append(toIndentedString(vrTotalBolaoSemTarifa)).append("\n");
        sb.append("    apostas: ").append(toIndentedString(apostas)).append("\n");
        sb.append("    qtdApostas: ").append(toIndentedString(qtdApostas)).append("\n");
        sb.append("    qtdNumeros: ").append(toIndentedString(qtdNumeros)).append("\n");
        sb.append("    qtdSimplesLoteca: ").append(toIndentedString(qtdSimplesLoteca)).append("\n");
        sb.append("    qtdDuplosLoteca: ").append(toIndentedString(qtdDuplosLoteca)).append("\n");
        sb.append("    qtdTriplosLoteca: ").append(toIndentedString(qtdTriplosLoteca)).append("\n");
        sb.append("    numeroCota: ").append(toIndentedString(numeroCota)).append("\n");
        sb.append("    dataHoraReserva: ").append(toIndentedString(dataHoraReserva)).append("\n");
        sb.append("    dataRegistroBolao: ").append(toIndentedString(dataRegistroBolao)).append("\n");
        sb.append("    horaRegistroBolao: ").append(toIndentedString(horaRegistroBolao)).append("\n");
        sb.append("    numeroTerminalLoterico: ").append(toIndentedString(numeroTerminalLoterico)).append("\n");
        sb.append("    apostasBolao: ").append(toIndentedString(apostasBolao)).append("\n");
        sb.append("    situacaoCota: ").append(toIndentedString(situacaoCota)).append("\n");
        sb.append("    tempoExpiracao: ").append(toIndentedString(tempoExpiracao)).append("\n");
        sb.append("    cotas: ").append(toIndentedString(cotas)).append("\n");
        sb.append("    totalRegistros: ").append(toIndentedString(totalRegistros)).append("\n");
        sb.append("    paginaAtual: ").append(toIndentedString(paginaAtual)).append("\n");
        sb.append("    ultimaPagina: ").append(toIndentedString(ultimaPagina)).append("\n");
        sb.append("    cpf: ").append(toIndentedString(cpf)).append("\n");
        sb.append("    nsu: ").append(toIndentedString(nsu)).append("\n");
        sb.append("    nsus: ").append(toIndentedString(nsus)).append("\n");
        sb.append("    reservaCotaBolao: ").append(toIndentedString(reservaCotaBolao)).append("\n");
        sb.append("    nsbc: ").append(toIndentedString(nsbc)).append("\n");
        sb.append("    retornoErro: ").append(toIndentedString(retornoErro)).append("\n");
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

}


