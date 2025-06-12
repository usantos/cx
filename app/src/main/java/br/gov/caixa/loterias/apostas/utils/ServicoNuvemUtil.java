package br.gov.caixa.loterias.apostas.utils;

import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;

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
	private static final List<String> listPublico = Arrays.asList(
			"/equipes-esportivas/listaEquipes",
			"/parametros-simulacao",
			"/combos",
			"/modalidades");
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
			"/usuarios/verificar-cadastro",
			"/carrinhos/gerar-cobranca-pix");
	private static final List<String> listLogadoParcial = Arrays.asList(
			"/apostas-favoritas",
			"/carrinhos-favoritos",
			"/carrinhos/apostas",
			"/carrinhos/combos",
			"/carrinhos/incluir-aposta-favorita",
			"/carrinhos/incluir-carrinho-favorito");

	private static final List<String> listLogadoEspecial = Arrays.asList(
			"/salvar-carrinho-favorito"); 	//compras/{idCompra}/salvar-carrinho-favorito


	public static String alteraCaminhoNuvem(String path) {

		String baseUrlCarrinho;

		if(isAmbienteTQS()) {
			baseUrlCarrinho = ConfiguracaoAppSingleton.getInstance().getUrlBaseCarrinhoPiloto();
			//baseUrlCarrinho = "https://apim-silce-tqs.azure-api.net/loterias-web/carrinho/v1"; //TQS
		} else {
			baseUrlCarrinho = ConfiguracaoAppSingleton.getInstance().getUrlBaseCarrinhoProducao();
		}

		if (baseUrlCarrinho == null || baseUrlCarrinho.isEmpty() || baseUrlCarrinho.equals("-")) {
			return null;
		}

		if (listPublico.contains(path)) {
			if (cpfAcessamNuvem()) {
				return baseUrlCarrinho + PUBLICO + path;
			}
			return null;
		}

		if (listLogadoExato.contains(path)) {
			if (cpfAcessamNuvem()) {
					return baseUrlCarrinho + LOGADO + path;
			}
			return null;
		}

		for(String parcial : listLogadoParcial) {
			if (path.startsWith(parcial)) {
				if (cpfAcessamNuvem()) {
					return baseUrlCarrinho + LOGADO + path;
				}
				return null;
			}
		}

		for(String parcial : listLogadoEspecial) {
			if (path.contains(parcial)) {
				if (cpfAcessamNuvem()) {
					return baseUrlCarrinho + LOGADO + path;
				}
				return null;
			}
		}

		return null;
	}

	private static boolean isAmbienteTQS() {
		return !BuildConfig.FLAVOR.equalsIgnoreCase("prd");
	}

	private static boolean cpfAcessamNuvem() {
		String cpfAcessam[] = ConfiguracaoAppSingleton.getInstance().getGrupoDeCpfsQueAcessamOServicoDaAzure().split(";");
		int finalCpfAcessamNuvem = new Integer(cpfAcessam[0]);

		if (finalCpfAcessamNuvem == 100) {
			return true;
		}

		String cpf = DadosUsuarioBO.obterCpf();
		if (cpf != null && !cpf.isEmpty() && cpf.length() >= 2) {

			//Verifica lista branca
			for (int i = 1; i < cpfAcessam.length; i++) {
				if (cpf.equals(cpfAcessam[i])) {
					return true;
				}
			}

			//Verifica se o final do CPF < finalCpfAcessamNuvem
			if (Integer.parseInt(cpf.substring(cpf.length() -2)) < finalCpfAcessamNuvem) {
				return true;
			}
		}

		return false;
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

	public static boolean isUrlServicoNuvem(String url) {
		if (url.contains(ServicoNuvemUtil.AZURE_API) ||
				url.contains(ServicoNuvemUtil.PRD_API) ||
				url.contains(ServicoNuvemUtil.TQS_API) ||
				url.contains(ServicoNuvemUtil.DES_API)) {
			return true;
		}
		return false;
	}

	public static String apiKeyChave() {
		if (isAmbienteTQS()) {
			return APIKEY_CHAVE_TQS;
		} else {
			return APIKEY_CHAVE_PRD;
		}
	}

}
