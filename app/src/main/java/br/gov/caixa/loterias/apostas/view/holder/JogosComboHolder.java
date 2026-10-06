package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaComboRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaSuperSeteComboRecyclerView;

public class JogosComboHolder extends LoteriasHolder<IdentificaoDeUmaApostaDas8Modalidades> {
	private TextView modalidade, concurso, valor, mesSorte, timeCoracao;
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
		mesSorte = itemView.findViewById(R.id.mes_sorte);
		timeCoracao = itemView.findViewById(R.id.time_coracao);

	}

	@Override
	public void bind(IdentificaoDeUmaApostaDas8Modalidades aposta, int position) {
		modalidade.setText(ViewUtils.getNomeModalidadePorApostaDuasLinas(aposta).toLowerCase());
		if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA ||
			aposta.getModalidade() == ModalidadeEnum.SUPER_7 ||
			aposta.getModalidade() == ModalidadeEnum.DIA_DE_SORTE){
			modalidade.setTextColor(ContextCompat.getColor(itemView.getContext(), new EstiloModalidadeMKP(aposta.getModalidade()).getCorLetraLista()));
			concurso.setTextColor(ContextCompat.getColor(itemView.getContext(), new EstiloModalidadeMKP(aposta.getModalidade()).getCorLetraLista()));
			valor.setTextColor(ContextCompat.getColor(itemView.getContext(), new EstiloModalidadeMKP(aposta.getModalidade()).getCorLetraLista()));
		} else {
			modalidade.setTextColor(ContextCompat.getColor(itemView.getContext(), new EstiloModalidadeMKP(aposta.getModalidade()).getCorClara()));
			concurso.setTextColor(ContextCompat.getColor(itemView.getContext(), new EstiloModalidadeMKP(aposta.getModalidade()).getCorClara()));
			valor.setTextColor(ContextCompat.getColor(itemView.getContext(), new EstiloModalidadeMKP(aposta.getModalidade()).getCorClara()));
		}
		String concursos = itemView.getContext().getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta));
		if (aposta.getQuantidadeTeimosinhas() != null && aposta.getQuantidadeTeimosinhas() > 0){
			concursos = concursos + " a " + (ViewUtils.getConcursoAtualPorAposta(aposta) + aposta.getQuantidadeTeimosinhas());
		}
		concurso.setText(concursos);
		ViewUtils.setMoedaFormatHtml(aposta.getValor(), this.valor);

		if (aposta.getModalidade() == ModalidadeEnum.SUPER_7){
			listView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 7));
			listView.setAdapter(new ListaSuperSeteComboRecyclerView(getListaSuperSete(aposta)));
		} else {
			listView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 6));
			listView.setAdapter(new ListaDezenaComboRecyclerView(getListaApresentacao(aposta), false));
		}
		apresentaDetalhesPorModalidade(aposta);
	}

	private List<List<String>> getListaSuperSete(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		List<List<String>> matriz = new ArrayList<>();
		if (aposta.getNumerosSelecionados() != null && !((ArrayList)aposta.getNumerosSelecionados()).isEmpty() && !isSupresinha(aposta)){
			for (int i = 0; i < ((ArrayList<?>) aposta.getNumerosSelecionados()).size(); i++){
				matriz.add(getListNumeros((List) ((ArrayList<?>) aposta.getNumerosSelecionados()).get(i), true));
			}
		} else {
			initMatriz(aposta, matriz);
			preencheMatrizXX(aposta, matriz);
		}
		return matriz;
	}

	private static void preencheMatrizXX(IdentificaoDeUmaApostaDas8Modalidades aposta, List<List<String>> matriz) {
		for(int i = 0; i < aposta.getQuantidadeNumeros(); i++){
			if (i < 7){
				matriz.get(i).add("XX");
			} else if (i < 14){
				matriz.get(i - 7).add("XX");
			} else {
				matriz.get(i - 14).add("XX");
			}
		}
	}

	private static void initMatriz(IdentificaoDeUmaApostaDas8Modalidades aposta, List<List<String>> matriz) {
		for(int i = 0; i < aposta.getQuantidadeNumeros(); i++){
			matriz.add(new ArrayList<>());
		}
	}

	private List<String> getListaApresentacao(IdentificaoDeUmaApostaDas8Modalidades aposta){
		if (aposta.getNumerosSelecionados() != null && !((ArrayList)aposta.getNumerosSelecionados()).isEmpty()){
			return getListNumeros((ArrayList) aposta.getNumerosSelecionados(), false);
		} else {
			return getListXX(aposta.getQuantidadeNumeros());
		}
	}

	private List<String> getListNumeros(List numerosSelecionados, boolean isTrevo){
		List<String> list = new ArrayList<>();
		List<Integer> inteirosList = AppUtils.converteListaInteiros(numerosSelecionados);
		for (Integer numero: inteirosList){
			if (isTrevo){
				list.add(numero.toString());
			} else {
				if (numero < 10){
					list.add("0" + numero);
				} else if (numero == 100){
					list.add("00");
				} else {
					list.add(numero.toString());
				}
			}
		}
		return list;
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
			trevosListView.setAdapter(new ListaDezenaComboRecyclerView(getListTrevosApresentacao(aposta), true));
		} else if (aposta.getModalidade() == ModalidadeEnum.DIA_DE_SORTE){
			containerMaisMilionaria.setVisibility(View.GONE);
			containerTimemania.setVisibility(View.GONE);
			containerDiaSorte.setVisibility(View.VISIBLE);
			if (aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNome() != null && !isSupresinha(aposta) ){
				mesSorte.setText("\nMês de sorte: " + aposta.getMesDeSorte().getNome());
			}
		} else if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			containerMaisMilionaria.setVisibility(View.GONE);
			containerTimemania.setVisibility(View.VISIBLE);
			containerDiaSorte.setVisibility(View.GONE);
			if (aposta.getTimeDoCoracao() != null && !isSupresinha(aposta)){
				timeCoracao.setText("\nTime do coração: " + aposta.getTimeDoCoracao().getNome() + "-" + aposta.getTimeDoCoracao().getUf());
			}
		} else {
			containerMaisMilionaria.setVisibility(View.GONE);
			containerTimemania.setVisibility(View.GONE);
			containerDiaSorte.setVisibility(View.GONE);
		}
	}

	private boolean isSupresinha(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		return aposta.getIndicadorSurpresinha() != null &&
				aposta.getIndicadorSurpresinha().getValor() != null &&
				aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA;
	}

	private List<String> getListTrevosApresentacao(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		if (aposta.getTrevosSelecionados() != null && !aposta.getTrevosSelecionados().isEmpty()){
			return getListNumeros(aposta.getTrevosSelecionados(), true);
		} else {
			return getListXX(aposta.getQuantidadeTrevos());
		}
	}
}
