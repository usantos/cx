package br.gov.caixa.loterias.apostas.view.config;

import br.gov.caixa.loterias.apostas.R;

public class ShapeConfig {
	private int drawable;
	private int drawableSelecionado;
	private int higth;
	private boolean isShapePadrao;

	public ShapeConfig(int drawable, int drawableSelecionado, int higth) {
		this.drawable = drawable;
		this.drawableSelecionado = drawableSelecionado;
		this.higth = higth;
		this.isShapePadrao = false;
	}

	public ShapeConfig() {
		this.drawable = R.drawable.ic_item_dezena;
		this.drawableSelecionado = R.drawable.ic_item_dezena_selecionado;
		this.isShapePadrao = true;
		this.higth = 0;
	}

	public int getDrawable() {
		return drawable;
	}

	public int getDrawableSelecionado() {
		return drawableSelecionado;
	}

	public int getHigth() {
		return higth;
	}

	public boolean isShapePadrao() {
		return isShapePadrao;
	}
}
