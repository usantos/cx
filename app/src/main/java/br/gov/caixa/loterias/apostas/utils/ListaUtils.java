package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SuperSeteAdapter;

public class ListaUtils {

	public static List<Integer> orderAscDezenas(List<Integer> dezenasSelecionadas){
		Integer       menorDezenaAtual = null;
		List<Integer> listaOrdenadaAsc = new ArrayList<>();
		while (dezenasSelecionadas.size() > 0) {
			for (Integer dezena : dezenasSelecionadas) {
				if (menorDezenaAtual == null) {
					menorDezenaAtual = dezena;
				} else if (menorDezenaAtual > dezena) {
					menorDezenaAtual = dezena;
				}
			}
			listaOrdenadaAsc.add(menorDezenaAtual);
			dezenasSelecionadas.remove(menorDezenaAtual);
			menorDezenaAtual = null;
		}
		dezenasSelecionadas = listaOrdenadaAsc;

		return dezenasSelecionadas;
	}

	public static List<ParametroPartida> orderAscPartidas(HashSet<ParametroPartida> partidasSelecionadas) {
		ParametroPartida       menorPartidaAtual = null;
		List<ParametroPartida> listaOrdenadaAsc  = new ArrayList<>();
		while (partidasSelecionadas.size() > 0) {
			for (ParametroPartida partida : partidasSelecionadas) {
				if (menorPartidaAtual == null) {
					menorPartidaAtual = partida;
				} else if (menorPartidaAtual.getNumero() > partida.getNumero()) {
					menorPartidaAtual = partida;
				}
			}
			listaOrdenadaAsc.add(menorPartidaAtual);
			partidasSelecionadas.remove(menorPartidaAtual);
			menorPartidaAtual = null;
		}

		return listaOrdenadaAsc;
	}

	public static ArrayList<ArrayList<Integer>> pegaListaSuperSete(ArrayList<SuperSeteAdapter> listaAdaptersSuperSete) {
		ArrayList<ArrayList<Integer>> listaNumerosSuperSete = new ArrayList<>();
		for (SuperSeteAdapter adapter : listaAdaptersSuperSete) {
			listaNumerosSuperSete.add(new ArrayList<>(adapter.dezenasSelecionadas));
		}
		return listaNumerosSuperSete;
	}

	public static List<Integer> transformaListDoubleEmListInteger(Object listNumeros) {
		List<Integer> listaInteiros = new ArrayList<>();
		try{
			List<Double> doubleLits = (List<Double>) listNumeros;
			if (doubleLits != null && !doubleLits.isEmpty()){
				if (doubleLits.get(0) instanceof Double){
					for (int num = 0; num < doubleLits.size(); num++){
						Integer integer = (Integer) doubleLits.get(num).intValue();
						listaInteiros.add(integer);
					}
				} else {
					listaInteiros = (List<Integer>) listNumeros;
				}
			}
		}catch (Exception e){

		}

		return listaInteiros;
	}
}
