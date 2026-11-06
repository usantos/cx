package br.gov.caixa.loterias.apostas.model.bo.silce.queryparam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.enums.TipoConsultaBolaoEnum;

public class FiltroMarketPlace implements Serializable {
    @SerializedName("tipoConsulta")
    private TipoConsultaBolaoEnum tipoConsulta;
    @SerializedName("idMunicipio")
    private int idMunicipio;
    @SerializedName("idUf")
    private int idUf;
    @SerializedName("pagina")
    private int pagina;
    @SerializedName("qtdPorPagina")
    private int qtdPorPagina;
    @SerializedName("qtdMinimaCota")
    private int qtdMinimaCota;
    @SerializedName("qtdMaximaCota")
    private int qtdMaximaCota;
    @SerializedName("quantidadeDezena")
    private int qtdDezenas;
    @SerializedName("quantidadeAposta")
    private int qtdApostas;
    @SerializedName("numerosEscolhidos")
    private String numerosQuero;
    @SerializedName("numerosNaoEscolhidos")
    private String numerosNaoQuero;
    @SerializedName("valorMinimoCota")
    private BigDecimal valorMinimoCota;
    @SerializedName("valorMaximoCota")
    private BigDecimal valorMaximoCota;
    @SerializedName("numeroLoterico")
    private Long numeroLoterico;
    @SerializedName("idModalidade")
    private Integer idModalidade;
    @SerializedName("tipoConcurso")
    private Integer tipoConcurso;

    public FiltroMarketPlace(TipoConsultaBolaoEnum tipoConsulta, int idMunicipio, int idUf, int pagina, int qtdPorPagina, Integer idModalidade, Integer tipoConcurso) {
        this.tipoConsulta = tipoConsulta;
        this.idMunicipio = idMunicipio;
        this.idUf = idUf;
        this.pagina = pagina;
        this.qtdPorPagina = qtdPorPagina;
        this.qtdMinimaCota = 0;
        this.qtdMaximaCota = 0;
        this.qtdDezenas = 0;
        this.qtdApostas = 0;
        this.valorMinimoCota = null;
        this.valorMaximoCota = null;
        this.numeroLoterico = null;
        this.idModalidade = idModalidade;
        this.tipoConcurso = tipoConcurso;
    }

    public TipoConsultaBolaoEnum getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsultaBolaoEnum tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public int getIdUf() {
        return idUf;
    }

    public void setIdUf(int idUf) {
        this.idUf = idUf;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getQtdPorPagina() {
        return qtdPorPagina;
    }

    public void setQtdPorPagina(int qtdPorPagina) {
        this.qtdPorPagina = qtdPorPagina;
    }

    public int getQtdMinimaCota() {
        return qtdMinimaCota;
    }

    public void setQtdMinimaCota(int qtdMinimaCota) {
        this.qtdMinimaCota = qtdMinimaCota;
    }

    public int getQtdMaximaCota() {
        return qtdMaximaCota;
    }

    public void setQtdMaximaCota(int qtdMaximaCota) {
        this.qtdMaximaCota = qtdMaximaCota;
    }

    public int getQtdDezenas() {return qtdDezenas;}

    public void setQtdDezenas(int qtdDezenas) {this.qtdDezenas = qtdDezenas;}

    public int getQtdApostas() {return qtdApostas;}

    public void setQtdApostas(int qtdApostas) {this.qtdApostas = qtdApostas;}

    public String getNumerosQuero() {
        return numerosQuero;
    }

    public void setNumerosQuero(String numerosQuero) {
        this.numerosQuero = numerosQuero;
    }

    public String getNumerosNaoQuero() {
        return numerosNaoQuero;
    }

    public void setNumerosNaoQuero(String numerosNaoQuero) {
        this.numerosNaoQuero = numerosNaoQuero;
    }

    public BigDecimal getValorMinimoCota() {
        return valorMinimoCota;
    }

    public void setValorMinimoCota(BigDecimal valorMinimoCota) {
        this.valorMinimoCota = valorMinimoCota;
    }

    public BigDecimal getValorMaximoCota() {
        return valorMaximoCota;
    }

    public void setValorMaximoCota(BigDecimal valorMaximoCota) {
        this.valorMaximoCota = valorMaximoCota;
    }

    public Long getNumeroLoterico() {
        return numeroLoterico;
    }

    public void setNumeroLoterico(Long numeroLoterico) {
        this.numeroLoterico = numeroLoterico;
    }

    public Integer getIdModalidade() {
        return idModalidade;
    }

    public void setIdModalidade(Integer idModalidade) {
        this.idModalidade = idModalidade;
    }

    public Integer getTipoConcurso() {
        return tipoConcurso;
    }

    public void setTipoConcurso(Integer tipoConcurso) {
        this.tipoConcurso = tipoConcurso;
    }
}
