package br.gov.caixa.loterias.apostas.view.config;

import br.gov.caixa.loterias.apostas.R;

public class DezenaConfig {
	private boolean isLabelDezena;
	private int color;
	private int drawable;
	private int drawableSelecionado;
	private boolean isShapePadrao;

	public DezenaConfig(boolean isDezena, int color, int drawable, int drawableSelecionado) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.drawable = drawable;
		this.drawableSelecionado = drawableSelecionado;
		this.isShapePadrao = false;
	}

	public DezenaConfig(boolean isDezena, int color) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.drawable = R.drawable.ic_item_dezena;
		this.drawableSelecionado = R.drawable.ic_item_dezena_selecionado;
		this.isShapePadrao = true;
	}

	public boolean isLabelDezena() {
		return isLabelDezena;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getDrawable() {
		return drawable;
	}

	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}

	public int getDrawableSelecionado() {
		return drawableSelecionado;
	}

	public boolean isShapePadrao(){
		return isShapePadrao;
	}
}
