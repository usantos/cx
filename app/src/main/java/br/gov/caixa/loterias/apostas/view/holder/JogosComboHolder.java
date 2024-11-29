package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaComboRecyclerView;

public class JogosComboHolder extends LoteriasHolder<IdentificaoDeUmaApostaDas8Modalidades> {
	private TextView modalidade, concurso, valor;
	private RecyclerView listView, trevosListView;

	private ConstraintLayout containerDiaSorte, containerTimemania, containerMaisMilionaria;

	public JogosComboHolder(View itemView) {
		super(itemView);
		modalidade = itemView.findViewById(R.id.id_modalidade);
		concurso = itemView.findViewById(R.id.id_concurso);
		valor = itemView.findViewById(R.id.id_valor);
		listView = itemView.findViewById(R.id.id_lista);
		containerDiaSorte = itemView.findViewById(R.id.id_dia_sorte);
		containerTimemania = itemView.findViewById(R.id.id_timemania);
		containerMaisMilionaria = itemView.findViewById(R.id.id_mais_milionaria);
		trevosListView = itemView.findViewById(R.id.id_lista_trevos);
	}

	@Override
	public void bind(IdentificaoDeUmaApostaDas8Modalidades aposta, int position) {
		modalidade.setText(ViewUtils.getNomeModalidadePorApostaDuasLinas(aposta).toLowerCase());
		if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			modalidade.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.timemania_letra_mkp));
		} else {
			modalidade.setTextColor(ContextCompat.getColor(itemView.getContext(), new EstiloModalidadeMKP(aposta.getModalidade()).getCorClara()));
		}
		concurso.setText(itemView.getContext().getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
		ViewUtils.setMoedaFormatHtml(aposta.getValor(), this.valor);

		listView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 6));
		listView.setAdapter(new ListaDezenaComboRecyclerView(getListXX(aposta.getQuantidadeNumeros()), false));
		apresentaDetalhesPorModalidade(aposta);
	}

	private List<String> getListXX(Integer total) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < total; i++){
			list.add(itemView.getContext().getString(R.string.x_x));
		}
		return list;
	}

	private void apresentaDetalhesPorModalidade(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
			containerMaisMilionaria.setVisibility(View.VISIBLE);
			containerTimemania.setVisibility(View.GONE);
			containerDiaSorte.setVisibility(View.GONE);
			trevosListView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 6));
			trevosListView.setAdapter(new ListaDezenaComboRecyclerView(getListXX(aposta.getQuantidadeTrevos()), true));
		} else if (aposta.getModalidade() == ModalidadeEnum.DIA_DE_SORTE){
			containerMaisMilionaria.setVisibility(View.GONE);
			containerTimemania.setVisibility(View.GONE);
			containerDiaSorte.setVisibility(View.VISIBLE);
		} else if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			containerMaisMilionaria.setVisibility(View.GONE);
			containerTimemania.setVisibility(View.VISIBLE);
			containerDiaSorte.setVisibility(View.GONE);
		} else {
			containerMaisMilionaria.setVisibility(View.GONE);
			containerTimemania.setVisibility(View.GONE);
			containerDiaSorte.setVisibility(View.GONE);
		}
	}
}
