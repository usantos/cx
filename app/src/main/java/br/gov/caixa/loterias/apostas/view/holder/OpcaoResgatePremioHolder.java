package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.OpcaoResgatePremio;

public class OpcaoResgatePremioHolder extends LoteriasHolder<OpcaoResgatePremio>{
	private View view;
	private ImageView ivIcon;
	private TextView tvTitle;
	private TextView tvSubtitle;

	public OpcaoResgatePremioHolder(View view){
		super(view);
		this.view = view;
		this.ivIcon = view.findViewById(R.id.ivIcon);
		this.tvTitle = view.findViewById(R.id.tvTitle);
		this.tvSubtitle = view.findViewById(R.id.tvSubtitle);
	}


	@Override
	public void bind(OpcaoResgatePremio item, int position) {
		ivIcon.setImageDrawable(item.getIcon());
		tvTitle.setText(item.getTitle());
		tvSubtitle.setText(item.getSubtitle());
		view.setOnClickListener(item.getListener());
	}
}
