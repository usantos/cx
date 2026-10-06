package br.gov.caixa.loterias.apostas.view.config;

import br.gov.caixa.loterias.apostas.R;

public class ColunaConfig {
	private int color;
	private int background;

	public ColunaConfig(int color, int background) {
		this.color = color;
		this.background = background;
	}

	public int getColor() {
		return color;
	}

	public int getBackground() {
		return background;
	}
}
