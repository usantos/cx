package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraDTO;
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
	private static final int VALOR_AGUARDANDO_PAGAMENTO_PIX = 10;
	private static final int VALOR_AGUARDANDO_DEVOLUCAO_PIX = 11;
	private static final int VALOR_CANCELADA_PAGAMENTO_NAO_IDENTIFICADO_TEMPO_LIMITE = 12;
	private static final int VALOR_FINALIZADA_TODAS_APOSTAS_EFETIVADAS = 13;
	private static final int VALOR_FINALIZADA_CONTEM_APOSTAS_NAO_EFETIVADAS = 14;

	public static boolean isDebitoNaoAutorizado(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_DEBITO_NAO_AUTORIZADO;
	}

	public static boolean isEstornada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_ESTORNADA;
	}

	public static boolean isCancelada(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_CANCELADA;
	}

	public static boolean isSituacaoPositiva(DTOEnumLong situacao) {
		return  situacao.getValor() == VALOR_DEBITO_REALIZADO ||
				situacao.getValor() == VALOR_FINALIZADA ||
				situacao.getValor() == VALOR_FINALIZADA_TODAS_APOSTAS_EFETIVADAS ||
				situacao.getValor() == VALOR_FINALIZADA_CONTEM_APOSTAS_NAO_EFETIVADAS;
	}

	public static boolean isSituacaoFINALIZADA(Integer situacao) {
		return  situacao != null && (situacao.equals(VALOR_DEBITO_REALIZADO) ||
				situacao.equals(VALOR_FINALIZADA) ||
				situacao.equals(VALOR_FINALIZADA_TODAS_APOSTAS_EFETIVADAS) ||
				situacao.equals(VALOR_FINALIZADA_CONTEM_APOSTAS_NAO_EFETIVADAS));
	}

	public static boolean isSituacaoNegativa(DTOEnumLong situacao) {
		return  situacao.getValor() == VALOR_DEBITO_NAO_AUTORIZADO ||
				situacao.getValor() == VALOR_ESTORNADA ||
				situacao.getValor() == VALOR_CANCELADA ||
				situacao.getValor() == VALOR_CANCELADA_PAGAMENTO_NAO_IDENTIFICADO_TEMPO_LIMITE ||
				situacao.getValor() == VALOR_AGUARDANDO_DEVOLUCAO_PIX;
	}

	public static boolean isSituacaoProcessamento(DTOEnumLong situacao) {
		return 	situacao.getValor() == VALOR_CARRINHO ||
				situacao.getValor() == VALOR_DEBITO_INICIADO ||
				situacao.getValor() == VALOR_EM_PROCESSAMENTO ||
				situacao.getValor() == VALOR_EM_PROCESSAMENTO_REPRESADA ||
				situacao.getValor() == VALOR_AGUARDANDO_PAGAMENTO_PIX ||
				situacao.getValor() == VALOR_AGUARDANDO_DEVOLUCAO_PIX;
	}

	public static boolean isSituacaoAguardandoPagamento(DTOEnumLong situacao) {
		return 	situacao.getValor() == VALOR_AGUARDANDO_PAGAMENTO_PIX;
	}

	public static boolean isCanceladaPix(DTOEnumLong situacao) {
		return situacao.getValor() == VALOR_CANCELADA_PAGAMENTO_NAO_IDENTIFICADO_TEMPO_LIMITE;
	}

	public static String getDescricao(CompraDTO item) {
		String descricao = "";
		if (MeioPagamentoUtil.isPix(item.getMeioPagamento()) && SituacaoCompraUtil.isCancelada(item.getSituacao())){
			descricao = Aplicacao.application.getBaseContext().getResources().getString(R.string.popup_pix_nao_realizado);
		} else {
			descricao = item.getSituacao().getDescricao();
		}
		return descricao;
	}

	public static  boolean isRepresada(DTOEnumLong situacao){
		return situacao.getValor() == VALOR_EM_PROCESSAMENTO_REPRESADA;
	}

}
