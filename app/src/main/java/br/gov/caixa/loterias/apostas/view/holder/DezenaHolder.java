package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
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

		if (config.isClickable()){
			txtNumero.setOnClickListener(this);
		}

	}

	private void atualizaShape(Dezena dezena) {
		Drawable shape;
		if (dezena.isSelected()) {
			if (config.isShapePadrao()) {
				shape = VectorUtils.getShape(config.getDrawableSelecionado(), config.getColor());
				if (config.getShapeConfig().getTextoSelecionadoColor() > 0){
					txtNumero.setTextColor(ContextCompat.getColor(context, config.getShapeConfig().getTextoSelecionadoColor()));
				} else {
					txtNumero.setTextColor(ContextCompat.getColor(context, R.color.branco));
				}
			}else {
				shape = AppCompatResources.getDrawable(context, config.getDrawableSelecionado());
				txtNumero.setTextColor(config.getColor());
			}
		} else {
			shape = VectorUtils.getShape(config.getDrawable(), config.getColor());
			txtNumero.setTextColor(ContextCompat.getColor(context, config.getColor()));
		}

		txtNumero.setBackground(shape);
	}

	public void atualizaDezena(Dezena dezena) {
		atualizaShape(dezena);
	}

	@Override
	public void onClick(View v) {
		listener.itemClick(this, getAdapterPosition());
	}
}
