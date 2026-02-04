package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnCartaoMeioPagamentoListener;

public class CartaoMeioPagamentoHolder extends LoteriasHolder<RetornoCartao> implements  View.OnClickListener {

	private AppCompatImageView imagem;
	private TextView numero;
	private AppCompatImageButton fav;
	private ImageButton deleta;

	private OnCartaoMeioPagamentoListener listener;
	private Context context;

	public CartaoMeioPagamentoHolder(View itemView, Context context, OnCartaoMeioPagamentoListener listener) {
		super(itemView);
		this.context = context;
		this.listener = listener;

		this.imagem = itemView.findViewById(R.id.imagem_cartao);
		this.numero = itemView.findViewById(R.id.numero_cartao);
		this.fav = itemView.findViewById(R.id.favoritar);
		this.deleta = itemView.findViewById(R.id.exlcuir);

		this.fav.setOnClickListener(this);
		this.deleta.setOnClickListener(this);
		this.itemView.setOnClickListener(this);
	}

	@Override
	public void bind(RetornoCartao cartao, int position) {
		this.imagem.setImageDrawable(getBandeira(cartao.getNomeMetodoPagamento()));
		this.numero.setText("  " + context.getResources().getString(R.string.mascara_cartao) + "  " + cartao.getUltimosDigitos());
		if (cartao.isFav()){
			this.fav.setBackground(context.getDrawable(R.drawable.ic_vector_fav_full));
		} else {
			this.fav.setBackground(context.getDrawable(R.drawable.vector_fav_empty));
		}
	}

	private Drawable getBandeira(String nomeMetodo){
		Drawable drawable = null;
		PorterDuffColorFilter porterDuffColorFilter = VectorUtils.getColorFilter(Color.BLUE);
		switch (nomeMetodo.toLowerCase()){
			case "visa":
				drawable = context.getDrawable(R.drawable.ic_visa);
				drawable.setColorFilter(porterDuffColorFilter);
				return drawable;
			case "master":
			case "mastercard":
				return context.getDrawable(R.drawable.ic_master_card);
			case "hipercard":
				return context.getDrawable(R.drawable.ic_hipercard);
			case "diners":
				return context.getDrawable(R.drawable.ic_diners);
			case "elo":
				//return context.getDrawable(R.drawable.ic_bandeira_elo);
				return context.getDrawable(R.drawable.elo_fundo_claro);
			case "amex":
			case "american express":
			case "americanexpress":
				//drawable = context.getDrawable(R.drawable.ic_american);
				drawable = context.getDrawable(R.drawable.ic_bandeira_amex2);
				//drawable.setColorFilter(porterDuffColorFilter);
				return drawable;
			default:
				return context.getDrawable(R.drawable.cartao_cvc);

		}
	}


	public void showButtonFav(boolean show){
		if (show){
			this.fav.setVisibility(View.VISIBLE);
		} else {
			this.fav.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.favoritar:
				listener.favoritar(this, getAdapterPosition());
				break;
			case R.id.exlcuir:
				listener.deletar(this, getAdapterPosition());
				break;
			default:
				listener.itemClick(this, getAdapterPosition());
		}
	}
}
