package br.gov.caixa.loterias.apostas.model.bean;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.Serializable;
public class OpcaoResgatePremio implements Serializable {
	private Drawable icon;
	private String descricao;
	private View.OnClickListener listener;

	public OpcaoResgatePremio(Drawable icon, String descricao, View.OnClickListener listener) {
		this.icon = icon;
		this.descricao = descricao;
		this.listener = listener;
	}

	public Drawable getIcon() {
		return icon;
	}

	public String getDescricao() {
		return descricao;
	}

	public View.OnClickListener getListener() {
		return listener;
	}
}
