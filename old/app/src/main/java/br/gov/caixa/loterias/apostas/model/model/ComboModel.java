package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import android.util.Log;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirComboDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;

public class ComboModel {
	private Activity activity;

	public ComboModel(Activity activity) {
		this.activity = activity;
	}

	public void buscaCombos(OnSilceListener listener) {
		//TODO Nuvem **VERIFICAR**
		//ServicoFactoryUtil.getApostaService().buscaCombo(new RequestListener<CombosDTOResponse>() {
		ApostaSilceBO.getInstance().buscaCombo(new RequestListener<CombosDTOResponse>() {
			@Override
			public void onResponse(CombosDTOResponse response) {
				AppCenterManager.registraEvento(Aplicacao.application.getApplicationContext().getResources()
						.getString(R.string.evento_entrou_combos_online));
				if (response.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(response.getRedirect(), activity);
				}

				listener.success(response.getPayload());
			}
			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, activity);
				listener.error(error);
			}
		});
	}

	public void adicionaComboCarrinho(IncluirComboDTO incluirComboDTO, RequestListener<CarrinhoDTOResponse> listener) {
		ServicoFactoryUtil.getApostaService().adicionarComboNoCarrinho(incluirComboDTO, new RequestListener<CarrinhoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoDTOResponse result) {
				try {
					AlertDialogUtils.dismiss();
					AppCenterManager.registraEvento("ENTROU_ADICAO_COMBO_CARRINHO_ONLINE");
					//CarrinhoSingleton.getInstance().getCarrinho().getApostas().add(aposta);
				} catch (Exception e){
					Log.d("", e.getLocalizedMessage());
				}

				//activity.startActivity(new Intent(activity, CarrinhoActivity.class));
				listener.onResponse(result);
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, activity);
				listener.onErrorResponse(error);
			}
		});
	}
}
