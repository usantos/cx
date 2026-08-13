package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;

public class ServicoNuvemUtil {

	public static final String APIKEY_DESCRICAO = "ocp-apim-subscription-key";
	//public static final String APIKEY_CHAVE = "8f9c0e5ec9604864acd357706e7b6522"; //DES
	public static final String APIKEY_CHAVE_TQS = "ca5af87c9f7647de88eb7dcb93c7b021";
	public static final String APIKEY_CHAVE_PRD = "635c94f6079c4c909e88389564c7e0fb";
	public static final String AZURE_API = "azure-api.net";
	public static final String PRD_API = "carrinho.caixa.gov.br";
	public static final String TQS_API = "carrinho.tqs.caixa.gov.br";
	public static final String DES_API = "carrinho.des.caixa.gov.br";

	private static final String PUBLICO = "/publico";
	private static final String LOGADO = "/logado";

	public static boolean isEndpointNuvem(String endpoint) {
		if (endpoint.startsWith(PUBLICO) || endpoint.startsWith(LOGADO)) {
			return true;
		}
		return false;
	}

	public static boolean isAmbienteTQS() {
		return !(BuildConfig.FLAVOR.equalsIgnoreCase("prd"));
	}

	public static void apresentaCaminhoTQS(String caminhoCompeto) {
		if (isAmbienteTQS()){
			//ToastUtil.toastShort(novoCaminho + fillParamToString(queryParams));
			MessagingUtils.criaNotificacaoNovaExpansivel(Aplicacao.application.getApplicationContext(),
					DateUtils.getDateTime(),
					caminhoCompeto,
					caminhoCompeto);
		}
	}

	public static String apiKeyChave() {
		if (isAmbienteTQS()) {
			return APIKEY_CHAVE_TQS;
		} else {
			return APIKEY_CHAVE_PRD;
		}
	}

	public static String getBaseUrlNuvem() {
		String baseUrlCarrinho;

		if(isAmbienteTQS()) {
			baseUrlCarrinho = SharedPreferencesUtils.getValorString(ConfiguracoesEnum.URL_BASE_CARRINHO_PILOTO.get(), ConfiguracoesDefaultEnum.URL_BASE_CARRINHO_PILOTO.asString());
			//baseUrlCarrinho = "https://apim-silce-tqs.azure-api.net/loterias-web/carrinho/v1"; //TQS
			//"https://silce.carrinho.tqs.caixa.gov.br/loterias-web/carrinho/v1"
		} else {
			baseUrlCarrinho = SharedPreferencesUtils.getValorString(ConfiguracoesEnum.URL_BASE_CARRINHO_PRODUCAO.get(), ConfiguracoesDefaultEnum.URL_BASE_CARRINHO_PRODUCAO.asString());
		}

		return baseUrlCarrinho;
	}

}
