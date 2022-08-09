package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;

public class SituacaoCompraUtil {
	// Situacoes negativas
	public static final String CANCELADA = "Cancelada";
	public static final String ESTORNADA = "ESTORNADA";
	public static final String DEBITO_NAO_AUTORIZADO = "DÉBITO NÃO AUTORIZADO";
	public static final String NEGATIVA = "NEGATIVA";

	// Situacoes positivas
	public static final String FINALIZADA = "Finalizada";
	public static final String EFETIVADA = "Efetivada";
	public static final String PREMIO_PAGO = "Prêmio Pago";
	public static final String POSITIVA = "POSITIVA";
	public static final String DEBITO_REALIZADO = "DÉBITO REALIZADO";

	// Situacao em processamento
	public static final String INDEFINICAO = "INDEFINIÇÃO";
	public static final String ANDAMENTO = "ANDAMENTO";
	public static final String CARRINHO = "CARRINHO";
	public static final String DEBITO_INICIADO = "Débito Iniciado";
	public static final String EM_PROCESSAMENTO = "EM PROCESSAMENTO";
	public static final String EM_PROCESSAMENTO_REPRESADA = "EM PROCESSAMENTO REPRESADA";

	private static final int VALOR_EFETIVADA = 4;
	private static final int VALOR_FINALIZADA = 8;
	private static final int VALOR_CANCELADA = 5;
	private static final int VALOR_PREMIO_PAGO = 11;

	public static boolean isPremioPago(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_PREMIO_PAGO || situacao.getDescricao().equalsIgnoreCase(PREMIO_PAGO);
	}

	public static boolean isEfetivada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_EFETIVADA || situacao.getDescricao().equalsIgnoreCase(EFETIVADA);
	}

	public static boolean isCancelada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_CANCELADA || situacao.getDescricao().equalsIgnoreCase(CANCELADA);
	}

	public static boolean isFinalizada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_FINALIZADA || situacao.getDescricao().equalsIgnoreCase(FINALIZADA);
	}

	public static boolean isSituacaoPositiva(DTOEnumLong situacao) {
		return situacao.getDescricao().equalsIgnoreCase(POSITIVA) ||
				situacao.getDescricao().equalsIgnoreCase(DEBITO_REALIZADO) ||
				situacao.getDescricao().equalsIgnoreCase(FINALIZADA);
	}

	public static boolean isSituacaoNegativa(DTOEnumLong situacao) {
		return situacao.getDescricao().equalsIgnoreCase(NEGATIVA) ||
				situacao.getDescricao().equalsIgnoreCase(DEBITO_NAO_AUTORIZADO) ||
				situacao.getDescricao().equalsIgnoreCase(ESTORNADA) ||
				situacao.getDescricao().equalsIgnoreCase(CANCELADA);
	}


	public static boolean isSituacaoProcessamento(DTOEnumLong situacao) {
		return situacao.getDescricao().equalsIgnoreCase(INDEFINICAO) ||
				situacao.getDescricao().equalsIgnoreCase(ANDAMENTO) ||
				situacao.getDescricao().equalsIgnoreCase(CARRINHO) ||
				situacao.getDescricao().equalsIgnoreCase(DEBITO_INICIADO) ||
				situacao.getDescricao().equalsIgnoreCase(EM_PROCESSAMENTO) ||
				situacao.getDescricao().equalsIgnoreCase(EM_PROCESSAMENTO_REPRESADA);
	}
}
