package br.gov.caixa.loterias.apostas.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;

public final class ApostaIdentityKey {

    private static final String NUMEROS = "|NUMEROS=";
    private static final String TREVOS = "|TREVOS=";
    private static final String TIME = "|TIME=";
    private static final String MES = "|MES=";
    private static final String MATRIZ = "|MATRIZ=";
    private static final String SEP = "|";

    private ApostaIdentityKey() {
        // Classe utilitária para evitar instanciação
    }

    public static String gerar(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {

        if (aposta == null || aposta.getModalidade() == null || Boolean.TRUE.equals(aposta.getIndicadorCotaBolao())) {
            return "";
        }

        switch (aposta.getModalidade()) {

            case SUPER_7:
                return gerarSuperSete(aposta);

            case MAIS_MILIONARIA:
                return gerarMaisMilionaria(aposta);

            case TIMEMANIA:
                return gerarTimemania(aposta);

            case DIA_DE_SORTE:
                return gerarDiaDeSorte(aposta);

            case LOTECA:
                return gerarLoteca(aposta);

            case LOTOGOL:
                return gerarLotogol(aposta);

            default:
                return gerarNumeros(aposta);
        }
    }

    private static String gerarLotogol(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        StringBuilder sb = new StringBuilder();
        sb.append(aposta.getModalidade());
        List<PartidaLotogolDTO> partidas = aposta.getPartidasLotogol();
        if (partidas == null) {
            return sb.toString();
        }
        for (PartidaLotogolDTO partida : partidas) {
            sb.append(SEP).append(partida.getIndexPlacarEquipe1()).append("x").append(partida.getIndexPlacarEquipe2());
        }
        return sb.toString();
    }

    private static String gerarLoteca(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        StringBuilder sb = new StringBuilder();
        sb.append(aposta.getModalidade());
        List<PartidaLotecaDTO> partidasOriginais = aposta.getPartidasLoteca();
        if (partidasOriginais == null) {
            return sb.toString();
        }
        List<PartidaLotecaDTO> partidas = new ArrayList<>(partidasOriginais);
        // Ordena por número, tratando partidas/números nulos como "maiores" (vão para o final),
        // evitando NullPointerException que o antigo (a, b) -> a.getNumero().compareTo(b.getNumero()) causaria.
        // Comparator.nullsLast/comparing exigem API 24+; projeto tem minSdk 23, por isso a comparação manual abaixo.
        Collections.sort(partidas, (a, b) -> {
            Integer na = (a != null) ? a.getNumero() : null;
            Integer nb = (b != null) ? b.getNumero() : null;
            if (na == null && nb == null) return 0;
            if (na == null) return 1;
            if (nb == null) return -1;
            return na.compareTo(nb);
        });
        
        for (PartidaLotecaDTO partida : partidas) {
            String array = getProg(partida);
            String numero = partida.getNumero() != null ? String.valueOf(partida.getNumero()) : "";
            sb.append(SEP).append(numero).append("=").append(array);
        }
        return sb.toString();
    }

    @NonNull
    private static String getProg(PartidaLotecaDTO partida) {
        StringBuilder prognostico = new StringBuilder();
        if (partida.getEquipe1() != null && Boolean.TRUE.equals(partida.getEquipe1().getIndicadorSelecao())) {
            prognostico.append("1");
        }
        if (Boolean.TRUE.equals(partida.getEmpate())) {
            prognostico.append("X");
        }
        if (partida.getEquipe2() != null && Boolean.TRUE.equals(partida.getEquipe2().getIndicadorSelecao())) {
            prognostico.append("2");
        }
        return prognostico.toString();
    }

    private static String gerarDiaDeSorte(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        return aposta.getModalidade() + NUMEROS + normalizar(aposta.getListaNumerosSelecionados())
                + MES + extrairNumeroMes(aposta.getMesDeSorte());
    }

    private static String gerarTimemania(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        return aposta.getModalidade() + NUMEROS + normalizar(aposta.getListaNumerosSelecionados())
                + TIME + extrairNumeroTime(aposta.getTimeDoCoracao());
    }

    private static String extrairNumeroMes(ParametroMesDeSorte mes) {
        return mes != null && mes.getNumero() != null ? String.valueOf(mes.getNumero()) : "";
    }

    private static String extrairNumeroTime(ParametroEquipe time) {
        return time != null && time.getNumero() != null ? String.valueOf(time.getNumero()) : "";
    }

    private static String gerarMaisMilionaria(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        return aposta.getModalidade() + NUMEROS + normalizar(aposta.getListaNumerosSelecionados()) + TREVOS + normalizar(aposta.getTrevosSelecionados());
    }

    private static String gerarSuperSete(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        ArrayList<ArrayList<Integer>> matriz = aposta.getMatrizNumerosSelecionados();
        List<List<Integer>> copia = new ArrayList<>();
        if (matriz != null) {
            for (List<Integer> coluna : matriz) {
                copia.add(coluna != null ? new ArrayList<>(coluna) : new ArrayList<>());
            }
        }

        return aposta.getModalidade() + MATRIZ + Utils.ordenaMatrizInteiro(copia);
    }

    private static String gerarNumeros(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {

        List<Integer> numeros = aposta.getListaNumerosSelecionados();

        return aposta.getModalidade() + SEP + normalizar(numeros);
    }

    private static List<Integer> normalizar(List<Integer> valores) {

        if (valores == null) {
            return Collections.emptyList();
        }

        List<Integer> copia = new ArrayList<>(valores);
        Collections.sort(copia);

        return Collections.unmodifiableList(copia);
    }

}