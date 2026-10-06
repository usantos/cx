package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.view.holder.ModalidadeHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class SelecaoModalidadesRecyclerAdapter extends RecyclerView.Adapter<ModalidadeHolder> {
	private final List<TipoAposta> tiposAposta;
	private Boolean isUnicaSelecao;
	private OnItemClickListener listener;

	public SelecaoModalidadesRecyclerAdapter(List<TipoAposta> tiposAposta, Boolean isUnicaSelecao, OnItemClickListener listener) {
		this.tiposAposta = tiposAposta;
		this.isUnicaSelecao = isUnicaSelecao;
		this.listener = listener;
	}

	@NonNull
	@Override
	public ModalidadeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linearlayout_tipos_apostas_arredondadas,
				parent, false);
		return new ModalidadeHolder(view, parent.getContext(), listener);
	}

	@Override
	public void onBindViewHolder(@NonNull ModalidadeHolder holder, int position) {
		holder.bind(tiposAposta.get(position), position);
	}

	@Override
	public int getItemCount() {
		return tiposAposta.size();
	}

	public List<ModalidadeEnum> getModalidadesEnumSelecionadas() {
		List<ModalidadeEnum> modalidadesEnum = new ArrayList<>();
		List<ModalidadeDTO>  modalidades     = getModalidadesSelecionadas();
		for (ModalidadeDTO modalidade : modalidades) {
			modalidadesEnum.add(ModalidadeEnum.fromModalidadeDTO(modalidade));
		}
		return modalidadesEnum;
	}

	public List<ModalidadeDTO> getModalidadesSelecionadas() {
		List<ModalidadeDTO> modalidades = new ArrayList<>();
		for (TipoAposta tipoAposta : tiposAposta) {
			if (tipoAposta.getSelect()) {
				ModalidadeDTO modalidade = new ModalidadeDTO();
				modalidade.setDescricao(tipoAposta.getTitulo());
				modalidade.setDescricaoEspecial(tipoAposta.getDescricaoEspecial());
				modalidade.setValor(tipoAposta.getValor());
				modalidades.add(modalidade);
			}
		}
		return modalidades;
	}

	public void limpaSelecaoModalidades() {
		if (tiposAposta != null) {
			for (TipoAposta aposta : tiposAposta) {
				aposta.setSelect(false);
			}
		}
		notifyDataSetChanged();
	}

	public void selecionaOPrimeiro() {
		if (tiposAposta != null) {
			for (int i = 0; i < tiposAposta.size(); i++) {
				if (i == 0) {
					tiposAposta.get(i).setSelect(true);
				} else {
					tiposAposta.get(i).setSelect(false);
				}
			}
		}
		notifyDataSetChanged();
	}

}