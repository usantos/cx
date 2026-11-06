package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.BuildConfig;

public class BuildVersionUtil {
	private static final String EMULADOR = "emulador";
	private static final String TQS = "tqs";
	private static final String PRD = "prd";
	private static final String AUTOMACAO = "automacao";

	public static boolean isEmulador(){
		return isVersao(EMULADOR);
	}

	public static boolean isTQS(){
		return isVersao(TQS);
	}

	public static boolean isPRD(){
		return isVersao(PRD);
	}

	public static boolean isAutomacao(){
		return isVersao(AUTOMACAO);
	}

	private static boolean isVersao(String buildVersion){
		return BuildConfig.FLAVOR.equalsIgnoreCase(buildVersion);
	}

}
