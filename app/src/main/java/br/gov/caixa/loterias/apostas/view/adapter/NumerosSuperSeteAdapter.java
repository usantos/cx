package br.gov.caixa.loterias.apostas.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class NumerosSuperSeteAdapter extends BaseAdapter {

	//region Variables
	public List<Dezena> dezenas = new ArrayList<>();
	public List<Integer> dezenasSelecionadas;
	public ConfiguracaoNumerosSuperSete configuracao;

	private int corSuperSete;
	private Context context;
	//endregion

	//region Initialization Methods
	public NumerosSuperSeteAdapter(int typeGameColorDark, Activity parentActivity, List<Integer> dezenasSelecionadas) {
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.context = parentActivity;
		this.corSuperSete = typeGameColorDark;
		this.configuracao = null;
		preencheNumeros();
	}

	public NumerosSuperSeteAdapter(ConfiguracaoNumerosSuperSete configuracao, Activity parentActivity, List<Integer> dezenasSelecionadas) {
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.context = parentActivity;
		this.configuracao = configuracao;
		preencheNumeros();
	}
	//endregion

	//region Layout Configuration Methods
	@Override
	public int getCount() {
		return dezenas.size();
	}

	@Override
	public Object getItem(int i) {
		return dezenas.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		TextView         textView;
		GradientDrawable shape = new GradientDrawable();
		int              sizeItens;
		sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());

		if (view == null) {
			textView = new TextView(this.context);

			textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
		} else {
			textView = (TextView) view;
		}
		if (configuracao == null) {
			shape.setColor(Color.WHITE);
			shape.setStroke(3, context.getResources().getColor(corSuperSete));
			textView.setTextColor(ContextCompat.getColor(context, R.color.black));
		} else {
			if (dezenas.get(i).isSelected()) {
				shape.setColor(context.getResources().getColor(configuracao.getCorInteriorCirculo()));
				shape.setStroke(3, context.getResources().getColor(configuracao.getCorBordaCirculoSelecionado()));
				textView.setTextColor(context.getResources().getColor(configuracao.getCorTextoCirculoSelecionado()));
			} else {
				shape.setColor(context.getResources().getColor(configuracao.getCorInteriorCirculo()));
				shape.setStroke(3, context.getResources().getColor(configuracao.getCorBordaCirculo()));
				textView.setTextColor(context.getResources().getColor(configuracao.getCorTextoCirculo()));
			}
		}
		shape.setCornerRadius(sizeItens / 2);
		textView.setBackground(shape);
		textView.setText(dezenas.get(i).getValue());
		textView.setTypeface(ViewUtils.getFontFutura(context));
		textView.setGravity(Gravity.CENTER);

		return textView;
	}
	//endregion

	//region Support Methods
	private void preencheNumeros() {
		for (Integer marcado : dezenasSelecionadas) {
			dezenas.add(new Dezena("" + marcado.intValue(), Boolean.FALSE));
		}
	}
	//endregion

}