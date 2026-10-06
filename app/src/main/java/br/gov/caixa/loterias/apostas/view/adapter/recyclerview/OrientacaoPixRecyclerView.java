package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.OrientacaoPix;
import br.gov.caixa.loterias.apostas.view.holder.OrientacaoPixHolder;

public class OrientacaoPixRecyclerView extends RecyclerView.Adapter<OrientacaoPixHolder>{

	private List<OrientacaoPix> orientacaoPixes;

	public OrientacaoPixRecyclerView(List<OrientacaoPix> orientacaoPixes) {
		this.orientacaoPixes = orientacaoPixes;
	}

	@NonNull
	@Override
	public OrientacaoPixHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orientacao_pix,
																		parent, false);
		return new OrientacaoPixHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull OrientacaoPixHolder holder, int position) {
		OrientacaoPix orientacao = orientacaoPixes.get(position);
		holder.bind(orientacao, position);
	}

	@Override
	public int getItemCount() {
		return orientacaoPixes.size();
	}

}
