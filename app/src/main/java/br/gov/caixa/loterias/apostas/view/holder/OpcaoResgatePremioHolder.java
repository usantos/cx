package br.gov.caixa.loterias.apostas.view.holder;

import static androidx.core.util.TypedValueCompat.dpToPx;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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
		if (item.getIcon() == ContextCompat.getDrawable(ivIcon.getContext(), R.drawable.logo_mercado_pago)) {
			ViewGroup.LayoutParams params = ivIcon.getLayoutParams();
			params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 19, ivIcon.getResources().getDisplayMetrics());
			params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27, ivIcon.getResources().getDisplayMetrics());
			ivIcon.setLayoutParams(params);
		}
		ivIcon.setImageDrawable(item.getIcon());
		tvTitle.setText(item.getTitle());
		tvSubtitle.setText(item.getSubtitle());
		view.setOnClickListener(item.getListener());
	}
}
