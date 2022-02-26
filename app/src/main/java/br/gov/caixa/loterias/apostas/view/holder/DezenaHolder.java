package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class DezenaHolder extends LoteriasHolder<Dezena> {

	private Context context;
	private int typeGameColorLight;

	public DezenaHolder(View itemView, Context context, int typeGameColorLight) {
		super(itemView);
		this.context = context;
		this.typeGameColorLight = typeGameColorLight;
	}

	@Override
	public void bind(Dezena dezena, int position) {
		TextView         textView;
		GradientDrawable shape     = new GradientDrawable();
		int              sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, context.getResources().getDisplayMetrics());

		if (itemView == null) {
			textView = new TextView(this.context);

			textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
		} else {
			textView = (TextView) itemView;
		}

		if (dezena.isSelected()) {
			shape.setColor(ContextCompat.getColor(context, typeGameColorLight));
			textView.setTextColor(Color.WHITE);
		} else {
			shape.setColor(Color.WHITE);
			textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));
		}

		shape.setCornerRadius(sizeItens / 2);
		shape.setStroke(1, Color.GRAY);
		textView.setBackground(shape);
		if (dezena.getValue().length() == 1) {
			textView.setText(String.format("0%s", dezena.getValue()));
		} else {

			textView.setText(dezena.getValue());
		}
		textView.setTypeface(ViewUtils.getFontFuturaBold(context));
		textView.setGravity(Gravity.CENTER);
	}

}
