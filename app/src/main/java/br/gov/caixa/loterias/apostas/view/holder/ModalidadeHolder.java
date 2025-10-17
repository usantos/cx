package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
		tituloTipoAposta.setHint("Botão");

		if (tipoAposta.getSelect()) {
			//imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevoFundoCor());
			setImageTrevo(tipoAposta.getImagemTrevoFundoCor());

			GradientDrawable drawable = (GradientDrawable) AppCompatResources
					.getDrawable(ctx, R.drawable.background_roudend_azul)
					.mutate().getConstantState().newDrawable().mutate();

			drawable.setColor(ContextCompat.getColor(ctx, tipoAposta.getCorBackground()));
			linearLayoutTipoAposta.setBackground(drawable);

			if (tipoAposta.getTextSelectColor() != null && tipoAposta.getTextSelectColor() > 0){
				tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, tipoAposta.getTextSelectColor()));
			} else {
				tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.branco));
			}
		} else {
			//imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
			setImageTrevo(tipoAposta.getImagemTrevo());

			GradientDrawable drawable = (GradientDrawable) AppCompatResources
					.getDrawable(ctx, R.drawable.custom_border_cinza_rounded)
					.mutate().getConstantState().newDrawable().mutate();

			linearLayoutTipoAposta.setBackground(drawable);

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
		//imageViewTipoAPosta.setImageResource(tipoAposta.c());
		setImageTrevo(tipoAposta.getImagemTrevo());

		Drawable baseDrawable = AppCompatResources.getDrawable(ctx, R.drawable.custom_border_cinza_rounded);
		GradientDrawable drawable = (GradientDrawable) baseDrawable.getConstantState().newDrawable().mutate();

		linearLayoutTipoAposta.setBackground(drawable);

		tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.cinzatextoselecionado));
	}

	public void atualizaSelecionado(TipoAposta tipoAposta) {
		//imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevoFundoCor());
		setImageTrevo(tipoAposta.getImagemTrevoFundoCor());

		Drawable baseDrawable = AppCompatResources.getDrawable(ctx, R.drawable.background_roudend_azul);
		GradientDrawable drawable = (GradientDrawable) baseDrawable.getConstantState().newDrawable().mutate();

		drawable.setColor(ContextCompat.getColor(ctx, tipoAposta.getCorBackground()));

		linearLayoutTipoAposta.setBackground(drawable);

		tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.branco));
	}

	private void setImageTrevo(Integer imagemTrevoFundoCor) {
		if (imagemTrevoFundoCor == null) {
			imageViewTipoAPosta.setVisibility(View.GONE);
		} else {
			imageViewTipoAPosta.setImageResource(imagemTrevoFundoCor);
		}
	}
}
