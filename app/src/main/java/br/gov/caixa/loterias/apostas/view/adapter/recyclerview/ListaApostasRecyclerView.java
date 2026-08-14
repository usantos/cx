package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Aposta;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.ApostaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ListaApostasRecyclerView extends RecyclerView.Adapter<ApostaHolder>{

	private List<Aposta> apostas;
	private List<Integer> apostasSelecionadas;
	private int color;
	private boolean isSurpresinha = false;
	private DezenaConfig apostasConfig;
	private OnItemClickListener listener;

	public ListaApostasRecyclerView(List<Aposta> apostas, List<Integer> apostasSelecionadas, int color, OnItemClickListener listener) {
		this.apostas = apostas;
		this.apostasSelecionadas = apostasSelecionadas;
		this.color = color;
		this.listener = listener;
		this.apostasConfig = new DezenaConfig(true, color, Color.WHITE, true);
	}

	public ListaApostasRecyclerView(List<Aposta> apostas, List<Integer> apostasSelecionadas, DezenaConfig apostaConfig, OnItemClickListener listener) {
		this.apostas = apostas;
		this.apostasSelecionadas = apostasSelecionadas;
		this.apostasConfig = apostasConfig;
		this.color = apostaConfig.getColor();
		this.listener = listener;
	}

	public ListaApostasRecyclerView(boolean isSurpresinha, List<Aposta> apostas, List<Integer> apostasSelecionadas, DezenaConfig apostaConfig, OnItemClickListener listener) {
		this.apostas = apostas;
		this.apostasSelecionadas = apostasSelecionadas;
		this.apostasConfig = apostasConfig;
		this.color = apostaConfig.getColor();
		this.listener = listener;
		this.isSurpresinha = isSurpresinha;
	}
	@NonNull
	@Override
	public ApostaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(apostasConfig.getLayout(),
																		parent, false);
		return new ApostaHolder(view, parent.getContext(), apostasConfig, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull ApostaHolder holder, int position) {
		Aposta asposta = apostas.get(position);
		if(!isSurpresinha){
			if (apostasSelecionadas!= null && apostasSelecionadas.contains(Integer.valueOf(asposta.getValue()))) {
				asposta.setSelected(Boolean.TRUE);
			} else {
				asposta.setSelected(Boolean.FALSE);
			}
		}
		holder.bind(asposta, position);
	}

	@Override
	public int getItemCount() {	return apostas.size(); }

	public void atualizaSelecionados(List<Integer> selecionados) {
		this.apostasSelecionadas = selecionados;
		notifyDataSetChanged();
	}
}
