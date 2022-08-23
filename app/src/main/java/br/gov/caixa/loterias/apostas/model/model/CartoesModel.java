package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import android.text.Editable;

import com.android.volley.VolleyError;
import com.mercadolibre.android.device.sdk.DeviceSDK;
import com.mercadopago.model.CardToken;

import org.json.JSONObject;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.MercadoPagoConnection;
import br.gov.caixa.loterias.apostas.model.bo.RecargaPayConnection;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CartaoRecargaPayDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StringResponse;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.CompraUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class CartoesModel extends AppModel {
	private MeioPagamentoModel model;

	public CartoesModel(Activity activity) {
		super(activity);
		model = new MeioPagamentoModel(getActivity());
	}

	public void buscaCartoesMercadoPago(OnSilceListener<List<RetornoCartao>> listener) {
		DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(getActivity());
		dadosUsuarioBO.cartoes(new RequestListener<ListCartaoDTOResponse>() {
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

	public void deletaCartaoMercadoPago(Long idCartao, OnSilceListener<List<RetornoCartao>> listener) {
		DadosUsuarioBO_.getInstance_(getActivity().getApplicationContext()).deleteCartao(idCartao, new RequestListener<ListCartaoDTOResponse>() {
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

	public String preencheDadosPagamentoCartaoSalvo(RetornoCartao cartao, Editable securityCode) {
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

	public String preencheDadosPagamento(CardToken cardToken){
		JSONObject jsonBody = new JSONObject();
		JSONObject jsonCardHolder = new JSONObject();
		JSONObject jsonIdentification = new JSONObject();
		try {
			jsonIdentification.put("type", "CPF");
			jsonIdentification.put("number", DadosUsuarioBO.obterCpf());
			jsonCardHolder.put("identification",jsonIdentification);
			jsonCardHolder.put("name",cardToken.getCardholder().getName());

			jsonBody.put("card_number", cardToken.getCardNumber().replace(getActivity().getResources().getString(R.string.espaco_em_branco), getActivity().getResources().getString(R.string.string_vazia)));
			jsonBody.put("expiration_year",Integer.valueOf(cardToken.getExpirationYear()));
			jsonBody.put("expiration_month", Integer.valueOf(cardToken.getExpirationMonth()));
			jsonBody.put("security_code", cardToken.getSecurityCode());
			jsonBody.put("cardholder", jsonCardHolder);
			jsonBody.put("device", DeviceSDK.getInstance().getInfoAsJsonString());
		}catch (Exception e) {
			e.printStackTrace();
		}

		//bin = editTextNumeroCartaoContent.getText().toString().replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia)).substring(0, 7);
		return jsonBody.toString();
	}

	public void realizaCompraMercadoPago(String bin, String requestBody, boolean isCartaoNovo, OnCompraAsync listener){
		AppCenterManager.registraEventoDeviceMP(getActivity().getResources().getString(R.string.app_center_mp_cartao_salvo), requestBody);
		MercadoPagoConnection.callWebServicePayment_method(bin , requestBody, getActivity(), onMercadoPagoListener(isCartaoNovo, listener));
	}

	public void realizaCompraRecargaPay(String tokenApostador, CartaoRecargaPayDTO cartao, RecargaPayConnection.RecargaPayListener listener) {
		RecargaPayConnection.callRecargaPayService(tokenApostador, getActivity(), cartao, listener);
	}

	private MercadoPagoConnection.MercadoPagoListener onMercadoPagoListener(boolean isCartaoNovo, OnCompraAsync listener) {
		return new MercadoPagoConnection.MercadoPagoListener() {
			@Override
			public void onSucesso(String tokenPagamento, String payment_method_id, boolean luhn_validation) {
				if (isCartaoNovo){
					if (luhn_validation){
						CompraUtil.registraApostaAsync(tokenPagamento, payment_method_id,
													   getActivity().getResources().getString(R.string.string_true),
													   getActivity().getResources().getString(R.string.string_vazia),
													   listener);
					} else {
						AlertDialogUtils.dismiss();
						apresentaModal(getActivity().getResources().getString(R.string.numero_cartao_invalido));
					}
				} else {
					CompraUtil.registraApostaAsync(tokenPagamento, payment_method_id,
												   getActivity().getResources().getString(R.string.string_true),
												   getActivity().getResources().getString(R.string.string_vazia),
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
		ViewUtils.alertTitleButton(getActivity(), R.string.label_atencao, msg,
								   getActivity().getResources().getString( R.string.ok ));
	}

	public void limparCarrinhoFavorito() {
		CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(getActivity());
		crud.deletaTodos();
	}

	public boolean isRecargaPay(MeioPagamentoConfig config) {
		return MeioPagamentoUtils.isRecargaPay(config.getMeioPagamento().getValor());
	}

	public boolean isMercadoPago(MeioPagamentoConfig config) {
		return MeioPagamentoUtils.isMercadoPago(config.getMeioPagamento().getValor());
	}

	public void buscaMeioPagamento(Long id, OnSilceListener<MeioPagamentoDTO> listener){
		model.buscaMeioPagamento(id,listener);
	}

	public void recuperaTokenApostadorRecargaPay(OnSilceListener<String> listener) {
		ApostaBO apostaBO = ApostaBO_.getInstance_(getActivity());
		apostaBO.getRecargaPayToken(new RequestListener<StringResponse>() {
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
}
