package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaComboRecyclerView;

public class DezenaSuperSeteComboHolder extends LoteriasHolder<List<String>> {
	private TextView txtNumeroColuna;
	private RecyclerView listView;
	private Context context;

	public DezenaSuperSeteComboHolder(View itemView, Context context) {
		super(itemView);

		txtNumeroColuna = itemView.findViewById(R.id.numero_coluna);
		listView = itemView.findViewById(R.id.numeros_lista);
		this.context = context;
	}

	@Override
	public void bind(List<String> list, int position) {
		txtNumeroColuna.setText(position + 1 + "");

		listView.setLayoutManager(new GridLayoutManager(context, 1));
		listView.setAdapter(new ListaDezenaComboRecyclerView(list, false));
	}

}
