package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class DezenaHolder extends LoteriasHolder<Dezena> implements  View.OnClickListener {

	private TextView txtNumero;

	private Context context;
	private DezenaConfig config;
	private OnItemClickListener listener;

	public DezenaHolder(View itemView, Context context, DezenaConfig config, OnItemClickListener listener) {
		super(itemView);
		this.context = context;
		this.config = config;
		this.listener = listener;

		txtNumero = itemView.findViewById(R.id.tv_item_numeto);
		txtNumero.setOnClickListener(this);
	}

	@Override
	public void bind(Dezena dezena, int position) {
		atualizaShape(dezena);

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

	private void atualizaShape(Dezena dezena) {
		Drawable shape;
		if (dezena.isSelected()) {
			if (config.isShapePadrao()) {
				shape = getShape(config.getDrawableSelecionado(), config.getColor());
				txtNumero.setTextColor(Color.WHITE);
			}else {
				shape = AppCompatResources.getDrawable(context, config.getDrawableSelecionado());
				txtNumero.setTextColor(config.getColor());
				txtNumero.setTypeface(Typeface.DEFAULT_BOLD);
			}
		} else {
			if (config.isShapePadrao()){
				shape = getShape(config.getDrawable(), config.getColor());
			}else {
				shape = AppCompatResources.getDrawable(context, config.getDrawable());
				txtNumero.setTextColor(ContextCompat.getColor(context, config.getColor()));
			}

		}
		txtNumero.setBackground(shape);
	}

	public void atualizaDezena(Dezena dezena) {
		atualizaShape(dezena);
	}

	@NotNull
	private Drawable getShape(int drawable, int color ) {
		Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, drawable);
		Drawable shape = DrawableCompat.wrap(unwrappedDrawable);
		DrawableCompat.setTint(shape, color);

		return shape;
	}

	@Override
	public void onClick(View v) {
		listener.click(this, getAdapterPosition());
	}
}
