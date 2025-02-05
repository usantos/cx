package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.OpcaoResgatePremio;

public class OpcaoResgatePremioHolder extends LoteriasHolder<OpcaoResgatePremio>{
	private View view;
	private ImageView icon;
	private TextView descricao;

	public OpcaoResgatePremioHolder(View view){
		super(view);
		this.view = view;
		this.icon = view.findViewById(R.id.iconOpcao);
		this.descricao = view.findViewById(R.id.descricaoOpcao);
	}


	@Override
	public void bind(OpcaoResgatePremio item, int position) {
		if (item.getIcon() != null){
			icon.setImageDrawable(item.getIcon());
		} else {
			icon.setVisibility(View.GONE);
		}
		descricao.setText(item.getDescricao());
		view.setOnClickListener(item.getListener());
	}
}
