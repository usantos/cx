package br.gov.caixa.loterias.apostas.model.bean;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.Serializable;
public class OpcaoResgatePremio implements Serializable {
	private Drawable icon;
	private String title;
	private String subtitle;
	private View.OnClickListener listener;

	private boolean isHeader = false;

	public OpcaoResgatePremio(Drawable icon, String title, String subtitle, View.OnClickListener listener) {
		this.icon = icon;
		this.title = title;
		this.subtitle = subtitle;
		this.listener = listener;
	}

	public OpcaoResgatePremio() {
		this.isHeader = true;
		this.icon = null;
		this.title = null;
		this.subtitle = null;
		this.listener = null;
	}

	public Drawable getIcon() {
		return icon;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getTitle() {
		return title;
	}

	public View.OnClickListener getListener() {
		return listener;
	}

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }
}
