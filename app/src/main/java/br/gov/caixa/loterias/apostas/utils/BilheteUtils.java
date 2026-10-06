package br.gov.caixa.loterias.apostas.utils;

public class BilheteUtils {

	public static String getNSBFormatado(String nsb) {
		String nsbFormatado = "";
		try {
			if (!nsb.contains("-")){
				nsbFormatado = nsb.substring(0, 4) + "-" + nsb.substring(4, nsb.length()-2) + "-" + nsb.substring(nsb.length()-2);
			} else {
				nsbFormatado = nsb;
			}
		}catch (Exception e){
			return nsb;
		}
		return nsbFormatado;
	}

}
