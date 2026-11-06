package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.view.config.ColunaConfig;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.SuperSeteHolder;

public class ComprasSuperSeteRecyclerViewAdapter extends RecyclerView.Adapter<SuperSeteHolder>{

	private List<ApostaBolaoDTO> apostas;
	private Context context;
	private DezenaConfig dezenaConfig;
	private ColunaConfig colunaConfig;
	private int cor;

	public ComprasSuperSeteRecyclerViewAdapter(List<ApostaBolaoDTO> apostas, Context context, DezenaConfig dezenaConfig) {
		this.apostas = apostas;
		this.context = context;
		this.dezenaConfig = dezenaConfig;
	}

	@NonNull
	@Override
	public SuperSeteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compras_super_sete,
																		parent, false);
		return new SuperSeteHolder(view, context, colunaConfig, dezenaConfig, cor);
	}

	@Override
	public void onBindViewHolder(@NonNull SuperSeteHolder holder, int position) {
		holder.bind(apostas.get(position), position);
	}

	@Override
	public int getItemCount() {
		return apostas.size();
	}
}
