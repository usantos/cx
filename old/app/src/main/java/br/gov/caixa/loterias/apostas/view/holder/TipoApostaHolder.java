package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.view.listener.TipoApostaListener;

public class TipoApostaHolder extends LoteriasHolder<TipoAposta> {

	private LinearLayout background;
	private TextView modalidade;
	private ImageView trevo;
	private TipoApostaListener listener;

	public TipoApostaHolder(View itemView, TipoApostaListener listener) {
		super(itemView);
		background = itemView.findViewById(R.id.LinearLayoutContentTiposAposta);
		modalidade = itemView.findViewById(R.id.tituloTipoApostaTextView);
		trevo = itemView.findViewById(R.id.imageviewTrevoTipoAposta);
		this.listener = listener;
	}

	@Override
	public void bind(TipoAposta tipoAposta, int position) {

		modalidade.setText(tipoAposta.getTitulo());

		if (tipoAposta.getSelect()){
			trevo.setImageResource(tipoAposta.getImagemTrevoFundoCor());
			background.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), tipoAposta.getCorBackground()));
			modalidade.setTextColor(ContextCompat.getColor(itemView.getContext(),tipoAposta.getTextColor()));
		}else{
			trevo.setImageResource(tipoAposta.getImagemTrevo());
			background.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.branco));
			background.setBackground(ContextCompat.getDrawable(itemView.getContext(),R.drawable.custom_border_cinza));
			modalidade.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.cinzanaoselecionado));
		}

		if (listener != null){
			itemView.setOnClickListener(v -> {
				listener.onItemClicked(tipoAposta, getAbsoluteAdapterPosition());
			});
		}

	}

}
