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

public class SuperSeteAdapter extends BaseAdapter {
	private Context context;
	public List<Dezena> dezenas = new ArrayList<>();
	private int typeGameColorLight;
	public List<Integer> dezenasSelecionadas, dezenasResultado;
	private static final int NUMERO_DEZENAS = 9;
	public static final int QTD_MAX_POR_COLUNA = 3;

	public SuperSeteAdapter(int typeGameColorLight, Activity parentActivity) {
		this.context = parentActivity;
		this.typeGameColorLight = typeGameColorLight;
		preencheDezenas();
	}

	public SuperSeteAdapter(int typeGameColorDark, Activity parentActivity, List<Integer> dezenasResultado) {
		this.dezenasResultado = dezenasResultado;
		this.context = parentActivity;
		this.typeGameColorLight = typeGameColorDark;
		preencheResultado();
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
		GradientDrawable shape     = new GradientDrawable();
		int              sizeItens;
		if (dezenasResultado == null) {
			sizeItens 		= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
		} else {
			sizeItens 		= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());

		}

		if (view == null) {
			textView = new TextView(this.context);

			textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
		} else {
			textView = (TextView) view;
		}
		if (dezenasResultado == null) {
			if (dezenasSelecionadas.contains(Integer.valueOf(dezenas.get(i).getValue()))) {
				shape.setColor(ContextCompat.getColor(context, typeGameColorLight));
				textView.setTextColor(Color.WHITE);
				dezenas.get(i).setSelected(Boolean.TRUE);
			} else {
				shape.setColor(Color.WHITE);
				textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));
				dezenas.get(i).setSelected(Boolean.FALSE);
			}

			shape.setCornerRadius(sizeItens / 2);
			shape.setStroke(1, Color.GRAY);
			textView.setBackground(shape);
			textView.setText(dezenas.get(i).getValue());
			textView.setTypeface(ViewUtils.getFontFuturaBold(context));
			textView.setGravity(Gravity.CENTER);
		} else {
			shape.setColor(Color.WHITE);
			shape.setStroke(5, context.getResources().getColor(R.color.superseteescuro));
			textView.setTextColor(ContextCompat.getColor(context, R.color.black));
			dezenas.get(i).setSelected(Boolean.FALSE);

			shape.setCornerRadius(sizeItens / 2);
			textView.setBackground(shape);
			textView.setText(dezenas.get(i).getValue());
			textView.setTypeface(ViewUtils.getFontFuturaBold(context));
			textView.setGravity(Gravity.CENTER);
		}


		return textView;
	}

	public void limpaNumeros(){
		dezenasSelecionadas = new ArrayList<>();
		notifyDataSetChanged();
	}

	private void preencheDezenas(){
		dezenasSelecionadas = new ArrayList<>();
		for (int i = 0; i <= NUMERO_DEZENAS ; i++) {
			dezenas.add(new Dezena("" + i, Boolean.FALSE));
		}
	}

	private void preencheResultado(){
		for (Integer marcado :dezenasResultado) {
			dezenas.add(new Dezena("" + marcado.intValue(), Boolean.FALSE));
		}
	}

	public int getQtdColunasMaximas(int qtdMaxPrognosticos){
		int qtdColunasDisponiveis = 1;
		if (qtdMaxPrognosticos >= 8 && qtdMaxPrognosticos <= 14) {
			qtdColunasDisponiveis = 2;
		} else if (qtdMaxPrognosticos >= 15 && qtdMaxPrognosticos <= 21) {
			qtdColunasDisponiveis = 3;
		}
		return qtdColunasDisponiveis;
	}
}