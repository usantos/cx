package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class EscolhaUtils {
    public static String obterTextoEscolha(ModalidadeEnum modalidade) {

        if (modalidade == ModalidadeEnum.MAIS_MILIONARIA) {
            return "Escolher Trevos";
        }

        if (modalidade == ModalidadeEnum.DIA_DE_SORTE) {
            return "Escolher Mês de Sorte";
        }

        if (modalidade == ModalidadeEnum.TIMEMANIA) {
            return "Escolher Time do Coração";
        }

        return "Escolha";
    }
}
