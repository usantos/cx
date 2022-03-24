package br.gov.caixa.loterias.apostas.view.config;

import br.gov.caixa.loterias.apostas.R;

public class DezenaConfig {
	private boolean isLabelDezena;
	private int color;
	private int drawable;
	private int drawableSelecionado;
	private boolean isShapePadrao;
	private boolean isClickable;

	public DezenaConfig(boolean isDezena, int color, int drawable, int drawableSelecionado, boolean isClickable) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.drawable = drawable;
		this.drawableSelecionado = drawableSelecionado;
		this.isClickable = isClickable;
		this.isShapePadrao = false;
	}

	public DezenaConfig(boolean isDezena, int color, boolean isClickable) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.isClickable = isClickable;
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

	public boolean isClickable() {
		return isClickable;
	}
}
