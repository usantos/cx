package br.gov.caixa.loterias.apostas.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;

public class DezenaUtils {

	public static List<Dezena> getDezenasOrdenadas(List<Integer> lista) {
		Collections.sort(lista);
		return getDezenas(lista);
	}

	public static List<Dezena> getDezenas(List<Integer> lista) {
		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 0; i < lista.size(); i++) {
			dezenas.add(new Dezena("" + lista.get(i), Boolean.FALSE, "" + lista.get(i)));
		}

		return dezenas;
	}

}
