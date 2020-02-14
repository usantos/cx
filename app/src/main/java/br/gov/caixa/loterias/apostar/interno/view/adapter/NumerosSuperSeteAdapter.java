package br.gov.caixa.loterias.apostar.interno.view.adapter;

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

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

public class NumerosSuperSeteAdapter extends BaseAdapter {
	private Context context;
	public List<Dezena> dezenas = new ArrayList<>();
	private int corSuperSete;
	public List<Integer> dezenasSelecionadas;

	public NumerosSuperSeteAdapter(int typeGameColorDark, Activity parentActivity, List<Integer> dezenasSelecionadas) {
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.context = parentActivity;
		this.corSuperSete = typeGameColorDark;
		preencheNumeros();
	}


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
		sizeItens 		= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());

		if (view == null) {
			textView = new TextView(this.context);

			textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
		} else {
			textView = (TextView) view;
		}
			shape.setColor(Color.WHITE);
			shape.setStroke(5, context.getResources().getColor(corSuperSete));
			textView.setTextColor(ContextCompat.getColor(context, R.color.black));
			dezenas.get(i).setSelected(Boolean.FALSE);

			shape.setCornerRadius(sizeItens / 2);
			textView.setBackground(shape);
			textView.setText(dezenas.get(i).getValue());
			textView.setTypeface(ViewUtils.getFontFuturaBold(context));
			textView.setGravity(Gravity.CENTER);

		return textView;
	}

	private void preencheNumeros(){
		for (Integer marcado : dezenasSelecionadas) {
			dezenas.add(new Dezena("" + marcado.intValue(), Boolean.FALSE));
		}
	}
}