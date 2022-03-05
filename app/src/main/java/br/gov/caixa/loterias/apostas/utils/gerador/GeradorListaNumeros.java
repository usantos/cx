package br.gov.caixa.loterias.apostas.utils.gerador;

import java.util.ArrayList;
import java.util.List;

public class GeradorListaNumeros extends GeradorNumerosAleatorios<List<Integer>> {
	private GeradorNumero geradorNumero;

	public GeradorListaNumeros(int prognosticoMin, int prognosticoMax) {
		super(prognosticoMin, prognosticoMax);
		this.geradorNumero = new GeradorNumero(prognosticoMin, prognosticoMax);
	}

	@Override
	public List<Integer> gerar(int qtdNumeros, List<Integer> selecionados){
		List<Integer> aleatorios = new ArrayList<>();

		if (selecionados != null && !selecionados.isEmpty()){
			aleatorios.addAll(selecionados);
		}

		while (qtdNumeros > aleatorios.size()){
			int numero = geradorNumero.gerar(getMin(), getMax());
			if (!aleatorios.contains(numero) && numero >= getMin() && numero <= getMax()) {
				aleatorios.add(numero);
			}
		}

		return aleatorios;
	}

}
