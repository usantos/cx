package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.LoginActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class LoginSilceModel extends AppModel {

	public LoginSilceModel(Activity activity) {
		super(activity);
	}

	public void sair(OnSilceListener<NetworkResponse> listener) {
		DadosUsuarioBO.getInstance().sair(new RequestListener<NetworkResponse>() {
			@Override
			public void onResponse(NetworkResponse result) {
				AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_logout_sucesso));
				SessaoUsuario.getInstance().setResponderAutoavaliacao(false);
				UltimaNotificacaoSingleton.getInstance().setPagamentoNaoIdentificado(false);

				new LoginSSOModel(getActivity()).sair(LoginActivity.class, () -> listener.success(result));
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
				listener.error(error);
			}
		});
	}


}
