package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.view.holder.PartidaLotecaHolder;

public class PartidasLotecaRecyclerViewAdapter extends RecyclerView.Adapter<PartidaLotecaHolder>{
	List<PartidaLotecaDTO> partidas;
	private Context context;
	private ResultadoConcursoDTO resultadoConcurso;
	private ConfiguracaoLoteca configuracaoLoteca;

	private Boolean isEspecial;

	public PartidasLotecaRecyclerViewAdapter(List<PartidaLotecaDTO> partidas, Context context, ConfiguracaoLoteca configuracaoLoteca) {
		this.partidas = partidas;
		this.context = context;
		this.configuracaoLoteca = configuracaoLoteca;
	}
	public PartidasLotecaRecyclerViewAdapter(List<PartidaLotecaDTO> partidas, Context context, ConfiguracaoLoteca configuracaoLoteca, Boolean isEspecial) {
		this.partidas = partidas;
		this.context = context;
		this.configuracaoLoteca = configuracaoLoteca;
		this.isEspecial = isEspecial;
	}

	public PartidasLotecaRecyclerViewAdapter(List<PartidaLotecaDTO> partidas, Context context, ConfiguracaoLoteca configuracaoLoteca, ResultadoConcursoDTO resultadoConcurso) {
		this.partidas = partidas;
		this.context = context;
		this.configuracaoLoteca = configuracaoLoteca;
		this.resultadoConcurso = resultadoConcurso;
	}

	@NonNull
	@Override
	public PartidaLotecaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partida_loteca,
																		parent, false);
		return new PartidaLotecaHolder(view, context, configuracaoLoteca, resultadoConcurso, isEspecial);
	}

	@Override
	public void onBindViewHolder(@NonNull PartidaLotecaHolder holder, int position) {
		holder.bind(partidas.get(position), position);
	}

	@Override
	public int getItemCount() {
		return partidas.size();
	}
}
