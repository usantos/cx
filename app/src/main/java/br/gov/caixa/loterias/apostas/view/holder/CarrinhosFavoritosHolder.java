package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.view.listener.OnCarrinhosFavoritosClickListener;

public class CarrinhosFavoritosHolder extends LoteriasHolder<CarrinhoFavoritoDTO> implements  View.OnClickListener {

	private TextView tvNomeCarrinho, tvValorTotalCarrinho;
	private ImageButton ibExcluir, ibDetalhes, ibTransformar;
	private ConstraintLayout clCard;
	private LinearLayout layoutDetalhes;
	private OnCarrinhosFavoritosClickListener listener;

	public CarrinhosFavoritosHolder(View itemView, OnCarrinhosFavoritosClickListener listener) {
		super(itemView);
		tvNomeCarrinho = itemView.findViewById(R.id.tv_nome_carrinho);
		tvValorTotalCarrinho = itemView.findViewById(R.id.tv_valor_total_carrinho);
		ibExcluir = itemView.findViewById(R.id.ib_excluir_carrinho);
		ibDetalhes = itemView.findViewById(R.id.ib_lupa_detalhes);
		ibTransformar = itemView.findViewById(R.id.ib_transformar_carrinho);
		clCard = itemView.findViewById(R.id.cl_card_carrinho_favorito);
		layoutDetalhes = itemView.findViewById(R.id.ll_apostas_carrinho);

		clCard.setOnClickListener(this);
		ibExcluir.setOnClickListener(this);
		ibDetalhes.setOnClickListener(this);
		ibTransformar.setOnClickListener(this);

		this.listener = listener;
	}

	@Override
	public void bind(CarrinhoFavoritoDTO carrinhoFavorito, int position) {
		this.tvNomeCarrinho.setText(carrinhoFavorito.getNome());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_nome_carrinho:
				break;
			case R.id.tv_valor_total_carrinho:
				break;
			case R.id.ib_excluir_carrinho:
				listener.onExcluirCarrinho(getAbsoluteAdapterPosition());
				break;
			case R.id.ib_lupa_detalhes:
				listener.onDetalhesCarrinho(layoutDetalhes, getAbsoluteAdapterPosition());
				break;
			case R.id.ib_transformar_carrinho:
				listener.onTransformaCarrinho(getAbsoluteAdapterPosition());
				break;
			case R.id.cl_card_carrinho_favorito:
				listener.onBuscaApostas(layoutDetalhes, clCard, true, getAbsoluteAdapterPosition());
				break;
			default:
				break;
		}
	}
}
