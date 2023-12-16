package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.view.holder.JogoBolaoHolder;

public class JogosBolaoRecyclerView extends RecyclerView.Adapter<JogoBolaoHolder>{
	private List<ApostaBolaoDTO> apostas;

	public JogosBolaoRecyclerView(List<ApostaBolaoDTO> apostas) {
		this.apostas = apostas;
	}

	@NonNull
	@Override
	public JogoBolaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jogo_bolao,
																		parent, false);
		return new JogoBolaoHolder(view, parent.getContext());
	}

	@Override
	public void onBindViewHolder(@NonNull JogoBolaoHolder holder, int position) {
		holder.bind(apostas.get(position), position);
	}

	@Override
	public int getItemCount() {
		return apostas.size();
	}
}
