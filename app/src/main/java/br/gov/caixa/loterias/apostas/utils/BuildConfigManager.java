package br.gov.caixa.loterias.apostas.utils;

import android.widget.ActionMenuView;

import br.gov.caixa.loterias.apostas.BuildConfig;

public class BuildConfigManager {

	private static String getCofigValue(String variavel, AmbienteEnum ambienteEnum) {
		if (BuildConfig.BUILD_TYPE.equals("prd")) {
			switch (variavel) {
				case "CAIXA_BASE_URL_SILCE" :
					return BuildConfig.CAIXA_BASE_URL_SILCE;
				case "SSO_CLIENT_ID" :
					return BuildConfig.SSO_CLIENT_ID;
				case "SSO_REDIRECT" :
					return BuildConfig.SSO_REDIRECT;
				case "URL_SSO" :
					return BuildConfig.URL_SSO;
				case "URL_ENDPOINT_TOKEN" :
					return BuildConfig.URL_ENDPOINT_TOKEN;
				case "URL_SSO_TOKEN" :
					return BuildConfig.URL_SSO_TOKEN;
				case "URL_SSO_AUTH" :
					return BuildConfig.URL_SSO_AUTH;
				case "URL_SSO_LOGOUT" :
					return BuildConfig.URL_SSO_LOGOUT;
				case "URL_SSO_ACCOUNT" :
					return BuildConfig.URL_SSO_ACCOUNT;
				case "MP_PAYMENT_URL" :
					return BuildConfig.MP_PAYMENT_URL;
				case "MP_CREATE_TOKEN_URL" :
					return BuildConfig.MP_CREATE_TOKEN_URL;
				case "RP_CREATE_TOKEN_URL" :
					return BuildConfig.RP_CREATE_TOKEN_URL;
				case "APP_CENTER_KEY" :
					return BuildConfig.APP_CENTER_KEY;
				case "QUEUE_IT_CUSTOMER_ID" :
					return BuildConfig.QUEUE_IT_CUSTOMER_ID;
				case "QUEUE_IT_EVENT_ALIAS_ID" :
					return BuildConfig.QUEUE_IT_EVENT_ALIAS_ID;
				case "QUEUE_IT_CUSTOM_LAYOUT_NAME" :
					return BuildConfig.QUEUE_IT_CUSTOM_LAYOUT_NAME;
				default:
					System.out.println("Não encontrou no BuildConfigManager: "+variavel);
					return null;
			}
		} else {

			return getAmbienteValorEspecifico(variavel, ambienteEnum);

		}
	}

	private static String getAmbienteValorEspecifico(String variavel, AmbienteEnum ambienteEnum)  {
		switch (ambienteEnum) {
			case EXTERNO:
				return getValueFromBuildConfig(variavel + "_EXTERNO");
			case INTERNO:
				return getValueFromBuildConfig(variavel + "_INTERNO");
			case PILOTO:
				return getValueFromBuildConfig(variavel + "_PILOTO");
			case HML:
				return getValueFromBuildConfig(variavel + "_HML");
			default:
				throw new RuntimeException("Ambeinte inesperado: "+ ambienteEnum);
		}
	}

	private static String getValueFromBuildConfig(String variavel) {
		try {
			return (String) BuildConfig.class.getField(variavel).get(null);
		} catch (Exception e) {
			throw new RuntimeException("Nao encontrou no BuildConfig: "+ variavel, e);
		}
	}

	public static String getVariavel(String variavel) {
		String ambienteSalvo = SharedPreferencesUtils.getValorString("AMBIENTE_SELECIONADO", AmbienteEnum.EXTERNO.name());
		AmbienteEnum ambienteEnum = AmbienteEnum.valueOf(ambienteSalvo);
		return getCofigValue(variavel, ambienteEnum);
	}

}
