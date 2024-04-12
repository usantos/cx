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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.gerador.GeradorListaNumeros;
import br.gov.caixa.loterias.apostas.utils.gerador.GeradorMatrizNumeros;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;

public class SimularApostaModel {

	private static final int PROGNOSTICO_MINIMO = 1;
	private static final int PROGNOSTICO_MINIMO_SUPER_SETE = 0;
	private static final int PROGNOSTICO_COLUNA_SUPER_SETE = 7;
	private static final int PROGNOSTICO_MAXIMO_SUPER_SETE = 9;

	private Activity activity;

	public SimularApostaModel(Activity activity) {this.activity = activity;}

	public void preencheNumerosAleatorios(int qtdNumeos, List<Integer> selecionados, int prognostico, OnSilceListener listener){
		preencheNumerosAleatoriosLocal(qtdNumeos, selecionados, prognostico, listener);
	}

	private void preencheNumerosAleatoriosSilce(int qtdDezenasPossiveisSelecionado, List<Integer> dezenasSelecionadas,
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

	private void preencheNumerosAleatoriosLocal(int qtdNumeos, List<Integer> selecionados, int prognostico, OnSilceListener listener){
		List<Integer> numerosAleatorios = new GeradorListaNumeros(PROGNOSTICO_MINIMO, prognostico).gerar(qtdNumeos, selecionados);

		if (numerosAleatorios != null && !numerosAleatorios.isEmpty()){
			listener.success(numerosAleatorios);
		}else {
			listener.error(null);
		}
	}

	public void preencheNumerosAleatoriosSuperSete(int qtdDezenasPossiveisSelecionado, ArrayList<ArrayList<Integer>> listaInteiros,
												   String nome, OnSilceListener listener) {
		preencheNumerosAleatoriosSuperSeteLocal(qtdDezenasPossiveisSelecionado, listaInteiros, nome, listener);
	}

	private void preencheNumerosAleatoriosSuperSeteLocal(int qtdDezenasPossiveisSelecionado, ArrayList<ArrayList<Integer>> listaInteiros,
														 String nome, OnSilceListener listener) {
		ArrayList<ArrayList<Integer>> matriz = new GeradorMatrizNumeros(PROGNOSTICO_MINIMO_SUPER_SETE, PROGNOSTICO_MAXIMO_SUPER_SETE,
																		PROGNOSTICO_COLUNA_SUPER_SETE)
				.gerar(qtdDezenasPossiveisSelecionado, listaInteiros);

		listener.success(matriz);
	}

	private void preencheNumerosAleatoriosSuperSeteSilce(int qtdDezenasPossiveisSelecionado, ArrayList<ArrayList<Integer>> listaInteiros,
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

	public void adidionaApostaNoCarrinho(Activity actv, IdentificaoDeUmaApostaDas8Modalidades aposta){
		if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(actv)) {
			AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(actv, aposta);
		}
	}

	public void addSurpresinhaCarrinho(IncluirSurpresinhaDTO surpresinha) {
		AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoSurpresinha(activity, surpresinha);
	}
}
