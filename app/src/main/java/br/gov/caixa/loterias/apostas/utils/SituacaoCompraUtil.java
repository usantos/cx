package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;

public class SituacaoCompraUtil {
	public static final String CANCELADA = "Cancelada";
	public static final String FINALIZADA = "Finalizada";
	public static final String EFETIVADA = "Efetivada";

	private static final int VALOR_EFETIVADA = 4;
	private static final int VALOR_FINALIZADA = 8;
	private static final int VALOR_CANCELADA = 5;

	public static boolean isPremioPago() {
		return false;
	}

	public static boolean isEfetivada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_EFETIVADA || situacao.getDescricao().equalsIgnoreCase(EFETIVADA);
	}

	public static boolean isCancelada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_CANCELADA || situacao.getDescricao().equalsIgnoreCase(CANCELADA);
	}

	public static boolean isFinalizada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_EFETIVADA || situacao.getDescricao().equalsIgnoreCase(FINALIZADA);
	}
}
