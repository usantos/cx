package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class SuperSeteHolder extends LoteriasHolder<List<List<Integer>>> {
	private TextView numeroJogo;
	private RecyclerView listView;
	private Context context;

	public SuperSeteHolder(View itemView, Context context) {
		super(itemView);
		numeroJogo = itemView.findViewById(R.id.numero_jogo);
		listView = itemView.findViewById(R.id.lista_super_sete);
		this.context = context;
	}

	@Override
	public void bind(List<List<Integer>> matriz, int position) {
		numeroJogo.setText("Jogo " + (position + 1));
	}

}
