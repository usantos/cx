package br.gov.caixa.loterias.apostas.utils;


import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;

public class MeioPagamentoUtils {
	public static final Long MERCADO_PAGO_VALUE = 1L;
	public static final Long RECARGAPAY_VALUE = 5L;
	public static final Long PIX = 6L;
	public static final String MP_MERCADO_PAGO = "1";
	public static final String MP_RECARGA_PAY = "5";

	public static final String MERCADO_PAGO_DESCRICAO = "Mercado pago";
	public static final String REGARGAPAY_DESCRICAO = "RecargaPay";
	public static final String PIX_DESCRICAO = "Pix";

	public static boolean isMercadoPago(Long valor){
		return valor.equals(MERCADO_PAGO_VALUE);
	}

	public static boolean isRecargaPay(Long valor) {
		return valor.equals(RECARGAPAY_VALUE);
	}

	public static DTOEnumLong getDTOENUM(Long valor, String descricao) {
		DTOEnumLong dtoEnum = new DTOEnumLong();
		dtoEnum.setValor(valor);
		dtoEnum.setDescricao(descricao);
		return dtoEnum;
	}

	public static MeioPagamentoConfig getConfigPix(boolean showSeta) {
		return new MeioPagamentoConfig(PIX_DESCRICAO,
									   R.color.verde_pix, R.drawable.ic_pix,
									   MeioPagamentoUtils.getDTOENUM(PIX, PIX_DESCRICAO), showSeta);
	}

	public static MeioPagamentoConfig getConfigRecarga(String titulo, boolean showSeta) {
		return new MeioPagamentoConfig(titulo, R.color.laranja_recarga_pay, R.drawable.recargapay,
									   MeioPagamentoUtils.getDTOENUM(RECARGAPAY_VALUE, REGARGAPAY_DESCRICAO), showSeta);
	}

	public static MeioPagamentoConfig getConfigMercadoPago(String titulo, boolean showSeta) {
		return new MeioPagamentoConfig(titulo, R.color.azul_mercado_pago, R.drawable.ic_mercado_pago,
									   MeioPagamentoUtils.getDTOENUM(MERCADO_PAGO_VALUE, MERCADO_PAGO_DESCRICAO), showSeta);
	}
}
