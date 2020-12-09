package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.io.Serializable;

public class ConfiguracaoNumeros implements Serializable {
	private int corInteriorCirculo;
	private int corInteriorSelecionado;
	private int corTextoCirculo;
	private int corTextoCirculoSelecionado;
	private int corBordaCirculo;
	private int corBordaCirculoSelecionado;

	public ConfiguracaoNumeros (int corNormal, int corSelecionado){
		setCorInteriorCirculo(corNormal);
		setCorTextoCirculo(corSelecionado);
		setCorBordaCirculo(corSelecionado);

		setCorInteriorSelecionado(corSelecionado);
		setCorTextoCirculoSelecionado(corNormal);
		setCorBordaCirculoSelecionado(corNormal);
	}

	public int getCorInteriorCirculo() {
		return corInteriorCirculo;
	}

	public void setCorInteriorCirculo(int corInteriorCirculo) {
		this.corInteriorCirculo = corInteriorCirculo;
	}

	public int getCorInteriorSelecionado() {
		return corInteriorSelecionado;
	}

	public void setCorInteriorSelecionado(int corInteriorSelecionado) {
		this.corInteriorSelecionado = corInteriorSelecionado;
	}

	public int getCorTextoCirculo() {
		return corTextoCirculo;
	}

	public void setCorTextoCirculo(int corTextoCirculo) {
		this.corTextoCirculo = corTextoCirculo;
	}

	public int getCorTextoCirculoSelecionado() {
		return corTextoCirculoSelecionado;
	}

	public void setCorTextoCirculoSelecionado(int corTextoCirculoSelecionado) {
		this.corTextoCirculoSelecionado = corTextoCirculoSelecionado;
	}

	public int getCorBordaCirculo() {
		return corBordaCirculo;
	}

	public void setCorBordaCirculo(int corBordaCirculo) {
		this.corBordaCirculo = corBordaCirculo;
	}

	public int getCorBordaCirculoSelecionado() {
		return corBordaCirculoSelecionado;
	}

	public void setCorBordaCirculoSelecionado(int corBordaCirculoSelecionado) {
		this.corBordaCirculoSelecionado = corBordaCirculoSelecionado;
	}

}