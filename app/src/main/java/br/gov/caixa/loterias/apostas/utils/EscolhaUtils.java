package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class EscolhaUtils {
    public static String obterTextoEscolha(ModalidadeEnum modalidade) {

        if (modalidade == ModalidadeEnum.MAIS_MILIONARIA) {
            return "Escolher Trevos";
        }

        if (modalidade == ModalidadeEnum.DIA_DE_SORTE) {
            return "Escolher mes da sorte";
        }

        if (modalidade == ModalidadeEnum.TIMEMANIA) {
            return "Escolher time do coracao";
        }

        return "Escolha";
    }
}
