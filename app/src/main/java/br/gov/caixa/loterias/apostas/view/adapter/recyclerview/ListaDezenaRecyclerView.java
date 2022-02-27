package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ListaDezenaRecyclerView extends RecyclerView.Adapter<DezenaHolder>{

	private List<Dezena> dezenas;
	private List<Integer> dezenasSelecionadas;
	private int typeGameColorLight;
	private OnItemClickListener listener;

	public ListaDezenaRecyclerView(List<Dezena> dezenas, List<Integer> dezenasSelecionadas, int typeGameColorLight, OnItemClickListener listener) {
		this.dezenas = dezenas;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.typeGameColorLight = typeGameColorLight;
		this.listener = listener;
	}

	@NonNull
	@Override
	public DezenaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dezena,
																		parent, false);
		DezenaConfig dezenaConfig = new DezenaConfig(false, typeGameColorLight,
													 R.drawable.ic_item_trevo, R.drawable.ic_item_trevo_selecionado);
		return new DezenaHolder(view, parent.getContext(), dezenaConfig, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull DezenaHolder holder, int position) {
		Dezena dezena = dezenas.get(position);
		if (dezenasSelecionadas.contains(Integer.valueOf(dezena.getValue()))) {
			dezena.setSelected(Boolean.TRUE);
		} else {
			dezena.setSelected(Boolean.FALSE);
		}
		holder.bind(dezena, position);
	}

	@Override
	public int getItemCount() {
		return dezenas.size();
	}
}
