package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ListaDezenaRecyclerView extends RecyclerView.Adapter<DezenaHolder>{

	private List<Dezena> dezenas;
	private List<Integer> dezenasSelecionadas;
	private int color;
	private boolean isSurpresinha = false;
	private DezenaConfig dezenaConfig;
	private OnItemClickListener listener;

	public ListaDezenaRecyclerView(List<Dezena> dezenas, List<Integer> dezenasSelecionadas, int color, OnItemClickListener listener) {
		this.dezenas = dezenas;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.color = color;
		this.listener = listener;
		this.dezenaConfig = new DezenaConfig(true, color, Color.WHITE, true);
	}

	public ListaDezenaRecyclerView(List<Dezena> dezenas, List<Integer> dezenasSelecionadas, DezenaConfig dezenaConfig, OnItemClickListener listener) {
		this.dezenas = dezenas;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.dezenaConfig = dezenaConfig;
		this.color = dezenaConfig.getColor();
		this.listener = listener;
	}

	public ListaDezenaRecyclerView(boolean isSurpresinha, List<Dezena> dezenas, List<Integer> dezenasSelecionadas, DezenaConfig dezenaConfig, OnItemClickListener listener) {
		this.dezenas = dezenas;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.dezenaConfig = dezenaConfig;
		this.color = dezenaConfig.getColor();
		this.listener = listener;
		this.isSurpresinha = isSurpresinha;
	}
	@NonNull
	@Override
	public DezenaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(dezenaConfig.getLayout(),
																		parent, false);
		return new DezenaHolder(view, parent.getContext(), dezenaConfig, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull DezenaHolder holder, int position) {
		Dezena dezena = dezenas.get(position);
		if(!isSurpresinha){
			if (dezenasSelecionadas!= null && dezenasSelecionadas.contains(Integer.valueOf(dezena.getValue()))) {
				dezena.setSelected(Boolean.TRUE);
			} else {
				dezena.setSelected(Boolean.FALSE);
			}
		}
		holder.bind(dezena, position);
	}

	@Override
	public int getItemCount() {	return dezenas.size(); }

	public void atualizaSelecionados(List<Integer> selecionados) {
		this.dezenasSelecionadas = selecionados;
		notifyDataSetChanged();
	}
}
