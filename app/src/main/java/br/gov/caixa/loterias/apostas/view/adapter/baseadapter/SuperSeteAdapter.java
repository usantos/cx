package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class SuperSeteAdapter extends BaseAdapter {
	private Context context;
	public List<Dezena> dezenas = new ArrayList<>();
	private int typeGameColor;
	public List<Integer> dezenasSelecionadas;
	private static final int NUMERO_DEZENAS = 9;
	public static final int QTD_MAX_POR_COLUNA = 3;

	public SuperSeteAdapter(int typeGameColor, Activity parentActivity) {
		this.context = parentActivity;
		this.typeGameColor = typeGameColor;
		preencheDezenas();
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
		int              sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, context.getResources().getDisplayMetrics());

		if (view == null) {
			textView = new TextView(this.context);

			textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
		} else {
			textView = (TextView) view;
		}

		if (dezenasSelecionadas.contains(Integer.valueOf(dezenas.get(i).getValue()))) {
			shape.setColor(ContextCompat.getColor(context, typeGameColor));
			textView.setTextColor(ContextCompat.getColor(context, R.color.verde_item_supersete));
			dezenas.get(i).setSelected(Boolean.TRUE);
		} else {
			shape.setColor(Color.WHITE);
			shape.setStroke(3, ContextCompat.getColor(context, R.color.cinza130_supersete));
			textView.setTextColor(ContextCompat.getColor(context, R.color.cinza130_supersete));
			dezenas.get(i).setSelected(Boolean.FALSE);
		}

		shape.setCornerRadius(sizeItens / 2);
		textView.setBackground(shape);
		textView.setText(dezenas.get(i).getValue());
		textView.setTypeface(ViewUtils.getFontFuturaBold(context));
		textView.setGravity(Gravity.CENTER);

		return textView;
	}

	public void limpaNumeros(){
		dezenasSelecionadas = new ArrayList<>();
		notifyDataSetChanged();
	}

	private void preencheDezenas(){
		dezenasSelecionadas = new ArrayList<>();
		for (int i = 0; i <= NUMERO_DEZENAS ; i++) {
			dezenas.add(new Dezena(context.getResources().getString(R.string.string_vazia) + i, Boolean.FALSE));
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