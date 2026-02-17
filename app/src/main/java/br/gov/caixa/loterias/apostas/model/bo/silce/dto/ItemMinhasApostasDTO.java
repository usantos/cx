package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.math.BigDecimal;
import java.util.List;

public class ItemMinhasApostasDTO {

    private final ModalidadeEnum modalidade;
    private final String concurso;
    private final String situacao;
    private final List<String> numerosSorteados;
    private final List<String> seusNumeros;
    private final String timeMesSorteado;
    private final String seuTimeMes;
    private final List<String> numerosSorteados2;
    private final List<String> seusNumeros2;
    private final BigDecimal valorPremio;
    private final boolean apurado;
    private boolean isExpanded = false;

    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, String timeMesSorteado, String seuTimeMes, List<String> numerosSorteados2, List<String> seusNumeros2, BigDecimal valorPremio, boolean apurado) {
        this.modalidade = modalidade;
        this.concurso = concurso;
        this.situacao = situacao;
        this.numerosSorteados = numerosSorteados;
        this.seusNumeros = seusNumeros;
        this.timeMesSorteado = timeMesSorteado;
        this.seuTimeMes = seuTimeMes;
        this.numerosSorteados2 = numerosSorteados2;
        this.seusNumeros2 = seusNumeros2;
        this.valorPremio = valorPremio;
        this.apurado = apurado;
    }

    //Construtor concurso não apurado
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, List<String> seusNumeros) {
        this(modalidade, null, null, null, seusNumeros, null, null, null, null, null, false);
    }

    //Construtor LOTOFACIL
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, BigDecimal valorPremio) {
        this(modalidade, concurso, situacao, numerosSorteados, seusNumeros, null, null, null, null, valorPremio, true);
    }

    //Construtor TIMEMANIA MESSORTE
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, String timeMesSorteado, String seuTimeMes, BigDecimal valorPremio) {
        this(modalidade, concurso, situacao, numerosSorteados, seusNumeros, timeMesSorteado, seuTimeMes, null, null, valorPremio, true);
    }

    //Construtor DUPLASENA
    public ItemMinhasApostasDTO(ModalidadeEnum modalidade, String concurso, String situacao, List<String> numerosSorteados, List<String> seusNumeros, List<String> numerosSorteados2, List<String> seusNumeros2, BigDecimal valorPremio) {
        this(modalidade, concurso, situacao, numerosSorteados, seusNumeros, null, null, numerosSorteados2, seusNumeros2, valorPremio, true);
    }

    public ModalidadeEnum getModalidade() {
        return modalidade;
    }

    public String getConcurso() {
        return concurso;
    }

    public String getSituacao() {
        return situacao;
    }

    public List<String> getNumerosSorteados() {
        return numerosSorteados;
    }

    public List<String> getSeusNumeros() {
        return seusNumeros;
    }

    public String getTimeMesSorteado() {
        return timeMesSorteado;
    }

    public String getSeuTimeMes() {
        return seuTimeMes;
    }

    public List<String> getNumerosSorteados2() {
        return numerosSorteados2;
    }

    public List<String> getSeusNumeros2() {
        return seusNumeros2;
    }

    public BigDecimal getValorPremio() {
        return valorPremio;
    }

    public boolean isApurado() {
        return apurado;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
