package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.BuildConfig;

public class BuildConfigManager {

	private static String getAmbienteValorEspecifico(String variavel, AmbienteEnum ambienteEnum)  {
		if (ambienteEnum == null) {
			return getValueFromBuildConfig(variavel);
		} else {
			return getValueFromBuildConfig(variavel + "_" + ambienteEnum);
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
		if (BuildConfig.FLAVOR.equals("prd") || BuildConfig.FLAVOR.equals("automacao")) {
			return getAmbienteValorEspecifico(variavel, null);
		} else {
			String ambienteSalvo = SharedPreferencesUtils.getValorString("AMBIENTE_SELECIONADO", AmbienteEnum.EXTERNO_ESTEIRA.name());
			AmbienteEnum ambienteEnum = AmbienteEnum.valueOf(ambienteSalvo);
			return getAmbienteValorEspecifico(variavel, ambienteEnum);
		}
	}

}
