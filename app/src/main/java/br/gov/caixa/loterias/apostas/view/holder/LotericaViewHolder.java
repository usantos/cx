package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class LotericaViewHolder extends LoteriasHolder<LotericaDTO> {
	TextView textView;
	public LotericaViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
		super(itemView);
		textView = itemView.findViewById(R.id.textNomeLoterica);

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
	public void bind(LotericaDTO item, int position) {
		textView.setText(mioloCodigoLoterica(item.getCodigo()) + " - " +item.getNomeFantasia().trim());
	}

	private String mioloCodigoLoterica(String codigoLoterico) {
		return codigoLoterico.substring(3,9);
	}
}
