package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.List;

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

}
