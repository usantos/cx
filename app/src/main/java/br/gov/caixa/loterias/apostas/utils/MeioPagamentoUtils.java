package br.gov.caixa.loterias.apostas.utils;


public class MeioPagamentoUtils {
	public static final Long MERCADO_PAGO = 1L;
	public static final Long RECARGAPAY = 5L;
	public static final String MP_MERCADO_PAGO = "1";
	public static final String MP_RECARGA_PAY = "5";

	public static boolean isMercadoPago(Long valor){
		return valor.equals(MERCADO_PAGO);
	}

	public static boolean isRecargaPay(Long valor) {
		return valor.equals(RECARGAPAY);
	}
}
