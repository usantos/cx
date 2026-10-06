package br.gov.caixa.loterias.apostas.utils.gerador;

public abstract class GeradorNumerosAleatorios<T> {
	private int prognosticoMin;
	private int prognosticoMax;

	protected GeradorNumerosAleatorios(int prognosticoMin, int prognosticoMax) {
		this.prognosticoMin = prognosticoMin;
		this.prognosticoMax = prognosticoMax;
	}

	public int getMin() {
		return prognosticoMin;
	}

	public int getMax() {
		return prognosticoMax;
	}

	public abstract T gerar(int qtdNumeros, T selecionados);


}
