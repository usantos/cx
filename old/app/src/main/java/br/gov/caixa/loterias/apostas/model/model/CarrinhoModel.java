package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UltimaCompraDTO;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.model.dao.crud.UltimaCompraCRUD;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;

import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

public class CarrinhoModel {

	private ParametrosSimulacaoModel model;

	private Activity activity;

	public CarrinhoModel(Activity activity) {
		this.activity = activity;
		this.model = new ParametrosSimulacaoModel(activity);
	}

	public void buscaCarrinhoSilce(OnSilceListener listener) {
		ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
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
		carrinho.setApostasIndividuais(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());

		return carrinho;
	}

	public Boolean temCarrinhoLocal(){
		CarrinhoDTO carrinhoLocal = lerCarrinhoLocal();
		return carrinhoLocal != null && !carrinhoLocal.getApostas().isEmpty();
	}

	public void validarCarrinho(OnSilceListener listener) {
		ServicoFactoryUtil.getApostaService().validarCarrinho(new RequestListener<ResourceResponse>() {
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
		ServicoFactoryUtil.getApostaService().validarCarrinhoFavorito(nome, new RequestListener<CarrinhoFavoritoDTOResponse>() {
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
		ServicoFactoryUtil.getApostaService().salvarCarrinhoFavorito(nome, null, true, new RequestListener<CarrinhoFavoritoDTOResponse>() {
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

	public void buscaParametroSiumulacao(OnSilceListener<ParametrosSimulacao> listener) {
		model.buscaParametroSiumulacao(listener);
	}

	public void limparCarrinho(OnSilceListener listener) {
		ServicoFactoryUtil.getApostaService().limparCarrinho(new RequestListener<CarrinhoDTOResponse>() {
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

	public void deletaComboCarrinho(ComboApostaDTO comboApostaDTO, OnSilceListener listener) {
		ServicoFactoryUtil.getApostaService().deleteComboCarrinho(comboApostaDTO.getId().toString(), new RequestListener<CarrinhoDTOResponse>() {
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
	public void deletaApostaCarrinho(IdentificaoDeUmaApostaDas8Modalidades aposta, OnSilceListener listener) {
		ServicoFactoryUtil.getApostaService().deleteApostaCarrinho(aposta.getId().toString(), new RequestListener<CarrinhoDTOResponse>() {
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

	public boolean existeCompraMesmoValorEm24horas(BigDecimal valorTotal){
		UltimaCompraCRUD crud = new UltimaCompraCRUD(activity);
		List<UltimaCompraDTO> list = crud.lerUltimaCompraPorValor(valorTotal);
		if (list == null || list.isEmpty()){
			return false;
		} else {
			boolean existeCompra = false;
			for (UltimaCompraDTO ultimaCompra : list){
				Date date = DateUtils.stringToDate(ultimaCompra.getDataCompra(), DateUtils.PATTERN_DD_MM_YYYY_HH_MM);
				if (date == null){
					return false;
				}

				DateTime data24HorasAtras = new DateTime().minusHours( 24 );

				DateTime dateUltimaCompra = new DateTime(date);
				if (dateUltimaCompra.isAfter( data24HorasAtras )){
					existeCompra = true;
					break;
				}
			}

			return existeCompra;
		}
	}

}
