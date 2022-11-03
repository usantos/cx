package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;

public class MeioPagamentoUtil {

	public static final Long MERCADO_PAGO = 1L;
	public static final Long RECARGA_PAY  = 5L;

	public static boolean isMercadoPago(DTOEnumLong meioPagamento) {
		return meioPagamento.getValor() == MERCADO_PAGO;
	}

	public static boolean isRecargaPay(DTOEnumLong meioPagamento) {
		return meioPagamento.getValor() == RECARGA_PAY;
	}
}
