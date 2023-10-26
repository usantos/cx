package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.BolaoBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class BolaoModel extends AppModel{

	public BolaoModel(Activity activity) {
		super(activity);
	}

	public void buscaBoloes(OnSilceListener<ListaBolaoDTO> listener) {
		new BolaoBO().getBoloesDisponiveis(1, 690, 41, 1, 10,
										   new RequestListener<BoloesDisponiveisResponse>() {
			@Override
			public void onResponse(BoloesDisponiveisResponse response) {
				AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_carregou_boloes_disponiveis));
				if (response.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
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
}
