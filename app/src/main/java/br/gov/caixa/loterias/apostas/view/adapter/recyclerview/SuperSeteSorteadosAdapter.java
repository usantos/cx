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
import br.gov.caixa.loterias.apostas.view.holder.SuperSeteSorteadosHolder;

public class SuperSeteSorteadosAdapter extends RecyclerView.Adapter<SuperSeteSorteadosHolder>{
	private List<List<List<Integer>>> matrizList;
	private Context context;
	private ColunaConfig colunaConfig;
	private DezenaConfig dezenaConfig;
	public SuperSeteSorteadosAdapter(List<List<List<Integer>>> matrizList, Context context, ColunaConfig colunaConfig, DezenaConfig dezenaConfig) {
		this.matrizList = matrizList;
		this.context = context;
		this.colunaConfig = colunaConfig;
		this.dezenaConfig = dezenaConfig;
	}

	@NonNull
	@Override
	public SuperSeteSorteadosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_super_sete,
																		parent, false);
		return new SuperSeteSorteadosHolder(view, context, colunaConfig, dezenaConfig);

	}

	@Override
	public void onBindViewHolder(@NonNull SuperSeteSorteadosHolder holder, int position) {
		holder.bind(matrizList.get(position), position);
	}

	@Override
	public int getItemCount() {
		return matrizList.size();
	}
}
