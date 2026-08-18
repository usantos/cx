package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class TimeCoracaoHolder extends LoteriasHolder<ParametroEquipe> implements  View.OnClickListener {
	private TextView nameTime;
	private TextView ufTime;
	private ImageView imageTime;
	private Context context;
	private OnItemClickListener listener;
	private View container;

	public TimeCoracaoHolder(View itemView, Context context, OnItemClickListener listener) {
		super(itemView);
		nameTime = itemView.findViewById(R.id.timeCoracaoNome);
		ufTime = itemView.findViewById(R.id.timeCoracaoUf);
		imageTime = itemView.findViewById(R.id.time_coracao_image);
		container = itemView.findViewById(
				R.id.itemTimeCoracaoLinearLayout
		);
		this.context = context;
		this.listener = listener;
		itemView.setOnClickListener(this);
	}

	@Override
	public void bind(ParametroEquipe equipe, int position) {
		equipe.setBrasaoSelecionadoId(R.drawable.time_selecionado);
		equipe.setBrasaoId(R.drawable.time);

		EscudoEquipeUtil.setEscudo(context, equipe, imageTime);
		container.setSelected(
				equipe.isSelecionado()
		);

		if (equipe.isSelecionado()) {
			nameTime.setTextColor(context.getResources().getColor(R.color.timemaniaCorTexto));
			ufTime.setTextColor(context.getResources().getColor(R.color.timemaniaCorTexto));
		} else {
			nameTime.setTextColor(context.getResources().getColor(R.color.cinza));
			ufTime.setTextColor(context.getResources().getColor(R.color.cinza));
		}

		nameTime.setText(equipe.getNome());
		ufTime.setText(equipe.getUf());
	}

	@Override
	public void onClick(View v) {
		listener.itemClick(this, getAbsoluteAdapterPosition());
	}

}
