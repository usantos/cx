package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import org.jetbrains.annotations.NotNull;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ModalidadeHolder extends LoteriasHolder<TipoAposta> implements  View.OnClickListener {
	private ImageView imageViewTipoAPosta;
	private LinearLayout linearLayoutTipoAposta;
	private TextView tituloTipoAposta;
	private Context ctx;
	private OnItemClickListener listener;

	public ModalidadeHolder(View itemView, Context ctx, OnItemClickListener listener) {
		super(itemView);
		this.ctx = ctx;
		this.listener = listener;

		imageViewTipoAPosta    = itemView.findViewById(R.id.imageviewTrevoTipoAposta);
		linearLayoutTipoAposta = itemView.findViewById(R.id.LinearLayoutContentTiposAposta);
		tituloTipoAposta       = itemView.findViewById(R.id.tituloTipoApostaTextView);

		itemView.setOnClickListener(this);
	}

	@Override
	public void bind(TipoAposta tipoAposta, int position) {

		tituloTipoAposta.setText(tipoAposta.getTitulo());

		if (tipoAposta.getSelect()) {
			imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevoFundoCor());
			linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(ctx, tipoAposta.getCorBackground()));
			if (tipoAposta.getTextSelectColor() != null && tipoAposta.getTextSelectColor() > 0){
				tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, tipoAposta.getTextSelectColor()));
			} else {
				tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.branco));
			}
		} else {
			imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
			linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(ctx, R.color.branco));
			linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(ctx, R.drawable.custom_border_cinza));
			if (tipoAposta.getTextColor() != null && tipoAposta.getTextColor() > 0){
				tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, tipoAposta.getTextColor()));
			} else {
				tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.cinzatextoselecionado));
			}
		}
	}

	@Override
	public void onClick(View v) {
		listener.itemClick(this, getAbsoluteAdapterPosition());
	}

	public void atualizaNaoSelecionado(TipoAposta tipoAposta) {
		imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
		linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(ctx, R.color.branco));
		linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(ctx, R.drawable.custom_border_cinza));
		tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.cinzatextoselecionado));
	}

	public void atualizaSelecionado(TipoAposta tipoAposta) {
		imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevoFundoCor());
		linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(ctx, tipoAposta.getCorBackground()));
		tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.branco));
	}
}
