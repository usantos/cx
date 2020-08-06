package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;

public class ConfiguracaoNumerosSuperSete implements Serializable {

	private int corInteriorCirculo;
	private int corInteriorSelecionado;
	private int corTextoCirculo;
	private int corTextoCirculoSelecionado;
	private int corBordaCirculo;
	private int corBordaCirculoSelecionado;
	private int corInteriorQuadrado;
	private int corTextoQuadrado;
	private int corBordaQuadrado;

	public ConfiguracaoNumerosSuperSete() {
	}

	public ConfiguracaoNumerosSuperSete(int corInteriorCirculo, int corTextoCirculo, int corBordaCirculo) {
		this.corInteriorCirculo = corInteriorCirculo;
		this.corTextoCirculo = corTextoCirculo;
		this.corBordaCirculo = corBordaCirculo;
	}

	public ConfiguracaoNumerosSuperSete(int corInteriorCirculo, int corInteriorSelecionado, int corTextoCirculo, int corTextoCirculoSelecionado, int corBordaCirculo,
										 int corBordaCirculoSelecionado) {
		this.corInteriorCirculo = corInteriorCirculo;
		this.corInteriorSelecionado = corInteriorSelecionado;
		this.corTextoCirculo = corTextoCirculo;
		this.corTextoCirculoSelecionado = corTextoCirculoSelecionado;
		this.corBordaCirculo = corBordaCirculo;
		this.corBordaCirculoSelecionado = corBordaCirculoSelecionado;
	}

	public ConfiguracaoNumerosSuperSete(int corInteriorCirculo, int corInteriorSelecionado, int corTextoCirculo, int corTextoCirculoSelecionado, int corBordaCirculo,
										int corBordaCirculoSelecionado, int corInteriorQuadrado, int textoQuadrado, int corBordaQuadrado) {
		this.corInteriorCirculo = corInteriorCirculo;
		this.corInteriorSelecionado = corInteriorSelecionado;
		this.corTextoCirculo = corTextoCirculo;
		this.corTextoCirculoSelecionado = corTextoCirculoSelecionado;
		this.corBordaCirculo = corBordaCirculo;
		this.corBordaCirculoSelecionado = corBordaCirculoSelecionado;
		this.corInteriorQuadrado = corInteriorQuadrado;
		this.corTextoQuadrado = textoQuadrado;
		this.corBordaQuadrado = corBordaQuadrado;
	}

	public int getCorInteriorSelecionado() {
		return corInteriorSelecionado;
	}

	public void setCorInteriorSelecionado(int corInteriorSelecionado) {
		this.corInteriorSelecionado = corInteriorSelecionado;
	}

	public int getCorTextoCirculoSelecionado() {
		return corTextoCirculoSelecionado;
	}

	public void setCorTextoCirculoSelecionado(int corTextoCirculoSelecionado) {
		this.corTextoCirculoSelecionado = corTextoCirculoSelecionado;
	}

	public int getCorBordaCirculoSelecionado() {
		return corBordaCirculoSelecionado;
	}

	public void setCorBordaCirculoSelecionado(int corBordaCirculoSelecionado) {
		this.corBordaCirculoSelecionado = corBordaCirculoSelecionado;
	}

	public int getCorBordaCirculo() {
		return corBordaCirculo;
	}

	public void setCorBordaCirculo(int corBordaCirculo) {
		this.corBordaCirculo = corBordaCirculo;
	}

	public int getCorInteriorCirculo() {
		return corInteriorCirculo;
	}

	public void setCorInteriorCirculo(int corInteriorCirculo) {
		this.corInteriorCirculo = corInteriorCirculo;
	}

	public int getCorTextoCirculo() {
		return corTextoCirculo;
	}

	public void setCorTextoCirculo(int corTextoCirculo) {
		this.corTextoCirculo = corTextoCirculo;
	}

	public int getCorBordaQuadrado() {
		return corBordaQuadrado;
	}

	public void setCorBordaQuadrado(int corBordaQuadrado) {
		this.corBordaQuadrado = corBordaQuadrado;
	}

	public int getCorInteriorQuadrado() {
		return corInteriorQuadrado;
	}

	public void setCorInteriorQuadrado(int corInteriorQuadrado) {
		this.corInteriorQuadrado = corInteriorQuadrado;
	}

	public int getTextoQuadrado() {
		return corTextoQuadrado;
	}

	public void setTextoQuadrado(int textoQuadrado) {
		this.corTextoQuadrado = textoQuadrado;
	}
}