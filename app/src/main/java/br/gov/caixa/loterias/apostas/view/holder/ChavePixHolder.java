package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ChavePixHolder extends LoteriasHolder<String> implements  View.OnClickListener {

	private TextView txtChave;
	private AppCompatImageView seta;
	private Context context;
	private OnItemClickListener listener;

	public ChavePixHolder(View itemView, Context context, OnItemClickListener listener) {
		super(itemView);
		this.context = context;
		this.listener = listener;

		txtChave = itemView.findViewById(R.id.chavePix);
		seta = itemView.findViewById(R.id.seta);
		itemView.setOnClickListener(this);
	}

	@Override
	public void bind(String chave, int position) {
		txtChave.setText(chave);
	}

	@Override
	public void onClick(View v) {
		listener.itemClick(this, getAdapterPosition());
	}

}
