package br.gov.caixa.loterias.apostas.model.bean;

import java.io.Serializable;
import java.util.List;

public class Paginacao implements Serializable {
	private List<Pagina> paginas;
	private String divisor;
	private int qtdPagina;
	private int indexPaginaAtual;
	private int cor;

	public Paginacao(List<Pagina> paginas, String divisor, int cor) {
		this.paginas = paginas;
		this.divisor = divisor;
		this.cor = cor;
		if (paginas != null && !paginas.isEmpty()){
			this.indexPaginaAtual = 0;
			this.qtdPagina = paginas.size();
		}
	}

	public List<Pagina> getPaginas() {
		return paginas;
	}

	public String getDivisor() {
		return divisor;
	}

	public int getQtdPagina() {
		return qtdPagina;
	}

	public int getCor() { return cor; }

	public Pagina getProxima(){
		Pagina proxima = null;
		if (indexPaginaAtual < (paginas.size() - 1)){
			indexPaginaAtual++;
			proxima = paginas.get(indexPaginaAtual);
		}
		return proxima;
	}

	public Pagina getAnterior() {
		Pagina anterior = null;
		if (indexPaginaAtual > 0){
			indexPaginaAtual--;
			anterior = paginas.get(indexPaginaAtual);
		}
		return anterior;
	}
}
