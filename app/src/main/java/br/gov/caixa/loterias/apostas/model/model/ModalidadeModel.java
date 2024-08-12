package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;

public class ModalidadeModel {

	private Activity activity;

	public ModalidadeModel(Activity activity) {
		this.activity = activity;
	}

	public void buscaModalidades(OnSilceListener<List<ModalidadeDTO>> listener) {
		ApostaBO.getInstance().getModalidades(new RequestListener<ModalidadeDTOResponse>() {
			@Override
			public void onResponse(ModalidadeDTOResponse response) {
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
}
