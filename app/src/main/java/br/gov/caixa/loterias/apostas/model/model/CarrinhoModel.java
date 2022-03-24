package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

public class CarrinhoModel {

	private ParametrosSimulacaoModel model;

	private Activity activity;

	public CarrinhoModel(Activity activity) {
		this.activity = activity;
		this.model = new ParametrosSimulacaoModel(activity);
	}

	public void buscaCarrinhoSilce(OnSilceListener listener) {

		final ApostaBO apostaBO = ApostaBO_.getInstance_(activity);

		apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoDTOResponse response) {
				AppCenterManager.registraEvento(Aplicacao.application.getApplicationContext().getResources()
																	 .getString(R.string.evento_entrou_carrinho_online));
				if (response.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(response.getRedirect(), activity);
				}

				listener.success(response.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				if (MensagensNetwork.isUnauthorizedError(error)) {
					listener.success(lerCarrinhoLocal());
				} else {
					RedirectNetwork.checkRedirect(error, activity);
				}
			}
		});
	}

	public CarrinhoDTO lerCarrinhoLocal() {
		DBLoteriasCrud crud = new DBLoteriasCrud(activity);
		CarrinhoDTO carrinho = new CarrinhoDTO();
		carrinho.setApostas(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());

		return carrinho;
	}

	public void validarCarrinho(OnSilceListener listener) {
		ApostaBO_.getInstance_(activity).validarCarrinho(new RequestListener<ResourceResponse>() {
			@Override
			public void onResponse(ResourceResponse result) {
				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				listener.error(error);
				RedirectNetwork.checkRedirect(error, activity);
			}
		});
	}

	public void validarCarrinhoFavorito(String nome, OnSilceListener listener) {
		ApostaBO_.getInstance_(activity).validarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoFavoritoDTOResponse result) {
				if (result.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
				}
				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				if (isErroNegocial(error)) {
					listener.error(error);
				} else {
					RedirectNetwork.checkRedirect(error, activity);
				}
			}
		});
	}

	public void favoritarCarrinho(String nome, OnSilceListener listener) {
		ApostaBO_.getInstance_(activity).salvarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoFavoritoDTOResponse result) {
				if (result.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
				}

				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				listener.error(error);
				RedirectNetwork.checkRedirect(error, activity);
			}
		});
	}

	public void limparCarrinhosFavoritosLocal() {
		CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(activity);
		crud.deletaTodos();
	}

	public void deletaCarrinhoLocal() {
		AppCenterManager.registraEvento( activity.getResources().getString(R.string.evento_limpou_carrinho_offline));
		DBLoteriasCrud crud = new DBLoteriasCrud(activity);
		crud.deleteAll();
	}

	public void buscaParametroSiumulacao(OnSilceListener<ParametrosSimulacaoResponse> listener) {
		model.buscaParametroSiumulacao(listener);
	}

	public void limparCarrinho(OnSilceListener listener) {
		ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
		apostaBO.limparCarrinho(new RequestListener<CarrinhoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoDTOResponse result) {
				AppCenterManager.registraEvento(activity.getResources().getString(R.string.evento_limpou_carrinho_online));
				if (result.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
				}
				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				if (MensagensNetwork.isUnauthorizedError(error)) {
					listener.error(error);
				} else {
					RedirectNetwork.checkRedirect(error, activity);
				}
			}
		});
	}

	public void deletaApostaCarrinho(IdentificaoDeUmaApostaDas8Modalidades aposta, OnSilceListener listener) {
		ApostaBO     apostaBO         = ApostaBO_.getInstance_(activity);
		apostaBO.deleteApostaCarrinho(aposta.getId().toString(), new RequestListener<CarrinhoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoDTOResponse response) {
				AlertDialogUtils.dismiss();
				if (response.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(response.getRedirect(), activity);
				}
				listener.success(response.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				if (MensagensNetwork.isUnauthorizedError(error)) {
					listener.error(error);
				}else {
					RedirectNetwork.checkRedirect(error, activity);
				}
			}
		});
	}

	public void deletarApostaLocal(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		DBLoteriasCrud crud = new DBLoteriasCrud(activity);
		crud.deleteIdentificaoDeUmaApostaDas8Modalidades(aposta);
	}
}
