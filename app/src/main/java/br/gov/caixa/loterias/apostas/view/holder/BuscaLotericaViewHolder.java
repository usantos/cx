package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaComposition;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class BuscaLotericaViewHolder extends LoteriasHolder<LotericaComposition> {
	TextView textView;
	ImageView ibHeart;
	public BuscaLotericaViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
		super(itemView);
		textView = itemView.findViewById(R.id.tv_buscar_fav);
		ibHeart = itemView.findViewById(R.id.iv_coracao);

		itemView.setOnClickListener(v -> {
			if (onItemClickListener != null) {
				int position = getAbsoluteAdapterPosition();
				if (position != RecyclerView.NO_POSITION) {
					onItemClickListener.itemClick(this, getAbsoluteAdapterPosition());
				}
			}
		});

	}

	@Override
	public void bind(LotericaComposition item, int position) {
		String contentDescription = "Lotérica favorita ";
		if (!item.isFavorite()) {
			contentDescription = "Lotérica não favorita ";
		}
		contentDescription += mioloCodigoLoterica(item.getLotericaDTO().getCodigo()) + " - " +item.getLotericaDTO().getNomeFantasia().trim();
		itemView.setContentDescription(contentDescription);
		textView.setText(mioloCodigoLoterica(item.getLotericaDTO().getCodigo()) + " - " +item.getLotericaDTO().getNomeFantasia().trim());

		ibHeart.setImageResource(item.isFavorite()?
				R.drawable.ic_orange_heart_filled
				:R.drawable.ic_orange_heart);
	}

	private String mioloCodigoLoterica(String codigoLoterico) {
		return codigoLoterico.substring(3,9);
	}

}
