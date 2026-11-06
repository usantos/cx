package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum UF {
    RO(11, "RO", "Rondônia"),
    AC(12, "AC", "Acre"),
    AM(13, "AM", "Amazonas"),
    RR(14, "RR", "Roraima"),
    PA(15, "PA", "Pará"),
    AP(16, "AP", "Amapá"),
    TO(17, "TO", "Tocantins"),
    MA(21, "MA", "Maranhão"),
    PI(22, "PI", "Piauí"),
    CE(23, "CE", "Ceará"),
    RN(24, "RN", "Rio Grande do Norte"),
    PB(25, "PB", "Paraíba"),
    PE(26, "PE", "Pernambuco"),
    AL(27, "AL", "Alagoas"),
    SE(28, "SE", "Sergipe"),
    BA(29, "BA", "Bahia"),
    MG(31, "MG", "Minas Gerais"),
    ES(32, "ES", "Espírito Santo"),
    RJ(33, "RJ", "Rio de Janeiro"),
    SP(35, "SP", "São Paulo"),
    PR(41, "PR", "Paraná"),
    SC(42, "SC", "Santa Catarina"),
    RS(43, "RS", "Rio Grande do Sul"),
    MS(50, "MS", "Mato Grosso do Sul"),
    MT(51, "MT", "Mato Grosso"),
    GO(52, "GO", "Goiás"),
    DF(53, "DF", "Distrito Federal");

    private final int ibgeId;
    private final String sigla;
    private final String nome;

    UF(int ibgeId, String sigla, String nome) {
        this.ibgeId = ibgeId;
        this.sigla = sigla;
        this.nome = nome;
    }

    public int getIbgeId() { return ibgeId; }
    public String getSigla() { return sigla; }
    public String getNome() { return nome; }

    // --- Índices para busca rápida ---
    private static final Map<Integer, UF> BY_ID;
    private static final Map<String, UF> BY_SIGLA;

    static {
        Map<Integer, UF> byId = new HashMap<>();
        Map<String, UF> bySigla = new HashMap<>();
        for (UF uf : values()) {
            byId.put(uf.ibgeId, uf);
            bySigla.put(uf.sigla.toUpperCase(), uf);
        }
        BY_ID = Collections.unmodifiableMap(byId);
        BY_SIGLA = Collections.unmodifiableMap(bySigla);
    }

    /** Busca pela UF usando o ID IBGE. Retorna null se não existir. */
    public static UF fromId(int ibgeId) {
        return BY_ID.get(ibgeId);
    }

    /** Busca pela UF usando a sigla (case-insensitive). Retorna null se não existir. */
    public static UF fromSigla(String sigla) {
        if (sigla == null) return null;
        return BY_SIGLA.get(sigla.trim().toUpperCase());
    }

    /** Retorna a sigla a partir do ID IBGE, ou null se não encontrado. */
    public static String siglaFromId(int ibgeId) {
        UF uf = fromId(ibgeId);
        return uf != null ? uf.getSigla() : null;
    }

    /** Retorna o nome da UF a partir do ID IBGE, ou null se não encontrado. */
    public static String nomeFromId(int ibgeId) {
        UF uf = fromId(ibgeId);
        return uf != null ? uf.getNome() : null;
    }

}
