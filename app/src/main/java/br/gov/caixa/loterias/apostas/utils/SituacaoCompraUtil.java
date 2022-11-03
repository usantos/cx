package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;

public class SituacaoCompraUtil {
//    AZUL (POSITIVA),
//        DEBITO_REALIZADO(6L),
//        FINALIZADA(8L),
//
//    VERMELHO (NEGATIVA),
//        DEBITO_NAO_AUTORIZADO(3L),
//        ESTORNADA(4L),
//        CANCELADA(5L),
//
//    AMARELO (INDEFINICAO,ANDAMENTO),
//        CARRINHO(1L),
//        DEBITO_INICIADO(2L),
//        EM_PROCESSAMENTO(7L),
//        EM_PROCESSAMENTO_REPRESADA(9L);

	private static final int VALOR_CARRINHO = 1;
	private static final int VALOR_DEBITO_INICIADO = 2;
	private static final int VALOR_DEBITO_NAO_AUTORIZADO = 3;
	private static final int VALOR_ESTORNADA = 4;
	private static final int VALOR_CANCELADA = 5;
	private static final int VALOR_DEBITO_REALIZADO = 6;
	private static final int VALOR_EM_PROCESSAMENTO = 7;
	private static final int VALOR_FINALIZADA = 8;
	private static final int VALOR_EM_PROCESSAMENTO_REPRESADA = 9;

	public static boolean isDebitoNaoAutorizado(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_DEBITO_NAO_AUTORIZADO;
	}

	public static boolean isCancelada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_CANCELADA;
	}

	public static boolean isSituacaoPositiva(DTOEnumLong situacao) {
		return  situacao.getValor() == VALOR_DEBITO_REALIZADO ||
				situacao.getValor() == VALOR_FINALIZADA;
	}

	public static boolean isSituacaoNegativa(DTOEnumLong situacao) {
		return  situacao.getValor() == VALOR_DEBITO_NAO_AUTORIZADO ||
				situacao.getValor() == VALOR_ESTORNADA ||
				situacao.getValor() == VALOR_CANCELADA;
	}

	public static boolean isSituacaoProcessamento(DTOEnumLong situacao) {
		return 	situacao.getValor() == VALOR_CARRINHO ||
				situacao.getValor() == VALOR_DEBITO_INICIADO ||
				situacao.getValor() == VALOR_EM_PROCESSAMENTO ||
				situacao.getValor() == VALOR_EM_PROCESSAMENTO_REPRESADA;
	}
}
