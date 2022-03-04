package br.gov.caixa.loterias.apostas.utils.gerador;

import java.util.List;
import java.util.Random;

public class GeradorNumero extends GeradorNumerosAleatorios<Integer> {

	public GeradorNumero(int prognostico) {
		super(prognostico);
	}

	@Override
	public Integer gerar(int qtdNumeros, List<Integer> selecionados){
		if (selecionados != null && !selecionados.isEmpty()){
			return selecionados.get(0);
		}

		return new Random().nextInt(getNumerosPossiveis().size());
	}

}
