package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.data.CompletaJogoNovoRsp;
import br.gov.caixa.loterias.apostas.model.bo.silce.data.CompletaJogoRsp;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class SimularApostaModel {

	private Activity activity;

	public SimularApostaModel(Activity activity) {this.activity = activity;}

	public void preencheNumerosAleatorios(int qtdDezenasPossiveisSelecionado, List<Integer> dezenasSelecionadas,
										   int qtdPrognosticoMaximo, OnSilceListener listener) {
		ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
		apostaBO.completaJogo(qtdDezenasPossiveisSelecionado, dezenasSelecionadas, 1,
							  qtdPrognosticoMaximo, new RequestListener<CompletaJogoRsp>() {
					@Override
					public void onResponse(CompletaJogoRsp response) {
						AppCenterManager.registraEvento("ENTROU_COMPLETAR_COM_NUMEROS_ALEATORIOS");

						listener.success(response.getPayload());

						if (response.getRedirect() != null) {
							RedirectNetwork.checkRedirectSucesso(response.getRedirect(), activity);
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						listener.error(error);
						RedirectNetwork.checkRedirect(error, activity);
					}
				});
	}

	public void preencheNumerosAleatoriosSuperSete(int qtdDezenasPossiveisSelecionado, ArrayList<ArrayList<Integer>> listaInteiros,
												   String nome, OnSilceListener listener) {
		ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
		apostaBO.completaJogoNovo(qtdDezenasPossiveisSelecionado, listaInteiros,
								  nome,
								  new RequestListener<CompletaJogoNovoRsp>() {
									  @Override
									  public void onResponse(CompletaJogoNovoRsp response) {
										  AppCenterManager.registraEvento( "ENTROU_COMPLETAR_COM_NUMEROS_ALEATORIOS");
										  if (response.getRedirect() != null) {
											  RedirectNetwork.checkRedirectSucesso(response.getRedirect(), activity);
										  }
										  listener.success(response.getPayload());
									  }

									  @Override
									  public void onErrorResponse(VolleyError error) {
										  RedirectNetwork.checkRedirect(error, activity);
									  }
								  });
	}
}
