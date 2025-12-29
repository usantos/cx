package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.view.holder.LotericasFavoritasHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemLotericaFavoritaListener;

public class ListaLotericasAdapter extends RecyclerView.Adapter<LotericasFavoritasHolder>{

	private List<LotericaFavoritaDTO> lotericasList;
	private OnItemLotericaFavoritaListener listener;
	private Activity activity;

    public ListaLotericasAdapter(
            List<LotericaFavoritaDTO> lotericasList,
            OnItemLotericaFavoritaListener listener,
            Activity activity) {
        this.lotericasList = lotericasList;
        this.listener = listener;
        this.activity = activity;
    }



    public void removeAt(int position) {
        if (position < 0 || position >= lotericasList.size()) return;
        lotericasList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, lotericasList.size() - position);
    }


    public void replaceAll(List<LotericaFavoritaDTO> novaLista) {
        lotericasList.clear();
        if (novaLista != null) lotericasList.addAll(novaLista);
        notifyDataSetChanged();
    }



    @NonNull
	@Override
	public LotericasFavoritasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_lotericas, parent, false);
		return new LotericasFavoritasHolder(activity, view, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull LotericasFavoritasHolder holder, int position) {
		LotericaFavoritaDTO lotericaFavorita = lotericasList.get(position);
		holder.bind(lotericaFavorita, position);
	}

	@Override
	public int getItemCount() {
		return lotericasList.size();
	}
}
