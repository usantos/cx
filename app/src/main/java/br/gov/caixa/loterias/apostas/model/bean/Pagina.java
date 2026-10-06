package br.gov.caixa.loterias.apostas.model.bean;

import android.text.SpannableStringBuilder;

import java.io.Serializable;

public class Pagina implements Serializable {
	private SpannableStringBuilder subtitulo;
	private int numeroPagina;

	public Pagina(SpannableStringBuilder subtitulo, int numeroPagina) {
		this.subtitulo = subtitulo;
		this.numeroPagina = numeroPagina;
	}

	public SpannableStringBuilder getSubtitulo() {
		return subtitulo;
	}

	public int getNumeroPagina() {
		return numeroPagina;
	}
}
