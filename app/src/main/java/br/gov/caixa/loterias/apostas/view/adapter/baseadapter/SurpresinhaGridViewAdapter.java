package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

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
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;

public class SurpresinhaGridViewAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> rows;
	private FragmentActivity activity;
	private int typeGameColorLight;

	public SurpresinhaGridViewAdapter(Context context, ArrayList<String> activitData, FragmentActivity activity, int typeGameColorLight) {
		this.context = context;
		this.rows = activitData;
		this.activity = activity;
		this.typeGameColorLight = typeGameColorLight;
	}

	@Override
	public int getCount() {
		return rows.toArray().length;
	}

	@Override
	public Object getItem(int i) {
		return rows.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		TextView         textView;
		GradientDrawable shape = new GradientDrawable();

		int sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, activity.getResources().getDisplayMetrics());

		if (view == null) {
			textView = new TextView(this.context);
			textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
		} else {
			textView = (TextView) view;
		}

		shape.setColor(Color.WHITE);
		textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));

		shape.setCornerRadius(sizeItens / 2);
		shape.setStroke(1, Color.GRAY);
		textView.setBackground(shape);
		textView.setTypeface(FonteUtils.getFonte(FontCaixaEnum.BOLD));
		textView.setText(rows.get(i));
		textView.setGravity(Gravity.CENTER);

		return textView;
	}

	public void atualizaDezenas(ArrayList<String> dezenas) {
		rows = dezenas;
		notifyDataSetChanged();
	}
}
