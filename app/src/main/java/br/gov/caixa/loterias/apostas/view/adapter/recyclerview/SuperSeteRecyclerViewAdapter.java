package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.view.config.ColunaConfig;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.SuperSeteHolder;

public class SuperSeteRecyclerViewAdapter extends RecyclerView.Adapter<SuperSeteHolder>{

	private int cor;
	private List<ApostaBolaoDTO> apostas;
	private Context context;
	private ColunaConfig colunaConfig;
	private DezenaConfig dezenaConfig;
	private ArrayList<ArrayList<Integer>> matrizSelecionados;

	public SuperSeteRecyclerViewAdapter(int cor, List<ApostaBolaoDTO> apostas, Context context, ColunaConfig colunaConfig, DezenaConfig dezenaConfig, ArrayList<ArrayList<Integer>> matrizSelecionados) {
		this.cor = cor;
		this.apostas = apostas;
		this.context = context;
		this.colunaConfig = colunaConfig;
		this.dezenaConfig = dezenaConfig;
		this.matrizSelecionados = matrizSelecionados;
	}

	@NonNull
	@Override
	public SuperSeteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_super_sete,
																		parent, false);
		return new SuperSeteHolder(view, context, colunaConfig, dezenaConfig, cor, matrizSelecionados);

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
