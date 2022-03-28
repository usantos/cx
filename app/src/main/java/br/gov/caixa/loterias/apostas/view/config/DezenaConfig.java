package br.gov.caixa.loterias.apostas.view.config;

public class DezenaConfig {
	private boolean isLabelDezena;
	private int color;
	private ShapeConfig shapeConfig;
	private boolean isClickable;

	public DezenaConfig(boolean isDezena, int color, ShapeConfig shapeConfig, boolean isClickable) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.shapeConfig = shapeConfig;
		this.isClickable = isClickable;
	}

	public DezenaConfig(boolean isDezena, int color, boolean isClickable) {
		this.isLabelDezena = isDezena;
		this.color = color;
		this.isClickable = isClickable;
		this.shapeConfig = new ShapeConfig();
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
}
