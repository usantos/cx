package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.holder.JogoBolaoHolder;

public class JogosBolaoRecyclerView extends RecyclerView.Adapter<JogoBolaoHolder>{
	private ModalidadeEnum modalidade;
	private int corTitulo;
	private List<ApostaBolaoDTO> apostas;
	private List<Integer> dezenasSelecionadas;
	private DezenaConfig dezenaConfig;
	private ResultadoConcursoDTO resultadoConcurso;
	private DezenaConfig trevoConfig;

	public JogosBolaoRecyclerView(ModalidadeEnum modalidade, int corTitulo, List<ApostaBolaoDTO> apostas, List<Integer> dezenasSelecionadas, DezenaConfig dezenaConfig) {
		this.modalidade = modalidade;
		this.corTitulo = corTitulo;
		this.apostas = apostas;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.dezenaConfig = dezenaConfig;
	}

	public JogosBolaoRecyclerView(ModalidadeEnum modalidade, int corTitulo, List<ApostaBolaoDTO> apostas,
								  List<Integer> dezenasSelecionadas, DezenaConfig dezenaConfig, DezenaConfig trevoConfig) {
		this.modalidade = modalidade;
		this.corTitulo = corTitulo;
		this.apostas = apostas;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.dezenaConfig = dezenaConfig;
		this.trevoConfig = trevoConfig;
	}

	public JogosBolaoRecyclerView(ModalidadeEnum modalidade, int corTitulo, List<ApostaBolaoDTO> apostas,
								  List<Integer> dezenasSelecionadas, DezenaConfig dezenaConfig, ResultadoConcursoDTO resultadoConcurso) {
		this.modalidade = modalidade;
		this.corTitulo = corTitulo;
		this.apostas = apostas;
		this.dezenasSelecionadas = dezenasSelecionadas;
		this.dezenaConfig = dezenaConfig;
		this.resultadoConcurso = resultadoConcurso;
	}

	@NonNull
	@Override
	public JogoBolaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jogo_bolao,
																		parent, false);
		return new JogoBolaoHolder(modalidade, corTitulo, dezenasSelecionadas, dezenaConfig, view, parent.getContext(), trevoConfig);
	}

	@Override
	public void onBindViewHolder(@NonNull JogoBolaoHolder holder, int position) {
		holder.bind(apostas.get(position), position);
		if (resultadoConcurso != null){
			holder.bindResultado(resultadoConcurso);
		}
	}

	@Override
	public int getItemCount() {
		return apostas.size();
	}
}
