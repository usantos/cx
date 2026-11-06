package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.view.listener.OnMeusCartoesListener;

public class MeuCartaoHolder extends LoteriasHolder<RetornoCartao> implements  View.OnClickListener {

	private ConstraintLayout clFav;
	private AppCompatImageButton fav;
	private AppCompatImageButton naoFav;
	private AppCompatImageButton exlcuir;
	private ImageView imagem;
	private TextView numero;
	private TextView data;

	private OnMeusCartoesListener listener;
	private Context context;

	public MeuCartaoHolder(View itemView, Context context, OnMeusCartoesListener listener) {
		super(itemView);
		this.context = context;
		this.listener = listener;

		this.clFav = itemView.findViewById(R.id.cl_favoritar);
		this.fav = itemView.findViewById(R.id.ib_favoritar_cartao);
		this.naoFav = itemView.findViewById(R.id.ib_nao_favorito);
		this.exlcuir = itemView.findViewById(R.id.ib_excluir_cartao);
		this.imagem = itemView.findViewById(R.id.iv_bandeira_cartao);
		this.numero = itemView.findViewById(R.id.tv_numero_cartao);
		this.data = itemView.findViewById(R.id.tv_data_vencimento);

		this.clFav.setOnClickListener(this);
		this.fav.setOnClickListener(this);
		this.naoFav.setOnClickListener(this);
		this.exlcuir.setOnClickListener(this);
	}

	@Override
	public void bind(RetornoCartao cartao, int position) {
		this.imagem.setImageDrawable(getBandeira(cartao.getNomeMetodoPagamento()));
		this.numero.setText(context.getResources().getString(R.string.mascara_cartao_sem_ultimos_digitos) + "  " + cartao.getUltimosDigitos());
		this.data.setText(context.getResources().getString(R.string.venc_ponto_dois_pontos) + cartao.getMesValidade() + context.getResources().getString(R.string.barra)  + cartao.getAnoValidade());

		if (cartao.isFav()){
			this.fav.setVisibility(View.VISIBLE);
			this.naoFav.setVisibility(View.GONE);
		} else {
			this.fav.setVisibility(View.GONE);
			this.naoFav.setVisibility(View.VISIBLE);
		}
	}

	private Drawable getBandeira(String nomeMetodo){
		switch (nomeMetodo.toLowerCase()){
			case "visa":
				return context.getDrawable(R.drawable.ic_visa);
			case "master":
			case "mastercard":
				return context.getDrawable(R.drawable.ic_master_card);
			case "hipercard":
				return context.getDrawable(R.drawable.ic_hipercard);
			case "diners":
				return context.getDrawable(R.drawable.ic_diners);
			case "elo":
				//return context.getDrawable(R.drawable.ic_elo);
				return context.getDrawable(R.drawable.elo_fundo_escuro);
			case "amex":
			case "american express":
				return context.getDrawable(R.drawable.ic_american);
			default:
				return context.getDrawable(R.drawable.cartao_cvc);
		}
	}

	public void showButtonFav(boolean show){
		if (show){
			this.clFav.setVisibility(View.VISIBLE);
		} else {
			this.clFav.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.cl_favoritar:
			case R.id.ib_favoritar_cartao:
			case R.id.ib_nao_favorito:
				listener.favoritar(this, getAdapterPosition());
				break;
			case R.id.ib_excluir_cartao:
				listener.deletar(this, getAdapterPosition());
				break;
		}
	}
}
