package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;

public class TrevoModel {

	private Activity activity;
	private SimularApostaModel model;

	public TrevoModel(Activity activity) {
		this.activity = activity;
		this.model = new SimularApostaModel(activity);
	}

	public void preencheNumerosAleatorios(int qtdDezenasPossiveisSelecionado, List<Integer> dezenasSelecionadas,
										  int qtdPrognosticoMaximo, OnSilceListener listener){
		model.preencheNumerosAleatorios(qtdDezenasPossiveisSelecionado,dezenasSelecionadas,
										qtdPrognosticoMaximo,listener);
	}

	public void adidionaApostaNoCarrinho(IdentificaoDeUmaApostaDas8Modalidades aposta){
		if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(activity)) {
			AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(activity, aposta);
		}
	}
}
