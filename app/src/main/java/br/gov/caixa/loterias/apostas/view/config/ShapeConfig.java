package br.gov.caixa.loterias.apostas.view.config;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.R;

public class ShapeConfig implements Serializable {
	private int drawable;
	private int drawableSelecionado;
	private int textoColor;
	private int textoSelecionadoColor;
	private boolean isShapePadrao;

	public ShapeConfig(int drawable, int drawableSelecionado, int textoColor, int textoSelecionadoColor) {
		this.drawable = drawable;
		this.drawableSelecionado = drawableSelecionado;
		this.textoColor = textoColor;
		this.textoSelecionadoColor = textoSelecionadoColor;
		this.isShapePadrao = false;
	}

	public ShapeConfig(int textoColor, int textoSelecionadoColor) {
		this.textoColor = textoColor;
		this.textoSelecionadoColor = textoSelecionadoColor;
		this.drawable = R.drawable.ic_item_dezena;
		this.drawableSelecionado = R.drawable.ic_item_dezena_selecionado;
		this.isShapePadrao = true;
	}

	public int getDrawable() {
		return drawable;
	}

	public int getDrawableSelecionado() {
		return drawableSelecionado;
	}

	public boolean isShapePadrao() {
		return isShapePadrao;
	}

	public int getTextoColor() {
		return textoColor;
	}

	public int getTextoSelecionadoColor() {
		return textoSelecionadoColor;
	}
}
