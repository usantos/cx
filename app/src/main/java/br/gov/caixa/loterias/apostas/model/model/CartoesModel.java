package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import android.text.Editable;

import com.android.volley.VolleyError;
import com.mercadolibre.android.device.sdk.DeviceSDK;

import org.json.JSONObject;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.MercadoPagoConnection;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.CompraUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class CartoesModel extends AppModel {

	public CartoesModel(Activity activity) {
		super(activity);
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

	public String preencheDadosPagamento(RetornoCartao cartao, Editable securityCode) {
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

	public void realizaCompraMercadoPago(RetornoCartao cartao, String requestBody, OnCompraAsync listener){
		AppCenterManager.registraEventoDeviceMP(getActivity().getResources().getString(R.string.app_center_mp_cartao_salvo), requestBody);
		MercadoPagoConnection.callWebServicePayment_method(cartao.getPrimeirosDigitos() , requestBody, getActivity(), onMercadoPagoListener(listener));
	}

	private MercadoPagoConnection.MercadoPagoListener onMercadoPagoListener(OnCompraAsync listener) {
		return new MercadoPagoConnection.MercadoPagoListener() {
			@Override
			public void onSucesso(String tokenPagamento, String payment_method_id, boolean luhn_validation) {
				CompraUtil.registraApostaAsync(tokenPagamento, payment_method_id,
											   getActivity().getResources().getString(R.string.string_true),
											   getActivity().getResources().getString(R.string.string_vazia),
											   listener);
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
}
