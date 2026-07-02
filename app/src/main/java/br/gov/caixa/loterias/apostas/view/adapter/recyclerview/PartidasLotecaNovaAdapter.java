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
import br.gov.caixa.loterias.apostas.view.holder.PartidaLotecaNovaHolder;

public abstract class PartidasLotecaNovaAdapter extends RecyclerView.Adapter<PartidaLotecaNovaHolder>{
	List<PartidaLotecaDTO> partidas;
	private Context context;
	private ResultadoConcursoDTO resultadoConcurso;
	private ConfiguracaoLoteca configuracaoLoteca;

//	private Boolean isEspecial;

//	public PartidasLotecaNovaAdapter(List<PartidaLotecaDTO> partidas, Context context, ConfiguracaoLoteca configuracaoLoteca) {
//		this.partidas = partidas;
//		this.context = context;
//		this.configuracaoLoteca = configuracaoLoteca;
//	}

//	public PartidasLotecaNovaAdapter(List<PartidaLotecaDTO> partidas, Context context, ConfiguracaoLoteca configuracaoLoteca, ResultadoConcursoDTO resultadoConcurso,
//									 Boolean isEspecial) {
//		this(partidas, context, configuracaoLoteca, resultadoConcurso);
//		this.isEspecial = isEspecial;
//	}


	public PartidasLotecaNovaAdapter(List<PartidaLotecaDTO> partidas, Context context, ConfiguracaoLoteca configuracaoLoteca, ResultadoConcursoDTO resultadoConcurso) {
		this.partidas = partidas;
		this.context = context;
		this.configuracaoLoteca = configuracaoLoteca;
		this.resultadoConcurso = resultadoConcurso;
	}

	@NonNull
	@Override
	public PartidaLotecaNovaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partida_loteca_nova, parent, false);
		//return new PartidaLotecaNovaHolder(view, context, configuracaoLoteca, resultadoConcurso, isEspecial);
		return new PartidaLotecaNovaHolder(view, context, configuracaoLoteca, resultadoConcurso);
	}

	public void onBindViewHolder(@NonNull PartidaLotecaNovaHolder holder, int position) {
		holder.bind(partidas.get(position), position);
	}

	@Override
	public int getItemCount() {
		return partidas.size();
	}
}
