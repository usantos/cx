package br.gov.caixa.loterias.apostas.utils.gerador;

import java.util.ArrayList;
import java.util.List;

public class GeradorListaNumeros extends GeradorNumerosAleatorios<List<Integer>> {
	private GeradorNumero geradorNumero;

	public GeradorListaNumeros(int prognostico) {
		super(prognostico);
		this.geradorNumero = new GeradorNumero(prognostico);
	}

	@Override
	public List<Integer> gerar(int qtdNumeros, List<Integer> selecionados){
		if (getNumerosPossiveis().size() < getPrognostico()){
			iniciaNumerosPossiveis(getPrognostico());
		}
		List<Integer> aleatorios = new ArrayList<>();

		if (selecionados != null && !selecionados.isEmpty()){
			aleatorios.addAll(selecionados);
			getNumerosPossiveis().removeAll(selecionados);
		}

		while (qtdNumeros > aleatorios.size()){
			int index = geradorNumero.gerar(1, null);
			Integer numero = getNumerosPossiveis().get(index);
			if (!aleatorios.contains(numero) && numero > 0 && numero <= getPrognostico()) {
				aleatorios.add(numero);
				getNumerosPossiveis().remove(index);
			}
		}

		return aleatorios;
	}

}
