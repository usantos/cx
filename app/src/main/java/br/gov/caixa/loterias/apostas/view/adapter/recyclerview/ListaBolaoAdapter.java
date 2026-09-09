package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;

public class ListaBolaoAdapter extends RecyclerView.Adapter<BolaoHolder>{

	private List<CotasBolaoDTO> bolaoList;
	private OnItemBolaoListener listener;
	private Activity activity;
	private FiltroAplicadoMarketplace filtro;
    private Boolean showCotasRestantes;

	public ListaBolaoAdapter(List<CotasBolaoDTO> bolaoList, OnItemBolaoListener listener, Activity activity, @Nullable FiltroAplicadoMarketplace filtro, Boolean showCotasRestantes){
		this.bolaoList = bolaoList;
		this.listener = listener;
		this.activity = activity;
		this.filtro = filtro;
        this.showCotasRestantes = showCotasRestantes;
    }

	@NonNull
	@Override
	public BolaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_bolao, parent, false);
		return new BolaoHolder(activity, view, listener, showCotasRestantes);
	}

	@Override
	public void onBindViewHolder(@NonNull BolaoHolder holder, int position) {
		CotasBolaoDTO cotaBolao = bolaoList.get(position);
		holder.setFiltro(filtro);
		holder.bind(cotaBolao, position);
	}

	@Override
	public int getItemCount() {
		return bolaoList.size();
	}
	public void atualizarFiltro(FiltroAplicadoMarketplace filtro) {
		this.filtro = filtro;
		notifyDataSetChanged();
	}
}
