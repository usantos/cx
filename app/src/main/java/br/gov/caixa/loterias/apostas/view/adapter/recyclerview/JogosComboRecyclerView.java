package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.view.holder.JogosComboHolder;

public class JogosComboRecyclerView extends RecyclerView.Adapter<JogosComboHolder>{
	private List<IdentificaoDeUmaApostaDas8Modalidades> apostas;

	public JogosComboRecyclerView(List<IdentificaoDeUmaApostaDas8Modalidades> apostas) {
		this.apostas = apostas;
	}

	@NonNull
	@Override
	public JogosComboHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jogo_combo,
																		parent, false);
		return new JogosComboHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull JogosComboHolder holder, int position) {
		holder.bind(apostas.get(position), position);
	}

	@Override
	public int getItemCount() {
		return apostas.size();
	}
}
