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
    private boolean apurado = true;
    private boolean temAcordeon = true;
    private boolean isBolao = false;
    private boolean isExpanded = false;

    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, String timeMesSorteado, String seuTimeMes, List<String> numerosSorteados2, List<String> seusNumeros2, List<String> trevosSorteados, List<String> seusTrevos, BigDecimal valorPremio, boolean apurado) {
        this.modalidade = modalidade;
        this.concurso = concurso;
        this.situacao = situacao;
        this.numerosSorteados = numerosSorteados;
        this.seusNumeros = seusNumeros;
        this.timeMesSorteado = timeMesSorteado;
        this.seuTimeMes = seuTimeMes;
        this.numerosSorteados2 = numerosSorteados2;
        this.seusNumeros2 = seusNumeros2;
        this.trevosSorteados = trevosSorteados;
        this.seusTrevos = seusTrevos;
        this.valorPremio = valorPremio;
        this.apurado = apurado;
    }

    public ItemMinhasApostasDTO() {
    }

    //Construtor concurso não apurado
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, List<String> seusNumeros) {
        this(modalidade, null, null, null, seusNumeros, null, null, null, null, null, null, null, false);
    }

    //Construtor LOTOFACIL
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, BigDecimal valorPremio) {
        this(modalidade, concurso, situacao, numerosSorteados, seusNumeros, null, null, null, null, null, null, valorPremio, true);
    }

    //Construtor TIMEMANIA MESSORTE
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, String timeMesSorteado, String seuTimeMes, BigDecimal valorPremio) {
        this(modalidade, concurso, situacao, numerosSorteados, seusNumeros, timeMesSorteado, seuTimeMes, null, null, null, null, valorPremio, true);
    }

    //Construtor DUPLASENA
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, List<String> numerosSorteados2, List<String> seusNumeros2, BigDecimal valorPremio) {
        this(modalidade, concurso, situacao, numerosSorteados, seusNumeros, null, null, numerosSorteados2, seusNumeros2, null, null, valorPremio, true);
    }

    //Construtor MAIS_MILIONARIA
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, BigDecimal valorPremio, List<String> trevosSorteados, List<String> seusTrevos) {
        this(modalidade, concurso, situacao, numerosSorteados, seusNumeros, null, null, null, null, trevosSorteados, seusTrevos, valorPremio, true);
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

    public boolean isApurado() {
        return apurado;
    }

    public void setApurado(boolean apurado) {
        this.apurado = apurado;
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
