package br.gov.caixa.loterias.apostas.view.config;

import br.gov.caixa.loterias.apostas.R;

public class DezenaConfig {
	private boolean isLabelDezena;
	private int color;
	private ShapeConfig shapeConfig;
	private boolean isClickable;
	private int layout;

	private boolean isQueroNaoQuero;

	public DezenaConfig(boolean isDezena, int color, int layout, ShapeConfig shapeConfig, boolean isClickable) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.shapeConfig = shapeConfig;
		this.isClickable = isClickable;
		this.layout = layout;
	}

	public DezenaConfig(boolean isDezena, int color, int textColor, boolean isClickable) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.isClickable = isClickable;
		this.shapeConfig = new ShapeConfig(color, textColor);
		this.layout = R.layout.item_dezena;
	}

	public DezenaConfig(boolean isDezena, int color, int textColor, boolean isClickable, boolean isQueroNaoQuero) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.isClickable = isClickable;
		this.shapeConfig = new ShapeConfig(color, textColor);
		this.layout = R.layout.item_dezena_quero_nao_quero_cartela;
		this.isQueroNaoQuero = isQueroNaoQuero;
	}
	public boolean isLabelDezena() {
		return isLabelDezena;
	}

	public int getColor() {
		return color;
	}
	public int getColorDefault(){
		return R.color.cinza110;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getDrawable() {
		return this.shapeConfig.getDrawable();
	}

	public int getDrawableSelecionado() {
		return this.shapeConfig.getDrawableSelecionado();
	}

	public boolean isShapePadrao(){
		return shapeConfig.isShapePadrao();
	}

	public boolean isClickable() {
		return isClickable;
	}

	public ShapeConfig getShapeConfig() {
		return shapeConfig;
	}

	public int getLayout() {
		return layout;
	}

	public boolean isQueroNaoQuero() {
		return isQueroNaoQuero;
	}
}
