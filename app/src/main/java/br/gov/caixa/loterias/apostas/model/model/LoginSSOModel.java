package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import br.gov.caixa.loterias.apostas.model.bo.AcessoBO;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;

public class LoginSSOModel extends AppModel {

	public interface SairListener {
		void onSaiu();
	}

	public LoginSSOModel(Activity activity) {
		super(activity);
	}

	public void sair(Class classDestino, SairListener listener) {
		AcessoBO.getInstance().iniciarLogoutSSO(classDestino, getActivity());
		LoginSP.loginRealizado(false);

		listener.onSaiu();
	}

}
