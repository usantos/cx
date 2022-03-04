package br.gov.caixa.loterias.apostas.utils.gerador;

import java.util.ArrayList;
import java.util.List;

public abstract class GeradorNumerosAleatorios<T> {
	private int prognostico;
	private List<Integer> numerosPossiveis;

	public GeradorNumerosAleatorios(int prognostico) {
		this.prognostico = prognostico;
		iniciaNumerosPossiveis(this.prognostico);
	}

	public abstract T gerar(int qtdNumeros, List<Integer> selecionados);

	protected void iniciaNumerosPossiveis(int prognostico) {
		numerosPossiveis = new ArrayList<>();
		for (int numero = 1; numero <= prognostico; numero++){
			numerosPossiveis.add(Integer.valueOf(numero));
		}
	}

	public int getPrognostico() {
		return prognostico;
	}

	public void setPrognostico(int prognostico) {
		this.prognostico = prognostico;
	}

	public List<Integer> getNumerosPossiveis() {
		return numerosPossiveis;
	}

	public void setNumerosPossiveis(List<Integer> numerosPossiveis) {
		this.numerosPossiveis = numerosPossiveis;
	}
}
