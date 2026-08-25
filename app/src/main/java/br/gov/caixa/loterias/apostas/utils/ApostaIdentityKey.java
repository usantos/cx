package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;

public class ApostaIdentityKey {

    public static String gerar(
            IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {

        if (aposta == null || aposta.getModalidade() == null) {
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

            default:
                return gerarNumeros(aposta);
        }
    }

    private static String gerarDiaDeSorte(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        return aposta.getModalidade()
                + "|NUMEROS="
                + normalizar(
                aposta.getListaNumerosSelecionados()
        )
                + "|MES="
                + String.valueOf(aposta.getMesDeSorte());
    }

    private static String gerarTimemania(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        return aposta.getModalidade()
                + "|NUMEROS="
                + normalizar(
                aposta.getListaNumerosSelecionados()
        )
                + "|TIME="
                + String.valueOf(aposta.getTimeDoCoracao());
    }

    private static String gerarMaisMilionaria(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        return aposta.getModalidade()
                + "|NUMEROS="
                + normalizar(
                        aposta.getListaNumerosSelecionados()
                )
                + "|TREVOS="
                + normalizar(
                        aposta.getTrevosSelecionados()
                );
    }

    private static String gerarSuperSete(IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {
        return aposta.getModalidade()
                + "|MATRIZ"
                + String.valueOf(aposta.getMatrizNumerosSelecionados());
    }

    private static String gerarNumeros(
            IdentificaoDeUmaApostaDas8Modalidades<?> aposta) {

        List<Integer> numeros =
                aposta.getListaNumerosSelecionados();

        return aposta.getModalidade()
                + "|" +
                normalizar(numeros);
    }

    private static List<Integer> normalizar(List<Integer> valores) {

        if (valores == null) {
            return Collections.emptyList();
        }

        List<Integer> copia = new ArrayList<>(valores);
        Collections.sort(copia);

        return copia;
    }

}