package br.gov.caixa.loterias.apostas.utils;

import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;

public class ServicoNuvemUtil {

	public static final String APIKEY_DESCRICAO = "ocp-apim-subscription-key";
	//public static final String APIKEY_CHAVE = "8f9c0e5ec9604864acd357706e7b6522"; //DES
	public static final String APIKEY_CHAVE = "ca5af87c9f7647de88eb7dcb93c7b021"; //TQS

	public static final String AZURE_API = "azure-api.net";
	//public static final String AZURE_API = ":8080";

	private static final String PUBLICO = "/publico";
	private static final String LOGADO = "/logado";
	private static final List<String> listPublico = Arrays.asList(
			"/equipes-esportivas/listaEquipes",
			"/parametros-simulacao",
			"/combos");
	private static final List<String> listLogadoExato = Arrays.asList(
			"/carrinhos",
			"/carrinhos/atualiza-teimosinha-aposta",
			"/carrinhos/incluir-aposta",
			"/carrinhos/incluir-apostas",
			"/carrinhos/incluir-combo",
			"/carrinhos/incluir-surpresinha",
			"/carrinhos/incluir-surpresinhas",
			"/carrinhos/iniciar-pagamento-externo",
			"/carrinhos/limpar",
			"/carrinhos/limpar-carrinho-favorito",
			"/carrinhos/registrar-apostas",
			"/carrinhos/registrar-apostas-async",
			"/carrinhos/registrar-apostas-sem-carrinho",
			"/carrinhos/salvar-carrinho-favorito",
			"/carrinhos/validar",
			"/carrinhos/validar-aposta-identica",
			"/carrinhos/verifica-compra-processamento",
			"/usuarios/verificar-cadastro");
	private static final List<String> listLogadoParcial = Arrays.asList(
			"/apostas-favoritas",
			"/carrinhos-favoritos",
			"/carrinhos/apostas",
			"/carrinhos/combos",
			"/carrinhos/incluir-aposta-favorita",
			"/carrinhos/incluir-carrinho-favorito");


	public static String alteraCaminhoNuvem(String path) {

		String baseUrlCarrinho;

		if(BuildConfig.DEBUG && !BuildConfig.FLAVOR.equalsIgnoreCase("prd")) {
			baseUrlCarrinho = ConfiguracaoAppSingleton.getInstance().getUrlBaseCarrinhoPiloto();

			//Apenas para teste
			//baseUrlCarrinho = "https://apim-lce-des.azure-api.net/loterias-web/carrinho/v1";
			//baseUrlCarrinho = "http://apim-silce-des.azure-api.net/loterias-web/carrinho/v1/";
			//baseUrlCarrinho = "https://apim-silce-des.azure-api.net/loterias-web/carrinho/v1"; //DES
			baseUrlCarrinho = "https://apim-silce-tqs.azure-api.net/loterias-web/carrinho/v1"; //TQS
			//baseUrlCarrinho = "http://localhost:8080/v1";
		} else {
			baseUrlCarrinho = ConfiguracaoAppSingleton.getInstance().getUrlBaseCarrinhoProducao();
		}

		if (baseUrlCarrinho.equals("-")) {
			return null;
		}

		if (listPublico.contains(path)) {
			if (finalCPFAcessaNuvem()) {
				apresentaCaminhoTQS(baseUrlCarrinho, PUBLICO, path);
				return baseUrlCarrinho + PUBLICO + path;
			}
			return null;
		}

		if (listLogadoExato.contains(path)) {
			if (finalCPFAcessaNuvem()) {
				apresentaCaminhoTQS(baseUrlCarrinho, LOGADO, path);
				return baseUrlCarrinho + LOGADO + path;
			}
			return null;
		}

		for(String parcial : listLogadoParcial) {
			if (path.startsWith(parcial)) {
				if (finalCPFAcessaNuvem()) {
					apresentaCaminhoTQS(baseUrlCarrinho, LOGADO, path);
					return baseUrlCarrinho + LOGADO + path;
				}
				return null;
			}
		}

		return null;
	}

	private static boolean finalCPFAcessaNuvem() {

		int finalCpfAcessamNuvem = ConfiguracaoAppSingleton.getInstance().getGrupoDeCpfsQueAcessamOServicoDaAzure();

		//Apenas para teste
		finalCpfAcessamNuvem = 100;

		if (finalCpfAcessamNuvem == 0) {
			return false;
		}

		String cpf = DadosUsuarioBO.obterCpf();
		if (cpf != null && !cpf.isEmpty() && cpf.length() >= 2) {
			//Verifica se o final do CPF < finalCpfAcessamNuvem
			if (Integer.parseInt(cpf.substring(cpf.length() -2)) < finalCpfAcessamNuvem) {
				return true;
			}
		}

		if (finalCpfAcessamNuvem == 100) {
			return true;
		}

		return false;
	}

	private static void apresentaCaminhoTQS(String baseUrlCarrinho, String pubLog, String path) {
		if (BuildConfig.DEBUG && !BuildConfig.FLAVOR.equalsIgnoreCase("prd")){
			//ToastUtil.toastShort(novoCaminho + fillParamToString(queryParams));
			MessagingUtils.criaNotificacaoNovaExpansivel(Aplicacao.application.getApplicationContext(),
					DateUtils.getDateTime(),
					baseUrlCarrinho,
					baseUrlCarrinho + "\n" + pubLog + path);
		}
	}

	public static boolean isUrlServicoNuvem(String url) {
		return url.contains(ServicoNuvemUtil.AZURE_API);
	}

}
