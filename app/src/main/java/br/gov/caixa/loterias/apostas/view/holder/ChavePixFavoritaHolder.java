package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.ChavePix;
import br.gov.caixa.loterias.apostas.view.listener.OnChaveFavoritaClickListener;

public class ChavePixFavoritaHolder extends LoteriasHolder<ChavePix> implements  View.OnClickListener {

	private TextView txtTipoChave;
	private TextView txtValorrChave;
	private AppCompatImageView lixeira;
	private Context context;
	private OnChaveFavoritaClickListener listener;

	public ChavePixFavoritaHolder(View itemView, Context context, OnChaveFavoritaClickListener listener) {
		super(itemView);
		this.context = context;
		this.listener = listener;

		txtTipoChave = itemView.findViewById(R.id.tipoChavePix);
		txtValorrChave = itemView.findViewById(R.id.valueChavePix);
		lixeira = itemView.findViewById(R.id.lixeira);
		itemView.setOnClickListener(this);
		lixeira.setOnClickListener(this);
	}

	@Override
	public void bind(ChavePix chave, int position) {
		txtTipoChave.setText(chave.getChave().getTexto());
		txtValorrChave.setText(chave.getValue());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.lixeira){
			listener.excluir(getAdapterPosition());
		} else {
			listener.itemClick(getAdapterPosition());
		}
	}

}
