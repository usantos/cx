package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
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
			tituloTipoAposta.setContentDescription(tipoAposta.getTitulo() + ", Selecionado");
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
			tituloTipoAposta.setContentDescription(tipoAposta.getTitulo());
			//imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
			setImageTrevo(tipoAposta.getImagemTrevo());

			GradientDrawable drawable = (GradientDrawable) AppCompatResources
					.getDrawable(ctx, R.drawable.custom_border_cinza_rounded)
					.mutate().getConstantState().newDrawable().mutate();

			linearLayoutTipoAposta.setBackground(drawable);

			if (tipoAposta.getTextColor() != null && tipoAposta.getTextColor() > 0){
				tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, tipoAposta.getTextColor()));
			} else {
				tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.cinzanaoselecionado));
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

		tituloTipoAposta.setTextColor(ContextCompat.getColor(ctx, R.color.cinzanaoselecionado));
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

			//Alinha o Texto Todas
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			tituloTipoAposta.setLayoutParams(params);
			tituloTipoAposta.setGravity(Gravity.CENTER);

		} else {
			imageViewTipoAPosta.setImageResource(imagemTrevoFundoCor);
		}
	}
}
