package br.gov.caixa.loterias.apostas.utils.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeradorNumerosAleatorios {
	private int prognostico;
	private List<Integer> numerosPossiveis;

	public GeradorNumerosAleatorios(int prognostico) {
		this.prognostico = prognostico;
		iniciaNumerosPossiveis();
	}

	public List<Integer> gerar(int qtdNumeros, List<Integer> selecionados){
		List<Integer> aleatorios = new ArrayList<>();

		if (selecionados != null && !selecionados.isEmpty()){
			aleatorios.addAll(selecionados);
			numerosPossiveis.removeAll(selecionados);
		}

		while (qtdNumeros > aleatorios.size()){
			int index = new Random().nextInt(numerosPossiveis.size());
			Integer numero = numerosPossiveis.get(index);
			if (!aleatorios.contains(numero) && numero > 0 && numero <= prognostico) {
				aleatorios.add(numero);
				numerosPossiveis.remove(index);
			}
		}

		iniciaNumerosPossiveis();
		return aleatorios;
	}

	private void iniciaNumerosPossiveis() {
		numerosPossiveis = new ArrayList<>();
		for (int numero = 1; numero <= prognostico; numero++){
			numerosPossiveis.add(Integer.valueOf(numero));
		}
	}
}
