package br.gov.caixa.loterias.apostas.utils.gerador;

import java.util.Random;

public class GeradorNumero extends GeradorNumerosAleatorios<Integer> {

	public GeradorNumero(int min, int max) {
		super(min, max);
	}

	@Override
	public Integer gerar(int min, Integer max){
		return new Random().nextInt((max - min) + 1) + min;
	}

}
