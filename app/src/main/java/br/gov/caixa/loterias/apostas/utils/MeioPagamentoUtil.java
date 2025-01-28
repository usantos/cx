package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;

public class MeioPagamentoUtil {

	public static final Long MERCADO_PAGO = 1L;
	public static final Long RECARGA_PAY  = 5L;
	public static final Long PIX = 6L;
	public static final Long AGENCIA = 9770L;
	public static final Long LOTERICA = 9660L;

	public static final String MERCADO_PAGO_DESCRICAO = "Mercado Pago";
	public static final String RECARGA_PAY_DESCRICAO = "Recarga Pay";
	public static final String PIX_DESCRICAO = "Pix";


	public static boolean isMercadoPago(DTOEnumLong meioPagamento) {
		return meioPagamento.getValor() == MERCADO_PAGO;
	}

	public static boolean isRecargaPay(DTOEnumLong meioPagamento) {
		return meioPagamento.getValor() == RECARGA_PAY;
	}

	public static boolean isPix(DTOEnumLong meioPagamento) {
		return meioPagamento.getValor() == PIX;
	}

	public static String getDescricao(DTOEnumLong meioPagamento){
		if (meioPagamento.getValor() == MERCADO_PAGO) {
			return MERCADO_PAGO_DESCRICAO;
		} else if (meioPagamento.getValor() == RECARGA_PAY) {
			return RECARGA_PAY_DESCRICAO;
		} else if (meioPagamento.getValor() == PIX) {
			return PIX_DESCRICAO;
		}
		return "";
	}

}
