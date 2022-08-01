package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.view.listener.OnMeusCartoesListener;

public class MeuCartaoHolder extends LoteriasHolder<RetornoCartao> implements  View.OnClickListener {

	private OnMeusCartoesListener listener;
	private Context context;

	public MeuCartaoHolder(View itemView, Context context, OnMeusCartoesListener listener) {
		super(itemView);
		this.context = context;
		this.listener = listener;
	}

	@Override
	public void bind(RetornoCartao cartao, int position) {
//		this.imagem.setImageDrawable(getBandeira(cartao.getIdMetodoPagamento()));
//		this.numero.setText("  " + context.getResources().getString(R.string.mascara_cartao) + "  " + cartao.getUltimosDigitos());
//		if (cartao.isFav()){
//			this.fav.setBackground(context.getDrawable(R.drawable.ic_vector_fav_full));
//		} else {
//			this.fav.setBackground(context.getDrawable(R.drawable.vector_fav_empty));
//		}
	}

	private Drawable getBandeira(String nomeMetodo){
		Drawable drawable = null;
		PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.BLUE,
																				PorterDuff.Mode.SRC_ATOP);
		switch (nomeMetodo){
			case "visa":
				drawable = context.getDrawable(R.drawable.ic_visa);
				drawable.setColorFilter(porterDuffColorFilter);
				return drawable;
			case "master":
				return context.getDrawable(R.drawable.ic_master_card);
			case "hipercard":
				return context.getDrawable(R.drawable.ic_hipercard);
			case "diners":
				return context.getDrawable(R.drawable.ic_diners);
			case "elo":
				return context.getDrawable(R.drawable.ic_elo);
			case "amex":
				drawable = context.getDrawable(R.drawable.ic_american);
				drawable.setColorFilter(porterDuffColorFilter);
				return drawable;
			default:
				return context.getDrawable(R.drawable.cartao_cvc);

		}
	}


	public void showButtonFav(boolean show){
//		if (show){
//			this.fav.setVisibility(View.VISIBLE);
//		} else {
//			this.fav.setVisibility(View.GONE);
//		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
//			case R.id.favoritar:
//				listener.favoritar(this, getAdapterPosition());
//				break;
//			case R.id.exlcuir:
//				listener.deletar(this, getAdapterPosition());
//				break;
		}
	}
}
