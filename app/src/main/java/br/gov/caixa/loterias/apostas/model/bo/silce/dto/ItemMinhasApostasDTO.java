package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.math.BigDecimal;
import java.util.List;

public class ItemMinhasApostasDTO {
    private ModalidadeEnum modalidade;
    private String concurso;
    private String situacao;
    private List<String> numerosSorteados;
    private List<String> seusNumeros;
    private String timeMesSorteado;
    private String seuTimeMes;
    private List<String> numerosSorteados2;
    private List<String> seusNumeros2;
    private List<String> trevosSorteados;
    private List<String> seusTrevos;
    private BigDecimal valorPremio;
    private boolean isSurpresinha = false;
    private boolean temAcordeon = true;
    private boolean isBolao = false;
    private boolean isExpanded = false;

    public ItemMinhasApostasDTO() {
    }

    public ModalidadeEnum getModalidade() {
        return modalidade;
    }
    public void setModalidade(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
    }

    public String getConcurso() {
        return concurso;
    }
    public void setConcurso(String concurso) {
        this.concurso = concurso;
    }

    public String getSituacao() {
        return situacao;
    }
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public List<String> getNumerosSorteados() {
        return numerosSorteados;
    }
    public void setNumerosSorteados(List<String> numerosSorteados) {
        this.numerosSorteados = numerosSorteados;
    }

    public List<String> getSeusNumeros() {
        return seusNumeros;
    }
    public void setSeusNumeros(List<String> seusNumeros) {
        this.seusNumeros = seusNumeros;
    }

    public String getTimeMesSorteado() {
        return timeMesSorteado;
    }
    public void setTimeMesSorteado(String timeMesSorteado) {
        this.timeMesSorteado = timeMesSorteado;
    }

    public String getSeuTimeMes() {
        return seuTimeMes;
    }
    public void setSeuTimeMes(String seuTimeMes) {
        this.seuTimeMes = seuTimeMes;
    }

    public List<String> getNumerosSorteados2() {
        return numerosSorteados2;
    }
    public void setNumerosSorteados2(List<String> numerosSorteados2) {
        this.numerosSorteados2 = numerosSorteados2;
    }

    public List<String> getSeusNumeros2() {
        return seusNumeros2;
    }
    public void setSeusNumeros2(List<String> seusNumeros2) {
        this.seusNumeros2 = seusNumeros2;
    }

    public List<String> getTrevosSorteados() {
        return trevosSorteados;
    }
    public void setTrevosSorteados(List<String> trevosSorteados) {
        this.trevosSorteados = trevosSorteados;
    }

    public List<String> getSeusTrevos() {
        return seusTrevos;
    }
    public void setSeusTrevos(List<String> seusTrevos) {
        this.seusTrevos = seusTrevos;
    }

    public BigDecimal getValorPremio() {
        return valorPremio;
    }
    public void setValorPremio(BigDecimal valorPremio) {
        this.valorPremio = valorPremio;
    }

    public boolean isSurpresinha() {
        return isSurpresinha;
    }

    public void setSurpresinha(boolean surpresinha) {
        isSurpresinha = surpresinha;
    }

    public boolean isTemAcordeon() {
        return temAcordeon;
    }

    public void setTemAcordeon(boolean temAcordeon) {
        this.temAcordeon = temAcordeon;
    }

    public boolean isBolao() {
        return isBolao;
    }

    public void setBolao(boolean bolao) {
        isBolao = bolao;
    }
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
