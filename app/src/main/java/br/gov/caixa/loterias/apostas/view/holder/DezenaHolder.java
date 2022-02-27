package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import org.jetbrains.annotations.NotNull;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;

public class DezenaHolder extends LoteriasHolder<Dezena> {

	private TextView txtNumero;

	private Context context;
	private DezenaConfig config;

	public DezenaHolder(View itemView, Context context, DezenaConfig config) {
		super(itemView);
		this.context = context;
		this.config = config;

		txtNumero = itemView.findViewById(R.id.tv_item_numeto);
	}

	@Override
	public void bind(Dezena dezena, int position) {
		Drawable shape;
		if (dezena.isSelected()) {
			shape = getShape(R.drawable.ic_item_dezena_selecionado, config.getColor());
			txtNumero.setTextColor(Color.WHITE);
		} else {
			shape = getShape(R.drawable.ic_item_dezena, config.getColor());
			txtNumero.setTextColor(ContextCompat.getColor(context, config.getColor()));
		}
		txtNumero.setBackground(shape);

		if(config.isLabelDezena()){
			if (dezena.getValue().length() == 1) {
				txtNumero.setText(String.format("0%s", dezena.getValue()));
			} else {
				txtNumero.setText(dezena.getValue());
			}
		}else {
			txtNumero.setText(dezena.getValue());
		}
		txtNumero.setTypeface(ViewUtils.getFontFuturaBold(context));
	}

	@NotNull
	private Drawable getShape(int drawable, int color ) {
		Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, drawable);
		Drawable shape = DrawableCompat.wrap(unwrappedDrawable);
		DrawableCompat.setTint(shape, color);

		return shape;
	}

}
