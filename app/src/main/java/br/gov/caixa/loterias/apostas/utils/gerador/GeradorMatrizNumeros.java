package br.gov.caixa.loterias.apostas.utils.gerador;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GeradorMatrizNumeros extends GeradorNumerosAleatorios<ArrayList<ArrayList<Integer>>> {
	private GeradorNumerosAleatorios geradorLista;
	private int prognosticoColunas;

	public GeradorMatrizNumeros(int prognosticoMinLista, int prognosticoMaxLista, int prognosticoColunas) {
		super(prognosticoMinLista, prognosticoMaxLista);
		this.prognosticoColunas = prognosticoColunas;
		geradorLista = new GeradorListaNumeros(prognosticoMinLista, prognosticoMaxLista);
	}

	@Override
	public ArrayList<ArrayList<Integer>> gerar(int qtdNumeros, ArrayList<ArrayList<Integer>> selecionados) {
		if (qtdNumeros == prognosticoColunas){
			return preenchimentoJogoMinimo(selecionados);
		}else {
			try {
				int repeticoes = qtdNumeros / prognosticoColunas;
				for(int qtd = 1; qtd <= repeticoes; qtd++){
					selecionados = preenchimentoPorLista(selecionados, qtd);
				}

				if(qtdNumeros > (repeticoes * prognosticoColunas)){
					int restante = getQuantidadeRestante(qtdNumeros, selecionados);

					selecionados = completaRestante(selecionados, restante, repeticoes + 1);
				}
			}catch (Exception e){

			}

			return selecionados;
		}
	}

	private int getQuantidadeRestante(int qtdNumeros, ArrayList<ArrayList<Integer>> selecionados) {
		return qtdNumeros - getQuantidadeSelecionados(selecionados);
	}

	private int getQuantidadeSelecionados(ArrayList<ArrayList<Integer>> selecionados){
		int qtd = 0;

		for (ArrayList<Integer> coluna: selecionados){
			if (coluna != null){
				qtd += coluna.size();
			}
		}

		return qtd;
	}

	private ArrayList<ArrayList<Integer>> completaRestante(ArrayList<ArrayList<Integer>> selecionados, int restante, int qtdNumero) {
		List<Integer> indices = new ArrayList<>();
		while (restante != 0){
			int coluna = new GeradorNumero(0, 6).gerar(0, 6);
			if ( coluna >= 0 && coluna <= 6){
				if (!indices.contains(coluna) && selecionados.get(coluna).size() < qtdNumero){
					ArrayList<Integer> gerado = (ArrayList<Integer>)geradorLista.gerar(qtdNumero, selecionados.get(coluna));
					selecionados.set(coluna, gerado);
					indices.add(coluna);
					restante--;
				}
			}
		}

		return selecionados;
	}

	private ArrayList<ArrayList<Integer>> preenchimentoJogoMinimo(ArrayList<ArrayList<Integer>> selecionados){
		return preenchimentoPorLista(selecionados, 1);
	}

	@NotNull
	private ArrayList<ArrayList<Integer>> preenchimentoPorLista(ArrayList<ArrayList<Integer>> selecionados, int qtdNumeros) {
		for(int coluna = 0; coluna < prognosticoColunas; coluna++){
			ArrayList<Integer> gerado = (ArrayList<Integer>) geradorLista.gerar(qtdNumeros, selecionados.get(coluna));
			selecionados.set(coluna, gerado);
		}
		return selecionados;
	}
}
