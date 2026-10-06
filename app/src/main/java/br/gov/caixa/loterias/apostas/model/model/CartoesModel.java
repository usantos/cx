package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;
import com.mercadolibre.android.device.sdk.DeviceSDK;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MercadoPagoConnection;
import br.gov.caixa.loterias.apostas.model.bo.RecargaPayConnection;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CardHolderRecargaPay;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CartaoRecargaPayDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DataExpiracao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StringResponse;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.CompraUtil;
import br.gov.caixa.loterias.apostas.utils.DataUtil;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class CartoesModel extends AppModel {
	private MeioPagamentoModel model;

	public CartoesModel(Activity activity) {
		super(activity);
		model = new MeioPagamentoModel(getActivity());
	}

	public void buscaCartoesPorMeioPagamento(String meioPagamento, OnSilceListener<List<RetornoCartao>> listener) {
		DadosUsuarioBO.getInstance().cartoes(meioPagamento, new RequestListener<ListCartaoDTOResponse>() {
			@Override
			public void onResponse(ListCartaoDTOResponse response) {
				AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_entrou_formas_pagamento));
				if (response.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso( response.getRedirect(), getActivity());
				}

				listener.success(response.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
				listener.error(error);
			}
		});
	}

	public void deletaCartao(String idCartao, String meioPagamento, OnSilceListener<List<RetornoCartao>> listener) {
		DadosUsuarioBO.getInstance().deleteCartao(idCartao, meioPagamento, new RequestListener<ListCartaoDTOResponse>() {
			@Override
			public void onResponse(ListCartaoDTOResponse result) {
				AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_efetuou_exclusao_forma_pagamento_sucesso));
				if(result != null && result.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso(result.getRedirect(), getActivity());
				}

				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
				listener.error(error);
			}
		});
	}

	//public String preencheDadosPagamentoCartaoSalvoMP(RetornoCartao cartao, Editable securityCode) {
	public String preencheDadosPagamentoCartaoSalvoMP(RetornoCartao cartao, String securityCode) {
		JSONObject jsonBody       = new JSONObject();
		JSONObject jsonCardHolder = new JSONObject();
		JSONObject jsonIdentification = new JSONObject();
		try {
			jsonIdentification.put("type", "CPF");
			jsonIdentification.put("number", DadosUsuarioBO.obterCpf());
			jsonCardHolder.put("identification",jsonIdentification);

			jsonBody.put("card_id",cartao.getIdCartao());
			jsonBody.put("expiration_year",cartao.getAnoValidade());
			jsonBody.put("expiration_month", cartao.getMesValidade());
			jsonBody.put("security_code", securityCode);
			jsonBody.put("cardholder", jsonCardHolder);
			jsonBody.put("device", DeviceSDK.getInstance().getInfoAsJsonString());
		}catch (Exception e) {
			e.printStackTrace();
		}

		return jsonBody.toString();
	}

	public String preencheDadosPagamento(String cpf, String name, String cardNumber, Integer year, Integer month, String securityCode){
		JSONObject jsonBody = new JSONObject();
		JSONObject jsonCardHolder = new JSONObject();
		JSONObject jsonIdentification = new JSONObject();
		try {
			jsonIdentification.put("type", "CPF");
			jsonIdentification.put("number", cpf);
			jsonCardHolder.put("identification", jsonIdentification);
			jsonCardHolder.put("name", name);

			jsonBody.put("card_number", cardNumber);
			jsonBody.put("expiration_year", year);
			jsonBody.put("expiration_month", month);
			jsonBody.put("security_code", securityCode);
			jsonBody.put("cardholder", jsonCardHolder);
			jsonBody.put("device", DeviceSDK.getInstance().getInfoAsJsonString());
		}catch (Exception e) {
			e.printStackTrace();
		}

		//bin = editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia)).substring(0, 7);
		return jsonBody.toString();
	}

	public void realizaCompraMercadoPago(String bin, String requestBody, String salvarCartao, boolean isCartaoNovo, OnCompraAsync listener){
		AppCenterManager.registraEventoDeviceMP(getActivity().getResources().getString(R.string.app_center_mp_cartao_salvo), requestBody);
		MercadoPagoConnection.callWebServicePayment_method(bin , requestBody, getActivity(), onMercadoPagoListener(salvarCartao, isCartaoNovo, listener));
	}

	public void realizaCompraRecargaPayNovoCartao(String tokenApostador, boolean isCartaoNovo, CartaoRecargaPayDTO cartao, OnCompraAsync listener) {
		RecargaPayConnection.callRecargaPayNovoCartao(tokenApostador, getActivity(), cartao, onRecargaPayListener(isCartaoNovo, listener));
	}

	public void realizaCompraRecargaPayCartaoSalvo(String tokenApostador, String cardToken, String cvv, OnCompraAsync listener) {
		RecargaPayConnection.callRecargaPayCartaoSalvo(tokenApostador, getActivity(), cardToken, cvv, onRecargaPayListener(false, listener));
	}

	private RecargaPayConnection.RecargaPayListener onRecargaPayListener( boolean isCartaoNovo, OnCompraAsync listener) {
		return new RecargaPayConnection.RecargaPayListener() {
			@Override
			public void onSucesso(String customerCardToken, String payment_method_id) {
				if (isCartaoNovo){
					CompraUtil.registraApostaAsync(5, customerCardToken, payment_method_id,
												   null,
												   null,
												   listener);
				} else {
					CompraUtil.registraApostaAsync(5, customerCardToken, payment_method_id,
												   getActivity().getResources().getString(R.string.string_true),
												   null,
												   listener);
				}
			}

			@Override
			public void onErro(String msgError) {
				AlertDialogUtils.dismiss();
				apresentaModal(msgError);
			}
		};
	}

	private MercadoPagoConnection.MercadoPagoListener onMercadoPagoListener(String salvarCartao, boolean isCartaoNovo, OnCompraAsync listener) {
		return new MercadoPagoConnection.MercadoPagoListener() {
			@Override
			public void onSucesso(String tokenPagamento, String payment_method_id, boolean luhn_validation) {
				if (isCartaoNovo){
					if (luhn_validation){
						CompraUtil.registraApostaAsync(1, tokenPagamento, payment_method_id,
													   null,
													   salvarCartao,
													   listener);
					} else {
						AlertDialogUtils.dismiss();
						apresentaModal(getActivity().getResources().getString(R.string.numero_cartao_invalido));
					}
				} else {
					CompraUtil.registraApostaAsync(1, tokenPagamento, payment_method_id,
												   getActivity().getResources().getString(R.string.string_true),
												   null,
												   listener);
				}
			}

			@Override
			public void onTokenFailure(String msgError) {
				AlertDialogUtils.dismiss();
				apresentaModal(msgError);
			}

			@Override
			public void onPaymentFailure(String msgError) {
				AlertDialogUtils.dismiss();
				apresentaModal(msgError);
			}
		};
	}

	private void apresentaModal(String msg) {
		DialogUtils.dialogEntendi(getActivity(), msg);
	}

	public void limparCarrinhoFavorito() {
		CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(getActivity());
		crud.deletaTodos();
	}

	public boolean isRecargaPay(Long value) {
		return MeioPagamentoUtils.isRecargaPay(value);
	}

	public boolean isMercadoPago(Long value) {
		return MeioPagamentoUtils.isMercadoPago(value);
	}

	public void buscaMeioPagamento(Long id, OnSilceListener<MeioPagamentoDTO> listener){
		model.buscaMeioPagamento(id,listener);
	}

	public void recuperaTokenApostadorRecargaPay(OnSilceListener<String> listener) {
		ApostaSilceBO.getInstance().getRecargaPayToken(new RequestListener<StringResponse>() {
			@Override
			public void onResponse(StringResponse result) {
				if (result.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso( result.getRedirect(), getActivity());
				}

				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
				listener.error(error);
			}
		});
	}

	public void buscaDadosPessoais(OnSilceListener<ApostadorDTO> listener) {
		DadosUsuarioBO.getInstance().getDadosUsuario( new RequestListener<ApostadorDTOResponse>() {
			@Override
			public void onResponse(ApostadorDTOResponse response) {
				listener.success(response.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
				listener.error(error);
			}
		} );
	}
	public String getBrand(String numeroCartao) {
		numeroCartao = numeroCartao.replace(" ", "");
		if (isELO(numeroCartao)){
			return "ELO";
		} else if (isMasterCard(numeroCartao)) {
			return "MASTERCARD";
		} else if (numeroCartao.startsWith("605032")){
			return "GRANDCARD";
		} else if (numeroCartao.startsWith("9792")){
			return "TROY";
		} else if (numeroCartao.startsWith("5067")){
			return "VERVE";
		} else if (numeroCartao.startsWith("604") || numeroCartao.startsWith("637")){
			return "CABAL";
		} else if (numeroCartao.startsWith("6062") || numeroCartao.startsWith("384")){
			return "HIPERCARD";
		} else if (numeroCartao.startsWith("34") || numeroCartao.startsWith("37")){
			return "AMERICANEXPRESS";
		} else if (numeroCartao.startsWith("30") || numeroCartao.startsWith("36") ||
				numeroCartao.startsWith("38") || numeroCartao.startsWith("39")){
			return "DINERS";
		} else if (numeroCartao.startsWith("35")){
			return "JCB";
		} else if (numeroCartao.startsWith("6011") || numeroCartao.startsWith("65")){
			return "DISCOVER";
		} else if (numeroCartao.startsWith("4")){
			return "VISA";
		} else {
			return "VISA";
		}
	}

	private static boolean isMasterCard(String numeroCartao) {
		return numeroCartao.startsWith("51") || numeroCartao.startsWith("52") ||
				numeroCartao.startsWith("53") || numeroCartao.startsWith("54") ||
				numeroCartao.startsWith("55") ||
				(numeroCartao.compareTo("2221") >= 0 && numeroCartao.compareTo("2720") <= 0) ||
				numeroCartao.startsWith("5031") || numeroCartao.startsWith("5081") ||
				numeroCartao.startsWith("5021") || numeroCartao.startsWith("5899") ||
				numeroCartao.startsWith("5063");
	}

	private static boolean isELO(String numeroCartao) {
		String regex509 =
				"509(508|509|518|519|918|921|926|929|932|935|938|940|946|948|951|952|957|961|963|971|975|978|979|980|983|986|996|999|" +
						"900|920|924|925|927|930|937|939|943|947|949|950|955|956|958|959|960|972|973|974|977|982|984|998|919|922|923|928|931|" +
						"933|934|936|941|942|944|945|953|954|962|964|976|981|985|995|997)";
		String regex650 =
				"650(948|949|953|955|956|957|960|968|971|946|952|961|962|966|973|977|947|950|951|954|958|959|963|964|965|967|970|972|" +
						"974|975|976|978|058|059|048)";
		String regexOutros =
				"(4011(78|79)|431274|438935|451416|45(7393|7631|7632)|5067(00|01|02|04|05|06|08|10|11|12|14|15|16|17|18|19|20|24|25|26|27|28|" +
						"29|30|31|32|33|37|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|60|61|62|63|64|65|70|71|72|73|74|75|77|78)|5090(00|01|" +
						"02|04|05|06|07|09|10|11|12|13|14|15|16|17|18|19|20|21|22|24|25|26|27|28|29)|509(0[3-9][0-9]|[1-4][0-9]{2}|5[0-2][0-7]|" +
						"5[2-9][0-9]|6[0-9][0-9]|7[0-9][0-9]|8[0-7][0-9]|88[0-7]|8[8-9][0-9]|9[0-9]{3})|636(297|368)|650(4[0-9]{2}|5[0-9]{2}|" +
						"6[0-9]{2}|7[0-9]{2}|8[0-9]{2}|9[0-3][0-9]|9[4-9]|580|581|582|583)|651(6[5-9][0-9]|[7-9][0-9]{2})|655(0[0-9]{2}|1[0-9]{2}|" +
						"2[0-9]{2}|3[0-9]{2}|4[0-9]{2}|5[0-9]{2}|6[0-9]{2}|7[0-9]{2}|8[0-9]{2}|9[0-9]{2})|6500580[1-3])";


		String regex = "^((" + regex509 + ")|(" + regex650 + ")|(" + regexOutros + "))$";

		Pattern ELO_PATTERN = Pattern.compile(regex);
		String bin = numeroCartao.replace(" ", "").substring(0, 6);
		Matcher matcher = ELO_PATTERN.matcher(bin);

		return matcher.matches();
	}

	public CardHolderRecargaPay getCardHolderRecargaPay(ApostadorDTO dadosPessoais) {
		CardHolderRecargaPay cardHolder = new CardHolderRecargaPay();
		cardHolder.setName(dadosPessoais.getNome());
		cardHolder.setType(RecargaPayConnection.CARDHOLDER_TYPE);
		cardHolder.setEmail(dadosPessoais.getEmail());
		cardHolder.setDocument(dadosPessoais.getCpf());
		cardHolder.setBirthdate(DataUtil.getStringDataFormatted(DataUtil.YYYY_MM_DD, dadosPessoais.getDataNascimento()));
		List<String> phones = new ArrayList<>();
		if (dadosPessoais.getTelefone() != null) {
			phones.add(dadosPessoais.getTelefone().getDdd()+dadosPessoais.getTelefone().getNumero());}
		else {
			phones.add("+55999999999");
		}
		cardHolder.setPhones(phones);
		return cardHolder;
	}

	public DataExpiracao getDataExpiracao(String mes, String ano) {
		DataExpiracao dataExpiracao = new DataExpiracao();
		dataExpiracao.setMes(Integer.valueOf(mes));
		dataExpiracao.setAno(Integer.valueOf(ano));
		return dataExpiracao;
	}

	public List<RetornoCartao> getCartoes2Primeiros(List<RetornoCartao> retornoCartaoList) {
		List<RetornoCartao> cartoes = new ArrayList<RetornoCartao>();
		cartoes.add(retornoCartaoList.get(0));
		cartoes.add(retornoCartaoList.get(1));
		return cartoes;
	}

	public ArrayList<String> getListaMeses() {
		ArrayList<String> listaMeses = new ArrayList<>();
		listaMeses.add(getActivity().getString(R.string.title_mesDeValidade));
		for(int x = 1; x <= 12; x++){
			listaMeses.add(x < 10 ? "0" + x: String.valueOf(x));
		}
		return listaMeses;
	}

	public ArrayList<String> getListaAnos() {
		ArrayList<String> listaAnos = new ArrayList<>();
		listaAnos.add(getActivity().getString(R.string.title_anoDeValidade));
		DateTime joda = new DateTime();
		listaAnos.add("" + joda.getYear());
		for(int x = 1; x <= 10; x++){
			listaAnos.add("" + joda.plusYears(x).getYear());
		}
		return listaAnos;
	}

	public CartaoRecargaPayDTO getCartaoRecargaPay(ApostadorDTO dadosPessoais, String numeroCartao, String mes, String ano,
												  String codSeguranca, boolean shouldSave) {
		CartaoRecargaPayDTO cartao = new CartaoRecargaPayDTO();
		cartao.setType(RecargaPayConnection.CARTAO_TYPE);
		cartao.setBrand(this.getBrand(numeroCartao));
		cartao.setNumber(numeroCartao);

		CardHolderRecargaPay cardHolder = this.getCardHolderRecargaPay(dadosPessoais);
		cartao.setCardHolder(cardHolder);

		DataExpiracao dataExpiracao = this.getDataExpiracao(mes, ano);
		cartao.setDataExpiracao(dataExpiracao);

		cartao.setSecurityCode(codSeguranca);
		cartao.setShouldSave(shouldSave);
		return cartao;
	}


}
