package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
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

	public void adicionaApostaNoCarrinho(Activity actv, IdentificaoDeUmaApostaDas8Modalidades aposta, BarraTituloDTO barraTituloDTO){
		if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(actv)) {
			if (barraTituloDTO != null && (barraTituloDTO.getDataSorteio() != null && barraTituloDTO.getNumeroConcurso() != null)){
				AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(actv, aposta, barraTituloDTO);
			}
			else {
				AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(actv, aposta, null);
			}
		}
	}

	public void addSurpresinhaCarrinho(IncluirSurpresinhaDTO surpresinha) {
		AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoSurpresinha(activity, surpresinha);
	}
}
